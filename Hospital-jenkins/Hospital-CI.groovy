pipeline {
    agent any
    environment {
        SONAR_HOME = tool 'sonar'
    }
    stages {
        stage('Cleaning Workspace') {
            steps {
                cleanWs()
                echo "Workspace cleaned successfully"
            }
        }
        stage('Cloning Repository') {
            steps {
                git url: 'https://github.com/furkhan-2000/Hospital-Proj', branch: 'main'
                echo "Repository cloned successfully"
            }
        }
        stage('Dynamic Tagging') {
            steps {
                script {
                    def commitHash = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.IMAGE_TAG = commitHash
                    echo "Using IMAGE_TAG: ${env.IMAGE_TAG}"
                }
            }
        }
        stage('Sonar Analysis & Quality Gate') {
                    steps {
                        withSonarQubeEnv('sonar') {
                            sh "${SONAR_HOME}/bin/sonar-scanner -Dsonar.projectName=Hospital-Proj -Dsonar.projectKey=Hospital-Proj"
                            echo "SonarQube analysis completed"
                        }
                    }
                }
        stage('Sonar Quality Gates') {
                    steps {
                        timeout(time: 2, unit: 'MINUTES') {
                            script {
                                def qg = waitForQualityGate(abortPipeline: false)
                                if (qg.status != "OK") {
                                    error "Quality Gate Failed: ${qg.status}"
                                }
                            }
                        }
                        echo "Quality gate passed"
                    }
                }
        stage('OWASP Dependency Check') {
            steps {
                timeout(time: 27, unit: 'MINUTES') {
                    retry(2) {
                        dependencyCheck(
                            additionalArguments: '--scan . --format XML --out ./ --prettyPrint',
                            odcInstallation: 'owasp'
                        )
                    }
                    dependencyCheckPublisher pattern: 'dependency-check-report.xml'
                    echo "OWASP dependency check completed"
                }
            }
        }
        stage('Trivy Vulnerability Scan') {
            steps {
                sh "trivy fs ./ --cache-dir ./trivyresult"
                echo "Trivy scan completed successfully"
            }
        }
        stage('Building & Deploying') {
            steps {
                sh '''
                    docker compose down
                    docker image prune -a -f
                    docker system prune --all -f
                    docker compose up -d
                '''
                echo "Deprecated images removed, new containers started"
            }
        }
        stage('Authenticating & Pushing Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerHubCred', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh '''
                        docker tag hospital-ci-urine-report-check:latest ${DOCKERHUB_USERNAME}/shark:urine-${IMAGE_TAG}
                        docker tag hospital-ci-blood-report-check:latest ${DOCKERHUB_USERNAME}/shark:blood-${IMAGE_TAG}
                        echo "Images tagged successfully"
                        
                        docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}
                        echo "Logged into Docker Hub"

                        docker push ${DOCKERHUB_USERNAME}/shark:urine-${IMAGE_TAG}
                        docker push ${DOCKERHUB_USERNAME}/shark:blood-${IMAGE_TAG}
                        echo "Images pushed successfully to Docker Hub"
                    '''
                }
            }
        }
    }
    post {
        success {
            build job: "Hospital-CD",
                  parameters: [string(name: 'IMAGE_TAG', value: "${env.IMAGE_TAG}")],
                  wait: false,
                  propagate: false
            echo "CD Job triggered successfully from CI pipeline"
        }
        failure {
            mail to: "furkhan2000@icloud.com",
                 subject: "Pipeline Failed",
                 body: "Hospital-CI Pipeline (Build ${env.BUILD_NUMBER}) failed. Please review console output at ${env.BUILD_URL} and fix ASAP"
            echo "Failure notification sent"
        }
    }
}

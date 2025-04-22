def call(String project, String imageTag, String dockerhubuser) {
   withCredentials([usernamePassword(
                    credentialsId: 'dockerHubCred',
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_PASSWORD'
                )]) {
                    echo "This is tagging part"
                    sh "docker tag testing-web:latest ${DOCKERHUB_USERNAME}/shark:workouts"
                    sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                    echo "successfully logged in ${DOCKERHUB_USERNAME}"
                    sh "docker push ${DOCKERHUB_USERNAME}/shark:workouts"
                    echo "successfully image pushed"
                }
}

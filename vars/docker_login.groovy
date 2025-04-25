def call(String project,
         String imageTag,
         String credentialsId = 'dockerHubCred') {

    withCredentials([usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'DOCKERHUB_USERNAME',
        passwordVariable: 'DOCKERHUB_PASSWORD'
    )]) {
        sh "docker tag ${project}:${imageTag} ${DOCKERHUB_USERNAME}/${project}:${imageTag}"

        sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"

        sh "docker push ${DOCKERHUB_USERNAME}/${project}:${imageTag}"

        echo "Docker image push complete"
    }
}

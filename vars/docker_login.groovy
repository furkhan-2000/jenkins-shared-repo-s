def call(String project, String imageTag, String credentialsId = 'dockerHubCred') {
    steps.withCredentials([steps.usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'DOCKERHUB_USERNAME',
        passwordVariable: 'DOCKERHUB_PASSWORD'
    )]) {
        steps.sh "docker tag ${project}:${imageTag} ${DOCKERHUB_USERNAME}/${project}:${imageTag}"
        steps.sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
        steps.sh "docker push ${DOCKERHUB_USERNAME}/${project}:${imageTag}"
    }
}

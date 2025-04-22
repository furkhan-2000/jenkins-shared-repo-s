def call(String project, String imageTag) {
    withCredentials([usernamePassword(
        credentialsId: 'dockerHubCred',
        usernameVariable: 'DOCKERHUB_USERNAME',
        passwordVariable: 'DOCKERHUB_PASSWORD'
    )]) {
        echo "This is tagging part"
        sh "docker tag ${project}:${imageTag} ${DOCKERHUB_USERNAME}/${project}:${imageTag}"
        sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
        echo "Successfully logged in as ${DOCKERHUB_USERNAME}"
        sh "docker push ${DOCKERHUB_USERNAME}/${project}:${imageTag}"
        echo "Successfully pushed image"
    }
}

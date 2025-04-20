def call(String project, String imageTag, String dockerhubuser) {
    withCredentials([usernamePassword(
        credentialsId: 'dockerHubCred',
        usernameVariable: 'DOCKERHUB_USER',
        passwordVariable: 'DOCKERHUB_PASS'
    )]) {
        sh "docker login -u ${dockerhubuser} -p ${DOCKERHUB_PASS}"
        sh "docker push ${dockerhubuser}/${project}:${imageTag}"
    }
}

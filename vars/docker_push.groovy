def call(String project, String imageTag, String dockerhubuser) {
  withCredentials([
    usernamePassword(
      credentialsId: 'dockerHubCred',
      usernameVariable: 'DOCKERHUB_USERNAME',
      passwordVariable: 'DOCKERHUB_PASSWORD'
    )
  ]) {
    // Log in to Docker Hub using the correct variable name
    sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"

    // Tag the Docker image using provided parameters instead of hardcoded names
    sh "docker tag ${project}:${imageTag} ${dockerhubuser}/${project}:${imageTag}"

    // Push the Docker image to Docker Hub
    sh "docker push ${dockerhubuser}/${project}:${imageTag}"
  }
}

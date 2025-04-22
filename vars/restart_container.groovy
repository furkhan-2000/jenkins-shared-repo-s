def call(List services = []) {
    echo "container down 😔😔 successfully"
    sh "docker compose down"
    echo "container running 🥳🥳 successfully"
    sh "docker compose up -d ${services.join(' ')}"
}

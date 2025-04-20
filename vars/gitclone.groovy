def call(String url, String branch) {
    echo "its code block"
    git url: "${url}", branch: "${branch}"
    echo "successfully git repo got cloned"
}

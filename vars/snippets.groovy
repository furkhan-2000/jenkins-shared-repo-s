def call(){
  sh "mkdir deleteplease"
  sh "rmdir deleteplease"
  sh "mkdir oberoie"
  echo "please delete oberoie"
  sh "rm -rf oberoie"
}

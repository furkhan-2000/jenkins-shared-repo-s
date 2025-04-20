def call(){
  sh "mkdir deleteplease"
  sh "rmdir deleteplease"
  sh "oberoie"
  echo "please delete oberoie"
  sh "rm -rf oberoie"

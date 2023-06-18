pipeline {
   agent any

   stages {
      stage('拉取代码') {
          steps {
              echo '拉取代码'
              checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'YHF', url: 'git@git.nju.edu.cn:201250203/web-frontend.git']])
            }
        }
    stage('编译构建') {
        steps {
            echo '编译构建'
            sh 'npm i'
            sh 'npm run build'
            }
    }
    stage('项目部署') {
        steps {
            echo '项目部署'
            sh 'chmod 777 -R .'
            sh './start.sh'
            }
        }
   }
}

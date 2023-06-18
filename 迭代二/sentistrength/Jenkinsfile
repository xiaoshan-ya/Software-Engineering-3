pipeline {
   agent any

   stages {
      stage('拉取代码') {
          steps {
              echo '拉取代码'
              checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'YHF', url: 'git@git.nju.edu.cn:201250203/sentistrength.git']])
            }
        }
    stage('编译构建') {
        steps {
            echo '编译构建'
            sh 'mvn clean package'
            }
    }
    stage('自动发布') {
        steps {
            echo '自动发布'
            sh 'chmod 777 -R .'
            sh './start.sh'
            }
        }
   }
}

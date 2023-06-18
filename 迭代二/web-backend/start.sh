#!/bin/bash

function kill_project()
{
  project_pid=`ps aux | grep "WebBackend-1.0-SNAPSHOT.jar" | grep -v grep | awk 'END{print $2}'`
  if [  $project_pid > 0 ];then
        echo "项目已经启动，开始关闭项目，项目pid为: $project_pid "
        echo "--------------------"
        kill -9 `ps aux | grep "WebBackend-1.0-SNAPSHOT.jar" | grep -v grep | awk 'END{print $2}'`
        echo '项目关闭成功，开始重启项目 '
        echo "--------------------"
  else
        echo "项目未启动，直接启动"
        echo "--------------------"
  fi
}

function start_project()
{
        source /etc/profile
        echo "切换路径：/var/lib/jenkins/workspace/web-backend"
        cd /var/lib/jenkins/workspace/web-backend
        echo "--------------------"
        echo "正在启动项目"
        JENKINS_NODE_COOKIE=dontKillMe nohup java -jar target/WebBackend-1.0-SNAPSHOT.jar>publish.log &
        sleep 10s
        echo "--------------------"

}

function check_project()
{
  check_pid=`ps aux | grep "WebBackend-1.0-SNAPSHOT.jar" | grep -v grep | awk 'END{print $2}'`
  if [ $check_pid  > 0 ];then
        echo "项目启动成功： pid = : $check_pid  "
  else
        echo "项目启动失败"
        exit 1
  fi
}

kill_project
start_project
check_project

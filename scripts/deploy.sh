#!/usr/bin/env bash

PROJECT_ROOT="/home/ec2-user/app"

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# file 복사
echo "$TIME_NOW > 파일 복사" >> $DEPLOY_LOG
cp /home/ec2-user/app/build/libs/kream-0.0.1-SNAPSHOT.jar   /home/ec2-user/app/kream.jar


echo "현재 구동 중인 애플리케이션 pid 확인" >> $DEPLOY_LOG

CURRENT_PID=$(pgrep -f /home/ec2-user/app/kream.jar)

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID" >> $DEPLOY_LOG

if [ -z "$CURRENT_PID" ]; then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포" >> $DEPLOY_LOG

JAR_NAME=$(ls -tr /home/ec2-user/app/kream.jar | tail -n 1)

echo "> JAR NAME: $JAR_NAME" >> $DEPLOY_LOG

echo "> $JAR_NAME 에 실행권한 추가" >> $DEPLOY_LOG

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행" >> $DEPLOY_LOG

nohup java -jar -Dspring.profiles.active=prod /home/ec2-user/app/kream.jar &

exit 0


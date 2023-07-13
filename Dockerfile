# Docker 이미지의 기본 베이스 이미지 설정
FROM openjdk:11

# 작업 디렉토리 생성
WORKDIR /app

# 호스트 머신의 현재 디렉토리에 있는 uplog.jar 파일을 컨테이너의 /app 디렉토리로 복사
COPY build/libs/uplog-0.0.1-SNAPSHOT.jar /app/uplog-0.0.1-SNAPSHOT.jar
COPY src/main/resources/application.yml /app/application.yml

# 컨테이너 내에서 실행할 명령어 설정
CMD ["java", "-jar", "uplog-0.0.1-SNAPSHOT.jar", "--spring.config.location=file:/app/application.yml"]

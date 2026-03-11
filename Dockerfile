FROM eclipse-temurin:17-jdk

WORKDIR /app

# 로그 디렉토리 생성
RUN mkdir -p /logs

# 빌드된 jar 파일 복사
COPY build/libs/*.jar app.jar

# 애플리케이션 실행
CMD ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]

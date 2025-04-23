FROM eclipse-temurin:21-jre-alpine-3.21

WORKDIR /app

COPY target/blog_post_manager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
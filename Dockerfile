FROM amazoncorretto:21

COPY target/java-project-defense-*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

EXPOSE 8080
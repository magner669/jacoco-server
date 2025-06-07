FROM eclipse-temurin:22-jdk

# Create a non-root user with a specific UID (e.g., 1001)
RUN adduser -u 1001 -M jacocoserver

COPY build/libs/jacoco-server-1.0-SNAPSHOT-boot.jar /app/jacoco-server.jar

RUN chown -R jacocoserver:jacocoserver /app

USER jacocoserver

ENTRYPOINT ["java", "-jar", "/app/jacoco-server.jar"]
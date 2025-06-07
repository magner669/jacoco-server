FROM ghcr.io/graalvm/graalvm-community:22.0.0.2

# Create a non-root user with a specific UID (e.g., 1001)
RUN adduser --disabled-password --gecos '' --uid 1001 jacocoserver

COPY build/libs/jacoco-server-0.0.1-SNAPSHOT.jar /app/jacoco-server.jar

RUN chown -R jacocoserver:jacocoserver /app

USER jacocoserver

ENTRYPOINT ["java", "-jar", "/app/jacoco-server.jar"]
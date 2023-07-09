FROM openjdk:17-jdk-slim
COPY target/open-api-converter.jar open-api-converter.jar
ENTRYPOINT exec java $JAVA_OPTS -jar open-api-converter.jar

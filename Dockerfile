FROM openjdk:11-jdk-slim
COPY target/open-api-converter.war open-api-converter.war
ENTRYPOINT exec java $JAVA_OPTS -jar open-api-converter.war

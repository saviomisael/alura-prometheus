FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/*jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Xms128M","-Xmx128M","-XX:PermSize=64m","-XX:MaxPermSize=128m","-jar","/app.jar"]

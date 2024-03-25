FROM openjdk:17-jdk-alpine
COPY target/my-project-backend-0.0.1-SNAPSHOT.jar /work/app.jar
WORKDIR /work
CMD ["java","-jar","app.jar"]
FROM maven:3.8.4-jdk-11 as builder

WORKDIR /MailDemo
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

CMD ["java","-jar","/MailDemo/target/MailDemo-1.0-SNAPSHOT.jar"]
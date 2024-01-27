FROM openjdk:17-jdk-alpine
COPY target/fd-caches-sb-aop-1.0-SNAPSHOT.jar fd-caches-sb-aop.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/fd-caches-sb-aop.jar"]
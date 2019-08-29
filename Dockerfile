FROM java:8
EXPOSE 8080
ARG JAR_FILE
ADD target/${JAR_FILE} /kubernetes-reload-config.jar
ENTRYPOINT ["java", "-jar","/kubernetes-reload-config.jar"]
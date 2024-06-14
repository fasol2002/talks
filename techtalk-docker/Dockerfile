FROM maven:3.9-sapmachine-21

WORKDIR /usr/src/mymaven
COPY . .
RUN mvn -B -Daether.dependencyCollector.impl=bf -Dmaven.artifact.threads=20 package

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/src/mymaven/target/app.jar"]
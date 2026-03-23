FROM eclipse-temurin:25
LABEL maintainer="Alisson Simon alissonsimon21@gmail.com"
WORKDIR /app
COPY target/match.stats-0.0.1-SNAPSHOT.jar /app/match.stats.jar
ENTRYPOINT ["java", "-jar", "match.stats.jar"]

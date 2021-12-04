FROM maven:3.6.3-openjdk-15 AS build
COPY ./ /app
WORKDIR /app
RUN mvn --show-version --update-snapshots --batch-mode clean package

FROM adoptopenjdk:17-jre-hotspot
RUN mkdir /app
WORKDIR /app
COPY --from=build ./app/api/target/station-catalog-api-0.0.1-SNAPSHOT.jar /app
EXPOSE 8080
CMD ["java", "-jar", "station-catalog-api-0.0.1-SNAPSHOT.jar"]

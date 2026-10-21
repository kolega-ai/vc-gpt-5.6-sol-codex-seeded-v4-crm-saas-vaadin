FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY gradle gradle
COPY gradlew settings.gradle build.gradle gradle.properties ./
COPY src src
RUN ./gradlew clean bootJar -Pvaadin.productionMode --no-daemon

FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 --create-home crm
WORKDIR /app
COPY --from=build /workspace/build/libs/customer-operations-crm-1.0.0.jar app.jar
RUN mkdir /app/storage && chown -R crm:crm /app
USER crm
EXPOSE 8080
ENTRYPOINT ["java","-Xms128m","-Xmx512m","-XX:UseSVE=0","-jar","/app/app.jar"]

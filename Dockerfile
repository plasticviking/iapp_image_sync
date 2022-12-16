FROM eclipse-temurin:17-alpine as builder
RUN mkdir /opt/iapp
WORKDIR /opt/iapp
COPY . .
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-alpine
RUN mkdir /opt/iapp
WORKDIR /opt/iapp
COPY --from=builder /opt/iapp/build/libs/iapp_image_sync-1.0-SNAPSHOT.jar /opt/iapp
RUN chgrp -R 0 /opt/iapp && chmod -R g=u /opt/iapp
CMD ["java", "-jar", "iapp_image_sync-1.0-SNAPSHOT.jar"]

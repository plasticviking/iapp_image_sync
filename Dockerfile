FROM eclipse-temurin:11
RUN mkdir /opt/iapp
WORKDIR /opt/iapp
COPY . .
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon
RUN chgrp -R 0 /opt/iapp && chmod -R g=u /opt/iapp
CMD ["java", "-jar", "build/libs/iapp_image_sync-1.0-SNAPSHOT.jar"]

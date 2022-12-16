FROM eclipse-temurin:11
RUN mkdir /opt/iapp
WORKDIR /opt/iapp
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon
CMD ["./gradlew", "bootRun"]

FROM eclipse-temurin:11
RUN mkdir /opt/iapp
WORKDIR /opt/iapp
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon
RUN mkdir /.gradle
RUN chgrp -R 0 /.gradle && chmod -R 0775 /.gradle
RUN chgrp -R 0 /opt/iapp && chmod -R g=u /opt/iapp
CMD ["./gradlew", "bootRun"]

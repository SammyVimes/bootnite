FROM adoptopenjdk:8-jre-hotspot
RUN mkdir /opt/app && mkdir /opt/ignite

EXPOSE 8080

COPY build/libs/bootnite-0.0.1-SNAPSHOT.jar /opt/app/app.jar
CMD ["java", "-jar", "/opt/app/app.jar"]

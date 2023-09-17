FROM maven:3.8.5-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM openjdk:17-alpine
COPY --from=build /home/app/target/hl-sonet-dialog-1.0-SNAPSHOT.jar application.jar
ENV OPTS="-Djava.rmi.server.hostname=0.0.0.0  -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=33083 -Dcom.sun.management.jmxremote.rmi.port=33083 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
ENV JAVA_TOOL_OPTIONS "-Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=33083 -Dcom.sun.management.jmxremote.rmi.port=33083 -Dcom.sun.management.jmxremote.host=0.0.0.0 -Djava.rmi.server.hostname=0.0.0.0"
EXPOSE 8080 33083
ENTRYPOINT ["java", "-jar", "application.jar"]
CMD java $OPTS -jar application.jar

#FROM openjdk:17-alpine
#
#WORKDIR /app
#
#ADD ./src/main/resources/static .
#
#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./
#RUN ./mvnw dependency:go-offline
#
#COPY src ./src
#
#CMD ["./mvnw", "spring-boot:run"]
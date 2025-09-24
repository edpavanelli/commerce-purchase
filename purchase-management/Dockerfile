FROM tomcat:10.1-jdk21

WORKDIR /usr/local/tomcat

COPY build/libs/*.jar /usr/local/tomcat/app.jar

EXPOSE 8443

ENTRYPOINT ["java","-jar","/usr/local/tomcat/app.jar"]
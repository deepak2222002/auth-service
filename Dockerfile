FROM tomcat:10.1-jdk21

WORKDIR /usr/local/tomcat

RUN rm -rf webapps/*

COPY target/*.war webapps/ROOT.war

COPY src/main/resources/auth-service.p12 conf/auth-service.p12

EXPOSE 8080
EXPOSE 8443

CMD ["catalina.sh", "run"]
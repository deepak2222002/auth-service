FROM tomcat:10.1-jdk21

WORKDIR /usr/local/tomcat

RUN rm -rf webapps/*

COPY target/*.war webapps/ROOT.war

EXPOSE 8092

CMD ["catalina.sh", "run"]
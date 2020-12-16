FROM store/oracle/jdk:11

WORKDIR /app

ARG DATABASE_URL
ARG JAVA_OPTS

ADD target/vtkdemo-0.0.1-SNAPSHOT.jar .
ADD repo/kitware/community/vtk-natives-linux-x64/8.2/vtk-natives-linux-x64-8.2.jar .
ADD repo/org/simpleitk/simpleitk-natives-linux-x64/2.0.0/simpleitk-natives-linux-x64-2.0.0.jar .

#RUN mv ./apache-maven-3.6.3 /opt/apache-maven-3.6.3
#RUN ln -s /opt/apache-maven-3.6.3 /opt/maven
#RUN mv /opt/maven/maven.sh /etc/profile.d/
#RUN chmod +x /etc/profile.d/maven.sh

RUN jar xf vtk-natives-linux-x64-8.2.jar /usr/lib
RUN jar xf simpleitk-natives-linux-x64-2.0.0.jar /usr/lib

RUN rm vtk-natives-linux-x64-8.2.jar
RUN rm simpleitk-natives-linux-x64-2.0.0.jar

RUN ldconfig -v /etc/ld.so.conf.d

#RUN source /etc/profile.d/maven.sh

#ENTRYPOINT [ "java" ]
#CMD [ "java", "-Dserver.port=$PORT $JAVA_OPTS -jar vtkdemo-0.0.1-SNAPSHOT.jar" ]
CMD java -jar -Dserver.port=$PORT -Dspring.datasource.url=jdbc:$DATABASE_URL $JAVA_OPTS vtkdemo-0.0.1-SNAPSHOT.jar
#CMD [ "/bin/sh" ]
#ENTRYPOINT [ "java" ]
#CMD [ "--version" ]

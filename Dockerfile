FROM alpine:latest

RUN apk --no-cache add bash openjdk11

WORKDIR /app

ADD . .
#ADD ./target ./target
#RUN chmod +x target/vtkdemo-0.0.1-SNAPSHOT.jar
RUN cp -v -f /app/repo/org/simpleitk/libSimpleITKJava/1.3.0.dev-234g68bd9/libSimpleITKJava-1.3.0.dev-234g68bd9.so /usr/lib
RUN ldconfig -v /etc/ld.so.conf.d

#ENTRYPOINT [ "java" ]
CMD [ "java", "-Dserver.port=$PORT $JAVA_OPTS -jar target/vtkdemo-0.0.1-SNAPSHOT.jar" ]
#ENTRYPOINT [ "java" ]
#CMD [ "--version" ]
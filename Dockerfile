FROM ubuntu:latest

WORKDIR /app

RUN apt-get -y update && apt-get -y install unzip wget
RUN wget https://raw.githubusercontent.com/chrishantha/install-java/master/install-java.sh && chmod +x ./install-java.sh

COPY docker/jdk-11.0.9_linux-x64_bin.tar.gz .
RUN mkdir -p /usr/lib/jvm
RUN yes | ./install-java.sh -f ./jdk-11.0.9_linux-x64_bin.tar.gz -p /usr/lib/jvm

RUN rm -Rf ./install-java.sh ./jdk-11.0.9_linux-x64_bin.tar.gz

ENV JAVA_HOME="/usr/lib/jvm/jdk-11.0.9"  \
    PATH="$JAVA_HOME/bin:$PATH"  \
    JAVA_VERSION=11.0.9 \
    JAVA_BUILD=12

RUN GLIBC_VERSION=2.28-r0 && \
    GCC_LIBS_VERSION=8.2.0-2 && \
    ZLIB_VERSION=1.2.11-2  && \
    JAVA_VERSION=11.0.9  && \
    JAVA_BUILD=12 && \
    apt-get install -y ca-certificates openssl libxrender1 libxtst6 libxi6 libgl1 libxt6 && \
    apt-get autoremove && \
    apt-get autoclean

RUN rm -rf /tmp/* /var/cache/apk/*

ADD repo/kitware/community/vtk-natives-linux-x64/8.2/vtk-natives-linux-x64-8.2.tar.gz ./libvtk
ADD repo/org/simpleitk/simpleitk-natives-linux-x64/2.0.0/simpleitk-natives-linux-x64-2.0.0.tar.gz ./libsimpleitk

RUN cp -R ./libvtk/* /lib
RUN cp -R ./libsimpleitk/* /lib

RUN rm -Rf  ./libvtk
RUN rm -Rf ./libsimpleitk

RUN ldconfig -v
ADD target/vtkdemo-0.0.1-SNAPSHOT.jar .

#ENTRYPOINT [ "java" ]
#CMD [ "java", "-Dserver.port=$PORT $JAVA_OPTS -jar vtkdemo-0.0.1-SNAPSHOT.jar" ]
#CMD java $JAVA_OPTS -jar vtkdemo-0.0.1-SNAPSHOT.jar
#CMD [ "/bin/bash" ]
ENTRYPOINT exec java $JAVA_OPS -jar vtkdemo-0.0.1-SNAPSHOT.jar
#CMD [ "--version" ]

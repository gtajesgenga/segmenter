FROM ubuntu:latest

ENV DEBIAN_FRONTEND noninteractive
ENV UBUNTU_VERSION 20.04
ENV LD_LIBRARY_PATH /usr/lib/jvm/java-11-openjdk-amd64/lib

WORKDIR /app

RUN apt-get -y update && apt-get -y install default-jre libxrender1 libxtst6 libxi6 libgl1 libxt6

RUN apt-get autoremove && \
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

ENTRYPOINT exec java $JAVA_OPS -jar vtkdemo-0.0.1-SNAPSHOT.jar


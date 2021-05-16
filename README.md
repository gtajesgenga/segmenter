# Segmentation

---
This repo contains the Segmentation service.

## Build & Deploy
### Manually
- This app is based on Spring Framework and uses Maven. It can be built with Maven command: `mvn clean package` from root folder.
- The generated JAR file located in _./target/_ folder requires that **SimpleITK v2.0.0** and **VTK v8.2** native libraries been installed on server.
  - For convenience, there are builds of these libraries for _Linux-x86_64_ architecture. Untar each file and install the `.so` files on server.
    - SimpleITK: [simpleitk-natives-linux-x64-2.0.0.tar.gz](repo/org/simpleitk/simpleitk-natives-linux-x64/2.0.0/simpleitk-natives-linux-x64-2.0.0.tar.gz)
    - VTK: [vtk-natives-linux-x64-8.2.tar.gz](repo/kitware/community/vtk-natives-linux-x64/8.2/vtk-natives-linux-x64-8.2.tar.gz)
  - To build and install SimpleITK manually: https://simpleitk.readthedocs.io/en/v2.0.0/building.html#building-simpleitk
  - To build and install VTK manually: https://vtk.org/Wiki/VTK/Configure_and_Build

### As a containerized Docker app
- Another way to deploy it is using `docker-compose` and the below instructions:
    1. Run `mvn clean package`
    2. After build the app you must execute [./run_with_docker-compose.sh](run_with_docker-compose.sh) script to run the app with _docker compose_.
    - This initializes the following containers:
        - Segmentation app itself. Running on host port [8080](http://localhost:8080)
        - Postgres DB. Running on host port [2345](http://localhost:2345). Check docker-compose.yaml for credentials.
    - The containers run as a **Tesis** project inside `tesis_tesis` docker network.

## Usage
Navigate to `http://<deployment_host>:8080/` to see the exposed endpoints in Swagger. Also, you can navigate to `http://<deployment_host:8080/ui/` to access to UI created to facilitate pipelines management.

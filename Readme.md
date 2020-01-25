# Doctors Delays - Server

## Prerequisites
 - [x] Windows
 
 - [x] Java 8

 - [x] python 3.8

 - [x] maven (+JAVA_HOME set to jdk)

## Installation
1. Clone the repository:

    ```sh
    $ git clone https://github.com/michaldeutch/DoctorsDelay.git
    ```
   
2. Run installation script:
    - Go to DoctorsDelay/Prerequisites and double click Installation-Script.bat
    
3. Configure project resources:
    
    - Open DoctorsDelay/config.xml
    - Edit as instructed in file
    
4. Run AI task:
    - Go to DoctorsDelay/Algorithms/scripts and double click ApiRunner.bat
   
5. Build and run tests:
    - from git bash:
        ```sh
        $ cd DoctorsDelay/
        ...
        $ mvn clean install
        ```
      
    - Another option is importing project to IntelliJ and running Server.CrowdSourcing.Main 
   
- [x] All modules should be built successfully

6. Run server (from git bash):
    ```sh
    $ cd Server/
    ...
    $ mvn clean package assembly:single
    ...
    $ cd target/
    ...
    $ java -jar Server-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```

7. Run Client from android studio emulator

8. Closing server with ctrl+c
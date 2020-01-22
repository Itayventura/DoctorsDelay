# Doctors Delays - Server

## Installation

1. Clone the repository and enter it:

    ```sh
    $ git clone https://github.com/michaldeutch/DoctorsDelay.git
    ...
    $ cd DoctorsDelay/
    ```
2. Asumming that python (3.8) is installed.
3. pip should be included in the installation of python and exists in the environment variables.
4. run (double-click or via cmd) the Installation-Script.bat in Prerequisites directory. 
	That will install the prerequisted related to Algorithms.
5. go to config.xml in this project and change the path to the model.pkl file exists in your 
	computer(absulte path).
6. To be able the Algorithms make estimations, the api python should be up. To do so, go to
	Algorithms\scripts and run ApiRunner.bat.
7. Now you are ready to make some predictions :) !
8. unzip MySQLServer
9. go to config.xml in this project and change the path to mysqld file in your 
     	computer(absolute path). at the moment, the path is relevant if you unzip MySQLServer in Desktop, and your user is "Itay"
10. if there are problems with connection to DB - run it again

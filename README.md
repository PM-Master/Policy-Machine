# Policy-Machine
NGAC ABAC Web Service Implementation

Policy Machine Webservices
Setup Guide

### Docker Deployment
*Note:* Using Neo4j only
To run the Policy Machine in a Docker container using Docker Compose, using the Docker Quickstart Terminal:
1. Download source code from GitHub
2. Run 'mvn clean package install' from the project root.  This will create the pm.war file in the 'target' folder.
3. In the docker terminal, navigate to the project root and run 'docker-compose up'.  This will deploy the pm.war file on a tomcat server, and start a neo4j instance.
4. Connect to the Neo4j database at DOCKER IP:7474. Create a user that will be used to connect the PM to the database.
4. Access the Policy Machine configuration page at DOCKER IP:8080/pm/config.jsp. Here you will be able to connect the PM to the Neo4j database.

---

The architecture is RESTful Web services with JAX-RS using MySQL and Neo4j. This version offers both the databases and you can pick the one of your choice. The code has been tested on Tomcat server using Neo4J (v 3.2.1) and MySQL (v5.5). 
Prerequisites – 
1.	Install database of your choice. 
2.	In case of MySQL database, run MySQLSetup.bat file and follow the instructions. This will create the schema and necessary metadata for the web services. 

A setup utility comes up upon successful server startup, which lets you pick your database and will connect to it for you. Alternatively, you can go to http://localhost:8080/pm/config.jsp.


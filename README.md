# Policy-Machine
NGAC ABAC Web Service Implementation

Policy Machine Webservices
Setup Guide

The architecture is RESTful Web services with JAX-RS using MySQL and Neo4j. This version offers both the databases and you can pick the one of your choice. The code has been tested on Tomcat server using Neo4J (v 3.2.1) and MySQL (v5.5). 
Prerequisites – 
1.	Install database of your choice. 
2.	In case of MySQL database, run MySQLSetup.bat file and follow the instructions. This will create the schema and necessary metadata for the web services. 

A setup utility comes up upon successful server startup, which lets you pick your database and will connect to it for you. Alternatively, you can go to http://localhost:8080/config.jsp.


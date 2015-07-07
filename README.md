# API Gateway migration tool
Tool to migrate API Gateway proxies from endpoints to listeners

How to build it:
mvn clean install

How to use it:
java -jar ./target/api-gateway-migration-tool-2.0-SNAPSHOT.jar -DagwSourceFolder={API Gateway 1.3.x Root Folder} -DagwTargetFolder={API Gateway 2.0 Root Folder}
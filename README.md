# API Gateway migration tool
Tool to migrate API Gateway proxies from endpoints to listeners

How to build it:
mvn clean compile assembly:single

How to use it:
java -jar -Dlog4j.configuration=file:/Users/federicoamdan/Projects/agw-migration-tool/src/log4j.properties -Dlog4j.debug=true ./target/agw-migration-tool-1.0-SNAPSHOT-jar-with-dependencies.jar -DagwRootFolder=/Users/federicoamdan/api-gateway-proxies-test
# springboot3-soap-client

springboot3-soap-client runs on port 9090 and consumes the webservice of project **springboot3-soap-server**, that runs on port 8080.
The app is a soap client as well as a rest server.

This application is a modern variant of a demo by Java Techie (https://www.youtube.com/watch?v=ceSqN3CWd14). The important changes are:

- Spring Boot 2 -> 3.x
- Other plugin with JAXB3 support: org.jvnet.jaxb/jaxb-maven-plugin
- Java 8 -> 17
- Test messages added
- Location generated sources: /src -> /target
- separate restcontroller class 

The jaxb-maven-plugin will compile loaneligibility.wsdl into fully annotated java classes. 
Note that this wsdl is not generated from a xsd schema (as was the case in project springboot3-soap-server), but stored as a source file in the resources folder.

# running the app

1. make sure the server side app runs
2. start the client app from the IDE or as follows:
```cmd
./mvnw spring-boot:run
```

# test messages

The http-tests directory contains two XML test messages.
These messages can be send to a running application with file tests.http.
In IntelliJ Ultimate, the tests can be started with the built-in HTTP client using the green arrow.

From the CLI, the test messages can be sent as requests using curl:
```curl
$ curl -v -X POST -H "Content-Type: application/json" --data @http-tests/approved.json http://localhost:9090/getLoanStatus
$ curl -v -X POST -H "Content-Type: application/json" --data @http-tests/denied.json http://localhost:9090/getLoanStatus
```
# springboot3-soap-client-mtls

springboot3-soap-client-mtls runs on port 9090 and consumes the webservice of project **springboot3-soap-server-mtls**, that runs on port 8080.
The app is a soap client as well as a rest server.

This application is based on the project **springboot3-soap-client**, but offers mutual (or two way) TLS as additional functionality.

Certificates, csr files and stores have been created as described in https://paras301.medium.com/implementing-ssl-tls-in-springboot-mutual-tls-mtls-part-2-b3eb64c6a78e.

# running the app

1. run the server side app **springboot3-soap-server-mtls**
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

# Timeit

How to start the Timeit application
---

1. Run `mvn clean install` to build your application.  This will build both backend (dropwizard) and front end (svelte)
1. Start application with `java -jar target/timeit-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`


Svelte App:
---
see README.md  located in src/main/webapp

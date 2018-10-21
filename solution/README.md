README
====
How to run the your solution...


## Prerequisites

- gulp (of course)
- java (of course)
- docker
- docker-compose
- application needs following free ports: 
    - 8080 - nginx web server
    - 8181 - expenses REST API
    - 13306 - mysql 


## Installation

> Note: tested on linux (Ubuntu)

**Step 1:** Build project (this will start unit tests automatically):

```bash
cd backend-coding-challenge/solution/expenses
chmod 755 gradlew
./gradlew clean build 
```

To also run integration tests use following
> Note: for integration tests requirement is to have Internet connection (without proxy and firewalls) and docker. 
> Integration tests are using dockerized mysql for testing.   

```bash
cd backend-coding-challenge/solution/expenses
chmod 755 gradlew
./gradlew clean build integrationTest
```

> To use code coverage check use task `jacocoTestReport` on the of gradle line. Results are in expenses/build/reports/jacoco/test/html/index.html.

**Step 2:** Run script for creating docker image: 

```bash
cd backend-coding-challenge/solution
chmod 755 build-docker.sh
./build-docker.sh
```

**Step 3:** Run application:

```bash
cd backend-coding-challenge/solution
docker-compose -f app.yml up
```


## Using application

Open browser URL: http://localhost:8080/ - it will open web application

 
IMPORTANT
====
To avoid unconcious bias, we aim to have your submission reviewed anonymously by one of our engineering team. Please try and avoid adding personal details to this document such as your name, or using pronouns that might indicate your gender.

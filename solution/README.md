README
====
How to run the your solution...

## Description

Solution is based on Spring Boot application. Backend is implementing REST API that is storing expense values in mysql 
database and contacting external service for getting exchange rates. 
Web application is deployed for this demo in Docker with nginx for serving web application static content 
and proxing REST API requests.
 
Spring Boot application uses mysql database. Configuration parameters are in application.properties
(backend-coding-challenge/solution/expenses/src/main/resources/application.properties - each property is described as 
part of JavaDoc).
For demo purposes the application DB is automatically generated and prepopulated with currency configuration and 
users. The application API is secured with Basic Auth as simple way to guard API requests.

Static web page is not secured but it triggers Basic auth during first request to REST API. 
It should be considered to introduce login and logout screens to better secure end-user visible content. 
But that demands greater changes in the web application.  

For demo purposes all (nginx, Spring Boot application and nginx server) is started preconfigurated 
in single Docker compose configuration (app.yml) described below.

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

Open browser URL: *http://localhost:8080/* - it will open web application

Limitations:
- Supported currencies 

Currently are supported following currencies: EUR, USD (defined in application.properties). 
It is possible to extend list by adding new one in property: 
`app.expense.default-currency.supported-currencies.short-name`, here is possible only to add currencies that are defined 
in the `currency` configuration table. In `currency` table are now only by default: GBP, EUR, USD, CHF.

- Predefined users 

Users are stored in table `expense_user`. Password is hashed with bcrypt 
(online generator *https://www.devglan.com/online-tools/bcrypt-hash-generator*).
Current users by default are user1, user2, user3 with password test.  

 
IMPORTANT
====
To avoid unconcious bias, we aim to have your submission reviewed anonymously by one of our engineering team. 
Please try and avoid adding personal details to this document such as your name, or using pronouns that might indicate your gender.

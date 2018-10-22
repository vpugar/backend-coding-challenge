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

Static web page is not secured but it triggers Basic auth during first request to REST API. This is done just to
check current user, as expenses are related to each user. Logout is not possible without totally 
closing the browser window (as there is incompatibility between basic auth behaviour between browsers during logout flow
 - firefox for example on removing session and sending HTTP status 401 does not display basic auth prompt),
It should be considered to introduce login and logout screens to better secure end-user visible content.  

For demo purposes all (nginx, Spring Boot application and nginx server) is started preconfigurated 
in single Docker compose configuration (app.yml) described below.

## Prerequisites

- gulp
- java (1.8)
- gradle (version 4.8)
- docker (tested with 17.04.0-ce)
- docker-compose (tested with 1.9.0)
- application needs following free ports: 
    - 8080 - nginx web server
    - 8181 - expenses REST API
    - 13306 - mysql 


## Installation

> Note: tested on linux (Ubuntu)

**Step 1:** Build project (this will start unit tests automatically):

```bash
cd backend-coding-challenge/solution/expenses
gradle clean build 
```

To also run integration tests use following
> Note: for integration tests requirement is to have Internet connection (without proxy and firewalls) and docker. 
> Integration tests are using dockerized mysql for testing.   

```bash
cd backend-coding-challenge/solution/expenses
gradle clean build integrationTest
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
Current users by default are *test1*, *test2*, *test3* with password *test*.  

- Remote service The Free Currency Converter API has limitation to provide rates maximally one year in past

- In application.properties is defined that maximal expense date can be 100 days in past 
 
IMPORTANT
====
To avoid unconcious bias, we aim to have your submission reviewed anonymously by one of our engineering team. 
Please try and avoid adding personal details to this document such as your name, or using pronouns that might indicate your gender.

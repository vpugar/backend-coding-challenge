#!/usr/bin/env bash

OLD_WD=$(pwd)

error_exit()
{
    echo "Returning to $OLD_WD"
    cd $OLD_WD
}

# Create nginx image with web application
cd ..
npm install
gulp clean
gulp build
cp -r static solution/nginx/
cd solution
docker build -t expenses/expenses-nginx -f ./Dockerfile_nginx .

# Create java image with expenses application
docker build -t expenses/expenses-app -f ./Dockerfile_expenses .

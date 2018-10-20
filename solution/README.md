README
====
How to run the your solution...

docker build -t expenses-vp -f ./Dockerfile_expenses .
cd ..
npm install
gulp clean
gulp build
cp -r static solution/nginx/
cd solution
docker build -t nginx-vp -f ./Dockerfile_nginx .

docker-compose -f app.yml up

IMPORTANT
====
To avoid unconcious bias, we aim to have your submission reviewed anonymously by one of our engineering team. Please try and avoid adding personal details to this document such as your name, or using pronouns that might indicate your gender.

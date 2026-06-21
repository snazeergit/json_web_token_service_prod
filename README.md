Swagger/Postman samples to test the application:

--------------------------------------
Register:
--------------------------------------
Request:
http://localhost:8080/api/auth/register

curl -X 'POST' \
  'http://localhost:8080/api/auth/register' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "username": "armaan",
  "email": "armaan@gmail.com",
  "password": "Password@123"
}'

Response:
{
  "success": true,
  "message": "User registered successfully"
}


--------------------------------------
Login:
--------------------------------------
Request: 
http://localhost:8080/api/auth/login

curl -X 'POST' \
  'http://localhost:8080/api/auth/login' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "username": "armaan",
  "password": "Password@123"
}'

Response:
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhcm1hYW4iLCJpYXQiOjE3ODIwNDg0NzIsImV4cCI6MTc4MjA0OTM3Mn0.cmuCbgCVLCZRl4IiWFzLzRZPxIySKMfEJg4kdx_FGYQcdwOZhDmJIUx9DuyumxXy",
  "refreshToken": "9f2d9749-e7ef-4bc6-adfb-7bee2b667198",
  "tokenType": "Bearer"
}


--------------------------------------
Refresh:
--------------------------------------
Request:
http://localhost:8080/api/auth/refresh

curl -X 'POST' \
  'http://localhost:8080/api/auth/refresh' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "refreshToken": "9f2d9749-e7ef-4bc6-adfb-7bee2b667198"
}'

Response:
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhcm1hYW4iLCJpYXQiOjE3ODIwNDg4MzAsImV4cCI6MTc4MjA0OTczMH0.4d0uOsksTQXKxLpcuWGDgBfHmWEjL3I1CX-kPkiqpvpm5wo_wjgMEbFkGImG3CeA",
  "refreshToken": "9f2d9749-e7ef-4bc6-adfb-7bee2b667198",
  "tokenType": "Bearer"
}


--------------------------------------
Logout:
--------------------------------------
Request:
http://localhost:8080/api/auth/logout

curl -X 'POST' \
  'http://localhost:8080/api/auth/logout' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhcm1hYW4iLCJpYXQiOjE3ODIwNDg4MzAsImV4cCI6MTc4MjA0OTczMH0.4d0uOsksTQXKxLpcuWGDgBfHmWEjL3I1CX-kPkiqpvpm5wo_wjgMEbFkGImG3CeA' \
  -d ''

Response:
{
  "success": true,
  "message": "Logged out successfully"
}
--------------------------------------

# JobScrapper-WebApp
This the webapp version of jobscrapper with backend built on java

#What is the job scrapper tool?

Our job scrapper tool uses AI and automates the process of scouring
through various job sites for relevant jobs and applying to them manually.
This time taking and cumbersome so we automate the manual effort.
And as added bonus add some AI magic to further refine our resume for each job posting.

#INTENDED TECH TECH-STACK#
Frontend- ReactJS, TailWind CSS
Backend- Java (25LTS) + SpringBoot
DB- MySQL, Mongo
Architecture- Microservices

CICD:- GCP

Enabling google authentication:-
1. Create the project on google cloud console
2. Create a Auth consent page, API & Services -> Consent Page
3. Create the Credentials, API & Services -> Credentials
4. Generate the Client ID
5. Put the ID in applications.yaml and fetch from there using @Value int controller
6. Use the client Id to verify token sent by front end

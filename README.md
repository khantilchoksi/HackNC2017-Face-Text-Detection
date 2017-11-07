# HackNC2017-Face-Text-Detection  

## Microsoft Azure  

## Google Cloud Vision API
## Inspiration
It is irritating to see things and not know them as well as have data in non digitized format. Hence we decided to create
something which could solve this problem once and for all. And what better platform to make it accessible to all than android. 
## What it does
The app allows you to take 2 different paths:
1) Analyze an image to get facial characteristics such as Age, Gender, Hair Color etc.
2) Analyze an image for textual recognition and get it in PDF format or get to know about significant words in the images. 
## How I built it
Android is used as a platform for all API I/O.
1) Analyzing an image to extract facial characteristics was developed using Microsoft Azure Face API.
->First get the subscription key and endpoint to access Microsoft Azure Face API.
->Created android front end for providing input image to Azure Face API Calls and displaying the facial characteristics of each individual face detected to user.
->Post call to the API with image as parameter gives us the facial characteristics in JSON Format.
2) Analyzing an image to assign a label to an image along with their probabilities and get the text from an image into PDF format that is digitalizing the data. All of these is done using the Google Cloud Vision API.
->Generate API Key on Google Cloud.
->Created android front end for providing input image to Google Cloud Vision API Calls and displaying the labels/tags with their respective probabilities.
->Post call to the API with image as parameter gives us the facial characteristics in JSON Format. 
## Challenges I ran into
-> Integrating Microsoft Azure Face API with android.
-> Getting the response from the API Call in the desired JSON Format.
-> Conversion from .txt to .pdf in android.
-> Displaying the probability of labels in Android. 
## Accomplishments that I'm proud of
-> Using Microsoft Azure Face API to solve basic recognition problem.
-> Integrating Microsoft Azure API calls with android application. 
## What I learned
-> JSON
-> Microsoft Azure Face API
-> Android
->Google Cloud Vision API 
## What's next for Face-Text-Recognition
-> Using facial Recognition for security
Outline : While a user sets up an account scan his image and store it in Local database. Now when a user enters an incorrect password capture an image of the current user using front camera compare it with the stored image and mail the image to the owner of device.
-> Allow to search any possible word in the text. 
## Built With
android
java
microsoft
azure
face-api
google-cloud
vision-api

# AR ReDesign

## Notes
- This is a school project for Metropolia university of applied sciences
- A decoration app that uses a phone's AR to preview how furniture would look before you buy it
  
## Features
- This project provides many examples on how to code android application with kotlin.
- We feature components listed below:

## Installation
- **Note** this project **requires two files to work** that you need to get on your own.
	- Create your own firebase project [here](https://firebase.google.com/)
	- Create **google authentication api** key and **google-services.json** with this [tutorial](https://firebase.google.com/docs/android/setup)
	- Add **google-services.json** file to `project_root/app/` directory
	- And create new file called **secrets.xml** to following directory `project_root/app/src/main/res/values`
	- Add following code with **your own API key**
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="request_id_token">YOUR-API-KEY</string>
</resources>
```
 
## Components of this project
 
 ### Basic components
 - **Activity** 
	- Main activities that are responsible for main functions. Login and main room view.
 - **Broadcast receiver**
	- Project is using**Firebase** online database that supports live data usage.
 - **content provider**
	- Project uses **Firebase storage** for storing all the images and 3D models of different furniture
 
 ### Fragments
 - This project uses fragments for most of its parts. For example for dialogs, parts of the screen, and hole-screen views.
 
 ### Persistence 
 - **File**
	- All the downloaded 3D models are stored in the app's file directory and are used later instead of downloading models again
 
 ### Web service
 - **Google authentication**
	- When a user logs in. This app uses Google sign in for secured authentication
	
 ### AR
 - A key feature of this app. Ability to place and delete multiple models in AR view.
 
 ### Camera
 - This app uses a camera for taking a photo of the room where a user is decorating. This helps the user to remember which rooms are which
 
 ### Live data
 - All the users data is updated and listened in real time.

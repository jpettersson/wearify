# Wearify

A weekend _**hack**_ that allows you to start playing Spotify playlists from your Wear watch. While already useful, this project is not a final product. Use it as a implementation reference or play around with it for fun. 

## Why?

This app adds a small amount of usefulness to a common use case: Start a Spotify playlist when I can't reach my phone. For instance, when I'm standing on the subway. 

## How does it work? 

The standard Android Studio gradle project contains two applications: 

* A playlist browser running on the wear watch.
* A companion mobile app responsible for authenticating with Spotify and downloading Playlists.
* Playback inside the Spotify app is "remote controlled" using an Intent.

The application is not distributed in the Play store, so you need to clone and build this project if you want to try it out. 

## How to use it

0. Install the Wear app on your Wear device. 
1. Install the Mobile app on your mobile device.
2. First, start the companion app on your device.
3. Use the "Auth!" button to get redirected to the Spotify Accounts website: Login and approve the application.
4. When authenticated, tap the "Get playlists!" button to download your most recent 50 playlists.
5. When playlists are downloaded, tap "Sync!" to send the playlists to the Wear device over the DataLayer.
6. On your Wear device, Say "start spotify" or launch the "Spotify" application through the menu.
7. The playlist activity is opened. Tap a playlist you'd like to play!
8. A Spotify intent is started on the mobile device and the Spotify application will begin playing your playlist.

## Fun technologies used

* Spotify Android SDK
* Spotify Web API
* Android Wear DataLayer & Messages

## Known issues & limitations

* Only the most recent 50 playlists are downloaded (no pagination yet)
* Playback of the special "Starred" playlist does not yet work
* Sometimes when the phone is locked you have to try starting a playlist 2-3 times before it actually starts playing

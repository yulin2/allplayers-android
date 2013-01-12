# AllPlayers.com Android App

[![Build Status](https://travis-ci.org/AllPlayers/allplayers-android.png?branch=master)](https://travis-ci.org/AllPlayers/allplayers-android)

How to build and run the application in Eclipse:

1.  Open Eclipse.
2.  Make sure the Android SDK is set (http://developer.android.com/sdk/eclipse-adt.html#installing).
3.  Navigate to File > Import.
4.  Select "Projects from Git" from the Git folder.
5.  Click "Next".
6.  If the project has not been cloned/pulled, yet:
```
    a.  Click "Clone".
    b.  Type (copy/paste) the project repository location into the URI box (located on GitHub.com project home page).
    c.	Click "Next".
    d.	Make sure "master" is checked. Then click "Next".
    e.	Select a local destination for the project.
    f.	Click "Finish".

    If the project has already been pulled:
    
    a.	Click "Add".
    b.	Browse to the parent directory of the project.
    c.	Click "Search".
    d.	Select the allplayers-android project and click "Ok".
```
7.  Click "Next".
8.  Select "Import Existing Projects" and then click "Next".
9.  Click "Finish".
10.  Navigate to File > Clean and click "Ok".
11.  Make sure the project is set as the main project and click the green run arrow.
12.  Select "Run as Android Application".
13.  Create an AVD with the target as Google APIs - API level 10.
14.  At this point, you may have to re-run the application for it to use the new AVD.
15.  The AVD should be running and the application is installed.

LudoUnchained
=============

Google User 
------------
*	ludounchained.fhm@gmail.com
*	wifhm123


Quick Links
------------
*	[Google APIs](https://code.google.com/apis/console)
*	[Google App Engine](https://appengine.google.com/)

Tutorials
---------
*	[Google App Engine Tutorial for Java](http://www.vogella.com/articles/GoogleAppEngineJava/article.html)
*	[Google IO Session Overview](http://bradabrams.com/2011/05/google-io-session-overview-android-app-engine-a-developers-dream-combination/)

Quick Setup
-----------
#### 1. Android ADT Bundle ####
[ADT Bundle for Windows](http://developer.android.com/sdk/index.html)
Entpacken unter _C:\ADT\_

#### 2. Eclipse Plugins installieren ####
* _C:\ADT\eclipse\eclipse.exe_ starten
* _Help_ -> _Install new Software..._
* Unter _Work with:_ "http://dl.google.com/eclipse/plugin/4.2" eintragen
* Select All und installieren

#### 3. Android SDK einrichten ####
_C:\ADT\SDK Manager.exe_ starten und folgende Pakete installieren:
* Android 4.2.2 (API 17) komplett (incl. Google APIs)
* Extras -> Android Support Library
* Extras -> Google Cloud Messaging for Android Library
Notfalls einfach komplett alles installieren.

#### 4. GitHub einrichten ####
* [GitHub für Windows](http://windows.github.com/) installieren und einrichten
* Das _enforcers/LudoUnchained_ Reporsitory rechtsklicken -> "clone to..." -> _C:\ADT\

#### 5. Workspace einrichten ####
Eine Verknüpfung zu _"C:\ADT\eclipse\eclipse.exe" -data C:\ADT\LudoUnchained_ erstellen

#### Testlauf ####
In dem Workspace befinden sich zwei Java Projekte.
*	Rechtsklick auf _LudoUnchained-AppEngine_ -> Run As -> Web Application
*	Rechtsklick auf _LudoUnchained_ -> Run As -> Android Application
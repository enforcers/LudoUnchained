LudoUnchained
=============

Quick Links
------------
*	[Google APIs](https://code.google.com/apis/console)
*	[Google App Engine](https://appengine.google.com/)

Tutorials
---------
*	[Google App Engine Tutorial for Java](http://www.vogella.com/articles/GoogleAppEngineJava/article.html)
*	[Google IO Session Overview](http://bradabrams.com/2011/05/google-io-session-overview-android-app-engine-a-developers-dream-combination/)

Einrichten von Android SDK
-------------------------
#### Android SDK downloaded und installieren ####
[Android SDK Tools for Windows](http://dl.google.com/android/installer_r22-windows.exe)

#### Android SDK konfigurieren ####
_SDK Manager.exe_ ausführen und folgende Pakete installieren:
* Android 4.2.2 (API 17) komplett (incl. Google APIs)
* Extras -> Android Support Library
* Extras -> Google Cloud Messaging for Android Library
Notfalls einfach komplett alles installieren.

#### Android AVD konfigurieren ####
_AVD Manager.exe_ ausführen und eine neues Virtual Device erstellen. Wichtig ist, dass als Target "Google API" läuft.

Einrichten von Eclipse
----------------------
#### Eclipse downloaden und installieren ####
[Eclipse IDE for Java EE Developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/junosr2)

#### Eclipse Plugins installieren ####
Folgende Eclipse Plugins installieren ( _Help -> Eclipse Marketplace_ )
*	Android Development Tools for Eclipse
*	Google Plugin for Eclipse

#### Workspace einbinden ####
In Eclipse als Workspace das _workspace_ Verzeichnis von GitHub angeben. Workspace ändern geht in Eclipse unter _File -> Switch Workspace..._

#### Testlauf ####
In dem Workspace befinden sich zwei Java Projekte.
*	Rechtsklick auf _LudoUnchained-AppEngine_ -> Run As -> Web Application
*	Rechtsklick auf _LudoUnchained_ -> Run As -> Android Application
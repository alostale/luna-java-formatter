# Eclipse Luna Java Formatter plugin

Ports Eclipse Luna (4.4) Java formatter as a plugin to be used in newer Eclipse versions.

Starting from Eclipse Mars (4.5) Java formatter was rewritten not being possible to format using old style, this can be problematic for projects using old formatting.

Original plugin can be found [here](http://eclipse-n-mati.blogspot.com.es/2015/06/eclipse-mars-how-to-switch-back-to.html).

## Installation
1. Drop `OldJavaFormater_xxx.jar` in your `eclipse/dropins` directory
2. Start Eclipse
3. Open `Preferences > Java > Code Style > Formatter` and select `Old Luna Java Formatter` as `Formatter Implementation`

## Build
1. Import as Eclipse project
2. Build jar: `File > Export... > Deployable plug-ins fragments`

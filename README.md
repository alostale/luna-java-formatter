# Eclipse Luna and Neon Java Formatter plugin

Ports Eclipse Luna (4.4) or Eclipse Neon (4.6) Java formatter as a plugin to be used in newer Eclipse versions.

Starting from Eclipse Mars (4.5) Java formatter was rewritten not being possible to format using old style, this can be problematic for projects using old formatting.

Starting from Eclipse Oxygen (4.7) Java formatter was rewritten again and not being possible to format using old style.

Original plugin can be found [here](http://eclipse-n-mati.blogspot.com.es/2015/06/eclipse-mars-how-to-switch-back-to.html).

## Installation
1. Drop `OldJavaFormatter_xxx.jar` in your `eclipse/dropins` directory
2. Start Eclipse
3. Open `Preferences > Java > Code Style > Formatter` and select `Old Luna Java Formatter` as `Formatter Implementation`

## Build
1. Import as Eclipse project
2. Build jar: `File > Export... > Deployable plug-ins fragments`

## Version Notes

* The Neon formatter only works with Eclipse 2018-12 and newer
* For Eclipse Mars (4.5) to 2018-09: Please use version 1.1.5 of this plugin.
* For Eclipse 2018-12: Please use version 1.2.0 of this plugin.
* For Eclipse 2019-03: Please use version 1.3.1 of this plugin.
* For Eclipse 2019-06: Please use version 1.4.1 of this plugin.
    * Please note that this may not work well with the new arrow based switch statements (if at all).
* For Eclipse 2019-12: Please use version 1.5.0 of this plugin.

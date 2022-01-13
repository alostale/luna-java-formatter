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

## Version Support Matrix

| Version | Supported Eclipse Version(s) | Formatters | Notes |
|:-------:|:-----------------------------|:----------:|:-----:|
| 1.1.5   | Mars (4.5) - 2018-09         | Luna       | *NA*  |
| 1.2.0   | 2018-12                      | Luna, Neon | Neon formatter support added.  |
| 1.3.1   | 2019-03                      | Luna, Neon | *NA*  |
| 1.4.1   | 2019-06, 2019-09             | Luna, Neon | Arrow based switch statement support may not work well (if at all). |
| 1.5.0   | 2019-12, 2020-03             | Luna, Neon | *NA*  |
| 1.6.0   | 2020-06                      | Luna, Neon | No code changes, but rather a rebuild with the 2020-06 toolchain. |
| 1.7.0   | 2020-09                      | Luna, Neon | No code changes, but rather a rebuild with the 2020-09 toolchain. |
| 1.8.0   | 2020-12, 2021-03             | Luna, Neon | No code changes, but rather a rebuild with the 2020-12 toolchain. |
| 1.9.0   | 2021-06                      | Luna, Neon | Changed library dependency versions, rebuild of plugin with 2021-06 toolchain. |
| 1.10.1  | 2021-09                      | Luna, Neon | Changed library dependency versions, addressed compiler warnings, addressed deprecated API warnings, addressed breaks caused by upstream API changes. |
| 1.11.0  | 2021-12                      | Luna, Neon | No code changes, but rather a rebuild with the 2021-12 toolchain. |

//
//   Copyright 2010 Tobias Schulte
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// 
class I18nGettextGriffonPlugin {
    // the plugin version
    String version = '0.3'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '0.9.5 > *'
    // the other plugins this plugin depends on
    Map dependsOn = [:]
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = 'Apache License, Version 2.0'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = 'http://griffon.codehaus.org/I18nGettext+Plugin'
    // URL where source can be found
    String source = 'https://github.com/tschulte/griffon-i18n-gettext'

    List authors = [
        [
            name: 'Tobias Schulte',
            email: 'tobias.schulte@gliderpilot.de'
        ]
    ]
    String title = 'I18n gettext plugin for griffonI18n gettext plugin for griffonI18n gettext plugin for griffon'
    // accepts Markdown syntax. See http://daringfireball.net/projects/markdown/ for details
    String description = '''\
Based on the grails plugin i18n-gettext by Rainer Brang, Backend-Server GmbH & Co. KG.
    
This plugin adds i18n support to your app, in 'gnu gettext'-style.
1 First, you need to wrap special tags or service calls around all strings you want to translate.
2 Then you call "griffon i18n-gettext" to extract all translatable strings from your sources.
3 Now you translate all strings from step 2 which you will find in .po files in your i18n directory.
4 Call "griffon i18n-gettext makemo" to compile your translated .po files into resource classes.
5 repeat 1-4 each time you added some new strings to your application. Existing translations will be merged in. 

-During runtime: The methods, you wrapped around the strings, will pick the correct translation according to the
current locale, and return the translated string. You may also force a locale for a specific call.
 
What you need: The developer needs these command line tools for the development machine: xgettext, msgmerge and msgfmt
The translator may like: PoEdit or alike to translate texts.

You will love the dead simple plural form handling and FormatMessage-like String handling. Additionally, you can 
forget about inventing lookup keys for your .properties files, because for gnu gettext, the original string is the key.
Plus: No more problems with special chars like german umlauts. gettext can handle that.

You can exclude directories from being scanned for translatable strings in your Config.groovy file.

Beware: 
a) Gnu gettext can not handle groovy's "here-doc" strings, so don't try to wrap tr() or alike around them. Also some
xgettext binaries seem to dislike here-doc strings so much, they don't parse files containing here-doc files correctly.
b) Your original strings should be english, because Gnu gettext can't cope with non-ascii characters as original strings.

Many thanks to:
Rainer Brang for his grails plugin

Enables localization of messages using GNU Gettext. This plugin is a port of the grails i18n-gettext plugin
Installation

To install just issue the following command

griffon install-plugin i18n-gettext

Usage
-----

This plugin adds some methods to view instances:

    tr(Map params, String text, Object... objects)
    trc(String comment, String text)
    trn(String singularText, String pluralText, Number n, Object... objects)
    trnc(String comment, String singularText, String pluralText, Number n, Object... objects)

You just wrap your texts in one of the tr-methods. They work only with double quotes, not with single quotes, and the named parameters must be the last parameters.

    tr("Content goes here")
    tr("Hello {0}", name) // uses MessageFormat
    tr("Hello \$name", name: 'Paul') // uses SimpleTemplateEngine -- make sure to escape $
    tr("Hello \${name}", name: 'Paul') // uses SimpleTemplateEngine -- make sure to escape $
    trc("Short for Order", "O", rb: 'MyResourceBundle', locale: Locale.German) // rb and locale might be given, but must be the last parameters

    // but
    tr(name: 'Paul', "Hello \$name") // will not work, because the named parameters must be the last parameters
    tr('Hello') // will not work, because you must use "
    tr("Hello $name", name: 'Paul') // will not work, because GStrings don't work
    tr("Hello \${name} {0}", 'Maier', name: 'Paul') // will not work, because you are not allowed to mix named and indexed parameters (MessageFormat will complain because of ${name})

Then execute

    griffon i18n-gettext

to generate the keys.pot and merge into existing po files, followed by

    griffon i18n-gettext init de

to create a German language file. Edit the griffon-app/i18n/de.po file. Then execute

    griffon i18n-gettext makemo

to generate a i18ngettext.jar in your lib folder.

When you added new texts to your source files or have changed something, just call griffon i18n-gettext again to regenerate the keys.pot file and merge into your existing po files. After translation, execute grifofn i18n-gettext makemo again.

Configuration
-----
### Dynamic method injection
The tr-methods are available on all view scripts by default. You can enable them on other mvc members by adding for example

    root.'I18nGettextGriffonAddon'.controller='*:methods'

to your Builder.groovy

### Other configuration
You can configure the behaviour of the plugin by defining some attributes in your BuildConfig.groovy

The default charset is assumed to be UTF-8. You can configure the charset of your source files by setting

    i18n.inputFileCharset = 'UTF-8'

By default all java and groovy files are parsed by the plugin. If you specify a list of directory names 

    i18n.excludedDirs =['dir_to_exclude']

By default the bundle name is 'Messages'. This can be changed with

    i18n.bundleName = 'Foo'

The default xgettext params are --add-comments and can be altered by

    i18n.xgettextParams = '--add-comments'

Parameters for msginit can be set with

    i18n.msginitParams = ''

Patameters for msgmerge can be set with

    i18n.msgmergeParams = ''

    
'''
}

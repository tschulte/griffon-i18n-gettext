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

	
//
// This script is executed by Griffon when the plugin is uninstalled from project.
// Use this script if you intend to do any additional clean-up on uninstall, but
// beware of messing up SVN directories!
//

// check to see if we already have a I18nGettextGriffonAddon
boolean addonIsSet1
builderConfig.each() { prefix, v ->
    v.each { builder, views ->
        addonIsSet1 = addonIsSet1 || 'I18nGettextGriffonAddon' == builder
    }
}

if (addonIsSet1) {
    println 'Removing I18nGettextGriffonAddon from Builder.groovy'
    builderConfigFile.text = builderConfigFile.text - "root.'I18nGettextGriffonAddon'.addon=true\n"
}

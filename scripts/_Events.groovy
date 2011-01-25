

def eventClosure1 = binding.variables.containsKey('eventSetClasspath') ? eventSetClasspath : {cl->}
eventSetClasspath = { cl ->
    eventClosure1(cl)
    if(compilingPlugin('i18n-gettext')) return
    griffonSettings.dependencyManager.flatDirResolver name: 'griffon-i18n-gettext-plugin', dirs: "${i18nGettextPluginDir}/addon"
    griffonSettings.dependencyManager.addPluginDependency('i18n-gettext', [
        conf: 'compile',
        name: 'griffon-i18n-gettext-addon',
        group: 'org.codehaus.griffon.plugins',
        version: i18nGettextPluginVersion
    ])
}

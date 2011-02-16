

def eventClosure1 = binding.variables.containsKey('eventSetClasspath') ? eventSetClasspath : {cl->}
eventSetClasspath = { cl ->
    eventClosure1(cl)
    if(compilingPlugin('sub-module')) return
    griffonSettings.dependencyManager.flatDirResolver name: 'griffon-sub-module-plugin', dirs: "${subModulePluginDir}/addon"
    griffonSettings.dependencyManager.addPluginDependency('sub-module', [
        conf: 'compile',
        name: 'griffon-sub-module-addon',
        group: 'org.codehaus.griffon.plugins',
        version: subModulePluginVersion
    ])
}

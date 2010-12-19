application {
    title = 'I18nGettextTest'
    startupGroups = ['Main']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "Main"
    'Main' {
        model = 'i18ngettexttest.MainModel'
        controller = 'i18ngettexttest.MainController'
        view = 'i18ngettexttest.MainView'
    }


}

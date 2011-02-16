
import griffon.core.GriffonApplication
import griffon.test.*

import java.text.*

class I18nGettextGriffonAddonTests extends GriffonUnitTestCase {
    GriffonApplication app
    I18nGettextGriffonAddon addon

    public void setUp() {
        addon = new I18nGettextGriffonAddon()
        addon.app = app
        setLocale(Locale.GERMAN)
    }
    
    public void testGetI18n() {
        setLocale(Locale.US)
        assertEquals("Salut", addon.getI18n(locale: Locale.FRENCH, rb: "I18nGettext").tr("Hello"))
        assertEquals("Hello", addon.getI18n(locale: Locale.UK, rb: "I18nGettext").tr("Hello"))
        assertEquals("Hallo", addon.getI18n(locale: Locale.GERMAN, rb: "I18nGettext").tr("Hello"))
        assertEquals("Hallo Tobias", addon.getI18n(locale: Locale.GERMAN, rb: "I18nGettext").tr("Hello {0}", "Tobias"))
    }


    public void testTrGerman() {
        assertEquals("Hallo", addon.tr("Hello", rb: "I18nGettext"))
    }

    public void testTrFrench() {
        setLocale(Locale.FRENCH)
        assertEquals("Salut", addon.tr("Hello", rb: "I18nGettext"))
    }

    public void testTrEnglish() {
        setLocale(Locale.US)
        assertEquals("Hello", addon.tr("Hello", rb: "I18nGettext"))
    }
    
    public void testTrWithParams() {
        assertEquals("Hallo Tobias", addon.tr("Hello {0}", "Tobias", rb: "I18nGettext"))
    }

    public void testTrWithNamedParams() {
        // Comment
        assertEquals("Hallo Tobias", addon.tr("Hello \$name", name: "Tobias", rb: "I18nGettext"))
        assertEquals("Salut Tobias", addon.tr("Hello \$name", name: "Tobias", rb: "I18nGettext", locale: Locale.FRENCH))
        
        assertEquals("Hallo Tobias", addon.tr("Hello \${name}", name: 'Tobias', rb: 'I18nGettext'))
        def format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN)
        def today = new Date()
        def formatted = format.format(today)
        assertEquals("Heute ist der ${format.format(today)}", 
                addon.tr("Today is \$date", date: formatted, rb: 'I18nGettext'))
    }
    
    private void setLocale(Locale locale) {
        app.locale = locale
        Locale.setDefault(locale)
    }
 

}

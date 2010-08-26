
import griffon.core.GriffonApplication
import griffon.test.*

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
        assertEquals("Salut", addon.getI18n(Locale.FRENCH).tr("Hello"))
        assertEquals("Hello", addon.getI18n(Locale.UK).tr("Hello"))
        assertEquals("Hallo", addon.getI18n(Locale.GERMAN).tr("Hello"))
        assertEquals("Hallo Tobias", addon.getI18n(Locale.GERMAN).tr("Hello {0}", "Tobias"))
    }


    public void testTrGerman() {
        assertEquals("Hallo", addon.tr("Hello"))
    }

    public void testTrFrench() {
        setLocale(Locale.FRENCH)
        assertEquals("Salut", addon.tr("Hello"))
    }

    public void testTrEnglish() {
        setLocale(Locale.US)
        assertEquals("Hello", addon.tr("Hello"))
    }
    
    public void testTrWithParams() {
        assertEquals("Hallo Tobias", addon.tr("Hello {0}", "Tobias"))
    }
    
    private void setLocale(Locale locale) {
        app.locale = locale
        Locale.setDefault(locale)
    }
 

}

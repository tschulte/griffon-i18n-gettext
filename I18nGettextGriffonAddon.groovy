import java.util.Locale
import org.xnap.commons.i18n.*


class I18nGettextGriffonAddon {
    def app
    
    def addonInit(app) {
            this.app = app
    }

    private tr = { String text, Object... params ->
        if (params) 
           return getI18n().tr(text, (Object[])params)
       return getI18n().tr(text)
    }

    private trc = { String context, String text ->
        return getI18n().trc(context, text)
    }

    private trn = { String text, long n, Object... params ->
        if (params) 
            return getI18n().trn(text, n, (Object[])params)
        return getI18n().trn(text, n)
    }
   
    private trnc = { String context, String text, long n, Object... params ->
        if (params) 
            return getI18n().trnc(context, text, n, (Object[])params)
        return getI18n().trnc(context, text, n)
    }

    private getI18n() {
        getI18n(app?.locale?:Locale.getDefault())
    }

    private getI18n = { Locale locale ->
        I18nFactory.getI18n(I18nGettextGriffonAddon.class, 'i18ngettext.Messages', locale)
    }
    
    def methods = [
       getI18n: getI18n,
       tr: tr,
       trc: trc,
       trn: trn,
       trnc: trnc
    ]

}

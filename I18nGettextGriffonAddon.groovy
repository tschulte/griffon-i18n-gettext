import java.util.Locale
import org.xnap.commons.i18n.*
import groovy.text.SimpleTemplateEngine


class I18nGettextGriffonAddon {
    def app
    
    def addonInit(app) {
            this.app = app
    }

    private tr = { Map params = [:], String text, Object... objects ->
        if (objects) 
           return extractParameters(params, getI18n(params).tr(text, (Object[])objects))
       return extractParameters(params, getI18n(params).tr(text))
    }

    private trc = { Map params = [:], String context, String text ->
        return extractParameters(params, getI18n(params).trc(context, text))
    }

    private trn = { Map params = [:], String singular, String plural, Number n, Object... objects ->
        if (objects) 
            return extractParameters(params, getI18n().trn(singular, plural, n, (Object[])objects))
        return extractParameters(params, getI18n(params).trn(singular, plural, n))
    }
   
    private trnc = { Map params = [:], String context, String singular, String plural, Number n, Object... objects ->
        if (objects) 
            return extractParameters(params, getI18n().trnc(context, singular, plural, n, (Object[])objects))
        return extractParameters(params, getI18n(params).trnc(context, singular, plural, n))
    }

    private getI18n = { Map params = [:] ->
        Locale locale = params.locale ?: app?.locale ?: Locale.getDefault()
        String rb = "i18ngettext.${params.rb ?: app?.config?.i18n?.bundleName ?: 'Messages'}"
        return I18nFactory.getI18n(I18nGettextGriffonAddon.class, rb, locale)
    }
    
    private String extractParameters(Map params, String translated) {
        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(translated).make(params)
        return template.toString()
    }
    
    def methods = [
       tr: tr,
       trc: trc,
       trn: trn,
       trnc: trnc
    ]

}

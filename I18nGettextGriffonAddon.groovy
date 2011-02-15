import java.util.Locale
import org.xnap.commons.i18n.*
import groovy.text.SimpleTemplateEngine


class I18nGettextGriffonAddon {
    def app
    
    def addonInit(app) {
            this.app = app
    }

    private tr = { Map params = [:], text, Object... objects ->
        if (params) 
           return extractParameters(params, getI18n(params).tr(text, (Object[])objects))
       return extractParameters(params, getI18n(params).tr(text))
    }

    private trc = { Map params = [:], context, text ->
        return extractParameters(params, getI18n(params).trc(context, text))
    }

    private trn = { Map params = [:], text, long n, Object... objects ->
        if (objects) 
            return extractParameters(params, getI18n().trn(text, n, (Object[])objects))
        return extractParameters(params, getI18n(params).trn(text, n))
    }
   
    private trnc = { Map params = [:], context, text, long n, Object... objects ->
        if (objects) 
            return extractParameters(params, getI18n().trnc(context, text, n, (Object[])objects))
        return extractParameters(params, getI18n(params).trnc(context, text, n))
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

package uz.ravshanbaxranov.doctordirect.other

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

class LocaleHelper(private val context: Context) : ContextWrapper(context) {


    fun selectLang(language: String): Boolean {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return true
    }


}
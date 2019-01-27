package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.controller.RootController;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * holds the resource bundle for current active locale
 */
public class Message {

    private static ResourceBundle bundle;

    // load resource bundle based on language prop or default to german
    static {
        if (PropsCtx.getLocale() != null) bundle = ResourceBundle.getBundle("bundles.message", PropsCtx.getLocale());
        else bundle =  ResourceBundle.getBundle("bundles.message", Locale.GERMAN);
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    @Deprecated
    public static String localize(String key) {
        return bundle.getString(key);
    }

    /**
     * toggle language in order: english --> german (--> property language) --> english
     * this only gets reflected by UI if FXML is reloaded, see: {@link RootController#reloadActionMenu()}
     */
    public static void toggleLanguage() {
        Locale locale = Locale.ENGLISH;

        if (bundle.getLocale().equals(Locale.ENGLISH))
            locale = Locale.GERMAN;
        else if (bundle.getLocale().equals(Locale.GERMAN) && PropsCtx.getLocale() != null)
            locale = PropsCtx.getLocale();

        bundle = ResourceBundle.getBundle("bundles.message", locale);
    }
}

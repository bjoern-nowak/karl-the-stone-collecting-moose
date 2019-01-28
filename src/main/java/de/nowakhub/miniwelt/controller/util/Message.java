package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.controller.RootController;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * holds the resource bundle for current active locale
 */
public class Message {

    private static ResourceBundle bundle;

    static {
        // load bundle based on props
        loadBundle(PropsCtx.getLocale());
    }

    /**
     * load resource bundle or default to english
     */
    private static void loadBundle(Locale locale) {
        try {
            bundle = ResourceBundle.getBundle("bundles.message", locale);
        } catch (Exception ex) {
            bundle = ResourceBundle.getBundle("bundles.message", Locale.ENGLISH);
        }
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
     * this only gets reflected by UI if FXML is reloaded.
     * @see RootController#reloadActionMenu()
     */
    public static void toggleLanguage() {
        Locale locale = Locale.ENGLISH;

        if (bundle.getLocale().equals(Locale.ENGLISH))
            locale = Locale.GERMAN;
        else if (bundle.getLocale().equals(Locale.GERMAN) && PropsCtx.getLocale() != null)
            locale = PropsCtx.getLocale();

        loadBundle(locale);
    }
}

package de.nowakhub.miniwelt.controller.util;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

/**
 * holds properties of corresponding file; loads is once; creates default if necessary
 */
public class PropsCtx {

    private final static Properties props = new Properties();

    // load property file if available once; otherwise create default property file
    static {
        try {
            Path path = Paths.get(System.getProperty("user.home")).resolve(".miniworld/miniworld.properties").toAbsolutePath();
            if (!path.toFile().exists()) {
                if (!path.getParent().toFile().exists()) path.getParent().toFile().mkdirs();
                Path defaultPath = Paths.get("default_miniworld.properties").toAbsolutePath();
                Files.copy(defaultPath, path);
            }

            props.load(new FileInputStream(path.toAbsolutePath().toFile()));
        } catch (Exception ex) {
            Alerts.showException(ex);
        }
    }

    /**
     * @return true only if property 'debug' is present
     */
    public static boolean isDebug() {
        return props.getProperty("debug") != null;
    }

    /**
     * @return true only if properties 'host' and 'port' are present
     */
    public static boolean hasServer() {
        return getHost() != null && getPortProp() != null;
    }

    /**
     * @return true only if property 'role' is present and equals 'tutor'
     */
    public static boolean isTutor() {
        return getRole() != null && getRole().equals("tutor");
    }

    /**
     * @return string of property 'host'
     */
    public static String getHost() {
        return props.getProperty("host");
    }

    /**
     * @return string of property 'port'
     */
    public static String getPortProp() {
        return props.getProperty("port");
    }

    /**
     * @return converted integer of property 'port'
     */
    public static Integer getPort() {
        return Integer.valueOf(getPortProp());
    }

    /**
     * @return converted integer of property 'role'
     */
    public static String getRole() {
        return props.getProperty("role");
    }


    /**
     * @return string of property 'language'
     */
    public static String getLanguage() {
        return props.getProperty("language");
    }

    /**
     * @return converted local of property 'language'
     */
    public static Locale getLocale() {
        return getLanguage() != null ? new Locale(getLanguage()) : null;
    }


    public static Properties get() {
        return props;
    }

    public static Properties getProps() {
        return props;
    }
}

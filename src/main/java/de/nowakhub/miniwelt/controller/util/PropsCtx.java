package de.nowakhub.miniwelt.controller.util;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropsCtx {
    private final static Properties props = new Properties();


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

    public static boolean hasServer() {
        return props.getProperty("host") != null && props.getProperty("port") != null;
    }
    public static boolean isTutor() {
        return props.getProperty("role") != null && props.getProperty("role").equals("tutor");
    }
    public static String getHost() {
        return props.getProperty("host");
    }
    public static Integer getPort() {
        return Integer.valueOf(props.getProperty("port"));
    }

    public static Properties get() {
        return props;
    }

    public static Properties getProps() {
        return props;
    }
}

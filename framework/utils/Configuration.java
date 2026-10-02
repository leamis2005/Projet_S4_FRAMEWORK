package framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final Properties properties = new Properties();
    private static boolean loaded = false;

    private Configuration() {
    }

    private static synchronized void load() {
        if (!loaded) {
            try (InputStream is = Configuration.class.getResourceAsStream("/framework/utils/config.properties")) {
                if (is != null) {
                    properties.load(is);
                } else {
                    throw new IllegalStateException("config.properties not found in classpath");
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config.properties", e);
            }
            loaded = true;
        }
    }

    public static String getControllerPackage() {
        load();
        String pkg = properties.getProperty("controller.package");
        if (pkg == null || pkg.trim().isEmpty()) {
            throw new IllegalStateException("controller.package is not defined in config.properties");
        }
        return pkg.trim();
    }

    public static String getViewPrefix() {
        load();
        String prefix = properties.getProperty("view.prefix");
        return prefix != null ? prefix.trim() : "/WEB-INF/views/";
    }

    public static String getViewSuffix() {
        load();
        String suffix = properties.getProperty("view.suffix");
        return suffix != null ? suffix.trim() : ".jsp";
    }
}
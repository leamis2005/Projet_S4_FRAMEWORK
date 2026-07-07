package framework.listener;

import framework.annotations.Controller;
import framework.annotations.URLMapping;
import framework.routing.Mapping;
import framework.routing.UrlMethod;
import framework.utils.PackageScanner;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameworkInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String packageName = context.getInitParameter("controller_package_name");
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Paramètre 'controller_package_name' manquant.");
        }

        try {
            Map<UrlMethod, Mapping> routes = new HashMap<>();
            List<Class<?>> controllers = PackageScanner.getAnnotatedClassesInPackage(packageName, Controller.class);

            for (Class<?> clazz : controllers) {
                String className = clazz.getName();
                for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(URLMapping.class)) {
                        URLMapping ann = method.getAnnotation(URLMapping.class);
                        String url = ann.value();
                        String httpMethod = ann.method();

                        UrlMethod key = new UrlMethod(url, httpMethod);
                        if (routes.containsKey(key)) {
                            throw new IllegalArgumentException("Route dupliquée : " + url + " " + httpMethod);
                        }
                        routes.put(key, new Mapping(className, method.getName()));
                    }
                }
            }

            context.setAttribute("routesWithMethod", routes);
            context.setAttribute("view_prefix", "/WEB-INF/views/");
            context.setAttribute("view_suffix", ".jsp");
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'initialisation du framework", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}

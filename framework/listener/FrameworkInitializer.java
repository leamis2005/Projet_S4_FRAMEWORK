package framework.listener;

import framework.routing.Mapping;
import framework.routing.UrlMethod;
import framework.utils.Configuration;
import framework.utils.PackageScanner;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.Map;

public class FrameworkInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            String packageName = Configuration.getControllerPackage();
            Map<UrlMethod, Mapping> routes = PackageScanner.scanControllerRoutes(packageName);

            context.setAttribute("routesWithMethod", routes);
            context.setAttribute("view_prefix", Configuration.getViewPrefix());
            context.setAttribute("view_suffix", Configuration.getViewSuffix());
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'initialisation du framework", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}

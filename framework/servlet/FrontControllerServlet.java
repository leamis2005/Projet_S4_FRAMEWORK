package framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import framework.routing.Mapping;
import framework.routing.UrlMethod;
import framework.utils.Configuration;
import framework.utils.ModelView;

public class FrontControllerServlet extends HttpServlet {

    private Map<UrlMethod, Mapping> routes;
    private String viewPrefix;
    private String viewSuffix;

    @Override
    public void init() throws ServletException {
        loadParams();
    }

    private void loadParams() {
        Map<UrlMethod, Mapping> ctxRoutes = (Map<UrlMethod, Mapping>) getServletContext()
                .getAttribute("routesWithMethod");
        if (ctxRoutes == null) {
            throw new IllegalArgumentException("La map 'routesWithMethod' est introuvable dans le contexte.");
        }
        this.routes = ctxRoutes;

        this.viewPrefix = Configuration.getViewPrefix();
        this.viewSuffix = Configuration.getViewSuffix();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        String url = requestURI.substring(contextPath.length());
        String httpMethod = req.getMethod();

        res.setContentType("text/html; charset=UTF-8");

        try {
            UrlMethod key = new UrlMethod(url, httpMethod);

            if (routes.containsKey(key)) {
                Mapping mapping = routes.get(key);
                Class<?> controllerClass = mapping.getControllerClass();
                Method method = mapping.getMethod();
                Object controller = controllerClass.getDeclaredConstructor().newInstance();
                method.setAccessible(true);
                Object result = method.invoke(controller);

                if (result instanceof ModelView) {
                    ModelView mv = (ModelView) result;
                    Map<String, Object> model = mv.getModel();
                    for (Map.Entry<String, Object> entry : model.entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }
                    String view = viewPrefix + mv.getView() + viewSuffix;
                    RequestDispatcher rd = req.getRequestDispatcher(view);
                    rd.forward(req, res);
                } else if (result instanceof String) {
                    String html = (String) result;
                    try (PrintWriter out = res.getWriter()) {
                        out.println("<!DOCTYPE html><html><body>");
                        out.println(html);
                        out.println("</body></html>");
                    }
                } else {
                    try (PrintWriter out = res.getWriter()) {
                        out.println("<!DOCTYPE html><html><body>");
                        out.println("<pre>" + url + " -> " + controllerClass.getName() + " -> " + method.getName() + "() : resultat non reconnu</pre>");
                        out.println("</body></html>");
                    }
                }
            } else {
                try (PrintWriter out = res.getWriter()) {
                    out.println("<!DOCTYPE html><html><body>");
                    out.println("<h1>URL non trouvee : " + url + " [" + httpMethod + "]</h1>");
                    out.println("<h2>Routes disponibles :</h2><ul>");
                    for (Map.Entry<UrlMethod, Mapping> entry : routes.entrySet()) {
                        Mapping m = entry.getValue();
                        out.println("<li>" + entry.getKey().getUrl() + " (" + entry.getKey().getMethod() + ") -> " + m.getControllerClass().getName() + " -> " + m.getMethod().getName() + "()</li>");
                    }
                    out.println("</ul>");
                    out.println("</body></html>");
                }
            }
        } catch (Exception e) {
            throw new ServletException("Erreur interne: " + e.getMessage(), e);
        }
    }
}

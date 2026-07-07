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

        this.viewPrefix = getServletContext().getInitParameter("view_prefix");
        if (this.viewPrefix == null) {
            this.viewPrefix = "/WEB-INF/views/";
        }
        this.viewSuffix = getServletContext().getInitParameter("view_suffix");
        if (this.viewSuffix == null) {
            this.viewSuffix = ".jsp";
        }
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
<<<<<<< HEAD
            String httpMethod = req.getMethod();
=======
>>>>>>> 4ef5112
            UrlMethod key = new UrlMethod(url, httpMethod);

            if (routes.containsKey(key)) {
                Mapping mapping = routes.get(key);
                Class<?> controllerClass = Class.forName(mapping.getClassName());
                Object controller = controllerClass.getDeclaredConstructor().newInstance();
                Method method = controllerClass.getDeclaredMethod(mapping.getMethod());
                method.setAccessible(true);
                Object result = method.invoke(controller);

                if (result instanceof ModelView mv) {
                    Map<String, Object> model = mv.getModel();
                    for (Map.Entry<String, Object> entry : model.entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }
                    String view = viewPrefix + mv.getView() + viewSuffix;
                    RequestDispatcher rd = req.getRequestDispatcher(view);
                    rd.forward(req, res);
                } else if (result instanceof String html) {
                    try (PrintWriter out = res.getWriter()) {
                        out.println("<!DOCTYPE html><html><body>");
                        out.println(html);
                        out.println("</body></html>");
                    }
                } else {
                    try (PrintWriter out = res.getWriter()) {
                        out.println("<!DOCTYPE html><html><body>");
                        out.println("<pre>" + url + " -> " + mapping.getClassName() + " -> " + mapping.getMethod() + "() : resultat non reconnu</pre>");
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
                        out.println("<li>" + entry.getKey().getUrl() + " (" + entry.getKey().getMethod() + ") -> " + m.getClassName() + " -> " + m.getMethod() + "()</li>");
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

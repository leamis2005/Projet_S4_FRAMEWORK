package framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import framework.annotations.Controller;
import framework.utils.PackageScanner;

public class FrontControllerServlet extends HttpServlet {

    private String controllersPackage;
    private List<String> listControllers;

    @Override
    public void init() throws ServletException {
        try {
            loadParams();
            scanControllers();
        } catch (Exception e) {
            throw new ServletException("Échec: ", e);
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

    // Pour charger tous les parametres de la FrontControllerServlet
    private void loadParams() {
        // package des @Controller
        final String PACKAGE_NAME_PARAM = "controller_package_name";
        this.controllersPackage = this.getInitParameter(PACKAGE_NAME_PARAM);
    }

    private void scanControllers()
            throws ServletException, IOException, ClassNotFoundException {
        if (this.controllersPackage == null || this.controllersPackage.trim().isEmpty()) {
            throw new ServletException("Le paramètre d'initialisation 'controller_package_name' est manquant.");
        }

        this.listControllers = PackageScanner.getAnnotatedClassesNamesInPackage(
                controllersPackage,
                Controller.class);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        StringBuilder mainURL = new StringBuilder(req.getRequestURL());
        String queryParameters = req.getQueryString();
        if (queryParameters != null) {
            mainURL.append('?').append(queryParameters);
        }

        res.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = res.getWriter()) {
            out.println("<!DOCTYPE html><html><head>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background: #f4f6f8; color: #222; margin: 40px; }");
            out.println(".container { background: white; padding: 20px; border: 1px solid #ddd; max-width: 700px; }");
            out.println("h3 { color: #2563eb; margin-bottom: 6px; }");
            out.println("ul { padding-left: 22px; }");
            out.println("li { margin: 6px 0; }");
            out.println("</style>");
            out.println("</head><body><div class=\"container\">");
            out.println("<h3>URL:</h3><p>" + mainURL + "</p>");
            out.println("<h3>Les controllers :</h3><p>" + controllersPackage + "</p><ul>");

            for (String controllerName : listControllers) {
                out.println("<li>" + controllerName + "</li>");
            }
            out.println("</ul></div></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne du framework.");
        }
    }

}

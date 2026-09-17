package pt.brunoribeiro.examples.ejb;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CalculatorServlet extends HttpServlet {

    @EJB
    private CalculatorService calculatorService;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {

            double a = Double.parseDouble(request.getParameter("a"));
            double b = Double.parseDouble(request.getParameter("b"));

            String operation = request.getParameter("operation");

            double result;

            switch (operation) {

                case "add":
                    result = calculatorService.add(a, b);
                    break;

                case "subtract":
                    result = calculatorService.subtract(a, b);
                    break;

                case "multiply":
                    result = calculatorService.multiply(a, b);
                    break;

                case "divide":
                    result = calculatorService.divide(a, b);
                    break;

                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("Unknown operation.");
                    return;
            }

            out.println("Result: " + result);

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("Parameters 'a' and 'b' must be numbers.");

        } catch (IllegalArgumentException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(e.getMessage());
        }
    }
}

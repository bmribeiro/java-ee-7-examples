package pt.brunoribeiro.examples.ejb;

import javax.ejb.Stateless;

@Stateless
public class CalculatorServiceBean implements CalculatorService {

    @Override
    public double add(double a, double b) {
        return a + b;
    }

    @Override
    public double subtract(double a, double b) {
        return a - b;
    }

    @Override
    public double multiply(double a, double b) {
        return a * b;
    }

    @Override
    public double divide(double a, double b) {

        if (b == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }

        return a / b;
    }
}
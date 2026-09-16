package pt.brunoribeiro.examples.soap.fault;

import javax.xml.ws.WebFault;

@WebFault(name = "CustomerNotFoundFault")
public class CustomerNotFoundException extends Exception {

    private final CustomerFault faultInfo;

    public CustomerNotFoundException(
            String message,
            CustomerFault faultInfo) {

        super(message);
        this.faultInfo = faultInfo;
    }

    public CustomerNotFoundException(
            String message,
            CustomerFault faultInfo,
            Throwable cause) {

        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public CustomerFault getFaultInfo() {
        return faultInfo;
    }
}
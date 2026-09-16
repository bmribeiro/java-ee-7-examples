package pt.brunoribeiro.examples.soap.endpoint;

import pt.brunoribeiro.examples.soap.fault.CustomerFault;
import pt.brunoribeiro.examples.soap.fault.CustomerNotFoundException;
import pt.brunoribeiro.examples.soap.model.Customer;
import pt.brunoribeiro.examples.soap.service.CustomerService;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(
        serviceName = "CustomerService",
        portName = "CustomerServicePort",
        targetNamespace =
                "http://soap.examples.brunoribeiro.pt/customer"
)
@SOAPBinding(
        style = SOAPBinding.Style.DOCUMENT,
        use = SOAPBinding.Use.LITERAL
)
public class CustomerSoapService {

    @EJB
    private CustomerService customerService;

    @WebMethod(operationName = "GetCustomer")
    public Customer getCustomer(
            @WebParam(name = "id") Long id)
            throws CustomerNotFoundException {

        Customer customer = customerService.findById(id);

        if (customer == null) {

            CustomerFault fault = new CustomerFault(
                    "CUSTOMER_NOT_FOUND",
                    "Customer with id " + id + " was not found"
            );

            throw new CustomerNotFoundException(
                    "Customer not found",
                    fault
            );
        }

        return customer;
    }

    @WebMethod(operationName = "CreateCustomer")
    public Customer createCustomer(
            @WebParam(name = "name") String name,
            @WebParam(name = "email") String email) {

        return customerService.create(name, email);
    }
}
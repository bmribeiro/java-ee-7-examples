# Java EE 7 — EJB Example

Simple Java EE 7 example demonstrating a **Stateless Session Bean**, a **Local Business Interface**, EJB dependency injection, and a Servlet running on GlassFish 4.

## Technologies

* Java 8
* Java EE 7
* EJB 3.2
* Servlet 3.1
* Maven
* GlassFish 4.1.2

## Architecture

```text
HTTP Request
     |
     v
CalculatorServlet
     |
     | @EJB
     v
CalculatorService
     |
     | implemented by
     v
CalculatorServiceBean
     |
     v
Business Logic
```

## Project Structure

```text
ejb/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── pt/brunoribeiro/examples/ejb/
        │       ├── CalculatorService.java
        │       ├── CalculatorServiceBean.java
        │       └── CalculatorServlet.java
        │
        └── webapp/
            └── WEB-INF/
                └── web.xml
```

## Components

### `CalculatorService`

Local EJB business interface:

```java
@Local
public interface CalculatorService {

    double add(double a, double b);

    double subtract(double a, double b);

    double multiply(double a, double b);

    double divide(double a, double b);
}
```

### `CalculatorServiceBean`

Stateless Session Bean containing the business logic:

```java
@Stateless
public class CalculatorServiceBean
        implements CalculatorService {
```

The Bean lifecycle is managed by the Java EE EJB container.

### `CalculatorServlet`

Web entry point that injects the EJB:

```java
@EJB
private CalculatorService calculatorService;
```

The Servlet does not instantiate the service manually. The EJB is resolved and injected by GlassFish.

## Servlet Mapping

The Servlet mapping is explicitly defined in:

```text
src/main/webapp/WEB-INF/web.xml
```

```xml
<servlet>
    <servlet-name>CalculatorServlet</servlet-name>
    <servlet-class>
        pt.brunoribeiro.examples.ejb.CalculatorServlet
    </servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>CalculatorServlet</servlet-name>
    <url-pattern>/calculator</url-pattern>
</servlet-mapping>
```

`web.xml` is not mandatory in Servlet 3.1 because the mapping could also be declared using `@WebServlet`.

It is used here to demonstrate an explicit Java EE deployment descriptor, a configuration style commonly found in legacy enterprise applications.

## Build

From the `ejb` directory:

```bash
mvn clean package
```

The generated application is:

```text
target/ejb.war
```

## Deploy

If an older version is already deployed:

```bash
C:\glassfish4\glassfish\bin\asadmin.bat undeploy ejb
```

Deploy the WAR:

```bash
C:\glassfish4\glassfish\bin\asadmin.bat deploy --contextroot ejb target\ejb.war
```

## Verify Deployment

List deployed applications:

```bash
C:\glassfish4\glassfish\bin\asadmin.bat list-applications
```

Expected result:

```text
ejb    <ejb, web>
```

List detected components:

```bash
C:\glassfish4\glassfish\bin\asadmin.bat list-sub-components ejb
```

Expected components include:

```text
CalculatorServiceBean  <StatelessSessionBean>
CalculatorServlet      <Servlet>
```

## Test

### Add

```text
http://localhost:8080/ejb/calculator?a=10&b=5&operation=add
```

Response:

```text
Result: 15.0
```

### Subtract

```text
http://localhost:8080/ejb/calculator?a=10&b=5&operation=subtract
```

Response:

```text
Result: 5.0
```

### Multiply

```text
http://localhost:8080/ejb/calculator?a=10&b=5&operation=multiply
```

Response:

```text
Result: 50.0
```

### Divide

```text
http://localhost:8080/ejb/calculator?a=10&b=2&operation=divide
```

Response:

```text
Result: 5.0
```

## Key Concepts

* `@Local` defines the local EJB business interface.
* `@Stateless` defines a Stateless Session Bean.
* `@EJB` injects an EJB managed by the application server.
* `web.xml` defines the Web application's Servlet mapping.
* The Servlet handles HTTP concerns while the EJB contains business logic.

## Request Flow

```text
GET /ejb/calculator?a=10&b=5&operation=add
                    |
                    v
               web.xml
                    |
                    v
          CalculatorServlet
                    |
                  @EJB
                    |
                    v
          CalculatorService
                    |
                    v
       CalculatorServiceBean
                    |
                    v
               add(10, 5)
                    |
                    v
             Result: 15.0
```

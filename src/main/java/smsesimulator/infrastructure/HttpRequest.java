package smsesimulator.infrastructure;

public class HttpRequest {

    private String uriBase;
    private String resource;

    public HttpRequest(String uriBase, String resource) {
        super();
        this.uriBase = uriBase;
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public String getUriBase() {
        return uriBase;
    }

}

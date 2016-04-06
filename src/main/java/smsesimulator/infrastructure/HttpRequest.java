package smsesimulator.infrastructure;

public class HttpRequest {

    private String uriBase;
    private String resource;
    private String fullUri;

    public HttpRequest(String uriBase, String resource, String fullUri) {
        super();
        this.uriBase = uriBase;
        this.resource = resource;
        this.fullUri = fullUri;
    }

    public String getResource() {
        return resource;
    }

    public String getUriBase() {
        return uriBase;
    }

    public String getFullUri() {
        return fullUri;
    }

    public void setFullUri(String fullUri) {
        this.fullUri = fullUri;
    }

}

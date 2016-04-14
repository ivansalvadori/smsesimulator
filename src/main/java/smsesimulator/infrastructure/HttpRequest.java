package smsesimulator.infrastructure;

public class HttpRequest {

    private String uriBase;
    private String fullUri;

    public HttpRequest(String uriBase, String fullUri) {
        super();
        this.uriBase = uriBase;
        this.fullUri = fullUri;
    }

    public String getUriBase() {
        return uriBase;
    }

    public String getFullUri() {
        return fullUri;
    }

}

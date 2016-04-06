package smsesimulator;

import java.util.ArrayList;
import java.util.List;

public class GatewayDescription {

    private String uriBase;
    private List<SemanticResource> semanticResources = new ArrayList<>();

    public GatewayDescription(String uriBase, List<SemanticDescription> semanticDescriptions) {
        this.uriBase = uriBase;
        for (SemanticDescription semanticDescription : semanticDescriptions) {
            semanticResources.addAll(semanticDescription.getSemanticResources());
        }
    }
    
    public String getUriBase() {
        return uriBase;
    }
    
    public List<SemanticResource> getSemanticResources() {
        return semanticResources;
    }

}

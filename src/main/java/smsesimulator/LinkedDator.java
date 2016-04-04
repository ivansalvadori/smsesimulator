package smsesimulator;

import java.util.Collection;
import java.util.List;

public class LinkedDator {

    public void analizeSemanticDescritptions(List<SemanticDescription> semanticDescriptions){
        
        for (SemanticDescription semanticDescription : semanticDescriptions) {
            List<SemanticResource> semanticResources = semanticDescription.getSemanticResources();
            for (SemanticResource semanticResource : semanticResources) {
                System.out.println("Entidade: " + semanticResource.getEntity());
                Collection<String> resourcesSemantics = semanticResource.getProperties().values();
                for (String prop : resourcesSemantics) {
                    System.out.println(prop);
                }
            }
        }
        
    }
    
}

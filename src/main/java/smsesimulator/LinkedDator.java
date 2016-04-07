package smsesimulator;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import smsesimulator.infrastructure.UriTemplate;

public class LinkedDator {

    public void analizeSemanticDescritptions(List<SemanticDescription> semanticDescriptions) {

        // Map<String, Sem>

        OntModel ontology = this.loadOntology("src/test/resources/Ontology1.owl");

        for (SemanticDescription semanticDescription : semanticDescriptions) {
            List<SemanticResource> semanticResources = semanticDescription.getSemanticResources();
            for (SemanticResource semanticResource : semanticResources) {
                String entity = semanticResource.getEntity();
            }
        }
    }

    public Map<String, String> createLinks(List<SemanticDescription> semanticDescriptions, Map<String, String> resourceRepresentation, String uriBase){
        
        Map<String, String> mapOfUris = new HashMap<>();
        
        for (SemanticDescription semanticDescription : semanticDescriptions) {
            List<SemanticResource> semanticResources = semanticDescription.getSemanticResources();
            for (SemanticResource semanticResource : semanticResources) {
                List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
                for (UriTemplate uriTemplate : uriTemplates) {
                    String entity = semanticResource.getEntity();
                    String uri = uriTemplate.getUri();
                    mapOfUris.put(entity, uri);
                }
            }
        }        
        
        OntModel ontology = this.loadOntology("src/test/resources/Ontology1.owl");
        List<ObjectProperty> objectProperties = ontology.listObjectProperties().toList();
        String entity = resourceRepresentation.get("entity");
        for (ObjectProperty objectProperty : objectProperties) {
            OntResource range = objectProperty.getRange();
            OntResource domain = objectProperty.getDomain();
            if(domain.toString().equals(entity)){
                System.out.println("adicionar o link " + objectProperty.getURI() + " em " + entity + " para " + range.toString());
                resourceRepresentation.put( objectProperty.getURI(),  String.format("%s/%s", uriBase, mapOfUris.get(range.toString()) ) );
            }
        }
        
        return resourceRepresentation;
        
        
    }

    private OntModel loadOntology(String ontologyFilePath) {
        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(ontologyFilePath);
            try {
                ontoModel.read(in, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JenaException je) {
            System.err.println("ERROR" + je.getMessage());
            je.printStackTrace();
        }

        return ontoModel;

    }
}

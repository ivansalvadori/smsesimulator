package smsesimulator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import smsesimulator.infrastructure.UriTemplate;

public class LinkedDator {

    public Map<String, String> createLinks(List<SemanticDescription> semanticDescriptions, Map<String, String> resourceRepresentation, String uriBase) {

        Map<String, String> mapEnityToUri = new HashMap<>();
        Map<String, SemanticResource> mapEntityToResource = new HashMap<>();

        for (SemanticDescription semanticDescription : semanticDescriptions) {
            List<SemanticResource> semanticResources = semanticDescription.getSemanticResources();
            for (SemanticResource semanticResource : semanticResources) {
                List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
                for (UriTemplate uriTemplate : uriTemplates) {
                    String entity = semanticResource.getEntity();
                    String uri = uriTemplate.getUri();
                    mapEnityToUri.put(entity, uri);
                    mapEntityToResource.put(entity, semanticResource);
                }
            }
        }

        OntModel ontology = this.loadOntology("src/test/resources/Ontology2.owl");
        List<ObjectProperty> objectProperties = ontology.listObjectProperties().toList();

        String entity = resourceRepresentation.get("entity");
        for (ObjectProperty objectProperty : objectProperties) {
            OntResource range = objectProperty.getRange();
            OntResource domain = objectProperty.getDomain();
            SemanticResource domainSemanticResource = mapEntityToResource.get(entity);
            if (domain.toString().equals(entity)) {
                Set<String> domainProperties = domainSemanticResource.getProperties().keySet();
                if (domainProperties.contains(objectProperty.getURI())) {
                    resourceRepresentation.put(objectProperty.getURI(), String.format("%s/%s", uriBase, mapEnityToUri.get(range.toString())));
                } else if (resourceCanResolveLink(domainSemanticResource, mapEntityToResource.get(domain.toString()).getUriTemplates())) {
                    System.out.println("adicionar o link " + objectProperty.getURI() + " em " + entity + " para " + range.toString());
                    resourceRepresentation.put(objectProperty.getURI(), String.format("%s/%s", uriBase, mapEnityToUri.get(range.toString())));
                }
            }
        }

        return resourceRepresentation;
    }

    private boolean resourceCanResolveLink(SemanticResource domainSemanticResource, List<UriTemplate> rangeUriTemplates) {
        Collection<String> rangeUriParameters = new ArrayList<>();
        for (UriTemplate uriTemplate : rangeUriTemplates) {
            rangeUriParameters.addAll(uriTemplate.getParameters().values());
        }
        Collection<String> domainResourceProperties = domainSemanticResource.getProperties().values();

        for (String param : rangeUriParameters) {
            if (domainResourceProperties.contains(param)) {
                return true;
            }
        }

        return false;
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

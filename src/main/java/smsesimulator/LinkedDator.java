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
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import smsesimulator.infrastructure.UriTemplate;

public class LinkedDator {

    private String pathToOntologyFile;

    public LinkedDator(String pathToOntologyFile) {
        this.pathToOntologyFile = pathToOntologyFile;
    }

    public Map<String, Object> createLinks(List<SemanticDescription> semanticDescriptions, Map<String, Object> resourceRepresentation, String uriBase) {

        String representationType = (String) resourceRepresentation.get("@type");
        ScenarioType scenarioType = verifyScenarioType(representationType, semanticDescriptions);

        if (scenarioType.equals(ScenarioType.Scenario1)) {
            System.out.println(representationType + ": scenario1");

        } else if (scenarioType.equals(ScenarioType.Scenario2)) {
            System.out.println(representationType + ": scenario2");

            Map<String, SemanticResource> mapEntityToResource = new HashMap<>();
            for (SemanticDescription semanticDescription : semanticDescriptions) {
                List<SemanticResource> semanticResources = semanticDescription.getSemanticResources();
                for (SemanticResource semanticResource : semanticResources) {
                    mapEntityToResource.put(semanticResource.getEntity(), semanticResource);
                }
            }

            OntModel ontology = this.loadOntology(pathToOntologyFile);
            List<ObjectProperty> objectProperties = ontology.listObjectProperties().toList();
            List<String> objectPropertiesUri = new ArrayList<>();
            for (ObjectProperty objectProperty : objectProperties) {
                objectPropertiesUri.add(objectProperty.getURI());
            }

            Set<String> resourceProperties = resourceRepresentation.keySet();
            for (String resourceProperty : resourceProperties) {
                if (objectPropertiesUri.contains(resourceProperty)) {
                    Map<String, Object> objectPropertyContent = (Map<String, Object>) resourceRepresentation.get(resourceProperty);
                    Object range = objectPropertyContent.get("@type");
                    SemanticResource semanticResource = mapEntityToResource.get(range);
                    List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
                    for (UriTemplate uriTemplate : uriTemplates) {
                        String uri = uriTemplate.getUri();
                        Set<String> paramKeys = uriTemplate.getParameters().keySet();
                        for (String paramKey : paramKeys) {
                            Object objectPropetyContentUriParamValue = objectPropertyContent.get(uriTemplate.getParameters().get(paramKey));
                            if(objectPropetyContentUriParamValue == null){
                                objectPropertyContent.remove("@id");
                                break;
                            }
                            uri = uri.replace("{" + paramKey + "}", objectPropetyContentUriParamValue.toString());
                            objectPropertyContent.put("@id", uriBase + "/" + uri);                              
                        }
                        
                        // if the object property has one link, stop the
                        // process.
                        // That's enough
                        if (objectPropertyContent.get("@id") != null) {
                            break;
                        }
                    }
                }
            }

        } else {
            System.out.println(representationType + ": scenario3");
        }       

        return resourceRepresentation;
    }

    private ScenarioType verifyScenarioType(String representationType, List<SemanticDescription> semanticDescriptions) {
        Map<String, SemanticResource> mapEntityToResource = new HashMap<>();

        for (SemanticDescription semanticDescription : semanticDescriptions) {
            List<SemanticResource> semanticResources = semanticDescription.getSemanticResources();
            for (SemanticResource semanticResource : semanticResources) {
                String entity = semanticResource.getEntity();
                if (mapEntityToResource.get(entity) != null) {
                    return ScenarioType.Scenario3;
                }
                mapEntityToResource.put(entity, semanticResource);
            }
        }

        SemanticResource semanticResource = mapEntityToResource.get(representationType);
        OntModel ontology = this.loadOntology(pathToOntologyFile);
        List<ObjectProperty> objectProperties = ontology.listObjectProperties().toList();
        List<String> objectPropertiesUri = new ArrayList<>();

        for (ObjectProperty objectProperty : objectProperties) {
            objectPropertiesUri.add(objectProperty.getURI());
        }

        List<SemanticResource> resourceObjectProperties = semanticResource.getObjectProperties();
        if (resourceObjectProperties != null) {
            for (SemanticResource resourceObjectProperty : resourceObjectProperties) {
                if (objectPropertiesUri.contains(resourceObjectProperty.getRel())) {
                    return ScenarioType.Scenario2;
                }
            }
        }

        return ScenarioType.Scenario1;
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

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

    private String pathToOntologyFile;

    public LinkedDator(String pathToOntologyFile) {
        this.pathToOntologyFile = pathToOntologyFile;
    }

    public Map<String, String> createLinks(List<SemanticDescription> semanticDescriptions, Map<String, String> resourceRepresentation, String uriBase) {

        Map<String, String> mapEntityToUri = new HashMap<>();
        Map<String, UriTemplate> mapUriToTemplate = new HashMap<>();
        Map<String, SemanticResource> mapEntityToResource = new HashMap<>();

        for (SemanticDescription semanticDescription : semanticDescriptions) {
            List<SemanticResource> semanticResources = semanticDescription.getSemanticResources();
            for (SemanticResource semanticResource : semanticResources) {
                List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
                for (UriTemplate uriTemplate : uriTemplates) {
                    String entity = semanticResource.getEntity();
                    String uri = uriTemplate.getUri();
                    mapEntityToUri.put(entity, uri);
                    mapEntityToResource.put(entity, semanticResource);
                    mapUriToTemplate.put(uri, uriTemplate);
                }
            }
        }

        OntModel ontology = this.loadOntology(pathToOntologyFile);
        List<ObjectProperty> objectProperties = ontology.listObjectProperties().toList();

        String entity = resourceRepresentation.get("entity");
        for (ObjectProperty objectProperty : objectProperties) {
            OntResource range = objectProperty.getRange();
            OntResource domain = objectProperty.getDomain();
            SemanticResource domainSemanticResource = mapEntityToResource.get(entity);
            if (domain.toString().equals(entity)) {
                Collection<String> domainProperties = domainSemanticResource.getProperties().values();
                String rangeUri = mapEntityToUri.get(range.toString());
                if (domainProperties.contains(objectProperty.getURI())) {
                    String resolvedLink = resolveLinkOneToN(objectProperty.getURI(), resourceRepresentation, mapUriToTemplate.get(rangeUri), uriBase);
                    resourceRepresentation.put(objectProperty.getURI(), resolvedLink);
                } else if (resourceCanResolveLink(domainSemanticResource, mapEntityToResource.get(domain.toString()).getUriTemplates())) {
                    String resolvedLink = resolveLinkOneToOne(resourceRepresentation, mapUriToTemplate.get(rangeUri), uriBase);
                    resourceRepresentation.put(objectProperty.getURI(), resolvedLink);
                }
            }
        }

        return resourceRepresentation;
    }

    // Not sure if this relation is oneToN
    private String resolveLinkOneToN(String objectProperty, Map<String, String> resourceRepresentation, UriTemplate uriTemplate, String uriBase) {
        String link = uriTemplate.getUri();
        Set<String> parameterKeys = uriTemplate.getParameters().keySet();
        for (String parameterKey : parameterKeys) {
            String domainResourcePropertyValue = resourceRepresentation.get(objectProperty);
            String parameterToReplace = String.format("{%s}", parameterKey);
            link = link.replace(parameterToReplace, domainResourcePropertyValue);
        }
        link = String.format("%s/%s", uriBase, link);
        return link;
    }

    // Not sure if this relation is oneToOne
    private String resolveLinkOneToOne(Map<String, String> resourceRepresentation, UriTemplate uriTemplate, String uriBase) {
        String link = uriTemplate.getUri();
        Set<String> parameterKeys = uriTemplate.getParameters().keySet();
        for (String parameterKey : parameterKeys) {
            String parameterSemanticDefinition = uriTemplate.getParameters().get(parameterKey);
            String domainResourcePropertyValue = resourceRepresentation.get(parameterSemanticDefinition);
            String parameterToReplace = String.format("{%s}", parameterKey);
            link = link.replace(parameterToReplace, domainResourcePropertyValue);
        }
        link = String.format("%s/%s", uriBase, link);
        return link;
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

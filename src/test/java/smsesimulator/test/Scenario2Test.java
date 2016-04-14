package smsesimulator.test;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import smsesimulator.GatewayDescription;
import smsesimulator.SemanticGateway;
import smsesimulator.SemanticMicroservice;
import smsesimulator.SemanticResource;
import smsesimulator.Simulator;
import smsesimulator.infrastructure.HttpRequest;
import smsesimulator.infrastructure.HttpResponse;
import smsesimulator.infrastructure.UriTemplate;

public class Scenario2Test {

    @Test
    public void createScenarioTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario2.json");
    }

    @Test
    public void gatewayDescriptionTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario2.json");
        SemanticGateway semanticGateway = new SemanticGateway("src/test/resources/Ontology2.owl", executor.getSemanticMicroservices());
        HttpResponse response = semanticGateway.processRequest(new HttpRequest(semanticGateway.getUriBase(), semanticGateway.getUriBase() + "/semanticDescription"));
        System.out.println(response);
    }
    
    @Test
    public void invocationMicroservicesTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario2.json");

        List<SemanticMicroservice> semanticMicroservices = executor.getSemanticMicroservices();
        for (SemanticMicroservice semanticMicroservice : semanticMicroservices) {
            List<SemanticResource> semanticResources = semanticMicroservice.getSemanticResources();
            for (SemanticResource semanticResource : semanticResources) {
                List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
                for (UriTemplate uriTemplate : uriTemplates) {
                    HttpResponse microserviceResponse = semanticMicroservice.processRequest(new HttpRequest(semanticMicroservice.getUriBase(), semanticMicroservice.getUriBase() + "/" + uriTemplate.getUri()));
                    System.out.println(microserviceResponse);
                }
            }
        }
    }

    @Test
    public void invocationGatewayMicroservicesTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario2.json");
        SemanticGateway semanticGateway = new SemanticGateway("src/test/resources/Ontology2.owl", executor.getSemanticMicroservices());

        HttpResponse response = semanticGateway.processRequest(new HttpRequest(semanticGateway.getUriBase(),  semanticGateway.getUriBase() + "/semanticDescription"));
        GatewayDescription gatewayDescription = (GatewayDescription) response.getBody();
        List<SemanticResource> semanticResources = gatewayDescription.getSemanticResources();
        for (SemanticResource semanticResource : semanticResources) {
            List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
            for (UriTemplate uriTemplate : uriTemplates) {
                HttpResponse microserviceResponse = semanticGateway.processRequest(new HttpRequest(semanticGateway.getUriBase(), semanticGateway.getUriBase() + "/" + uriTemplate.getUri()));
                System.out.println(microserviceResponse);
            }
        }
    }
}

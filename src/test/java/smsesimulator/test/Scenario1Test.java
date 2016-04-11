package smsesimulator.test;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import smsesimulator.GatewayDescription;
import smsesimulator.LinkedDator;
import smsesimulator.SemanticGateway;
import smsesimulator.SemanticResource;
import smsesimulator.Simulator;
import smsesimulator.infrastructure.HttpRequest;
import smsesimulator.infrastructure.HttpResponse;
import smsesimulator.infrastructure.UriTemplate;

public class Scenario1Test {

    @Test
    public void createScenarioTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario1.json");
    }

    @Test
    public void gatewayDescriptionTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario1.json");
        SemanticGateway semanticGateway = new SemanticGateway(executor.getSemanticMicroservices());
        HttpResponse response = semanticGateway.processRequest(new HttpRequest(semanticGateway.getUriBase(), "semanticDescription", semanticGateway.getUriBase() + "/semanticDescription"));
        System.out.println(response);
    }

    @Test
    public void invocationMicroservicesTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario1.json");
        SemanticGateway semanticGateway = new SemanticGateway(executor.getSemanticMicroservices());

        HttpResponse response = semanticGateway.processRequest(new HttpRequest(semanticGateway.getUriBase(), "semanticDescription", semanticGateway.getUriBase() + "/semanticDescription"));
        GatewayDescription gatewayDescription = (GatewayDescription) response.getBody();
        List<SemanticResource> semanticResources = gatewayDescription.getSemanticResources();
        for (SemanticResource semanticResource : semanticResources) {
            List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
            for (UriTemplate uriTemplate : uriTemplates) {
                HttpResponse microserviceResponse = semanticGateway.processRequest(new HttpRequest(semanticGateway.getUriBase(), semanticResource.getEntity(), semanticGateway.getUriBase() + "/" + uriTemplate.getUri()));
                System.out.println(microserviceResponse);
            }
        }
    }

    @Test
    public void LinkedDatorTest() throws IOException {
        LinkedDator lk = new LinkedDator();
    }

}

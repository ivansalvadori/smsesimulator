package smsesimulator.test;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import smsesimulator.Simulator;
import smsesimulator.infrastructure.HttpRequest;
import smsesimulator.infrastructure.HttpResponse;
import smsesimulator.infrastructure.UriTemplate;
import smsesimulator.GatewayDescription;
import smsesimulator.LinkedDator;
import smsesimulator.SemanticDescription;
import smsesimulator.SemanticGateway;
import smsesimulator.SemanticResource;

public class SimulatorTest {

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
    public void LinkedDatorTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario1.json");
        SemanticGateway semanticGateway = new SemanticGateway(executor.getSemanticMicroservices());
        List<SemanticDescription> semanticDescriptions = semanticGateway.getSemanticDescriptions();
        LinkedDator lk = new LinkedDator();
        lk.analizeSemanticDescritptions(semanticDescriptions);
    }

    @Test
    public void invocationMicroservicesTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario2.json");
        SemanticGateway semanticGateway = new SemanticGateway(executor.getSemanticMicroservices());

        HttpResponse response = semanticGateway.processRequest(new HttpRequest(semanticGateway.getUriBase(), "semanticDescription", semanticGateway.getUriBase() + "/semanticDescription"));
        GatewayDescription gatewayDescription = (GatewayDescription) response.getBody();
        List<SemanticResource> semanticResources = gatewayDescription.getSemanticResources();
        for (SemanticResource semanticResource : semanticResources) {
            List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
            for (UriTemplate uriTemplate : uriTemplates) {
                semanticGateway.processRequest(new HttpRequest(semanticGateway.getUriBase(), semanticResource.getEntity(), semanticGateway.getUriBase() + "/" + uriTemplate.getUri()));

            }
        }
    }
}

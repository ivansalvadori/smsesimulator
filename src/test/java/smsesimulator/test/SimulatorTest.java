package smsesimulator.test;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import smsesimulator.Simulator;
import smsesimulator.infrastructure.HttpRequest;
import smsesimulator.infrastructure.HttpResponse;
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
    public void CreateApiGateway() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario1.json");
        SemanticGateway semanticGateway = new SemanticGateway(executor.getSemanticMicroservices());
        semanticGateway.processRequest(null);
    }
	
	
	@Test
    public void gatewayDescriptionTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario1.json");
        SemanticGateway semanticGateway = new SemanticGateway(executor.getSemanticMicroservices());
        HttpResponse response = semanticGateway.processRequest(new HttpRequest("", "semanticDescription"));
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
	    executor.createScenario("src/test/resources/scenario1.json");
	    SemanticGateway semanticGateway = new SemanticGateway(executor.getSemanticMicroservices());
	    
        HttpResponse response = semanticGateway.processRequest(new HttpRequest("", "semanticDescription"));
        List<SemanticDescription> semanticDescriptions = (List<SemanticDescription>) response.getBody(); 
        for (SemanticDescription semanticDescription : semanticDescriptions) {
            List<SemanticResource> semanticResources = semanticDescription.getSemanticResources();
            for (SemanticResource semanticResource : semanticResources) {
                semanticGateway.processRequest(new HttpRequest(semanticDescription.getUriBase(), semanticResource.getEntity()));
            }
            
           
        }
	    
	    System.out.println(semanticDescriptions);       
	    
	}

}

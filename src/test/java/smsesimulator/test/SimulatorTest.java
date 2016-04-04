package smsesimulator.test;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import smsesimulator.Simulator;
import smsesimulator.infrastructure.HttpRequest;
import smsesimulator.LinkedDator;
import smsesimulator.SemanticDescription;
import smsesimulator.SemanticGateway;

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
    public void invocationMicroservicesTest() throws IOException {
        Simulator executor = new Simulator();
        executor.createScenario("src/test/resources/scenario1.json");
        SemanticGateway semanticGateway = new SemanticGateway(executor.getSemanticMicroservices());
        semanticGateway.processRequest(new HttpRequest());
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

}

package smsesimulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.JsonPath;

public class Simulator {
    
    private List<SemanticMicroservice> semanticMicroservices = new ArrayList<>();

	public void createScenario(String filePath) throws IOException {
		String jsonScenario = new String(Files.readAllBytes(Paths.get(filePath)));
		semanticMicroservices = createSemanticMicroservices(jsonScenario);
		System.out.println(String.format("Scenario has been created. %d microservices detected.", semanticMicroservices.size()));
	}

	private List<SemanticMicroservice> createSemanticMicroservices(String jsonScenario) {
		List<SemanticMicroservice> list = new ArrayList<>();
		int numberOfMicroservices = JsonPath.parse(jsonScenario).read("$.semanticMicroservices.length()");
		for (int i = 0; i < numberOfMicroservices; i++) {
            String filter = String.format("$.semanticMicroservices[%d]", i);
            SemanticMicroservice semanticMicroservice = JsonPath.parse(jsonScenario).read(filter, SemanticMicroservice.class);
			list.add(semanticMicroservice);
		}
		return list;
	}
	
	public List<SemanticMicroservice> getSemanticMicroservices() {
        return semanticMicroservices;
    }
}

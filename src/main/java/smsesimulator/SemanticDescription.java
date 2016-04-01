package smsesimulator;

import java.util.List;

public class SemanticDescription {

	private String uriBase;
	List<SemanticResource> semanticResources;

	public SemanticDescription(String uriBase, List<SemanticResource> semanticResources) {
		this.uriBase = uriBase;
		this.semanticResources = semanticResources;
	}
	
	public List<SemanticResource> getSemanticResources() {
		return semanticResources;
	}
	
	public String getUriBase() {
		return uriBase;
	}

}

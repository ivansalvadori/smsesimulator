package smsesimulator;

import java.util.List;

import com.google.gson.Gson;

public class SemanticDescription {

	private String uriBase;
	List<SemanticResource> semanticResources;

	public SemanticDescription(String uriBase, List<SemanticResource> semanticResources) {
		this.uriBase = uriBase;
		this.semanticResources = semanticResources;
	}
	
	@Override
	public String toString() {
	    return new Gson().toJson(this);
	}
	
	public List<SemanticResource> getSemanticResources() {
		return semanticResources;
	}
	
	public String getUriBase() {
		return uriBase;
	}

}

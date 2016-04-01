package smsesimulator;

import java.util.List;

import com.google.gson.Gson;

public class SemanticMicroservice implements Publisher, Subscriber {

	private String uriBase;
	List<SemanticResource> semanticResources;
	private SemanticDescription semanticDescription;

	public SemanticMicroservice() {		
		this.register();		
	}

	public HttpResponse processRequest(HttpRequest req) {
		return null;
	}

	public void register() {
		MessageChannel.registerAsPublisher(this);
		MessageChannel.registerAsSubscriber(this);
	}

	@Override
	public void receiveMessage(String msg) {
		if (msg.equals("requestForDescription")) {
			semanticDescription = new SemanticDescription(uriBase, semanticResources);
			MessageChannel.send(new Gson().toJson(this.semanticDescription));
		}
	}

	@Override
	public void sendMessage(String msg) {
		MessageChannel.send(msg);

	}

	public List<SemanticResource> getSemanticResources() {
		return semanticResources;
	}

	public void setSemanticResources(List<SemanticResource> semanticResources) {
		this.semanticResources = semanticResources;
	}
	
	public SemanticDescription getSemanticDescription() {
		return semanticDescription;
	}

}

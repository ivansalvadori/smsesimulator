package smsesimulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import smsesimulator.infrastructure.DhcpServer;
import smsesimulator.infrastructure.HttpRequest;
import smsesimulator.infrastructure.HttpResponse;
import smsesimulator.infrastructure.HttpResponse.HttpResponseBuilder;
import smsesimulator.infrastructure.MessageChannel;
import smsesimulator.infrastructure.Publisher;
import smsesimulator.infrastructure.Subscriber;
import smsesimulator.infrastructure.WebApi;

public class SemanticMicroservice implements Publisher, Subscriber, WebApi {

    private String uriBase;
    List<SemanticResource> semanticResources;
    private SemanticDescription semanticDescription;
    private transient Map<String, SemanticResource> resourcesMap = new HashMap<>();

    public SemanticMicroservice() {
        this.uriBase = DhcpServer.getIpAddress();
        this.register();
    }

    public HttpResponse processRequest(HttpRequest req) {
        System.out.println(String.format("Processing: %s for %s", req.getUriBase(), req.getResource()));
        if (!req.getUriBase().equals(uriBase)) {
            return new HttpResponseBuilder().body("Not found").build();
        }
        String resource = req.getResource();

        // creating a map to facilitate the search for the requested resource
        for (SemanticResource semanticResource : semanticResources) {
            resourcesMap.put(semanticResource.getEntity(), semanticResource);
        }

        SemanticResource semanticResource = resourcesMap.get(resource);
        if(semanticResource == null){
            return new HttpResponseBuilder().body("Not found").build();
        }
        return new HttpResponseBuilder().body(semanticResource.serializeAnExample()).build();
    }

    public void register() {
        MessageChannel.registerAsPublisher(this);
        MessageChannel.registerAsSubscriber(this);
    }

    @Override
    public void receiveMessage(String msg) {
        if (msg.equals("requestForDescription")) {
            MessageChannel.send(new Gson().toJson(this.getSemanticDescription()));
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
        this.semanticDescription = new SemanticDescription(uriBase, semanticResources);
        return semanticDescription;
    }

}

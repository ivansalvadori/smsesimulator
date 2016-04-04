package smsesimulator;

import java.util.List;

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

    public SemanticMicroservice() {
        this.uriBase = DhcpServer.getIpAddress();
        this.register();
    }

    public HttpResponse processRequest(HttpRequest req) {
        return new HttpResponseBuilder().body(semanticResources.get(0).serializeAnExample()).build();
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

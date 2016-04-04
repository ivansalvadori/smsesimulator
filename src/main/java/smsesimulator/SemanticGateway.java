package smsesimulator;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import smsesimulator.infrastructure.DhcpServer;
import smsesimulator.infrastructure.HttpRequest;
import smsesimulator.infrastructure.HttpResponse;
import smsesimulator.infrastructure.MessageChannel;
import smsesimulator.infrastructure.Publisher;
import smsesimulator.infrastructure.Subscriber;
import smsesimulator.infrastructure.WebApi;
import smsesimulator.infrastructure.HttpResponse.HttpResponseBuilder;

public class SemanticGateway implements Publisher, Subscriber, WebApi {

    private List<SemanticDescription> semanticDescriptions = new ArrayList();
    private List<SemanticMicroservice> semanticMicroservices = new ArrayList<>();
    private String uriBase;

    public SemanticGateway() {
        this.uriBase = DhcpServer.getIpAddress();
        MessageChannel.registerAsPublisher(this);
        MessageChannel.registerAsSubscriber(this);
        sendMessage("requestForDescription");
    }

    public SemanticGateway(List<SemanticMicroservice> microservices) {
        this.uriBase = DhcpServer.getIpAddress();
        this.semanticMicroservices = microservices;
        for (SemanticMicroservice semanticMicroservice : microservices) {
            semanticDescriptions.add(semanticMicroservice.getSemanticDescription());
        }
    }

    @Override
    public void receiveMessage(String msg) {
        try {
            SemanticDescription semanticDescription = new Gson().fromJson(msg, SemanticDescription.class);
            semanticDescriptions.add(semanticDescription);
        } catch (Exception e) {
            System.out.println(String.format("A received message '%s' is not a description. That's ok", msg));
        }
    }

    @Override
    public void sendMessage(String msg) {
        MessageChannel.send(msg);
    }

    @Override
    public HttpResponse processRequest(HttpRequest req) {
        if (req.getResource().equals("semanticDescription")) {
            return new HttpResponseBuilder().body(this.getSemanticDescriptions()).build();
        }
        
        System.out.println(String.format("GATEWAY: Processing: %s for %s", req.getUriBase(), req.getResource()));
        
           
       
        return null;
    }
    
    public List<SemanticDescription> getSemanticDescriptions() {
        return semanticDescriptions;
    }

}

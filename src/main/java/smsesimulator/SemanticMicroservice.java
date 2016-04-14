package smsesimulator;

import java.util.Collection;
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
import smsesimulator.infrastructure.UriTemplate;
import smsesimulator.infrastructure.WebApi;

public class SemanticMicroservice implements Publisher, Subscriber, WebApi {

    private String uriBase;
    List<SemanticResource> semanticResources;
    private SemanticDescription semanticDescription;
    private transient Map<String, SemanticResource> mapUriToResource = new HashMap<>();

    public SemanticMicroservice() {
        this.uriBase = DhcpServer.getIpAddress();
        this.register();
    }

    @Override
    public HttpResponse processRequest(HttpRequest req) {
        System.out.println(String.format("MICROSERVICE: %s received %s", req.getUriBase(), req.getFullUri()));
        if (!req.getUriBase().equals(uriBase)) {
            return new HttpResponseBuilder().body("Not found").build();
        }

        // creating a map to facilitate the search for the requested resource
        for (SemanticResource semanticResource : semanticResources) {
            List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
            for (UriTemplate uriTemplate : uriTemplates) {
                mapUriToResource.put(uriBase + "/" + uriTemplate.getUri(), semanticResource);
            }
        }

        SemanticResource semanticResource = mapUriToResource.get(req.getFullUri());
        if (semanticResource == null) {
            return new HttpResponseBuilder().body("Not found").build();
        }
        return new HttpResponseBuilder().body(this.serializeAnRepresentation(semanticResource)).build();
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

    private Map<String, Object> serializeAnRepresentation(SemanticResource semanticResource) {
        Map<String, Object> representation = new HashMap<>();
        representation.put("@type", semanticResource.getEntity());

        Collection<String> dataProperties = semanticResource.getDataProperties();
        if (dataProperties != null) {

            for (String semanticProperty : dataProperties) {
                representation.put(semanticProperty, String.valueOf(Math.random()).replace("0.", ""));
            }
        }

        List<SemanticResource> objectProperties = semanticResource.getObjectProperties();
        if (objectProperties != null) {
            for (SemanticResource objectProperty : objectProperties) {
               Map<String, Object> objectPropertySerialization = serializeAnRepresentation(objectProperty);
               representation.put(objectProperty.getRel(), objectPropertySerialization);
            }

        }

        return representation;
    }

    public String getUriBase() {
        return uriBase;
    }
}

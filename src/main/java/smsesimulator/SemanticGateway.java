package smsesimulator;

import java.util.ArrayList;
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

public class SemanticGateway implements Publisher, Subscriber, WebApi {
    private String pathToOntologyFile;
    private List<SemanticDescription> semanticDescriptions = new ArrayList();
    private List<SemanticMicroservice> semanticMicroservices = new ArrayList<>();
    Map<String, SemanticMicroservice> microserviceMap;
    private String uriBase;

    public SemanticGateway() {
        this.uriBase = DhcpServer.getIpAddress();
        MessageChannel.registerAsPublisher(this);
        MessageChannel.registerAsSubscriber(this);
        sendMessage("requestForDescription");
        microserviceMap = this.generateMicroserviceMap(this.semanticMicroservices);
    }

    public SemanticGateway(String pathToOntologyFile, List<SemanticMicroservice> microservices) {
        this.pathToOntologyFile = pathToOntologyFile;
        this.uriBase = DhcpServer.getIpAddress();
        this.semanticMicroservices = microservices;
        for (SemanticMicroservice semanticMicroservice : microservices) {
            semanticDescriptions.add(semanticMicroservice.getSemanticDescription());
        }
        microserviceMap = this.generateMicroserviceMap(this.semanticMicroservices);
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
        System.out.println(String.format("GATEWAY: %s received %s", req.getUriBase(), req.getFullUri()));

        if (!req.getUriBase().equals(this.uriBase)) {
            return new HttpResponseBuilder().body("Not found").build();

        }

        if (req.getFullUri().contains("/semanticDescription")) {
            return new HttpResponseBuilder().body(this.generateGatewayDescription(this.semanticDescriptions)).build();
        }

        // Here is where the gateway request the microservices
        String requestedResource = req.getFullUri().replace(this.uriBase + "/", "");
        SemanticMicroservice semanticMicroservice = microserviceMap.get(requestedResource);
        if (semanticMicroservice != null) {
            String microservicesUriBase = semanticMicroservice.getSemanticDescription().getUriBase();
            HttpResponse response = semanticMicroservice.processRequest(new HttpRequest(microservicesUriBase, semanticMicroservice.getSemanticDescription().getUriBase() + "/" + requestedResource));
            Map<String, Object> representationWithLinks = createLinks(this.semanticDescriptions, response);
            response.setBody(representationWithLinks);
            return response;
        }
        return null;
    }

    private Map<String, Object> createLinks(List<SemanticDescription> semanticDescriptions2, HttpResponse response) {
        LinkedDator linkedDator = new LinkedDator(pathToOntologyFile);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        return linkedDator.createLinks(semanticDescriptions2, body, this.getUriBase());
    }

    private GatewayDescription generateGatewayDescription(List<SemanticDescription> microserviceDescriptions) {
        return new GatewayDescription(this.uriBase, microserviceDescriptions);

    }

    private Map<String, SemanticMicroservice> generateMicroserviceMap(List<SemanticMicroservice> semanticMicroservices) {
        Map<String, SemanticMicroservice> map = new HashMap<>();

        for (SemanticMicroservice semanticMicroservice : semanticMicroservices) {
            List<SemanticResource> semanticResources = semanticMicroservice.getSemanticResources();
            for (SemanticResource semanticResource : semanticResources) {
                List<UriTemplate> uriTemplates = semanticResource.getUriTemplates();
                for (UriTemplate uriTemplate : uriTemplates) {
                    String uri = uriTemplate.getUri();
                    map.put(uri, semanticMicroservice);
                }
            }
        }
        return map;
    }

    public List<SemanticDescription> getSemanticDescriptions() {
        return semanticDescriptions;
    }

    public String getUriBase() {
        return uriBase;
    }

}

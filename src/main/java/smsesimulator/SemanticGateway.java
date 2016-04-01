package smsesimulator;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class SemanticGateway implements Publisher, Subscriber{
    
    private List<SemanticDescription> semanticDescriptions = new ArrayList();
    private String uriBase;
    
    public SemanticGateway() {
        MessageChannel.registerAsPublisher(this);
        MessageChannel.registerAsSubscriber(this);
    }

    @Override
    public void receiveMessage(String msg) {
        //TODO melhorar
        if(msg.startsWith("{\"semanticResources\"")){
            SemanticDescription semanticDescription = new Gson().fromJson(msg, SemanticDescription.class);     
            semanticDescriptions.add(semanticDescription);
        }
    }

    @Override
    public void sendMessage(String msg) {
       MessageChannel.send(msg);        
    }

}

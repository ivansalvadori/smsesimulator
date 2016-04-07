package smsesimulator;

import java.util.List;
import java.util.Map;

import smsesimulator.infrastructure.UriTemplate;

public class SemanticResource {

    private String entity;
    private Map<String, String> properties;
    private List<UriTemplate> uriTemplates;

    public String getEntity() {
        return entity;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<UriTemplate> getUriTemplates() {
        return uriTemplates;
    }

    public void setUriTemplates(List<UriTemplate> uriTemplates) {
        this.uriTemplates = uriTemplates;
    }

    public Object serializeAnExample() {
        this.properties.put("entity", this.entity);
        return this.properties;
    }

}

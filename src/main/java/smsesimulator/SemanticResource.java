package smsesimulator;

import java.util.List;
import java.util.Map;

import smsesimulator.infrastructure.UriTemplate;

public class SemanticResource {

    private String rel;
    private String entity;
    private List<String> dataProperties;
    private List<SemanticResource> objectProperties;
    private List<UriTemplate> uriTemplates;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public List<String> getDataProperties() {
        return dataProperties;
    }

    public void setDataProperties(List<String> dataProperties) {
        this.dataProperties = dataProperties;
    }

    public List<SemanticResource> getObjectProperties() {
        return objectProperties;
    }

    public void setObjectProperties(List<SemanticResource> objectProperties) {
        this.objectProperties = objectProperties;
    }

    public List<UriTemplate> getUriTemplates() {
        return uriTemplates;
    }

    public void setUriTemplates(List<UriTemplate> uriTemplates) {
        this.uriTemplates = uriTemplates;
    }

}
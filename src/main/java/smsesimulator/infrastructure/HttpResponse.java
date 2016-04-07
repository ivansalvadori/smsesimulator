package smsesimulator.infrastructure;

import com.google.gson.Gson;

public class HttpResponse {
    
    private Object body;
    
    public HttpResponse(HttpResponseBuilder builder) {
        this.body = builder.body;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this.body);
    }
       
    public static class HttpResponseBuilder{
        private Object body;
        
        public HttpResponseBuilder body(Object body){
            this.body = body;
            return this;
        }
        
        public HttpResponse build(){
            return new HttpResponse(this);
        }        
    }
    
    public Object getBody() {
        return body;
    }
    
    public void setBody(Object body) {
        this.body = body;
    }

  

}

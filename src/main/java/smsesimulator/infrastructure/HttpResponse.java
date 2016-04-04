package smsesimulator.infrastructure;

import com.google.gson.Gson;

public class HttpResponse {
    
    private String body;
    
    public HttpResponse(HttpResponseBuilder builder) {
        this.body = builder.body;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this.body);
    }
       
    public static class HttpResponseBuilder{
        private String body;
        
        public HttpResponseBuilder body(String body){
            this.body = body;
            return this;
        }
        
        public HttpResponse build(){
            return new HttpResponse(this);
        }
        
    }

  

}

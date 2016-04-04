package smsesimulator.infrastructure;

public class DhcpServer {
    
    private static int count;
    
    public static String getIpAddress(){
        return String.format("192.168.10.%d", ++count);
    }

}

package smsesimulator.infrastructure;

import java.util.ArrayList;
import java.util.List;

public class MessageChannel {
	
	private static List<Publisher> publishers = new ArrayList<>();
	private static List<Subscriber> subscribers = new ArrayList<>();
	
	public static void registerAsPublisher(Publisher publisher){
		publishers.add(publisher);		
	}
	
	public static void registerAsSubscriber(Subscriber subscriber){
		subscribers.add(subscriber);		
	}
	
	public static void send(String msg){
		System.out.println("sending the following message: " + msg);
		for (Subscriber subscriber : subscribers) {
			subscriber.receiveMessage(msg);
		}
	}

}

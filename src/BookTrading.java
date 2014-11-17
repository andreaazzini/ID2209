import jade.core.Agent;

public class BookTrading extends Agent {
	protected void setup() {
		System.out.println("Hello! Buyeragent " + getAID().getName() + " is ready!");
	}
}
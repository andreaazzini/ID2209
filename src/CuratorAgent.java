package src;

import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import src.MuseumAgent;

public class CuratorAgent extends MuseumAgent {
	protected void setup() {
		register(this, "curator");
		System.out.println("Curator Agent " + getAID().getName() + " successfully initialized");
		
		addBehaviour(new WakerBehaviour(this, 10000) {
			boolean messageReceived = false;

			protected void onWake() {
				// set messageReceived = true if message received
				if (!messageReceived) {
					System.out.println("Message not received yet...");
					this.reset(10000);
				} else {
					// send information to the waker
				}
			}
		});
	}

	protected void takeDown() {
		deregister(this);
		System.out.println("Curator agent " + getAID().getName() + " terminating.");
	}
}
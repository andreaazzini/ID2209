package src;

import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import src.MuseumAgent;

public class CuratorAgent extends MuseumAgent {
	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription(); 
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("curator");
		sd.setName("JADE-curator");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd); 
		} catch (FIPAException fe) { 
			fe.printStackTrace();
		}

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
		try {
			DFService.deregister(this); 
		} catch (FIPAException fe) {
     		fe.printStackTrace();
		}

		System.out.println("Curator agent " + getAID().getName() + " terminating.");
	}
}
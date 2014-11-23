package src;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import src.MuseumAgent;

public class CuratorAgent extends MuseumAgent {

	protected void setup() {
		register(this, "curator");
		System.out.println("Curator Agent " + getAID().getName() + " successfully initialized");
		
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				if (msg != null && msg.getContent().equals("ask")) {
					AID sender = msg.getSender();
					System.out.println("Received message: " + msg.getContent() +
						" from " + sender);
					ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
					reply.setSender(myAgent.getAID());
					reply.addReceiver(sender);
					reply.setContent("tell this is an important info");
					send(reply);
				}
			}
		});
	}

	protected void takeDown() {
		deregister(this);
		System.out.println("Curator agent " + getAID().getName() + " terminating.");
	}
}
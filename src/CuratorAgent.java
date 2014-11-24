package src;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.ACLMessage;
import java.util.Random;

import src.MuseumAgent;

public class CuratorAgent extends MuseumAgent {
	private int acceptancePrice;

	protected void setup() {
		acceptancePrice = new Random().nextInt(8) * 10000 + 10000;

		register(this, "curator");
		System.out.println("Curator Agent " + getAID().getName() + " successfully initialized");
		System.out.printf("Acceptance price: %d\n", acceptancePrice);
		
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					if (msg.getContent().equals("ask")) {
						AID sender = msg.getSender();
						System.out.println("Received message: " + msg.getContent() +
							" from " + sender);
						ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
						reply.setSender(myAgent.getAID());
						reply.addReceiver(sender);
						reply.setContent("tell this is an important info");
						send(reply);
					} else if (msg.getContent().equals("start-of-auction")) {
						System.out.println("Start of a new auction");
					} else if (msg.getPerformative() == ACLMessage.CFP) {
						if (msg.getContent().equals(Integer.toString(acceptancePrice))) {
							System.out.println("Sending proposal...");
							ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
							proposal.setSender(myAgent.getAID());
							proposal.addReceiver(msg.getSender());
							proposal.setProtocol(InteractionProtocol.FIPA_DUTCH_AUCTION);
							send(proposal);
						}
					} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
						System.out.println("Proposal accepted!");
					}
				}
			}
		});
	}

	protected void takeDown() {
		deregister(this);
		System.out.println("Curator agent " + getAID().getName() + " terminating.");
	}
}
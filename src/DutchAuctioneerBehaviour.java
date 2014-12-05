package src;

import jade.core.AID;
import jade.core.Location;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.List;
import java.util.ArrayList;

public class DutchAuctioneerBehaviour extends Behaviour {
	private List<AID> participants;
	private int price;
	private boolean proposalAccepted;
	private boolean participantsInformed;
	private Location home;

	public DutchAuctioneerBehaviour(List<AID> participants, int initialPrice, Location home) {
		super();
		this.home = home;
		price = initialPrice;
		proposalAccepted = false;
		participantsInformed = false;
		this.participants = new ArrayList<AID>();
		for (AID p : participants) {
			this.participants.add(p);
		}
	}

	public void action() {
		if (!participantsInformed) {
			ACLMessage informStartAuction = new ACLMessage(ACLMessage.INFORM);
			informStartAuction.setSender(myAgent.getAID());
			for (AID p : participants) {
				informStartAuction.addReceiver(p);
			}
			informStartAuction.setContent("start-of-auction");
			informStartAuction.setProtocol(InteractionProtocol.FIPA_DUTCH_AUCTION);
			myAgent.send(informStartAuction);
			participantsInformed = true;
			myAgent.doWait(3000);
		}

		ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
		cfp.setSender(myAgent.getAID());
		for (AID p : participants) {
			cfp.addReceiver(p);
		}
		cfp.setContent(Integer.toString(price));
		cfp.setProtocol(InteractionProtocol.FIPA_DUTCH_AUCTION);
		System.out.printf("Sending a new cfp with price: %d\n", price);
		price -= 10000;
		myAgent.send(cfp);
		
		long startTime = System.currentTimeMillis();
		while (proposalAccepted || (System.currentTimeMillis() - startTime) < 3000) {
		    ACLMessage proposal = myAgent.receive();
		    if (proposal != null && proposal.getPerformative() == ACLMessage.PROPOSE) {
		    	ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		    	accept.setSender(myAgent.getAID());
		    	accept.addReceiver(proposal.getSender());
		    	accept.setContent("accept");
		    	accept.setProtocol(InteractionProtocol.FIPA_DUTCH_AUCTION);
		    	myAgent.send(accept);
		    	proposalAccepted = true;

		    	ACLMessage auctionOver = new ACLMessage(ACLMessage.INFORM);
			    auctionOver.setSender(myAgent.getAID());
			    for (AID p : participants) {
					auctionOver.addReceiver(p);
				}
				auctionOver.setContent("auction-over");
				myAgent.send(auctionOver);
				break;
		    }
		}
	}

	public boolean done() {
		return proposalAccepted;
	}

	public int onEnd() {
		myAgent.doMove(home);
		return 0;
	}
}
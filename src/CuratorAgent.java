package src;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPANames.ContentLanguage;
import jade.domain.FIPANames.InteractionProtocol;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.List;
import java.util.Random;

import src.MuseumAgent;

public class CuratorAgent extends MuseumAgent {
	private int acceptancePrice;
	private static final int CLONES = 2;
	private static boolean containersCreated = false;
	private Location home;

	protected void setup() {
		home = here();

		if (!containersCreated) {
			Runtime rt = Runtime.instance();
			for (int i = 0; i < CLONES; i++) {
				rt.createAgentContainer(new ProfileImpl());
			}
			containersCreated = true;
		}

		//Register the SL content language
		getContentManager().registerLanguage(new SLCodec(), ContentLanguage.FIPA_SL);
		//Register the mobility ontology
		getContentManager().registerOntology(JADEManagementOntology.getInstance());
		// Register the curator in the DL
		register(this, "curator");

		// Send a request to the AMS to obtain the Containers
		Action action = new Action(getAMS(), new QueryPlatformLocationsAction());
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST); 
		request.addReceiver(getAMS()); 
		request.setOntology(JADEManagementOntology.getInstance().getName());
		request.setLanguage(ContentLanguage.FIPA_SL);
		request.setProtocol(InteractionProtocol.FIPA_REQUEST);
		try {
			getContentManager().fillContent(request, action);
	    	send(request);
	    	// Retrieve the information
	    	ACLMessage receivedMessage = blockingReceive(MessageTemplate.MatchSender(getAMS()));
			Result r = (Result)getContentManager().extractContent(receivedMessage); 
			List containerList = (List)r.getValue();
			SequentialBehaviour sb = new SequentialBehaviour();
			for (int i = 0; i < CLONES; i++) {
				final int index = i;
				final AID mainAID = getAID();
				sb.addSubBehaviour(new OneShotBehaviour(this) {
					public void action() {
						if (myAgent.getAID().equals(mainAID)) {
							doClone((ContainerID)containerList.get(index), getLocalName() + "_clone" + index);
						}
					}
				});
				addBehaviour(sb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		addBehaviour(new Behaviour(this) {
			private boolean proposalAccepted = false;

			public void action() {
				ACLMessage msg = blockingReceive();
				if (msg.getContent().equals("ask")) {
					AID sender = msg.getSender();
					System.out.println("Received message: " + msg.getContent() +
						" from " + sender);
					ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
					reply.setSender(myAgent.getAID());
					reply.addReceiver(sender);
					reply.setContent("tell this is an important info");
					send(reply);
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
				} else if (msg.getContent().equals("auction-over")) {
					proposalAccepted = true;
				}
			}

			public boolean done() {
				return proposalAccepted;
			}

			public int onEnd() {
				doDelete();

				return 0;
			}
		});
	}

	protected void takeDown() {
		System.out.println("Curator agent " + getAID().getName() + " terminating.");
	}

	public void afterClone() {
		// Randomly determine the acceptance price
		acceptancePrice = new Random().nextInt(8) * 10000 + 10000;
		System.out.printf("Acceptance price: %d\n", acceptancePrice);
		System.out.println("Curator Agent " + getAID().getName() + " successfully initialized");
	}
}
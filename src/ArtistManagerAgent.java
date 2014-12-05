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
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPANames.ContentLanguage;
import jade.domain.FIPANames.InteractionProtocol;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.QueryAgentsOnLocation;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.List;
import java.util.ArrayList;
import src.DutchAuctioneerBehaviour;
import src.MuseumAgent;

public class ArtistManagerAgent extends MuseumAgent {
	private List<AID> availableCurators;
	private static final int INITIALPRICE = 100000;
	private Location home;
	private List<AID> containerAgents;

	protected void setup() {
		home = here();
		//Register the SL content language
		getContentManager().registerLanguage(new SLCodec(), ContentLanguage.FIPA_SL);
		//Register the mobility ontology
		getContentManager().registerOntology(JADEManagementOntology.getInstance());

		availableCurators = new ArrayList<AID>();
		containerAgents = new ArrayList<AID>();

		register(this, "artistmanager");
		getAvailableCurators();
		if (availableCurators.size() >= 2) {
			System.out.println("Artist Manager Agent " + getAID().getName() + " successfully initialized");

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
				jade.util.leap.List containerList = (jade.util.leap.List)r.getValue();
				SequentialBehaviour sb = new SequentialBehaviour();
				for (int i = 0; i < containerList.size() - 1; i++) {
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
		} else {
			System.out.println("There aren't enough curators to start a new aution");
			System.out.println("The Artist Manager will be killed...");
			doDelete();
		}
	}

	protected void takeDown() {
		deregister(this);
		System.out.println("Artist Manager agent " + getAID().getName() + " terminating.");
	}

	private void getAvailableCurators() {
		DFAgentDescription template = new DFAgentDescription(); 
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("curator");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);

			for (int i = 0; i < result.length; i++) {
				availableCurators.add(result[i].getName());
			}
		} catch (FIPAException fe) {
            fe.printStackTrace();
		}
	}

	public void afterClone() {
		addBehaviour(new DutchAuctioneerBehaviour(getContainerAgents(), INITIALPRICE, home));
	}

	private List<AID> getContainerAgents() {
		//Register the SL content language
		getContentManager().registerLanguage(new SLCodec(), ContentLanguage.FIPA_SL);
		//Register the mobility ontology
		getContentManager().registerOntology(JADEManagementOntology.getInstance());
		// Send a request to the AMS to obtain the agents in my container
		QueryAgentsOnLocation agentAction = new QueryAgentsOnLocation();
		agentAction.setLocation(here());
		Action newAction = new Action(getAMS(), agentAction);
		ACLMessage agentRequest = new ACLMessage(ACLMessage.REQUEST); 
		agentRequest.addReceiver(getAMS()); 
		agentRequest.setOntology(JADEManagementOntology.getInstance().getName());
		agentRequest.setLanguage(ContentLanguage.FIPA_SL);
		agentRequest.setProtocol(InteractionProtocol.FIPA_REQUEST);
		containerAgents = new ArrayList<AID>();
		try {
			getContentManager().fillContent(agentRequest, newAction);
	    	send(agentRequest);
	    	// Retrieve the information
	    	ACLMessage receivedMessage = blockingReceive(MessageTemplate.MatchSender(getAMS()));
			Result r = (Result)getContentManager().extractContent(receivedMessage); 
			jade.util.leap.List agentList = (jade.util.leap.List)r.getValue();
			for (int i = 0; i < agentList.size(); i++) {
				if (!getAID().equals((AID)agentList.get(i))) {
					containerAgents.add((AID)agentList.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return containerAgents;
	}
}
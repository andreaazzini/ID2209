package src;

import java.util.List;
import java.util.ArrayList;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import src.MuseumAgent;
import src.Artifact;
import src.CommunicationBehaviour;
import src.ParallelTourGuideBehaviour;

public class TourGuideAgent extends MuseumAgent {
	private ParallelBehaviour pb;
	private List<Artifact> artifacts;
	private AID sender;
	private List<AID> availableCurators;
	private int curatorNumber;
	private List<ACLMessage> outgoingMessages;

	protected void setup() {
		artifacts = new ArrayList<Artifact>();
		outgoingMessages = new ArrayList<ACLMessage>();
		availableCurators = new ArrayList<AID>();
		curatorNumber = 0;

		register(this, "tourguide");

		System.out.println("Tour Guide Agent " + getAID().getName() + " successfully initialized");
		
		pb = new ParallelTourGuideBehaviour();
		pb.addSubBehaviour(listen());
		addBehaviour(pb);
	}

	protected void takeDown() {
		deregister(this);
		System.out.println("Tour Guide agent " + getAID().getName() + " terminating.");
	}

	protected Behaviour listen() {
		return new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive(MessageTemplate.MatchContent("start"));
				if (msg != null) {
					sender = msg.getSender();
					String content = msg.getContent();
					System.out.println("Received message: " + content +
						" from " + sender);

					if (content.equals("start")) {
						updateAvailableCurators();
						if (availableCurators.size() > 0) {
							pb.addSubBehaviour(sendingBehaviour(sender));
						} else {
							System.out.println("No curators available");
							ACLMessage message = new ACLMessage(ACLMessage.INFORM);
							message.setSender(myAgent.getAID());
							message.addReceiver(sender);
							message.setContent("fail");
							send(message);
						}
					}
				}
			}
		};
	}

	private Behaviour sendingBehaviour(AID sender) {
		return new OneShotBehaviour(this) {
			public void action() {
				System.out.println("Sending message to the designated curator...");
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				message.setSender(myAgent.getAID());
				message.addReceiver(availableCurators.get(0));
				message.setContent("ask");
				send(message);

				ACLMessage msg;
				do {
					msg = receive();
				} while (msg == null);
				
				if (msg != null) {
					System.out.println("Received reply: " + msg.getContent() + 
						" from " + msg.getSender());
				
					String[] infos = msg.getContent().split(" ");
					if (infos[0].equals("tell")) {
						ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
						reply.setSender(myAgent.getAID());
						reply.addReceiver(sender);
					
						String info = "";
						for (int i = 1; i < infos.length; i++) {
							info = new String(info + infos[i] + " ");
						}
						reply.setContent("info " + info);
						
						send(reply);
					}
				}
			}
		};
	}

	private void updateAvailableCurators() {
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
}
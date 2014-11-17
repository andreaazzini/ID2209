package src;

import java.util.List;
import java.util.ArrayList;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.proto.states.MsgReceiver;
import jade.lang.acl.ACLMessage;
import src.MuseumAgent;
import src.Artifact;
import src.ParallelTourGuideBehaviour;

public class TourGuideAgent extends MuseumAgent {
	private List<Artifact> artifacts;
	private AID[] curators;
	private int curatorNumber;

	protected void setup() {
		artifacts = new ArrayList<Artifact>();
		curatorNumber = 0;

		register(this, "tourguide");

		System.out.println("Tour Guide Agent " + getAID().getName() + " successfully initialized");
		ParallelBehaviour pb = new ParallelTourGuideBehaviour();
		pb.addSubBehaviour(getBuildTourBehaviour());
		pb.addSubBehaviour(getReceiveMessageBehaviour());
		addBehaviour(pb);
	}

	protected void takeDown() {
		deregister(this);
		System.out.println("Tour Guide agent " + getAID().getName() + " terminating.");
	}

	protected Behaviour getBuildTourBehaviour() {
		return new CyclicBehaviour(this) {
			public void onStart() {
				System.out.println("CyclicBehaviour starting...");
			}

			public void action() {
				DFAgentDescription template = new DFAgentDescription(); 
				ServiceDescription sd = new ServiceDescription(); 
				sd.setType("curator");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);

					int foundCurators = result.length;
					if (foundCurators != curatorNumber) {
						// Update the list of curators
						curators = new AID[result.length];
						for (int i = 0; i < result.length; i++) {
							curators[i] = result[i].getName();
						}
						// Update the current curator number
						curatorNumber = foundCurators;

						System.out.printf("Found %d curator agents\n", curatorNumber);
					}
				} catch (FIPAException fe) {
                    fe.printStackTrace();
				}
			}
		};
	}

	protected Behaviour getReceiveMessageBehaviour() {
		return new MsgReceiver(this, null, 10000, null, null) {
			protected void handleMessage(ACLMessage msg) {
				// Do something with the message
			}

			public int onEnd() {
				System.out.println("MsgReceiver ending...");
				return 0;
			}
		};
	}
}
package src;

import java.util.List;
import java.util.ArrayList;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.proto.states.MsgReceiver;
import jade.lang.acl.ACLMessage;
import src.MuseumAgent;
import src.Artifact;
import src.ParallelTourGuideBehaviour;

public class TourGuideAgent extends MuseumAgent {
	private List<Artifact> artifacts;

	protected void setup() {
		artifacts = new ArrayList<Artifact>();
		System.out.println("Tour Guide Agent " + getAID().getName() + " successfully initialized");
		ParallelBehaviour pb = new ParallelTourGuideBehaviour();
		pb.addSubBehaviour(getBuildTourBehaviour());
		pb.addSubBehaviour(getReceiveMessageBehaviour());
		addBehaviour(pb);
	}

	protected void takeDown() {
		System.out.println("Tour Guide agent " + getAID().getName() + " terminating.");
	}

	protected Behaviour getBuildTourBehaviour() {
		return new CyclicBehaviour(this) {
			public void onStart() {
				System.out.println("CyclicBehaviour starting...");
			}

			public void action() {
				// Retrieve information and modify artifacts consequently
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
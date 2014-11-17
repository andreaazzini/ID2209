package src;

import jade.core.behaviours.WakerBehaviour;
import src.MuseumAgent;

public class CuratorAgent extends MuseumAgent {
	protected void setup() {
		System.out.println("Curator Agent " + getAID().getName() + " successfully initialized");
		addBehaviour(new WakerBehaviour(this, 60000) {
			boolean messageReceived = false;

			protected void onWake() {
				// set messageReceived = true if message received
				if (!messageReceived) {
					this.reset(60000);
				} else {
					// send information to the waker
				}
			}
		});
	}

	protected void takeDown() {
		System.out.println("Curator agent " + getAID().getName() + " terminating.");
	}
}
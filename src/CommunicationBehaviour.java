package src;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CompositeBehaviour;
import jade.util.leap.Collection;
import jade.util.leap.List;
import jade.util.leap.ArrayList;
import src.ProfilerAgent;
import src.Menu;

public class CommunicationBehaviour extends CompositeBehaviour {
	private List children;
	private int step;
	private MuseumAgent agent;

	public CommunicationBehaviour(MuseumAgent agent) {
		super(agent);
		this.agent = agent;
		children = new ArrayList();
	}

	public Collection getChildren() {
		return children;
	}

	public Behaviour getCurrent() {
		System.out.println(step);
		return (Behaviour) children.get(step);
	}

	protected boolean checkTermination(boolean currentDone, int currentResult) {
		return currentDone && step == 2;
	}

	protected void scheduleFirst() {
		step = 0;
	}

	protected void scheduleNext(boolean currentDone, int currentResult) {
		if (currentDone) {
			System.out.println("NEXT");
			step++;
		}
	}

	public int onEnd() {
		if (agent instanceof ProfilerAgent) {
			new Menu(agent).display();
		}

		return 0;
	}

	public void setSendingBehaviour(Behaviour sendingBehaviour) {
		children.add(sendingBehaviour);
	}

	public void setReceivingBehaviour(Behaviour receivingBehaviour) {
		children.add(receivingBehaviour);
	}
}
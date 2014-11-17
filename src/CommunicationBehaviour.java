package src;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CompositeBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.util.leap.Collection;
import jade.util.leap.List;
import jade.util.leap.ArrayList;
import src.ProfilerAgent;
import src.Menu;

public class CommunicationBehaviour extends CompositeBehaviour {
	private List children;
	private int currentIndex;
	private MuseumAgent agent;

	public CommunicationBehaviour(MuseumAgent agent) {
		super(agent);
		this.agent = agent;
		children = new ArrayList();
		children.add(new OneShotBehaviour(agent) {
			public void action() {
				System.out.println("Retrieving information...");
			}
		});
		children.add(new OneShotBehaviour(agent) {
			public void action() {
				System.out.println("Receiving information...");
			}
		});
	}

	public Collection getChildren() {
		return children;
	}

	public Behaviour getCurrent() {
		return (Behaviour) children.get(currentIndex);
	}

	protected boolean checkTermination(boolean currentDone, int currentResult) {
		return currentIndex == children.size() - 1;
	}

	protected void scheduleFirst() {
		currentIndex = 0;
	}

	protected void scheduleNext(boolean currentDone, int currentResult) {
		currentIndex++;
	}

	public int onEnd() {
		new Menu(agent).display();

		return 0;
	}
}
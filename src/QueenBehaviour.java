package src;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class QueenBehaviour extends CyclicBehaviour {
	private int index;

	public QueenBehaviour(int index) {
		super();
		this.index = index;
	}

	public void action() {
		ACLMessage msg = myAgent.receive();

		if (msg != null) {
			if (msg.getContent().equals("position")) {
				Integer column = ((Queen)myAgent).findColumn();
				ACLMessage position = new ACLMessage(ACLMessage.INFORM);
				position.setSender(myAgent.getAID());
				position.setContent("position");
				if (column != null) {
					System.out.printf("Found the position (%d, %d)\n", index, column);
					Queen.getOccupiedColumns().add(column);
					AID receiver = ((Queen)myAgent).getNext();
					if (receiver != null) {
						position.addReceiver(receiver);
					} else {
						ACLMessage takedown = new ACLMessage(ACLMessage.INFORM);
						takedown.setSender(myAgent.getAID());
						takedown.addReceiver(((Queen)myAgent).getPrevious());
						takedown.setContent("takedown");
						((Queen)myAgent).printBoard();
						myAgent.send(takedown);
						myAgent.doDelete();
					}
				} else {
					System.out.println("Impossible to find the position");
					position.addReceiver(((Queen)myAgent).getPrevious());
				}
				myAgent.send(position);
			} else if (msg.getContent().equals("takedown")) {
				ACLMessage takedown = new ACLMessage(ACLMessage.INFORM);
				takedown.setSender(myAgent.getAID());
				takedown.addReceiver(((Queen)myAgent).getPrevious());
				takedown.setContent("takedown");
				myAgent.send(takedown);
				myAgent.doDelete();
			}
		}
	}
}
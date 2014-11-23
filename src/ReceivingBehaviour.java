package src;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceivingBehaviour extends SimpleBehaviour {
	
	public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			System.out.println("Received reply: " + msg.getContent() + 
			" from " + msg.getSender());
			
			String[] infos = msg.getContent().split(" ");
			if (infos[0].equals("tell")) {
				ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
				reply.setSender(myAgent.getAID());
				reply.addReceiver(msg.getSender());
				
				String info = "";
				for (int i = 1; i < infos.length; i++) {
					info = new String(info + infos[i] + " ");
				}

				reply.setContent("info " + info);
				myAgent.send(reply);
			} else if (infos[0].equals("fail")) {
				((ProfilerAgent)myAgent).getMenu().display();
			}
		} else { 
			this.block();
		}
	}

	public boolean done() {
		return false; 
	}
}
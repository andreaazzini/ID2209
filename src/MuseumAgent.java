package src;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;

public abstract class MuseumAgent extends Agent {
	protected void register(MuseumAgent agent, String type) {
		DFAgentDescription dfd = new DFAgentDescription(); 
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType(type);
		sd.setName("JADE-" + type);
		dfd.addServices(sd);
		try {
			DFService.register(agent, dfd); 
		} catch (FIPAException fe) { 
			fe.printStackTrace();
		}
	}

	protected void register(MuseumAgent agent, String type, String name) {
		DFAgentDescription dfd = new DFAgentDescription(); 
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType(type);
		sd.setName(name);
		dfd.addServices(sd);
		try {
			DFService.register(agent, dfd); 
		} catch (FIPAException fe) { 
			fe.printStackTrace();
		}
	}

	protected void deregister(MuseumAgent agent) {
		try {
			DFService.deregister(agent); 
		} catch (FIPAException fe) {
     		fe.printStackTrace();
		}
	}
}
package src;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import java.util.List;
import java.util.ArrayList;
import src.DutchAuctioneerBehaviour;
import src.MuseumAgent;

public class ArtistManagerAgent extends MuseumAgent {
	private List<AID> availableCurators;
	private static final int INITIALPRICE = 100000;

	protected void setup() {
		availableCurators = new ArrayList<AID>();
		register(this, "artistmanager");
		getAvailableCurators();
		if (availableCurators.size() >= 3) {
			System.out.println("Artist Manager Agent " + getAID().getName() + " successfully initialized");
			addBehaviour(new DutchAuctioneerBehaviour(availableCurators, INITIALPRICE));
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
}
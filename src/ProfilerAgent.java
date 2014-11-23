package src;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import src.User;
import src.Menu;
import src.MuseumAgent;
import src.CommunicationBehaviour;
import src.ReceivingBehaviour;

public class ProfilerAgent extends MuseumAgent {
	private User user;
	private Menu menu;
	private List<AID> availableTours;

	protected void setup() {
		register(this, "profiler");
		menu = new Menu(this);
		availableTours = new ArrayList<AID>();
		System.out.println("Profiler Agent " + getAID().getName() + " successfully initialized");
		menu.display();
	}

	protected void takeDown() {
		deregister(this);
		System.out.println("Profiler agent " + getAID().getName() + " terminating.");
	}

	protected void registerUser() {
		addBehaviour(new OneShotBehaviour(this) {
			public void action() {
				Scanner scanner = new Scanner(System.in);
				System.out.print("Name: ");
				String name = scanner.next();
				System.out.print("Surname: ");
				String surname = scanner.next();
				System.out.print("Age: ");
				int age = scanner.nextInt();
				System.out.print("Occupation: ");
				String occupation = scanner.next();
				System.out.print("Male (true) or female (false): ");
				boolean gender = scanner.nextBoolean();
				user = new User(name, surname, age, occupation, gender);
				System.out.println("User's information has been stored");
				menu.display();
			}
		});
	}

	protected void startTour() {
		updateAvailableTours();
		if (availableTours.size() > 0) {
			addBehaviour(sendingBehaviour(availableTours));
		} else {
			System.out.println("No tour available");
			menu.display();
		}
	}

	private Behaviour sendingBehaviour(List<AID> availableTours) {
		return new OneShotBehaviour(this) {
			public void action() {
				System.out.println("Sending message to the first available tour...");
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				message.setSender(myAgent.getAID());
				message.addReceiver(availableTours.get(0));
				message.setContent("start");
				send(message);

				ACLMessage msg;
				do {
					msg = receive();
				} while (msg == null);
				
				if (msg != null) {
					System.out.println("Received reply: " + msg.getContent() + 
						" from " + msg.getSender());
				
					String[] infos = msg.getContent().split(" ");
					if (infos[0].equals("fail")) {
						System.out.println("Failed to receive a response");
						menu.display();
					} else if (infos[0].equals("info")) {
						System.out.println("Information acquired");
						menu.display();
					}
				}
			}
		};
	}

	private void updateAvailableTours() {
		DFAgentDescription template = new DFAgentDescription(); 
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("tourguide");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);

			for (int i = 0; i < result.length; i++) {
				availableTours.add(result[i].getName());
			}
		} catch (FIPAException fe) {
            fe.printStackTrace();
		}
	}

	protected Menu getMenu() {
		return menu;
	}
}
package src;

import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import java.util.Scanner;
import src.User;
import src.Menu;
import src.MuseumAgent;
import src.CommunicationBehaviour;

public class ProfilerAgent extends MuseumAgent {
	private User user;
	private Menu menu;

	protected void setup() {
		menu = new Menu(this);
		System.out.println("Profiler Agent " + getAID().getName() + " successfully initialized");

		DFAgentDescription dfd = new DFAgentDescription(); 
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("profiler");
		sd.setName("JADE-profiler");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd); 
		} catch (FIPAException fe) { 
			fe.printStackTrace();
		}
		
		menu.display();
	}

	protected void takeDown() {
		try {
			DFService.deregister(this); 
		} catch (FIPAException fe) {
     		fe.printStackTrace();
		}

		System.out.println("Profiler agent " + getAID().getName() + " terminating.");
	}

	protected void register() {
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
		addBehaviour(new OneShotBehaviour(this) {
			public void action() {
				DFAgentDescription template = new DFAgentDescription(); 
				ServiceDescription sd = new ServiceDescription(); 
				sd.setType("tourguide");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);

					if (result.length > 0) {
						Scanner sc = new Scanner(System.in);

						System.out.println("AVAILABLE TOURS");
						for (int i = 0; i < result.length; i++) {
							System.out.printf("%d) %s\n", i + 1, result[i].getName());
						}

						System.out.print("Choose the tour: ");
						int choice = sc.nextInt();

						try {
							System.out.printf("You have chosen tour %s.\n", result[choice - 1]);
						} catch (ArrayIndexOutOfBoundsException e) {
							System.out.println("The chosen tour does not exist");
						}
						
					} else {
						System.out.println("No tours available");
					}

					menu.display();
				} catch (FIPAException fe) {
		            fe.printStackTrace();
				}
			}
		});
	}

	protected void obtainInfo() {
		addBehaviour(new CommunicationBehaviour(this));
	}
}
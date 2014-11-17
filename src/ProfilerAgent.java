package src;

import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
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
		menu.display();
	}

	protected void takeDown() {
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
		addBehaviour(new CommunicationBehaviour(this));
	}

	protected void obtainInfo() {
		final int maxTicks = 5;
		addBehaviour(new TickerBehaviour(this, 5000) {
			protected void onTick() {
				if (this.getTickCount() < maxTicks)
					System.out.println("Retrieving interesting information...");
				else {
					this.stop();
					menu.display();
				}
			}
		});
	}
}
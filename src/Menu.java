package src;

import java.util.Scanner;
import src.MuseumAgent;
import src.ProfilerAgent;

public class Menu {
	private Scanner sc;
	private ProfilerAgent agent;

	public Menu(MuseumAgent agent) {
		sc = new Scanner(System.in);
		this.agent = (ProfilerAgent) agent;
	}

	public void display() {
		System.out.println("COMMAND LINE PROFILER AGENT MENU");
		System.out.println("1) Register");
		System.out.println("2) Start Tour");
		System.out.println("0) Quit");
		System.out.print("\nYour choice: ");
		int choice = sc.nextInt();
		choose(choice);
	}

	private void choose(int choice) {
		switch (choice) {
			case 1:
				agent.registerUser();
				break;
			case 2:
				agent.startTour();
				break;
			case 0:
				agent.doDelete();
				break;
		}
	}
}
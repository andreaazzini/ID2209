package src;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import java.util.ArrayList;
import java.util.List;

public class Queen extends MuseumAgent {
	private int row;
	private int n;
	private AID prev;
	private AID next;
	private static List<Integer> occupiedColumns;
	private static AID[] queens;

	protected void setup() {
		row = Integer.parseInt((String)getArguments()[0]);
		n = Integer.parseInt((String)getArguments()[1]);
		queens = new AID[n];
		occupiedColumns = new ArrayList<Integer>();
		register(this, "queen");
		System.out.println("Queen " + getAID().getName() + " successfully initialized");
		addBehaviour(new Behaviour() {
			public void action() {
				DFAgentDescription template = new DFAgentDescription(); 
				ServiceDescription sd = new ServiceDescription(); 
				sd.setType("queen");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					if (result.length == n) {
						queens[row] = getAID();
					}
				} catch (FIPAException fe) {
		            fe.printStackTrace();
				}
			}

			public boolean done() {
				for (AID queen : queens) {
					if (queen == null) {
						return false;
					}
				}
				return true;
			}

			public int onEnd() {
				for (int i = 0; i < n; i++) {
					if (queens[i].equals(getAID())) {
						if (i > 0) {
							prev = queens[i - 1];
						} else {
							prev = null;
						}

						if (i < n - 1) {
							next = queens[i + 1];
						} else {
							next = null;
						}
					}
				}
				
				return 0;
			}
		});

		addBehaviour(new QueenBehaviour(row));
	}

	protected void takeDown() {
		deregister(this);
		System.out.println("Queen " + getAID().getName() + " terminating.");
	}

	public Integer findColumn() {
		for (Integer i = 0; i < n; i++) {
			int size = occupiedColumns.size();
			if (size == row) {
				if (!occupiedColumns.contains(i)) {
					if (!conflictsDiagonally(i, occupiedColumns)) {
						return i;
					}
				}
			} else {
				if (i <= occupiedColumns.get(row)) {
					continue;
				} else {
					List<Integer> temp = new ArrayList<Integer>();
					for (int j = 0; j < row; j++) {
						temp.add(occupiedColumns.get(j));
					}
					if (!temp.contains(i)) {
						if (!conflictsDiagonally(i, temp)) {
							for (int k = 0; k < size - row; k++) {
								occupiedColumns.remove(size - k - 1);
							}
							return i;
						}
					}
				}
			}
		}
		return null;
	}

	public boolean conflictsDiagonally(Integer column, List<Integer> columns) {
		for (Integer occupiedColumn : columns) {
			Integer occupiedRow = new Integer(columns.indexOf(occupiedColumn));
			if (occupiedRow - row == occupiedColumn - column) {
				return true;
			} else if (occupiedRow - row == column - occupiedColumn) {
				return true;
			}
		}
		return false;
	}

	public AID getPrevious() {
		return prev;
	}

	public AID getNext() {
		return next;
	}

	public static List<Integer> getOccupiedColumns() {
		return occupiedColumns;
	}

	public void printBoard() {
		System.out.println();
		for (int i = 0; i < n; i++) {
			System.out.print("|");
			for (int j = 0; j < n; j++) {
				if (j == occupiedColumns.get(i)) {
					System.out.print("*|");
				} else {
					System.out.print(" |");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Country {

	private String name;
	private Set<Territory> territories;
	private String path;
	private int balance;

	public Country(String name, String path) {
		this.name = name;
		this.path = path;
		this.territories = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public Set<Territory> getTerritories() {
		return territories;
	}

	public void addTerritory(Territory territory) {
		if (territories.contains(territory))
			throw new IllegalStateException(this + " already contains " + territory);
		territories.add(territory);
		territory.setOwner(this);
	}

	public void removeTerritory(Territory territory) {
		if (!territories.contains(territory))
			throw new IllegalStateException(this + " does not contain " + territory);
		territories.remove(territory);
	}

	public int getBalance() {
		return balance;
	}

	public int getIncome() {
		if (!isOccupied()) {
			int income = 0;
			for (Territory territory : territories)
				income += territory.getValue();
			return income;
		} else {
			return 0;
		}
	}

	public void collectIncome() {
		balance += getIncome();
	}

	public int getVictoryPoints() {
		int points = 0;
		for (Territory territory : territories)
			if (territory.isVictoryCity())
				points++;
		return points;
	}

	public boolean isOccupied() {
		for (Territory territory : territories)
			if (territory.isCapital() && territory.getOriginalOwner() == this)
				return territory.isCaptured();
		return false;
	}

	public String toString() {
		return name;
	}

	public String getSummary() {
		String result = getName() + " has " + getVictoryPoints();
		if (!isOccupied()) {
			result += ", " + getBalance() + " IPCs in the bank, and an income of " + getIncome() + ".";
		} else {
			result += " and is occupied.";
		}
		return result;
	}

	public void save(PrintStream stream) {
		List<String> territoryNames = new LinkedList<>();
		for (Territory territory : territories)
			territoryNames.add(territory.getName());
		territoryNames.sort(null);
		for (String name : territoryNames)
			stream.println(name);
	}

}

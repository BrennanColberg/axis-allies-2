import java.awt.Color;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Country {

	private String name;
	private Set<Territory> territories;
	private Alliance alliance;
	private String path;
	private int balance;
	private Color color;

	public Country(String name, String alliance, String path) {
		this(name, Alliance.fromString(alliance), path);
	}

	public Country(String name, Alliance alliance, String path) {
		this.name = name;
		this.alliance = alliance;
		this.path = path;
		this.territories = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public Alliance getAlliance() {
		return alliance;
	}

	public boolean alliedTo(Country other) {
		return this.alliance == other.alliance;
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

	public Set<Territory> getConquerableTerritories() {
		Set<Territory> result = new HashSet<>();
		for (Territory territory : territories)
			for (Territory borderingTerritory : territory.getBorderingTerritories())
				if (!this.alliedTo(borderingTerritory.getCountry()))
					result.add(borderingTerritory);
		return result;
	}

	public boolean isOccupied() {
		for (Territory territory : territories)
			if (territory.isCapital() && territory.getOriginalCountry() == this)
				return territory.isCaptured();
		return false;
	}

	public int getVictoryPoints() {
		int points = 0;
		for (Territory territory : territories)
			if (territory.isVictoryCity())
				points++;
		return points;
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

	public Color getColor() {
		return color;
	}

	public String toString() {
		return name;
	}

	public String getSummary() {
		String result = getName() + " has " + getVictoryPoints() + " victory points";
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

enum Alliance {

	Allies(), Axis();

	static Alliance fromString(String str) {
		str = str.toLowerCase();
		for (Alliance alliance : Alliance.values())
			if (str.equals(alliance.toString().toLowerCase()))
				return alliance;
		return null;
	}

}

import java.util.Set;

public class Country {

	private String name;
	private Set<Territory> territories;

	public Country(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Set<Territory> getTerritories() {
		return territories;
	}

	public void addTerritory(Territory territory) {
		if (!territories.contains(territory))
			throw new IllegalStateException(this + " does not contain " + territory);
		territories.add(territory);
	}

	public void removeTerritory(Territory territory) {
		if (territories.contains(territory))
			throw new IllegalStateException(this + " already contains " + territory);
		territories.remove(territory);
	}

	public String toString() {
		return name;
	}

}

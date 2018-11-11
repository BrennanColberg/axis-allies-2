import java.util.HashSet;
import java.util.Set;

public class Country {

	private String name;
	private Set<Territory> territories;
	private String path;

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

	public String toString() {
		return name;
	}

}

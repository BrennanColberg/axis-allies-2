import java.util.HashSet;
import java.util.Set;

public class Territory {

	// declared attributes
	private String name;
	private int value;
	private Type type;
	private String cityName;
	// optional
	private Country owner;
	private Country originalOwner;
	private Set<Territory> borderingTerritories;
	
	private Territory() {
		this.borderingTerritories = new HashSet<>();
	}

	public Territory(String name, int value) {
		this();
		this.name = name;
		this.value = value;
		this.type = Type.Normal;
	}

	public Territory(String name, int value, boolean capital, String cityName) {
		this();
		this.name = name;
		this.value = value;
		this.type = capital ? Type.Capital : Type.VictoryCity;
		this.cityName = cityName;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return type != Type.Neutral ? value : -1;
	}

	public String getCityName() {
		return cityName;
	}

	public boolean isNeutral() {
		return type == Type.Neutral;
	}

	public boolean isVictoryCity() {
		return type == Type.VictoryCity || type == Type.Capital;
	}

	public boolean isCapital() {
		return type == Type.Capital;
	}

	public Country getOwner() {
		return owner;
	}

	public Country getOriginalOwner() {
		return originalOwner;
	}

	public boolean isCaptured() {
		if (originalOwner == null)
			throw new IllegalStateException(this + " has never been owned!");
		return owner != originalOwner;
	}

	public void setOwner(Country owner) {
		if (this.owner != null) {
			this.owner.removeTerritory(this);
			this.owner = owner;
		} else {
			this.originalOwner = this.owner = owner;
		}
	}
	
	public Set<Territory> getBorderingTerritories() {
		return borderingTerritories;
	}
	
	public void addBorderingTerritory(Territory other) {
		this.borderingTerritories.add(other);
		other.borderingTerritories.add(this);
	}
	
	public boolean isBordering(Territory other) {
		return borderingTerritories.contains(other);
	}

	public String toString() {
		return this.isNeutral() ? name : name + " (" + value + ")";
	}

}

enum Type {
	Neutral(), Normal(), VictoryCity(), Capital();
}

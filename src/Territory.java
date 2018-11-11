import java.util.HashSet;
import java.util.Set;

public class Territory {

	// declared attributes
	private String name;
	private int value;
	private Type type;
	private String cityName;
	// optional
	private Country country;
	private Country originalCountry;
	private Set<Territory> borderingTerritories;
	private Set<Unit> units;

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

	public Country getCountry() {
		return country;
	}

	public Country getOriginalCountry() {
		return originalCountry;
	}

	public boolean isCaptured() {
		if (originalCountry == null)
			throw new IllegalStateException(this + " has never been owned!");
		return country != originalCountry;
	}

	public void setOwner(Country owner) {
		if (this.country != null) {
			this.country.removeTerritory(this);
			this.country = owner;
		} else {
			this.originalCountry = this.country = owner;
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

	public Set<Unit> getUnits() {
		return units;
	}

	public Set<Unit> getUnits(Alliance alliance) {
		Set<Unit> result = new HashSet<>();
		for (Unit unit : units)
			if (unit.getAlliance() == alliance)
				result.add(unit);
		return result;
	}

	public void addUnit(Unit unit) {
		units.add(unit);
	}

	public void removeUnit(Unit unit) {
		units.remove(unit);
	}

	public String toString() {
		switch (this.type) {
		case Neutral:
			return name;
		case Normal:
			return name + " (" + value + ")";
		case VictoryCity:
			return name + " [" + value + "]";
		case Capital:
			return name + " <" + value + ">";
		}
		return null;
	}

}

enum Type {
	Neutral(), Normal(), VictoryCity(), Capital();
}

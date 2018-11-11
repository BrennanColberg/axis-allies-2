import java.util.Collection;
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

	public Territory(String name, Type type) {
		this.name = name;
		this.type = type;
		this.borderingTerritories = new HashSet<>();
		this.units = new HashSet<>();
	}

	public Territory(String name, int value) {
		this(name, Type.Normal);
		this.value = value;
	}

	public Territory(String name, int value, boolean capital, String cityName) {
		this(name, capital ? Type.Capital : Type.VictoryCity);
		this.value = value;
		this.cityName = cityName;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return type != Type.Neutral ? value : -1;
	}

	public int getValueOfUnits() {
		int totalUnitValue = 0;
		for (Unit unit : units)
			if (!unit.getName().equals("Industrial Complex") && !unit.getName().equals("AA Gun"))
				totalUnitValue += unit.getCost();
		return totalUnitValue;
	}

	public int getDefenseStrength() {
		int totalStrength = 0;
		for (Unit unit : units)
			if (!unit.getName().equals("Industrial Complex"))
				totalStrength += unit.getDefense() + 2;
		return totalStrength;
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

	public boolean isOwnable() {
		return type == Type.Normal || type == Type.VictoryCity || type == Type.Capital;
	}

	public Country getCountry() {
		return country;
	}

	public Country getOriginalCountry() {
		return originalCountry;
	}

	public Alliance getAlliance() {
		return country.getAlliance();
	}

	public boolean isCaptured() {
		if (originalCountry == null)
			throw new IllegalStateException(this + " has never been owned!");
		return country != originalCountry;
	}

	public void setCountry(Country owner) {
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

	public void addUnits(Collection<Unit> units) {
		for (Unit unit : units)
			this.units.add(unit);
	}
	
	public void removeUnit(Unit unit) {
		units.remove(unit);
	}

	public void removeUnits(Collection<Unit> units) {
		for (Unit unit : units)
			this.units.remove(unit);
	}
	
	public String toString() {
		switch (type) {
		case Neutral:
			return name;
		case Normal:
			return name + " (" + value + ")";
		case VictoryCity:
			return name + " [" + value + "]";
		case Capital:
			return name + " <" + value + ">";
		case SeaZone:
			return "Sea Zone " + name;
		}
		return null;
	}

}

enum Type {
	SeaZone(), Neutral(), Normal(), VictoryCity(), Capital();
}

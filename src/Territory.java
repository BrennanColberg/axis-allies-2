
public class Territory {

	private String name;
	private String cityName;
	private int value;
	private Type type;
	private Country owner;

	public Territory(String name, int value) {
		this(name, value, Type.Normal);
	}

	public Territory(String name, int value, Type type) {
		this(name, value, type, null);
	}

	public Territory(String name, int value, Type type, String cityName) {
		this.name = name;
		this.value = value;
		this.type = type;
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

	public void setOwner(Country owner) {
		if (this.owner != null)
			this.owner.removeTerritory(this);
		this.owner = owner;
	}

	public String toString() {
		if (this.isNeutral()) {
			return name;
		} else {
			return name + " (" + value + ")";
		}
	}

}

enum Type {
	Neutral(), Normal(), VictoryCity(), Capital();
}

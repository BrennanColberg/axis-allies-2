import java.awt.Color;

public class Unit {

	private String name;
	private int offense;
	private int defense;
	private int travel;
	private int moves;
	private int cost;
	private Country country;
	private Territory territory;

	public Unit(String name, int offense, int defense, int travel, int cost) {
		this.name = name;
		this.offense = offense;
		this.defense = defense;
		this.moves = this.travel = travel;
		this.cost = cost;
	}

	public Unit(Unit model, Country country) {
		this(model.name, model.offense, model.defense, model.travel, model.cost);
		this.country = country;
	}

	public Unit(Unit model, Country country, Territory territory) {
		this(model, country);
		this.setTerritory(territory);
	}

	public String getName() {
		return name;
	}

	public int getOffense() {
		return offense;
	}

	public int getDefense() {
		return defense;
	}

	public int getTravel() {
		return travel;
	}

	public int getMoves() {
		return moves;
	}

	public boolean canMove() {
		return canMove(1);
	}

	public boolean canMove(int moves) {
		return moves <= this.moves;
	}

	public void resetMoves() {
		moves = travel;
	}

	public int getCost() {
		return cost;
	}

	public Country getCountry() {
		return country;
	}

	public Alliance getAlliance() {
		return country.getAlliance();
	}

	public Color getColor() {
		return country.getColor();
	}

	public Territory getTerritory() {
		return territory;
	}

	private void setTerritory(Territory territory) {
		this.territory.removeUnit(this);
		this.territory = territory;
		this.territory.addUnit(this);
	}

	public void moveTo(Territory territory) {
		// gets distance between territories
		// checks if it can move that far
		// moves to territory
		this.setTerritory(territory);
	}

}
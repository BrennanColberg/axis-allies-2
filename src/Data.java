import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

@SuppressWarnings("resource")
public class Data {

	public Map<String, Territory> territories;
	public List<Country> countries;
	public Map<Alliance, List<Country>> alliances;
	public Map<String, Unit> units;

	public Data(String path) throws FileNotFoundException {
		// load territories
		territories = loadTerritories(path);
		loadBorders(path, territories);
		// load countries
		countries = loadCountries(path);
		alliances = loadAlliances(countries);
		loadTerritoryOwnership(territories, countries);
		// load units
		units = loadUnits(path);
		loadStartingUnits(path, units, territories);
	}

	private Map<String, Territory> loadTerritories(String path) throws FileNotFoundException {
		Scanner file = new Scanner(new File(path + "territories.txt"));
		Map<String, Territory> territories = new HashMap<>();
		while (file.hasNextLine()) {
			Scanner line = new Scanner(file.nextLine());
			Territory territory;
			// get territory name
			String name = line.next();
			while (line.hasNext() && !line.hasNextInt())
				name += " " + line.next();
			// if it's a valid territory
			if (line.hasNextInt()) {
				// get value
				int value = line.nextInt();
				// see if it's a victory city
				if (line.hasNextBoolean()) {
					// see if it's a capital city
					boolean capital = line.nextBoolean();
					// get city name
					String cityName = line.next();
					while (line.hasNext())
						cityName += " " + line.next();
					// initialize
					territory = new Territory(name, value, capital, cityName);
				} else {
					// initialize
					territory = new Territory(name, value);
				}
				// if it's a sea zone
			} else {
				territory = new Territory(name, Type.SeaZone);
			}
			territories.put(territory.getName(), territory);
		}
		return territories;
	}

	private List<Country> loadCountries(String path) throws FileNotFoundException {
		Scanner file = new Scanner(new File(path + "countries.txt"));
		List<Country> countries = new LinkedList<>();
		while (file.hasNextLine()) {
			Scanner line = new Scanner(file.nextLine());
			Country country = new Country(line.next(), line.next(), path + line.next());
			countries.add(country);
		}
		return countries;
	}

	private Map<Alliance, List<Country>> loadAlliances(List<Country> countries) {
		Map<Alliance, List<Country>> alliances = new HashMap<>();
		for (Country country : countries) {
			Alliance alliance = country.getAlliance();
			if (!alliances.containsKey(alliance))
				alliances.put(alliance, new LinkedList<>());
			alliances.get(alliance).add(country);
		}
		return alliances;
	}

	private Map<String, Unit> loadUnits(String path) throws FileNotFoundException {
		Scanner file = new Scanner(new File(path + "units.txt"));
		Map<String, Unit> units = new HashMap<>();
		while (file.hasNextLine()) {
			Scanner line = new Scanner(file.nextLine());
			String name = line.next();
			while (line.hasNext() && !line.hasNextInt())
				name += " " + line.next();
			int offense = line.nextInt();
			int defense = line.nextInt();
			int travel = line.nextInt();
			int cost = line.nextInt();
			Unit unit = new Unit(name, offense, defense, travel, cost);
			units.put(unit.getName(), unit);
		}
		return units;
	}

	private void loadTerritoryOwnership(Map<String, Territory> territories, List<Country> countries)
			throws FileNotFoundException {
		for (Country country : countries) {
			Scanner file = new Scanner(new File(country.getPath()));
			while (file.hasNextLine()) {
				String name = file.nextLine();
				if (territories.containsKey(name)) {
					country.addTerritory(territories.get(name));
				} else {
					throw new IllegalArgumentException("\"" + name + "\" is not a valid territory!");
				}
			}
			// starting income
			country.collectIncome();
		}
		for (Territory territory : territories.values())
			if (territory.getCountry() == null && territory.isOwnable())
				throw new IllegalStateException(territory.getName() + " is unowned!");
	}

	private void loadBorders(String path, Map<String, Territory> territories) throws FileNotFoundException {
		Scanner file = new Scanner(new File(path + "borders.txt"));
		while (file.hasNextLine()) {
			String[] names = file.nextLine().split("\\|");
			names[0] = names[0].trim();
			names[1] = names[1].trim();
			if (territories.containsKey(names[0]) && territories.containsKey(names[1]))
				territories.get(names[0]).addBorderingTerritory(territories.get(names[1]));
		}
	}

	private void loadStartingUnits(String path, Map<String, Unit> units, Map<String, Territory> territories)
			throws FileNotFoundException {
		Scanner file = new Scanner(new File(path + "starting-units.txt"));
		while (file.hasNextLine()) {
			Scanner line = new Scanner(file.nextLine());
			String territoryName = line.next();
			while (line.hasNext() && !line.hasNextInt())
				territoryName += " " + line.next();
			Territory territory = territories.get(territoryName);
			if (territory.getCountry() == null)
				throw new IllegalArgumentException(territory + " has no country!");
			while (line.hasNextInt()) {
				int quantity = line.nextInt();
				String unitName = line.next();
				while (line.hasNext() && !line.hasNextInt())
					unitName += " " + line.next();
				if (!units.containsKey(unitName))
					throw new IllegalArgumentException("\"" + unitName + "\" is not a valid unit!");
				Unit prototype = units.get(unitName);
				for (int i = 0; i < quantity; i++)
					new Unit(prototype, territory);
			}
		}
	}

	/**
	 * Calculates the approximate number of infantry needed to conquer 50% of the
	 * time (decimal value).
	 * 
	 * @param territory territory to test defenses of
	 * @return the calculated infantry quotient
	 */
	public double calculateInfantryQuotient(Territory territory) {
		int infantry = 1;
		double lastPercentage = 0;
		double percentage = 0;
		while ((percentage = calculateWinPercentage(territory, infantry)) < 0.5) {
			lastPercentage = percentage;
			infantry++;
		}
		double difference = percentage - lastPercentage;
		double offset = 0.5 - lastPercentage;
		return infantry - 1 + offset / difference;
	}

	private double calculateWinPercentage(Territory territory, int infantry) {

		// set up infantry to attack
		Set<Unit> offense = new HashSet<>();
		for (int i = 0; i < infantry; i++)
			offense.add(new Unit(units.get("Infantry"), territory.getAlliance().other()));
		territory.addUnits(offense);

		// calculate 1000 wins
		int offenseWins = 0;
		int runs = 200;
		for (int i = 0; i < runs; i++) {
			Alliance winner = Battlefield.testCombat(territory, false);
			if (winner == territory.getAlliance().other()) {
				offenseWins++;
			}
		}

		// retreat
		territory.removeUnits(offense);

		return (double) offenseWins / runs;

	}

}

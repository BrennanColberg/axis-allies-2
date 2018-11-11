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
public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		// get folder
		Scanner console = new Scanner(System.in);
		System.out.print("What version are you playing? ");
		String path = console.nextLine() + "/";
		// load territories
		Map<String, Territory> territories = loadTerritories(path);
		loadBorders(path, territories);
		// load countries
		List<Country> countries = loadCountries(path);
		//Map<Alliance, List<Country>> alliances = loadAlliances(countries);
		loadTerritoryOwnership(territories, countries);
		// load units
		Map<String, Unit> units = loadUnits(path);
		loadStartingUnits(path, units, territories);

		Territory germany = territories.get("Germany");
		Set<Unit> attacking = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			attacking.add(new Unit(units.get("Infantry"), countries.get(4)));
		}
		germany.addUnits(attacking);
		Battlefield.conductCombat(germany, countries.get(4));
		
	}

	private static Map<String, Territory> loadTerritories(String path) throws FileNotFoundException {
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

	private static List<Country> loadCountries(String path) throws FileNotFoundException {
		Scanner file = new Scanner(new File(path + "countries.txt"));
		List<Country> countries = new LinkedList<>();
		while (file.hasNextLine()) {
			Scanner line = new Scanner(file.nextLine());
			Country country = new Country(line.next(), line.next(), path + line.next());
			countries.add(country);
		}
		return countries;
	}

	private static Map<Alliance, List<Country>> loadAlliances(List<Country> countries) {
		Map<Alliance, List<Country>> alliances = new HashMap<>();
		for (Country country : countries) {
			Alliance alliance = country.getAlliance();
			if (!alliances.containsKey(alliance))
				alliances.put(alliance, new LinkedList<>());
			alliances.get(alliance).add(country);
		}
		return alliances;
	}

	private static Map<String, Unit> loadUnits(String path) throws FileNotFoundException {
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

	private static void loadTerritoryOwnership(Map<String, Territory> territories, List<Country> countries)
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

	private static void loadBorders(String path, Map<String, Territory> territories) throws FileNotFoundException {
		Scanner file = new Scanner(new File(path + "borders.txt"));
		while (file.hasNextLine()) {
			String[] names = file.nextLine().split("\\|");
			names[0] = names[0].trim();
			names[1] = names[1].trim();
			if (territories.containsKey(names[0]) && territories.containsKey(names[1]))
				territories.get(names[0]).addBorderingTerritory(territories.get(names[1]));
		}
	}

	private static void loadStartingUnits(String path, Map<String, Unit> units, Map<String, Territory> territories)
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

}

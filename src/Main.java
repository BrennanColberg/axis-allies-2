import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		String path = "1942/";
		// load in territories
		Map<String, Territory> territories = loadTerritories(path);
		loadBorders(path, territories);
		// load in countries
		List<Country> countries = loadCountries(path);
		Map<Alliance, List<Country>> alliances = loadAlliances(countries);
		loadTerritoryOwnership(territories, countries);
		// load in units
		Map<String, Unit> units = loadUnits(path);
		loadStartingUnits(path, units, territories);

		// country analysis
		for (Country country : countries) {
			Map<String, Integer> unitCount = new HashMap<>();
			int value = 0;
			for (String name : units.keySet())
				unitCount.put(name, 0);
			for (Territory territory : country.getTerritories())
				for (Unit unit : territory.getUnits()) {
					unitCount.put(unit.getName(), unitCount.get(unit.getName()) + 1);
					value += unit.getCost();
				}
			for (String name : unitCount.keySet())
				if (unitCount.get(name) > 0)
					System.out.println(country.getName() + " has " + unitCount.get(name) + " of " + name);
			System.out.println(country.getName() + " has " + value + " IPCs worth of units.");
			System.out.printf("%s starts with %.1f turns' worth of units.\n", country.getName(),
					(double) value / country.getIncome());
			System.out.println();
		}

		// soft underbelly quotient
		Map<Territory, Double> hardness = new HashMap<>();
		for (Territory territory : territories.values())
			if (territory.getValue() != 0)
				for (Territory border : territory.getBorderingTerritories())
					if (territory.getCountry() != null && border.getCountry() != null
							&& !territory.getCountry().alliedTo(border.getCountry())) {
						int value = territory.getValue();
						int unitValue = territory.getValueOfUnits();
						double strength = territory.getDefenseStrength();
						hardness.put(territory, (double) strength);
					}
		List<Entry<Territory, Double>> order = new LinkedList<>(hardness.entrySet());
		order.sort(Entry.comparingByValue());
		for (Entry<Territory, Double> entry : order)
			System.out.printf("%.0f | %s\n", entry.getValue(), entry.getKey());

	}

	@SuppressWarnings("resource")
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

	@SuppressWarnings("resource")
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

	@SuppressWarnings("resource")
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

	@SuppressWarnings("resource")
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

	@SuppressWarnings("resource")
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

	@SuppressWarnings("resource")
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

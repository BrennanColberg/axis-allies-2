import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		String path = "1942/";
		List<Country> countries = loadCountries(path);
		Map<String, Territory> territories = loadTerritories(path);
		loadTerritoryOwnership(territories, countries);
		
		for (Country country : countries) {
			System.out.printf("%s has %d victory points, %d IPCs in the bank, and an income of %d.\n",
					country.getName(),
					country.getVictoryPoints(),
					country.getBalance(),
					country.getIncome());
		}
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
			Country country = new Country(line.next(), path + line.next());
			countries.add(country);
		}
		return countries;
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
			if (territory.getOwner() == null)
				throw new IllegalStateException(territory.getName() + " is unowned!");
	}

}

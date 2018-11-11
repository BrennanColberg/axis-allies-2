import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

	private static List<Country> playOrder = new LinkedList<>();
	// lowercase name -> Country
	private static Map<String, Country> countries = new HashMap<>();
	private static Set<Territory> territories = new HashSet<>();

	public static void main(String[] args) throws FileNotFoundException {
		loadCountries("1942/");
		System.out.println(playOrder);
		loadTerritories("1942/");
		System.out.println(territories);
	}

	@SuppressWarnings("resource")
	private static void loadCountries(String path) throws FileNotFoundException {
		Scanner file = new Scanner(new File(path + "countries.txt"));
		while (file.hasNextLine()) {
			Scanner line = new Scanner(file.nextLine());
			Country country = new Country(line.next(), path + line.next());
			playOrder.add(country);
			countries.put(country.getName().toLowerCase(), country);
		}
	}

	@SuppressWarnings("resource")
	private static void loadTerritories(String path) throws FileNotFoundException {
		Scanner file = new Scanner(new File(path + "territories.txt"));
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

				territory = new Territory(name, value, capital, cityName);
			} else {
				territory = new Territory(name, value);
			}

			territories.add(territory);

		}
	}

}

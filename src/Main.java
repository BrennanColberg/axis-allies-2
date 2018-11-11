import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
	
	private static List<Country> playOrder = new LinkedList<>();
	// lowercase name -> Country
	private static Map<String, Country> countries = new HashMap<>();

	public static void main(String[] args) throws FileNotFoundException {
		loadCountries("1942/");
		System.out.println(playOrder);
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
	
	private static void loadTerritories() {
		for (Country country : playOrder) {
			
			
			
		}
	}

}

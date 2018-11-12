import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@SuppressWarnings("resource")
public class Main {

	private static Data data;

	public static void main(String[] args) throws FileNotFoundException {
		data = new Data("1942/");
		Scanner console = new Scanner(System.in);
		int index = 0;
		while (true) {
			Country current = data.countries.get(index);

			// purchase units
			
			
			// combat move
			
			
			// conduct combat
			
			
			// noncombat move
			
			
			// placing units
			
			
			// next country
			index++;
			if (index == 6)
				index = 0;
		}
	}

	public void printInfantryQuotientOfTerritory(String territoryName) {
		Territory territory = data.territories.get(territoryName);
		double quotient = data.calculateInfantryQuotient(territory);
		System.out.println("IQ: " + quotient);
	}

	public static void printInfantryQuotientOfArmy(Scanner console) {
		Set<Unit> defense = queryForUnits(console, Alliance.Allies, "Defense");
		Territory territory = new Territory();
		territory.addUnits(defense);
		territory.setCountry(data.countries.get(0));
		double quotient = data.calculateInfantryQuotient(territory);
		System.out.println("IQ: " + quotient);
	}

	public static void conductConsoleCombat(Scanner console) {
		Set<Unit> offense = queryForUnits(console, Alliance.Allies, "Offense");
		Set<Unit> defense = queryForUnits(console, Alliance.Axis, "Defense");
		Alliance winner = Battlefield.conductCombat(offense, defense, true);
		System.out.println((winner == Alliance.Allies ? "Offense" : "Defense") + " wins!");
	}

	private static Set<Unit> queryForUnits(Scanner console, Alliance alliance, String side) {
		boolean running = true;
		Set<Unit> units = new HashSet<>();
		while (running) {
			System.out.print("Input [" + side + "]: ");
			String rawInput = console.nextLine();
			if (rawInput.trim().length() > 0) {
				Scanner input = new Scanner(rawInput);
				try {
					int count = input.nextInt();
					String unitName = input.next();
					while (input.hasNext())
						unitName += " " + input.next();
					for (int i = 0; i < count; i++)
						units.add(new Unit(data.units.get(unitName.trim()), alliance));
				} catch (Exception e) {
					System.out.println("stop being bad, you caused a " + e.getClass().getName());
				}
			} else {
				running = false;
			}
		}
		return units;
	}

}

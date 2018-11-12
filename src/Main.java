import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

public class Main {

	private static Data data;

	public static void main(String[] args) throws FileNotFoundException {
		data = new Data("1942/");

		System.out.println(calculateInfantryQuotient(data.territories.get("Borneo")));
		System.out.println(calculateInfantryQuotient(data.territories.get("Germany")));
		System.out.println(calculateInfantryQuotient(data.territories.get("Anglo-Egypt Sudan")));

	}

	/**
	 * Calculates the approximate number of infantry needed to conquer 50% of the
	 * time (decimal value).
	 * 
	 * @param territory territory to test defenses of
	 * @return the calculated infantry quotient
	 */
	private static double calculateInfantryQuotient(Territory territory) {
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

	private static double calculateWinPercentage(Territory territory, int infantry) {

		// set up infantry to attack
		Set<Unit> offense = new HashSet<>();
		for (int i = 0; i < infantry; i++)
			offense.add(new Unit(data.units.get("Infantry"), territory.getAlliance().other()));
		territory.addUnits(offense);

		// calculate 1000 wins
		int offenseWins = 0;
		int runs = 1000;
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

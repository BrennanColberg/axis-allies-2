import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

public class Main {
	
	private static Data data;

	public static void main(String[] args) throws FileNotFoundException {
		data = new Data("1942/");

		System.out.println(calculateWeakness(data.territories.get("Borneo")));
		System.out.println(calculateWeakness(data.territories.get("Borneo")));
		System.out.println(calculateWeakness(data.territories.get("Anglo-Egypt Sudan")));

	}
	
	// currently returns chance of winning with 10 infantry... should be # of infantry
	// needed to win a majority of the time
	private static double calculateWeakness(Territory territory) {
		
		// setup 10 infantry to attack
		Set<Unit> offense = new HashSet<>();
		for (int i = 0; i < 5; i++)
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

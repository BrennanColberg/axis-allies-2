import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("resource")
public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Data data = new Data("1942/");

		Territory germany = data.territories.get("Germany");
		Set<Unit> attacking = new HashSet<>();
		for (int i = 0; i < 2; i++)
			attacking.add(new Unit(data.units.get("Infantry"), data.countries.get(4)));
		germany.addUnits(attacking);
		int alliedWins = 0;
		int runs = 1000;
		for (int i = 0; i < runs; i++)
			if (Battlefield.testCombat(germany, false) == Alliance.Allies)
				alliedWins++;
		System.out.println("Allies win: " + alliedWins + " / " + runs + " (" + ((double) alliedWins / runs) + ")");

	}

}

import java.util.HashSet;
import java.util.Set;

public class Battlefield {

	/**
	 * Conducts combat randomly in a territory, implementing the result.
	 * 
	 * @param territory territory in which the battle takes place
	 * @param current   country currently having their turn (attacker)
	 * @param log       whether or not to log events to the console
	 * @return the alliance that wins
	 */
	public static Alliance conductCombat(Territory territory, Country current, boolean log) {
		Set<Unit> offense = territory.getUnits(territory.getAlliance().other());
		Set<Unit> defense = territory.getUnits(territory.getAlliance());
		Alliance winner = conductCombat(offense, defense, log);
		if (winner == territory.getAlliance().other())
			territory.setCountry(current);
		if (log)
			System.out.println(territory.getCountry() + " won!");
		return winner;
	}

	/**
	 * Tests one instance of the result of combat, without modifying anything.
	 * 
	 * @param territory territory in which the battle takes place
	 * @param log       whether or not to log events to the console
	 * @return the alliance that wins
	 */
	public static Alliance testCombat(Territory territory, boolean log) {
		Set<Unit> offense = new HashSet<>(territory.getUnits(territory.getAlliance().other()));
		Set<Unit> defense = new HashSet<>(territory.getUnits(territory.getAlliance()));
		Alliance winner = conductCombat(offense, defense, log);
		if (log)
			System.out.println(winner + " won!");
		return winner;
	}

	/**
	 * Performs the calculations necessary to determine the outcome of combat.
	 * 
	 * @param offense set of units, all on one alliance
	 * @param defense set of units, all on the other alliance
	 * @return the alliance that wins
	 */
	private static Alliance conductCombat(Set<Unit> offense, Set<Unit> defense, boolean log) {

		// figure out & check alliances (beforehand in case of annihilation)
		Alliance offenseAlliance = getSideAlliance(offense);
		Alliance defenseAlliance = getSideAlliance(defense);

		// go until someone loses
		while (offense != null && defense != null && !offense.isEmpty() && !defense.isEmpty()) {
			conductOneRoundOfCombat(offense, defense, log);
		}

		// final result
		return (offense == null || offense.isEmpty()) ? defenseAlliance : offenseAlliance;
	}

	private static Alliance getSideAlliance(Set<Unit> units) {
		Alliance alliance = null;
		for (Unit unit : units) {
			if (alliance == null) {
				alliance = unit.getAlliance();
			} else if (alliance != unit.getAlliance()) {
				throw new IllegalStateException("Units fighting together are not allied!");
			}
		}
		return alliance;
	}

	private static void conductOneRoundOfCombat(Set<Unit> offense, Set<Unit> defense, boolean log) {

		// roll for offense
		int offenseHits = 0;
		if (log)
			System.out.println();
		for (Unit unit : offense)
			if (rollHit(unit.getOffense(), log))
				offenseHits++;
		if (log)
			System.out.println("Offense hit " + offenseHits + " times!");

		// roll for defense
		int defenseHits = 0;
		if (log)
			System.out.println();
		for (Unit unit : defense)
			if (rollHit(unit.getDefense(), log))
				defenseHits++;
		if (log)
			System.out.println("Defense hit " + defenseHits + " times!");

		// remove casualties from defense (weakest first)
		if (log)
			System.out.println();
		for (int i = 0; i < offenseHits; i++) {
			if (!defense.isEmpty()) {
				Unit weakest = null;
				for (Unit unit : defense)
					if (weakest == null || unit.getDefense() < weakest.getDefense())
						weakest = unit;
				defense.remove(weakest);
				if (log)
					System.out.println("Eliminated " + weakest);
			}
		}

		// remove casualties from offense (weakest first)
		if (log)
			System.out.println();
		for (int i = 0; i < defenseHits; i++) {
			if (!offense.isEmpty()) {
				Unit weakest = null;
				for (Unit unit : offense)
					if (weakest == null || unit.getOffense() < weakest.getOffense())
						weakest = unit;
				offense.remove(weakest);
				if (log)
					System.out.println("Eliminated " + weakest);
			}
		}

		if (log) {
			System.out.println();
			System.out.println("---");
		}

	}

	private static boolean rollHit(int target, boolean log) {
		int roll = (int) Math.floor(Math.random() * 6) + 1;
		boolean result = roll <= target;
		if (log)
			System.out.println(target + ":" + roll + (result ? " HIT" : " MISS"));
		return result;
	}

}

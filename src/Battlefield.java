import java.util.Set;

public class Battlefield {

	/**
	 * 
	 * @param territory territory in which the battle takes place
	 * @param current   country currently having their turn (attacker)
	 */
	public static void conductCombat(Territory territory, Country current) {
		Set<Unit> offense = territory.getUnits(territory.getAlliance().other());
		Set<Unit> defense = territory.getUnits(territory.getAlliance());
		Alliance winner = conductCombat(offense, defense);
		if (winner == territory.getAlliance().other())
			territory.setCountry(current);
		System.out.println(territory.getCountry() + " won!");
	}

	/**
	 * Performs the calculations necessary to determine the outcome of combat.
	 * 
	 * @param offense set of units, all on one alliance
	 * @param defense set of units, all on the other alliance
	 * @return
	 */
	private static Alliance conductCombat(Set<Unit> offense, Set<Unit> defense) {

		// figure out & check alliances (beforehand in case of annihilation)
		Alliance offenseAlliance = getSideAlliance(offense);
		Alliance defenseAlliance = getSideAlliance(defense);

		// go until someone loses
		while (offense != null && defense != null && !offense.isEmpty() && !defense.isEmpty()) {
			conductOneRoundOfCombat(offense, defense);
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

	private static void conductOneRoundOfCombat(Set<Unit> offense, Set<Unit> defense) {

		// roll for offense
		int offenseHits = 0;
		System.out.println();
		for (Unit unit : offense)
			if (rollHit(unit.getOffense()))
				offenseHits++;
		System.out.println("Offense hit " + offenseHits + " times!");

		// roll for defense
		int defenseHits = 0;
		System.out.println();
		for (Unit unit : defense)
			if (rollHit(unit.getDefense()))
				defenseHits++;
		System.out.println("Defense hit " + defenseHits + " times!");

		// remove casualties from defense (weakest first)
		System.out.println();
		for (int i = 0; i < offenseHits; i++) {
			if (!defense.isEmpty()) {
				Unit weakest = null;
				for (Unit unit : defense)
					if (weakest == null || unit.getDefense() < weakest.getDefense())
						weakest = unit;
				defense.remove(weakest);
				System.out.println("Eliminated " + weakest);
			}
		}

		// remove casualties from offense (weakest first)
		System.out.println();
		for (int i = 0; i < defenseHits; i++) {
			if (!offense.isEmpty()) {
				Unit weakest = null;
				for (Unit unit : offense)
					if (weakest == null || unit.getOffense() < weakest.getOffense())
						weakest = unit;
				offense.remove(weakest);
				System.out.println("Eliminated " + weakest);
			}
		}

		System.out.println();
		System.out.println("---");

	}
	
	private static boolean rollHit(int target) {
		int roll = (int) Math.floor(Math.random() * 6) + 1;
		boolean result = roll <= target;
		System.out.println(target + ":" + roll + (result ? " HIT" : " MISS"));
		return result;
	}

}

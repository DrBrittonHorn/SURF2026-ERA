package tracks.levelGeneration.constraints;

public class DungeonRoguelikeConstraint extends AbstractConstraint {

	/**
	 * number of goals in the level
	 */
	public double JustOneGoal;

    /**
	 * number of keys in the level
	 */
	public double JustOneKey;
	
	/**
	 * Check if there is only one goal in the level
	 * @return	return 1 if there is only one goal
     * and the number of goals as 0.X if there are more than one
	 */
	@Override
	public double checkConstraint() {
        if (JustOneGoal == 1 && JustOneKey == 1) {
            //System.out.println("GameEndConstraint: 1");
            return 1;
        } 
        //System.out.println("GameEndConstraint: " + (JustOneGoal / 10.0));
		return (JustOneGoal + JustOneKey) / 100.0;
	}
}

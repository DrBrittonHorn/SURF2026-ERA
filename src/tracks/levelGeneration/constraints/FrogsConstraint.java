package tracks.levelGeneration.constraints;

public class FrogsConstraint extends AbstractConstraint {
	/**
	 * number of goals in the level
	 */
	public double OnlyOneGoal;
	
	/**
	 * Check if there is only one goal in the level
	 * @return	return 1 if there is only one goal
     * and the number of goals as 0.X if there are more than one
	 */
	@Override
	public double checkConstraint() {
        if (OnlyOneGoal == 1) {
            //System.out.println("GameEndConstraint: 1");
            return 1;
        } 
        //System.out.println("GameEndConstraint: " + (OnlyOneGoal / 10.0));
		return (OnlyOneGoal) / 10.0;
	}
}
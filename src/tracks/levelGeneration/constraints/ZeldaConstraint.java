package tracks.levelGeneration.constraints;

import java.util.HashMap;

import core.game.GameDescription;
import core.game.GameDescription.TerminationData;

public class ZeldaConstraint extends AbstractConstraint {
	/**
	 * number of goals in the level
	 */
	public double OnlyOneGoal;

    /**
	 * number of keys in the level
	 */
	public double OnlyOneKey;
	
	/**
	 * Check if there is only one goal in the level
	 * @return	return 1 if there is only one goal
     * and the number of goals as 0.X if there are more than one
	 */
	@Override
	public double checkConstraint() {
        if (OnlyOneGoal == 1 && OnlyOneKey == 1) {
            //System.out.println("GameEndConstraint: 1");
            return 1;
        } 
        //System.out.println("GameEndConstraint: " + (OnlyOneGoal / 10.0));
		return (OnlyOneGoal + OnlyOneKey) / 10.0;
	}
}

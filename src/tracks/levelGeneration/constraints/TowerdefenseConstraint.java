package tracks.levelGeneration.constraints;

import java.util.HashMap;

import core.game.GameDescription;
import core.game.GameDescription.TerminationData;

public class TowerdefenseConstraint extends AbstractConstraint {
	/**
	 * number of horizontal towers in the level
	 */
	public double HoriTowers;

    /**
	 * number of vertical towers in the level
	 */
	public double VertTowers;
	
	/**
	 * Check if there are at least one of each kind of tower in the level
	 * @return	return 1 if there are
     * and 0 if there are not
	 */
	@Override
	public double checkConstraint() {
        if (HoriTowers >= 1 && VertTowers >= 1) {
            return 1.0;
        } 
		return 0.0;
	}
}
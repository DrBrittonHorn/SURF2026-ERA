package tracks.levelGeneration.constraints;

public class AliensConstraint extends AbstractConstraint {
	/**
	 * number of goals in the level
	 */
	public boolean AvatarUnder;


	
	/**
	 * Check if the avatar is below the spawners
	 * @return	return 1 if it is
     * and 0 if it is not
	 */
	@Override
	public double checkConstraint() {
        System.out.println((AvatarUnder ? 1 : 0));
        return (AvatarUnder ? 1 : 0);
	}
}

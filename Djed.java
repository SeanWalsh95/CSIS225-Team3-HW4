
/**
 * Class for the Djed unit in the game khet
 * 
 * @author CSIS225 Team 3
 * 
 */
class Djed extends GamePiece{
    /**
     * Basic constructor for the Djed class
     */
    public Djed(){
        name = "Djed";
        team = "neutral";
        direction = 0;
    }
    
    /**
     * Basic constructor for the Djed class
     * 
     * @param team The name of the team that the piece belongs to
     * @param direction The initial direction the piece will be facing
     */
    public Djed(String team, int direction){
        this();
        this.team = team;
        this.direction = direction;
    }

    /**
     * Method to find how the GamePeice affects the laser
     * 
     * @param laserDirection the direction the laser is moving prior to 
	 * entering the GamePeice
     * @return the direction of the laser when exiting the game peice 
     * returns -1 if the gmaePeice is destroyed by the laser
     */
    public int getNewDirection(int laserDirection){
        if(direction == 0 && laserDirection == 0)
            return 270;
        if(direction == 0 && laserDirection == 90)
            return 180;
        if(direction == 0 && laserDirection == 180)
            return 90;
        if(direction == 0 && laserDirection == 270)
            return 0;

        if(direction == 90 && laserDirection == 0)
            return 90;
        if(direction == 90 && laserDirection == 90)
            return 0;
        if(direction == 90 && laserDirection == 180)
            return 270;
        if(direction == 90 && laserDirection == 270)
            return 180;

        if(direction == 180 && laserDirection == 0)
            return 270;
        if(direction == 180 && laserDirection == 90)
            return 180;
        if(direction == 180 && laserDirection == 180)
            return 90;
        if(direction == 180 && laserDirection == 270)
            return 0;

        if(direction == 270 && laserDirection == 0)
            return 90;
        if(direction == 270 && laserDirection == 90)
            return 0;
        if(direction == 270 && laserDirection == 180)
            return 270;
        if(direction == 270 && laserDirection == 270)
            return 180;
        return -5;
    }
}

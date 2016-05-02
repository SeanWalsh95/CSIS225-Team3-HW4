
/**
 * Class for the Pharaoh unit in the game khet
 * 
 * @author CSIS225 Team 3
 * 
 */
class Pharaoh extends GamePiece{
    /**
     * Basic constructor for the Pharaoh class
     */
    public Pharaoh(){
        name = "Pharaoh";
        team = "neutral";
        direction = 0;
    }
    
    /**
     * Basic constructor for the Pharaoh class
     * 
     * @param team The name of the team that the piece belongs to
     * @param direction The initial direction the piece will be facing
     */
    public Pharaoh(String team, int direction){
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
        return -1;
    }
}
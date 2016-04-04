
/**
 * Class for the Pharaoh unit in the game khet
 * 
 * @author CSIS225 Team 3
 */
class Obelisk extends GamePiece{
    protected boolean stacked;
    
    /**
     * Basic constructor for the Obelisk class
     */
    public Obelisk(){
        name = "Obelisk";
        team = "neutral";
        stacked = false;
        direction = 0;
    }
    
    /**
     * Basic constructor for the Obelisk class
     * 
     * @param team The name of the team that the piece belongs to
     * @param direction The initial direction the piece will be facing
     */
    public Obelisk(String team, int direction){
        this();
        this.team = team;
        this.direction = direction;
    }
    
    /**
     * Method to find how the GamePeice affects the laser
     * 
     * @param laserDirection the direction the laser is moving prior to entering the GamePeice
     * @return the direction of the laser when exiting the game peice 
     * returns -1 if the gmaePeice is destroyed by the laser
     */
    public int getNewDirection(int laserDirection){
        return -1;
    }
}


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
     * Basic constructor for the Obelisk class
     * 
     * @param team The name of the team that the piece belongs to
     * @param direction The initial direction the piece will be facing
     * @param stacked indicates if the obelisk is stakced with another obelisk
     */
    public Obelisk(String team, int direction, boolean stacked){
        this();
        this.team = team;
        this.direction = direction;
        this.stacked = stacked;
    }
    
    /**
     * Method to get the directory path of an image that represents the GamePeice
     * 
     * @return a string that represents the path of the image relative to the working directory
     */
    public String getImage(){
        if(stacked)
            return image+team+name+direction+"Stacked.png";
        else
            return image+team+name+direction+".png";
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

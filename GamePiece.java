import java.awt.*;
/**
 * Abstract class of a GamePiece for the game "Khet"
 * 
 * @author CSIS225 Team 3
 */
public abstract class GamePiece
{
    String image = "recources\\gamePieces\\";
    String name, team;
    int direction;  //direction peice is pointing

    int xPos, yPos;

    public void setXYpos(int x, int y){
        xPos = x;
        yPos = y;
    }

    public Point getDrawPoint(){
        return new Point(xPos, yPos);
    }

    public Point getCenterPoint(){
        return new Point(xPos+25, yPos+25);
    }

    /**
     * method that rotates the game peice in increments of 90
     * 
     * only allows the direction to be set to the following values (0,90,180,270)
     */
    public void rotate(){
        direction = ((((direction+90)/90)%4)*90);
    }

    
    /**
     * method that rotates the game peice 90 counterclockwise
     * 
     * only allows the direction to be set to the following values (0,90,180,270)
     */
    public void rotateLeft(){
        this.rotate();
        this.rotate();
        this.rotate();
    }
    
    /**
     * method that rotates the game peice 90 clockwise
     * 
     * only allows the direction to be set to the following values (0,90,180,270)
     */
    public void rotateRight(){
        this.rotate();
    }
    
    /**
     * Method to get the directory path of an image that represents the GamePeice
     * 
     * @return a string that represents the path of the image relative to the working directory
     */
    public String getImage(){
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
        return laserDirection;
    }
}
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * Write a description of class board here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class GameBoard extends JPanel{
    private GamePiece[][] board = new GamePiece[8][10];
    protected Laser laser = new Laser();

    public static final int tileSize = 50, borderSize = 1;

    protected String currentPlayer = "white";
    protected ArrayList<int[]> whiteOnlyTiles, redOnlyTiles;

    /**
     * Basic constructor for the GameBoard class initializes the board to the classic setup
     */
    public GameBoard(){
        whiteOnlyTiles = new ArrayList<int[]>();
        whiteOnlyTiles.add(new int[]{0,1});
        whiteOnlyTiles.add(new int[]{7,1});
        whiteOnlyTiles.add(new int[]{0,9});
        whiteOnlyTiles.add(new int[]{1,9});
        whiteOnlyTiles.add(new int[]{2,9});
        whiteOnlyTiles.add(new int[]{3,9});
        whiteOnlyTiles.add(new int[]{4,9});
        whiteOnlyTiles.add(new int[]{5,9});
        whiteOnlyTiles.add(new int[]{6,9});
        whiteOnlyTiles.add(new int[]{7,9});

        redOnlyTiles = new ArrayList<int[]>();
        redOnlyTiles.add(new int[]{0,8});
        redOnlyTiles.add(new int[]{7,8});
        redOnlyTiles.add(new int[]{0,0});
        redOnlyTiles.add(new int[]{1,0});
        redOnlyTiles.add(new int[]{2,0});
        redOnlyTiles.add(new int[]{3,0});
        redOnlyTiles.add(new int[]{4,0});
        redOnlyTiles.add(new int[]{5,0});
        redOnlyTiles.add(new int[]{6,0});
        redOnlyTiles.add(new int[]{7,0});

        setBoardClassic();
    }

    @Override
    /**
     * Paints the board and handles drawing the laser
     * 
     * @param g The graphics object for the applet
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        int topBorder = (this.getHeight()-((tileSize+borderSize)*8))/2;
        int leftBorder = (this.getWidth()-((tileSize+borderSize)*10))/2;
        for(int[] p : whiteOnlyTiles){
            g.setColor(new Color(180,180,180));
            g.fillRect(leftBorder+(p[1]*(tileSize+borderSize)), topBorder+(p[0]*(tileSize+borderSize)),tileSize,tileSize);
        }
        for(int[] p : redOnlyTiles){
            g.setColor(new Color(255,85,80));
            g.fillRect(leftBorder+(p[1]*(tileSize+borderSize)), topBorder+(p[0]*(tileSize+borderSize)),tileSize,tileSize);
        }

        for(int row=0; row < 8; row++){
            for(int col=0; col < 10; col++){
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(borderSize));
                g2.setColor(Color.black);
                g2.drawRect(leftBorder-borderSize+(col*(tileSize+borderSize)), topBorder-borderSize+(row*(tileSize+borderSize)), (tileSize+borderSize), (tileSize+borderSize));
                board[row][col].setXYpos(leftBorder+(col*(tileSize+borderSize)),topBorder+(row*(tileSize+borderSize)));
                if(!(board[row][col] instanceof NullPiece)){
                    Image gpImage = Toolkit.getDefaultToolkit().getImage(board[row][col].getImage());
                    g.drawImage(gpImage, leftBorder+(col*(tileSize+borderSize)), topBorder+(row*(tileSize+borderSize)),tileSize,tileSize, this);
                    if(board[row][col] instanceof Obelisk){
                        Obelisk ob = (Obelisk) board[row][col];
                        if(ob.stacked){
                            g.setColor(Color.BLUE); 
                            Point p = board[row][col].getCenterPoint();
                            g.fillOval((int)p.getX()-5,(int)p.getY()-5,10,10);
                        }
                    }
                }
            }
        }

        if(laser.showLaser){
            ArrayList<Point> points = getPxPointFromTileList(laser.getTraversedTiles(board,currentPlayer));
            for(int i=0; i < points.size()-1; i++){
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(
                    (int)points.get(i).getX(),
                    (int)points.get(i).getY(),
                    (int)points.get(i+1).getX(),
                    (int)points.get(i+1).getY()
                );
            }
        }
    }

    /**
     * Method that tells you the piece located at the given coordinates
     * 
     * @param row The row component of the coordinates
     * @param col The column component of the coordinates
     * 
     * @return The piece located at the given coordinates
     */
    public GamePiece getPiece(int row, int col){
        return board[row][col] ;
    }

    /**
     * Method to retrieve the current state of the board array
     * 
     * @return The current board array
     */
    public GamePiece[][] getBoard(){
        return board;
    }

    /**
     * rotates a given game peice on the board counterclockwise
     * 
     * @param row the row of the peice intended to be rotated
     * @param col the column of the peice intended to be rotated
     * @return true if successful false if not
     */
    public boolean rotatePieceLeft(int row, int col){
        if(row >= 0 && row < 8 && col >=0 && col < 10){
            board[row][col].rotateLeft();
            repaint();
            return true;
        }else{
            return false;
        }
    }

    /**
     * rotates a given game peice on the board clockwise
     * 
     * @param row the row of the peice intended to be rotated
     * @param col the column of the peice intended to be rotated
     * @return true if successful false if not
     */
    public boolean rotatePieceRight(int row, int col){
        if(row >= 0 && row < 8 && col >=0 && col < 10){
            board[row][col].rotateRight();
            repaint();
            return true;
        }else{
            return false;
        }
    }

    /**
     * Method for piece movement
     * 
     * @param tileA current coordinates of the piece
     * @param tileB destination coordinates of the piece
     */
    public void movePiece(int[] tileA, int[] tileB){
        GamePiece gpA = board[tileA[0]][tileA[1]];
        GamePiece gpB = board[tileB[0]][tileB[1]];
        board[tileA[0]][tileA[1]] = gpB;
        board[tileB[0]][tileB[1]] = gpA;
    }

    /**
     * Method for obelisk piece stacking movement
     * 
     * @param tileA current coordinates of the obelisk piece to be moved
     * @param tileB location of the stack
     */
    public void stackObelisk(int[] tileA, int[] tileB){
        board[tileA[0]][tileA[1]] = new NullPiece();
        ((Obelisk)board[tileB[0]][tileB[1]]).stacked = true;
    }

    /**
     * Method for obelisk piece unstacking movement
     * 
     * @param tileA current coordinates of the obelisk piece to be moved
     * @param tileB location of the new lone obelisk
     */
    public void unstackObelisk(int[] tileA, int[] tileB){
        GamePiece gpA = board[tileA[0]][tileA[1]];
        board[tileB[0]][tileB[1]] = new Obelisk(gpA.team,gpA.direction);
        ((Obelisk)board[tileA[0]][tileA[1]]).stacked = false;
    }

    /**
     * This method clears all pieces from the board
     */
    public void clearBoard(){
        for(int row=0; row < 8; row++){
            for(int col=0; col < 10; col++){
                board[row][col] = new NullPiece();
            }
        }
    }

    /**
     * Sets the board to the classic setup
     */
    public void setBoardClassic(){
        clearBoard();
        board[3][0] = new Pyramid("red",0);
        board[4][0] = new Pyramid("red",90);
        board[1][2] = new Pyramid("red",180);
        board[0][7] = new Pyramid("red",90);
        board[3][7] = new Pyramid("red",90);
        board[4][7] = new Pyramid("red",0);
        board[5][6] = new Pyramid("red",90);
        board[0][4] = new Obelisk("red",0);
        board[0][6] = new Obelisk("red",0);
        board[3][4] = new Djed("red",0);
        board[3][5] = new Djed("red",90);
        board[0][5] = new Pharaoh("red",0);

        board[4][9] = new Pyramid("white",180);
        board[3][9] = new Pyramid("white",270);
        board[6][7] = new Pyramid("white",0);
        board[7][2] = new Pyramid("white",270);
        board[4][2] = new Pyramid("white",270);
        board[3][2] = new Pyramid("white",180);
        board[2][3] = new Pyramid("white",270);
        board[7][5] = new Obelisk("white",0);
        board[7][3] = new Obelisk("white",0);
        board[4][4] = new Djed("white",90);
        board[4][5] = new Djed("white",0);
        board[7][4] = new Pharaoh("white",0);
    }

    /**
     * Sets the board to the Imhotep setup
     */
    public void setBoardImhotep(){
        clearBoard();
        board[3][0] = new Pyramid("red",0);
        board[4][0] = new Pyramid("red",90);
        board[2][6] = new Pyramid("red",0);
        board[5][6] = new Pyramid("red",90);
        board[3][8] = new Pyramid("red",90);
        board[4][8] = new Pyramid("red",0);
        board[4][5] = new Pyramid("red",270);
        board[0][4] = new Obelisk("red",0);
        board[0][6] = new Obelisk("red",0);
        board[3][5] = new Djed("red",90);
        board[0][7] = new Djed("red",90);
        board[0][5] = new Pharaoh("red",0);

        board[4][9] = new Pyramid("white",180);
        board[3][9] = new Pyramid("white",270);
        board[2][3] = new Pyramid("white",270);
        board[5][3] = new Pyramid("white",180);
        board[4][1] = new Pyramid("white",270);
        board[3][1] = new Pyramid("white",180);
        board[3][4] = new Pyramid("white",90);
        board[7][5] = new Obelisk("white",0);
        board[7][3] = new Obelisk("white",0);
        board[4][4] = new Djed("white",90);
        board[7][2] = new Djed("white",90);
        board[7][4] = new Pharaoh("white",0);
    }

    /**
     * Sets the board to the Dynasty setup
     */
    public void setBoardDynasty(){
        clearBoard();
        board[2][0] = new Pyramid("red",0);
        board[3][0] = new Pyramid("red",90);
        board[0][4] = new Pyramid("red",180);
        board[2][4] = new Pyramid("red",180);
        board[4][3] = new Pyramid("red",270);
        board[0][6] = new Pyramid("red",90);
        board[4][5] = new Pyramid("red",90);
        board[0][5] = new Obelisk("red",0);
        board[2][5] = new Obelisk("red",0);
        board[3][2] = new Djed("red",0);
        board[2][6] = new Djed("red",90);
        board[1][5] = new Pharaoh("red",0);

        board[5][9] = new Pyramid("white",180);
        board[4][9] = new Pyramid("white",270);
        board[3][6] = new Pyramid("white",90);
        board[5][5] = new Pyramid("white",0);
        board[7][5] = new Pyramid("white",0);
        board[7][3] = new Pyramid("white",180);
        board[3][4] = new Pyramid("white",270);
        board[4][7] = new Djed("white",0);
        board[5][3] = new Djed("white",90);
        board[7][4] = new Obelisk("white",0);
        board[5][4] = new Obelisk("white",0);
        board[6][4] = new Pharaoh("white",0);
    }

    /**
     * Allows Players to set the board
     */
    public void setBoardCustom(){
        clearBoard();
        board[7][0] = new Pharaoh("red",0);
        board[0][9] = new Pharaoh("white",0);
    }

    /**
     * Generates the ArrayList of points the laser will be drawn in
     * 
     * @param tiles contains all the tiles on the board
     * 
     * @return the coordinates the laser will be drawn on
     */
    public ArrayList<Point> getPxPointFromTileList(ArrayList<int[]> tiles){
        ArrayList<Point> pointsPx = new ArrayList<Point>();
        for(int i=0; i < tiles.size(); i++){
            int[] set = tiles.get(i);
            Point pt = board[set[0]][set[1]].getCenterPoint();
            if(set[0] == 0 && set[1] == 0){
                pt = new Point((int)pt.getX(),(int)pt.getY()-((tileSize/2)-borderSize));
            }else if(set[0] == 7 && set[1] == 9){
                pt = new Point((int)pt.getX(),(int)pt.getY()+((tileSize/2)-borderSize));
            }
            pointsPx.add(pt);
        }
        if(laser.effect.equals("missed")){
            int[] set = tiles.get(tiles.size()-1);
            int lastDir = laser.lastDirection;
            Point pt = board[set[0]][set[1]].getCenterPoint();
            if(lastDir == 0){
                pt = new Point((int)pt.getX(),(int)pt.getY()-tileSize);
            }
            if(lastDir == 90){
                pt = new Point((int)pt.getX()+tileSize,(int)pt.getY());
            }
            if(lastDir == 180){
                pt = new Point((int)pt.getX(),(int)pt.getY()+tileSize);
            }
            if(lastDir == 270){
                pt = new Point((int)pt.getX()-tileSize,(int)pt.getY());
            }
            pointsPx.add(pt);
        }
        return pointsPx;
    }

    /**
     * Removes a piece from the game board at the given coordinates
     * 
     * @param row The row component of the coordinates
     * @param col The column component of the coordinates
     */
    public void removePiece(int row, int col){
        GamePiece gp = board[row][col];
        if(gp instanceof Obelisk){
            Obelisk ob = (Obelisk) gp;
            if(ob.stacked){
                ob.stacked = false;
            }else{
                board[row][col] = new NullPiece();
            }
        }else
            board[row][col] = new NullPiece();
    }
}

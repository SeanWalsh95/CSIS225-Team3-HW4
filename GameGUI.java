import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Class GUI - write a description of the class here
 * 
 * @author (your name) 
 * @version (a version number)
 */
public class GameGUI extends JApplet
implements MouseListener, ActionListener{
    // instance variables - replace the example below with your own
    GameBoard board = new GameBoard();

    JPanel textPanel,buttonPanel, boardPanel;
    JButton laserButton, endTurnButton, moveButton, rotateLeftButton, rotateRightButton, continueButton; 
    JLabel lastPointLBL, infoLBL, currentPlayerLBL, moveUnitLBL;
    //boolean flags to determine who's move it currently is

    private boolean rotateGamePieceLeft = false, rotateGamePieceRight = false;
    private boolean redMove = false, whiteMove = true, turnEnded = false;
    private boolean movingGamePiece = false, movePointASelected = false, movePointBSelected = false;

    int[] movePointA = new int[]{-1,-1};
    int[] movePointB = new int[]{-1,-1};

    /**
     * Called by the browser or applet viewer to inform this JApplet that it
     * has been loaded into the system. It is always called before the first 
     * time that the start method is called.
     */
    public void init()
    {
        // this is a workaround for a security conflict with some browsers
        // including some versions of Netscape & Internet Explorer which do 
        // not allow access to the AWT system event queue which JApplets do 
        // on startup to check access. May not be necessary with your browser. 

        JRootPane rootPane = this.getRootPane();    
        rootPane.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);

        board.setBorder(BorderFactory.createTitledBorder(
                "This is the Game Board Panel"));

        board.currentPlayer = "white";
        lastPointLBL = new JLabel("");
        infoLBL = new JLabel("");
        currentPlayerLBL = new JLabel("Current Player: "+board.currentPlayer);
        moveUnitLBL = new JLabel("");

        textPanel = new JPanel(new FlowLayout());
        textPanel.add(lastPointLBL);
        textPanel.add(infoLBL);
        textPanel.add(currentPlayerLBL);
        textPanel.add(moveUnitLBL);

        laserButton = new JButton("Draw Laser");
        laserButton.setActionCommand("DrawLaser");
        endTurnButton = new JButton("End Turn");
        endTurnButton.setActionCommand("EndTurn");
        moveButton = new JButton("Move Peice");
        moveButton.setActionCommand("MovePeice");
        rotateLeftButton = new JButton("Rotate Left");
        rotateLeftButton.setActionCommand("RotateLeft");
        rotateRightButton = new JButton("Rotate Right");
        rotateRightButton.setActionCommand("RotateRight");
        continueButton = new JButton("Continue");
        continueButton.setActionCommand("Continue");
        continueButton.setEnabled(false);

        buttonPanel = new JPanel(new FlowLayout());
        //buttonPanel.add(laserButton);
        buttonPanel.add(rotateLeftButton);
        buttonPanel.add(rotateRightButton);
        buttonPanel.add(moveButton);
        buttonPanel.add(endTurnButton);
        buttonPanel.add(continueButton);

        this.add(buttonPanel,BorderLayout.NORTH);
        this.add(textPanel,BorderLayout.SOUTH);
        this.add(board,BorderLayout.CENTER);

        //laserButton.addActionListener( this );
        endTurnButton.addActionListener( this );
        moveButton.addActionListener( this );
        rotateLeftButton.addActionListener( this );
        rotateRightButton.addActionListener( this );
        continueButton.addActionListener( this );
        addMouseListener( this );
    }

    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        if ("DrawLaser".equals(command)) {
            toggleLaser();
        }
        if("MovePeice".equals(command)){
            if(turnEnded)
                infoLBL.setText("your turn is you cannot make any more moves");
            else{
                movePointASelected = false;
                movePointBSelected = false;
                movingGamePiece = true;
            }
        }
        if("RotateLeft".equals(command)){
            if(turnEnded)
                infoLBL.setText("your turn is you cannot make any more moves");
            else
                rotateGamePieceLeft = true;
        }
        if("RotateRight".equals(command)){
            if(turnEnded)
                infoLBL.setText("your turn is you cannot make any more moves");
            else
                rotateGamePieceRight = true;
        }
        if("EndTurn".equals(command)){
            continueButton.setEnabled(true);
            rotateLeftButton.setEnabled(false);
            rotateRightButton.setEnabled(false);
            moveButton.setEnabled(false);
            endTurnButton.setEnabled(false);
            infoLBL.setText("");
            checkWinConditions();
        }
        if("Continue".equals(command)){
            turnEnded = false;
            if(board.laser.effect.equals("hit piece")){
                board.removePiece(board.laser.lastHit[0],board.laser.lastHit[1]);
            }
            swapPlayers();
            currentPlayerLBL.setText("Currnet Player: "+board.currentPlayer);
            textPanel.repaint();
            board.laser.showLaser = false;
            continueButton.setEnabled(false);
            rotateLeftButton.setEnabled(true);
            rotateRightButton.setEnabled(true);
            moveButton.setEnabled(true);
            endTurnButton.setEnabled(true);
            repaint();
        }
    }

    public void mouseEntered( MouseEvent e ) {}

    public void mouseExited( MouseEvent e ) {}

    public void mousePressed( MouseEvent e ) {}

    public void mouseReleased( MouseEvent e ) {}

    public void mouseClicked( MouseEvent e ) {
        int x = e.getX();
        int y = e.getY();
        int[] selectedTile = getTile(x,y);
        int tileX = selectedTile[0];
        int tileY = selectedTile[1];

        lastPointLBL.setText("X:"+x+" Y:"+y);

        if(movingGamePiece){
            if(!movePointASelected || !movePointBSelected){
                GamePiece gp = board.getPiece(selectedTile[0],selectedTile[1]);
                if(!movePointASelected){
                    if (!(gp instanceof NullPiece)){
                        movePointA = new int[]{selectedTile[0],selectedTile[1]};
                        movePointASelected = true;
                    }
                }else if(!movePointBSelected){
                    movePointB = new int[]{selectedTile[0],selectedTile[1]};
                    movePointBSelected = true;
                }
            }
            if(movePointASelected && movePointBSelected){
                GamePiece gpA = board.getPiece(movePointA[0],movePointA[1]);
                GamePiece gpB = board.getPiece(movePointB[0],movePointB[1]);

                if(gpA instanceof Obelisk && gpB instanceof Obelisk){
                    board.stackObelisk(movePointA,movePointB);
                    turnEnded = true;
                    repaint();
                }else if(gpA instanceof Obelisk){
                    Obelisk ob = (Obelisk) gpA;
                    boolean unstack = true;
                    //check if they want to unstack or move the stack store in unstack
                    if(checkValidMove(movePointA,movePointB)){
                        if(ob.stacked && unstack){
                            board.unstackObelisk(movePointA,movePointB);
                            turnEnded = true;
                            repaint();
                        }else{
                            board.movePiece(movePointA,movePointB);
                            turnEnded = true;
                            repaint();
                        }
                    }
                }else if(checkValidMove(movePointA,movePointB)){
                    board.movePiece(movePointA,movePointB);
                    turnEnded = true;
                    repaint();
                }

                movePointASelected = false;
                movePointBSelected = false;
                movingGamePiece = false;
            }
        }
        if(rotateGamePieceLeft || rotateGamePieceRight){
            GamePiece gp = board.getPiece(selectedTile[0],selectedTile[1]);
            if(gp.team.equals(board.currentPlayer)){
                if(rotateGamePieceLeft){
                    board.rotatePieceLeft(selectedTile[0],selectedTile[1]);
                }else{
                    board.rotatePieceRight(selectedTile[0],selectedTile[1]);
                }
                turnEnded = true;
            }
            rotateGamePieceLeft = false;
            rotateGamePieceRight = false;
        }
        e.consume();
    }

    public boolean checkValidMove(int[] a, int[] b){
        GamePiece tileA = board.getPiece(a[0],a[1]);
        GamePiece tileB = board.getPiece(b[0],b[1]);

        infoLBL.setText("");

        if(!tileA.team.equals(board.currentPlayer)){
            infoLBL.setText("not your unit");
            return false;
        }

        if(tileA.team.equals("white")){
            for(int[] pt : board.redOnlyTiles){
                if( (b[0] == pt[0]) && (b[1] == pt[1]) ){
                    infoLBL.setText("white cannot move here");
                    return false;
                }
            }
        }else{
            for(int[] pt : board.whiteOnlyTiles){
                if( (b[0] == pt[0]) && (b[1] == pt[1]) ){
                    infoLBL.setText("red cannot move here");
                    return false;
                }
            }
        }

        if(!checkRangeOfMove(new int[]{a[0],a[1]},new int[]{b[0],b[1]})){
            infoLBL.setText("cant move that far");
            return false;
        }

        if( (!(tileA instanceof Djed)) && (!(tileB instanceof NullPiece))){
            infoLBL.setText("only djed can swap");
            return false;
        } 

        return true;
    }

    /**
     * returns true if in range false if out of range
     */
    public boolean checkRangeOfMove(int[] a, int[] b){
        ArrayList<int[]> validMoves = new ArrayList<int[]>();
        int row = a[0], col = a[1];
        if(row-1 >= 0 && col-1 >= 0)
            validMoves.add( new int[]{row-1,col-1});//topLeft
        if(col-1 >= 0)
            validMoves.add( new int[]{row,col-1});//top
        if(row+1 < 8 && col-1 >= 0)
            validMoves.add( new int[]{row+1,col-1} );//topRight

        if(row-1 >= 0)
            validMoves.add( new int[]{row-1,col});//left 
        if(row+1 < 8)
            validMoves.add( new int[]{row+1,col} );//right

        if(row-1 >= 0 && col+1 < 10)
            validMoves.add( new int[]{row-1,col+1});//botLeft
        if(col+1 < 10)
            validMoves.add( new int[]{row,col+1});//bot
        if(row+1 < 8 && col+1 < 10)
            validMoves.add( new int[]{row+1,col+1});//botRight

        for(int[] val : validMoves){
            if( (b[0] == val[0]) && (b[1] == val[1]) ){
                return true;
            }
        }
        return false;
    }

    /**
     * Called by the browser or applet viewer to inform this JApplet that it 
     * should start its execution. It is called after the init method and 
     * each time the JApplet is revisited in a Web page. 
     */
    public void start()
    {
        // provide any code requred to run each time 
        // web page is visited
    }

    /** 
     * Called by the browser or applet viewer to inform this JApplet that
     * it should stop its execution. It is called when the Web page that
     * contains this JApplet has been replaced by another page, and also
     * just before the JApplet is to be destroyed. 
     */
    public void stop()
    {
        // provide any code that needs to be run when page
        // is replaced by another page or before JApplet is destroyed 
    }

    /**
     * Paint method for applet.
     * 
     * @param  g   the Graphics object for this applet
     */
    public void paint(Graphics g)
    {
        // simple text displayed on applet
        clearWindow(g);

        board.repaint();
    }

    public void clearWindow(Graphics g){
        g.setColor(Color.white);
        //g.setColor(Color.green);
        g.fillRect(0, 0, (int) this.getWidth(), (int) this.getHeight());
        textPanel.repaint();
        buttonPanel.repaint();
    }

    public int[] getTile(int x, int y){
        int topBorder = (this.getHeight()-408)/2;
        int leftBorder = (this.getWidth()-510)/2; 

        int tileCol = (x-(leftBorder+1))/51;
        int tileRow = (y-(topBorder+1))/51;

        return new int[]{tileRow,tileCol};
    }

    public void swapPlayers(){
        if(redMove){
            //set start to white start
            board.currentPlayer = "white";
            redMove = false;
            whiteMove = true;
            return;
        }
        if(whiteMove){
            //set start to red start
            board.currentPlayer = "red";
            whiteMove = false;
            redMove = true;
            return;
        }
    }

    public void toggleLaser(){
        if(board.laser.showLaser){
            board.laser.showLaser = false;
        }else{
            board.laser.showLaser = true;
        }
        repaint();
    }

    /**
     * Called by the browser or applet viewer to inform this JApplet that it
     * is being reclaimed and that it should destroy any resources that it
     * has allocated. The stop method will always be called before destroy. 
     */
    public void destroy()
    {
        // provide code to be run when JApplet is about to be destroyed.
    }

    /**
     * Returns information about this applet. 
     * An applet should override this method to return a String containing 
     * information about the author, version, and copyright of the JApplet.
     *
     * @return a String representation of information about this JApplet
     */
    public String getAppletInfo()
    {
        // provide information about the applet
        return "Title:   \nAuthor:   \nA simple applet example description. ";
    }

    /**
     * Returns parameter information about this JApplet. 
     * Returns information about the parameters than are understood by this JApplet.
     * An applet should override this method to return an array of Strings 
     * describing these parameters. 
     * Each element of the array should be a set of three Strings containing 
     * the name, the type, and a description.
     *
     * @return a String[] representation of parameter information about this JApplet
     */
    public String[][] getParameterInfo()
    {
        // provide parameter information about the applet
        String paramInfo[][] = {
                {"firstParameter",    "1-10",    "description of first parameter"},
                {"status", "boolean", "description of second parameter"},
                {"images",   "url",     "description of third parameter"}
            };
        return paramInfo;
    }

    public void checkWinConditions(){
        board.laser.showLaser = true;
        repaint();
        if(board.laser.effect.equals("hit piece")){
            infoLBL.setText(board.laser.laserDetails);
            GamePiece gp = board.getPiece(board.laser.lastHit[0],board.laser.lastHit[1]);
            if(gp.name.equals("Pharaoh")){
                if(gp.team.equals("white")){
                    infoLBL.setText("RED WINS");
                }else{
                    infoLBL.setText("WHITE WINS");  
                }
            }
        }
    }
}

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
    JButton endTurnButton, continueButton, instructionsButton; 
    JLabel lastPointLBL, infoLBL, currentPlayerLBL, moveUnitLBL;

    private boolean redMove = false, whiteMove = true, turnEnded = false, gameOver = false;

    private int xClickA, yClickA, xClickB, yClickB;

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

        int appletWidth = (((GameBoard.tileSize+GameBoard.borderSize)*10)+GameBoard.tileSize*2);
        int appletHeight = (((GameBoard.tileSize+GameBoard.borderSize)*8)+GameBoard.tileSize*2);

        this.setSize(new Dimension(appletWidth,appletHeight));

        board.setBorder(BorderFactory.createTitledBorder("Game Board"));

        lastPointLBL = new JLabel("");
        infoLBL = new JLabel("");
        currentPlayerLBL = new JLabel("");
        moveUnitLBL = new JLabel("");

        textPanel = new JPanel(new FlowLayout());
        textPanel.add(lastPointLBL);
        textPanel.add(infoLBL);
        textPanel.add(currentPlayerLBL);
        textPanel.add(moveUnitLBL);

        endTurnButton = new JButton("End Turn");
        endTurnButton.setActionCommand("EndTurn");
        continueButton = new JButton("Continue");
        continueButton.setActionCommand("Continue");
        continueButton.setEnabled(false);
        instructionsButton = new JButton("instructions");
        instructionsButton.setActionCommand("Instructions");

        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(endTurnButton);
        buttonPanel.add(continueButton);
        buttonPanel.add(instructionsButton);

        this.add(buttonPanel,BorderLayout.NORTH);
        this.add(textPanel,BorderLayout.SOUTH);
        this.add(board,BorderLayout.CENTER);

        setPlayerWhite();//defaults to white
        selectStartingPlayer();
        selectStartingConfig();

        endTurnButton.addActionListener( this );
        continueButton.addActionListener( this );
        instructionsButton.addActionListener( this );
        addMouseListener( this );
    }

    /**
     * Presents user with the option of setting the starting player
     */
    public void selectStartingPlayer(){
        String[] jOptionButtons = { "Red", "White" };
        int playerStartChoice = JOptionPane.showOptionDialog(null, "Who will start?", "choose a starting player", 
                JOptionPane.PLAIN_MESSAGE, 0, null, jOptionButtons, jOptionButtons[1]);
        if(playerStartChoice == 0)
            setPlayerRed();
        else
            setPlayerWhite();
    }

    /**
     * Presents user with the option of setting the starting board configuration
     */
    public void selectStartingConfig(){
        String[] jOptionButtons = { "Classic", "Imhotep", "Dynasty", "Custom" };
        int playerStartChoice = JOptionPane.showOptionDialog(null, "starting layout?", "choose a starting layout", 
                JOptionPane.PLAIN_MESSAGE, 0, null, jOptionButtons, jOptionButtons[0]);
        if(playerStartChoice == 0)
            board.setBoardClassic();
        else if(playerStartChoice == 1){
            board.setBoardImhotep();
        }else if(playerStartChoice == 2){
            board.setBoardDynasty();
        }else if(playerStartChoice == 3){
            board.setBoardCustom();
        }
    }

    /**
     * method to handle button actions
     * 
     * @param e A buttons ActionEvent
     */
    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        if("EndTurn".equals(command)){
            continueButton.setEnabled(true);
            endTurnButton.setEnabled(false);
            board.laser.showLaser = true;
            repaint();
            //checkWinConditions();
        }
        if("Continue".equals(command)){
            if(gameOver){

            } else {
                turnEnded = false;
                checkWinConditions();
                swapPlayers();
                continueButton.setEnabled(false);
                endTurnButton.setEnabled(true);
                board.laser.showLaser = false;
                repaint();
            }
        }
        if("Instructions".equals(command)){
            informUserPopup("Left Click = Rotate CounterClockwise\n"+
                "Right Click = Rotate Clockwise\n"+
                "Click Drag = move unit","Instructions");
        }
    }

    public void mouseEntered( MouseEvent e ) {}

    public void mouseExited( MouseEvent e ) {}

    public void mouseClicked( MouseEvent e ) {}

    /**
     * method to find the location of press of the mouse
     */
    public void mousePressed( MouseEvent e ) {
        xClickA = e.getX();
        yClickA = e.getY();
    }

    /**
     * method to find the released location of the mouse and perform and action based on the diffrence between the press and the release
     */
    public void mouseReleased( MouseEvent e ) {
        xClickB = e.getX();
        yClickB = e.getY();
        int[] tileA = getTile(xClickA,yClickA);
        int[] tileB = getTile(xClickB,yClickB);
        //lastPointLBL.setText("["+tileA[0]+","+tileA[1]+"] ["+tileB[0]+","+tileB[1]+"]");
        //lastPointLBL.setText("["+xClickA+","+yClickA+"] ["+xClickB+","+yClickB+"]");
        if(!turnEnded){
            if( tileA[0] == tileB[0] && tileA[1] == tileB[1]){//rotate
                if (e.getButton() == MouseEvent.BUTTON1)
                    rotateGamePiece(tileA,true);
                else
                    rotateGamePiece(tileA,false);
            }else if(checkRangeOfMove(tileA,tileB)){//move
                movingGamePiece(tileA,tileB);
            }
        }else{
            informUserPopup("you have no more moves left this turn \n end your turn","Error");
        }
    }

    /**
     * method to rotate a given game peice
     * 
     * @param tile an int array of size 2 that holds the position of the Piece on the board
     * @param rotateRight boolean to indacate weather the piece should be turned right or left
     */
    public void rotateGamePiece(int[] tile, boolean rotateRight){
        GamePiece gp = board.getPiece(tile[0],tile[1]);
        if(gp.team.equals(board.currentPlayer)){
            if(rotateRight)
                board.rotatePieceLeft(tile[0],tile[1]);
            else
                board.rotatePieceRight(tile[0],tile[1]);
            turnEnded = true;
            repaint();
        }
    }

    /**
     * method to move game piece on the board from one tile to another
     * 
     * @param tileA an int array of size 2 that holds the position of the piece to be moved
     * @param tileB an int array of size 2 that holds the position of the tile the peice is to be moved to
     */
    public void movingGamePiece(int[] tileA, int[] tileB){
        GamePiece gpA = board.getPiece(tileA[0],tileA[1]);
        GamePiece gpB = board.getPiece(tileB[0],tileB[1]);
        if(gpA instanceof Obelisk || gpB instanceof Obelisk){
            if(gpA instanceof Obelisk && gpB instanceof Obelisk){
                board.stackObelisk(tileA,tileB);
                turnEnded = true;
                repaint();
            }else{
                Obelisk ob = (Obelisk) gpA;
                boolean unstack = false;
                if(ob.stacked){
                    String[] jOptionButtons = { "unstack", "move" };
                    int playerStartChoice = JOptionPane.showOptionDialog(null, "Do you want to move or unstack?", "move or unstack", 
                            JOptionPane.PLAIN_MESSAGE, 0, null, jOptionButtons, jOptionButtons[1]);
                    if(playerStartChoice == 0)
                        unstack = true;
                }

                //check if they want to unstack or move the stack store in unstack
                if(ob.stacked && unstack){
                    board.unstackObelisk(tileA,tileB);
                    turnEnded = true;
                    repaint();
                }else if(checkValidMove(tileA,tileB)){
                    board.movePiece(tileA,tileB);
                    turnEnded = true;
                    repaint();
                }
            }
        }else if(checkValidMove(tileA,tileB)){
            board.movePiece(tileA,tileB);
            turnEnded = true;
            repaint();
        }
    }

    /**
     * method to check if a move is valid according to the game rules
     * 
     * @param a an int array of size 2 that holds the position of the piece to be moved
     * @param b an int array of size 2 that holds the position of the tile the peice is to be moved to
     * @return true if the move is a valid move false if it is not
     */
    public boolean checkValidMove(int[] a, int[] b){
        GamePiece tileA = board.getPiece(a[0],a[1]);
        GamePiece tileB = board.getPiece(b[0],b[1]);

        infoLBL.setText("");

        if(!tileA.team.equals(board.currentPlayer)){
            informUserPopup("not your unit","Error");
            return false;
        }

        if(tileA.team.equals("white")){
            for(int[] pt : board.redOnlyTiles){
                if( (b[0] == pt[0]) && (b[1] == pt[1]) ){
                    informUserPopup("white cannot move here","Error");
                    return false;
                }
            }
        }else{
            for(int[] pt : board.whiteOnlyTiles){
                if( (b[0] == pt[0]) && (b[1] == pt[1]) ){
                    informUserPopup("red cannot move here","Error");
                    return false;
                }
            }
        }

        if(!(tileA instanceof Djed) && !(tileB instanceof NullPiece)){
            informUserPopup("only djed can swap","Error");
            return false;
        } 

        return true;
    }

    /**
     * method that checks to see if the intended position to move a given peice is within one tile in any direction
     * 
     * @param a an int array of size 2 that holds the position of the piece to be moved
     * @param b an int array of size 2 that holds the position of the tile the peice is to be moved to
     * @return true if in range false if out of range
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
        informUserPopup("unit cannot move that far","Error");
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
        textPanel.repaint();
        buttonPanel.repaint();
    }

    /**
     * paints a white rectangele the current size of the applet to clear the screen
     * 
     * @param  g   the Graphics object for this applet
     */
    public void clearWindow(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int) this.getWidth(), (int) this.getHeight());
    }

    /**
     * method to translate pixel coordinates into tile coordinates
     * 
     * @param x position in px to be converted into a column location
     * @param y position in px to be converted into a row location
     * @return an int array of size 2 containing the translated tile coordinates
     * bolth indices will be -1 if the x, y coordinates are out of the bounds of the board
     * 
     */
    public int[] getTile(int x, int y){
        int topBorder = (this.getHeight()-((board.tileSize+board.borderSize)*8))/2;
        int leftBorder = (this.getWidth()-((board.tileSize+board.borderSize)*10))/2;

        //int topBorder = (this.getHeight()-408)/2;
        //int leftBorder = (this.getWidth()-510)/2; 

        int tileCol = (x-(leftBorder+board.borderSize))/(board.tileSize+board.borderSize);
        int tileRow = (y-(topBorder+board.borderSize))/(board.tileSize+board.borderSize);

        if(tileRow >= 0 && tileRow < 8 && tileCol >= 0 && tileCol < 10)
            return new int[]{tileRow,tileCol};
        else
            return new int[]{-1,-1};
    }

    /**
     * method to swap the currnet player with the waiting player
     */
    public void swapPlayers(){
        if(redMove){
            setPlayerWhite();
        }else{
            setPlayerRed(); 
        }
    }

    /**
     * method to set currnet player to red
     */
    public void setPlayerRed(){
        board.currentPlayer = "red";
        textPanel.setBackground(new Color(255,85,80));
        currentPlayerLBL.setText("Current Player: "+board.currentPlayer);
        whiteMove = false;
        redMove = true;
    }

    /**
     * method to set currnet player to white
     */
    public void setPlayerWhite(){
        board.currentPlayer = "white";
        textPanel.setBackground(new Color(180,180,180));
        currentPlayerLBL.setText("Current Player: "+board.currentPlayer);
        redMove = false;
        whiteMove = true;
    }

    /**
     * toggles the visibility of the laser
     * 
     */
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

    /**
     * method to check if a player has won the game
     */
    public void checkWinConditions(){
        if(board.laser.effect.equals("hit piece")){
            informUserPopup(board.laser.laserDetails,"Piece Destroyed");
            GamePiece gp = board.getPiece(board.laser.lastHit[0],board.laser.lastHit[1]);
            if(gp.name.equals("Pharaoh")){
                if(gp.team.equals("white")){
                    informUserPopup("RED WINS", "Winner");
                }else{
                    informUserPopup("WHITE WINS", "Winner");
                }
                gameOver = true;
            }
            board.removePiece(board.laser.lastHit[0],board.laser.lastHit[1]);
        }
    }

    /**
     * method to show a popup window the the user with informative text
     */
    public static void informUserPopup(String message, String titleBar)
    {
        JOptionPane.showMessageDialog(null, message, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}

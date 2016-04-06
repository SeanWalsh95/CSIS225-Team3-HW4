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
    //boolean flags to determine who's move it currently is

    private boolean redMove = false, whiteMove = true, turnEnded = false;

    private int xClickA, yClickA, xClickB, yClickB;

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

        endTurnButton.addActionListener( this );
        continueButton.addActionListener( this );
        instructionsButton.addActionListener( this );
        addMouseListener( this );
    }

    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        if("EndTurn".equals(command)){
            continueButton.setEnabled(true);
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
            endTurnButton.setEnabled(true);
            repaint();
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

    public void mousePressed( MouseEvent e ) {
        xClickA = e.getX();
        yClickA = e.getY();
    }

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

        if(tileRow >= 0 && tileRow < 8 && tileCol >= 0 && tileCol < 10)
            return new int[]{tileRow,tileCol};
        else
            return new int[]{-1,-1};
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

    public static void informUserPopup(String message, String titleBar)
    {
        JOptionPane.showMessageDialog(null, message, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}

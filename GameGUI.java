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
    private boolean redMove = false, whiteMove = true;
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
            movingGamePiece = true;
            rotateLeftButton.setEnabled(false);
            rotateRightButton.setEnabled(false);
            moveButton.setEnabled(false);
        }
        if("RotateLeft".equals(command)){
            rotateGamePieceLeft = true;
            rotateLeftButton.setEnabled(false);
            rotateRightButton.setEnabled(false);
            moveButton.setEnabled(false);
        }
        if("RotateRight".equals(command)){
            rotateGamePieceRight = true;
            rotateLeftButton.setEnabled(false);
            rotateRightButton.setEnabled(false);
            moveButton.setEnabled(false);
        }
        if("EndTurn".equals(command)){
            endTurnButton.setEnabled(false);
            rotateLeftButton.setEnabled(false);
            rotateRightButton.setEnabled(false);
            moveButton.setEnabled(false);
            checkWinConditions();
        }
        if("Continue".equals(command)){
            if(board.laser.effect.equals("hit piece")){
                board.removePiece(board.laser.lastHit[0],board.laser.lastHit[1]);
            }
            swapPlayers();
            currentPlayerLBL.setText("Currnet Player: "+board.currentPlayer);
            textPanel.repaint();
            board.laser.showLaser = false;
            repaint();
            endTurnButton.setEnabled(true);
            rotateLeftButton.setEnabled(true);
            rotateRightButton.setEnabled(true);
            moveButton.setEnabled(true);
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

        Point tmpPt = (board.getPiece(tileX,tileY).getCenterPoint());
        //lastPointLBL.setText("tX:"+selectedTile[0]+" tY:"+selectedTile[1]+" (tCpX:"+tmpPt.getX()+"tCpY"+tmpPt.getY()+") X:"+x+" Y:"+y);
        //lastPointLBL.setText("X:"+selectedTile[0]+" Y:"+selectedTile[1]);
        lastPointLBL.setText("X:"+x+" Y:"+y);
        if(movingGamePiece){
            if(!movePointASelected){
                if (board.getPiece(selectedTile[0],selectedTile[1]) != null){
                    movePointA = new int[]{selectedTile[0],selectedTile[1]};
                    movePointASelected = true;
                }
            }else{
                movePointB = new int[]{selectedTile[0],selectedTile[1]};
                movePointBSelected = true;
            }
            GamePiece gpA = board.getPiece(movePointA[0],movePointA[1]);
            moveUnitLBL.setText("|Moving: "+gpA.team+" "+gpA.name+" at "+"["+movePointA[0]+","+movePointA[1]+"]");
            textPanel.repaint();
            if(movePointASelected && movePointBSelected){
                moveGamePiece();
                repaint();
            }
        }
        if(rotateGamePieceLeft || rotateGamePieceRight){
            GamePiece gp = board.getPiece(selectedTile[0],selectedTile[1]);
            if(gp.team.equals(board.currentPlayer)){
                infoLBL.setText("");
                if(rotateGamePieceLeft){
                    board.rotatePieceLeft(selectedTile[0],selectedTile[1]);
                    rotateGamePieceLeft = false;
                }else{
                    board.rotatePieceRight(selectedTile[0],selectedTile[1]);
                    rotateGamePieceRight = false;
                }
            }else{
                infoLBL.setText("Can only move your own units");
            }
        }
        e.consume();
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

    public void moveGamePiece(){
        board.movePiece(movePointA, movePointB);
        moveUnitLBL.setText("");
        movingGamePiece = false;
        movePointASelected = false;
        movePointBSelected = false;
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

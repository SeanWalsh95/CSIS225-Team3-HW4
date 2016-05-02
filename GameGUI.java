import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.URISyntaxException;

/**
 * Class GUI - write a description of the class here
 * 
 * @author (your name) 
 * @version (a version number)
 */
public class GameGUI extends JApplet
implements MouseListener, ActionListener, MouseMotionListener{
    // instance variables - replace the example below with your own
    GameBoard board = new GameBoard();

    JPanel textPanel,buttonPanel, boardPanel;
    JButton endTurnButton, continueButton, instructionsButton; 
    JLabel lastPointLBL, infoLBL, currentPlayerLBL, moveUnitLBL;

    private boolean redMove = false, whiteMove = true, turnEnded = false, gameOver = false;
    private boolean customSetup = false, redSetup = false, whiteSetup = false;

    private int xClickA, yClickA, xClickB, yClickB;

    ////////////////////////////////////////////////////////////////////////////////
    // instance variables - replace the example below with your own
    //string that stores file location
    //of sounds when needed
    String menuAudio = "";

    boolean playGame = false;
    boolean runGame = false;
    int firstRun = 1;

    //The Menu Screen States
    //true = that is the current menu
    boolean StartMenuMainScreen = true;
    boolean StartMenuSelectAGameScreen = false;
    boolean StartMenuQuitScreen = false;
    boolean RulesScreenPage1 = false;
    boolean RulesScreenPage2 = false;
    boolean RulesScreenPage3 = false;
    String gameToPlay = "";

    //Main Menu buttons
    boolean playButton = false;
    boolean rulesButton = false;
    boolean quitButton = false;

    //SelectAGameScreen Buttons
    boolean backButton = false;
    boolean classicButton = false;
    boolean dynastyButton = false;
    boolean imhotepButton = false;
    boolean customButton = false;
    boolean startGameButton = false;
    boolean starGameButtonVisibile = false;

    //RulesScreen Buttons
    boolean RulesScreenNextButton = false;
    boolean RulesScreenPreviousButton = false;

    //Current Selected Board
    boolean classicBoard = false;
    boolean dynastyBoard = false;
    boolean imhotepBoard = false;
    boolean emptyBoard = false;
    boolean buttonNotSelected = true;

    //double Buffer test
    //private Image bufferImage;
    //private Graphics bufferGraphics;

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

        // set the size of the applet to the size of the background image.
        setSize(530, 265);

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

        //Background Audio Setup
        playBackgroundSound();

        //double buffer test
        //bufferGraphics = bufferImage.getGraphics();

        //set the mouseCursor image
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image mouseCursor = toolkit.getImage("resources/images/mouseCursor/mouseCursorFinal.png");
        Cursor a = toolkit.createCustomCursor(mouseCursor , new Point(this.getX(),this.getY()), "img");
        setCursor(a);

        // provide any initialisation necessary for your JApplet
        addMouseListener( this );
        addMouseMotionListener( this );
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
            customSetup = true;
            continueButton.setEnabled(true);
            endTurnButton.setEnabled(false);
            board.setBoardCustom();
            repaint();
            informUserPopup("Move pieces to starting positions","Move pieces");
        }
    }

    /**
     * method to handle button actions
     * 
     * @param e A buttons ActionEvent
     */
    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        if(customSetup){
            if("Continue".equals(command)){
                customSetup = false;
                continueButton.setEnabled(false);
                endTurnButton.setEnabled(true);
                informUserPopup("The Game has now started","Game Start");
            }
        }else if(!gameOver){
            if("EndTurn".equals(command)){
                continueButton.setEnabled(true);
                endTurnButton.setEnabled(false);
                board.laser.showLaser = true;
                repaint();
                //checkWinConditions();
            }
            if("Continue".equals(command)){
                turnEnded = false;
                checkWinConditions();
                swapPlayers();
                continueButton.setEnabled(false);
                endTurnButton.setEnabled(true);
                board.laser.showLaser = false;
                repaint();
            }
            if("Instructions".equals(command)){
                informUserPopup("Left Click = Rotate CounterClockwise\n"+
                    "Right Click = Rotate Clockwise\n"+
                    "Click Drag = move unit","Instructions");
            }
        }else{
            informUserPopup("the game is over thanks for playing", "Game Over");
        }
    }

    public void mouseEntered( MouseEvent e ) {}

    public void mouseExited( MouseEvent e ) {}

    public void mouseDragged(MouseEvent e){}

    /**
     * public void mouseMoved is triggered when event is moved
     * @param MouseEvent e is the event that the mouse creates
     */
    public void mouseMoved(MouseEvent e)
    {
        //Graphics g = getGraphics();
        int x = e.getX();
        int y = e.getY();

        //only if StartMenuMainScreen == true
        if(StartMenuMainScreen == true)
        {

            //when mouse enters a the Play Game button change the color of button
            if(x >= 144 && x <= 144+241 && y >= 27 && y <= 27+56 && playButton == false)
            {
                playButton = true;
                repaint();
                //repaint(144, 27,144+241, 27+56);
            }

            //when mouse enters a the Rules button change the color of button
            else if(x >= 144 && x <= 144+241 && y >= 91 && y <= 75+56 && rulesButton == false)
            {
                rulesButton = true;
                repaint();
                //repaint(144, 91,144+241, 75+56);
            }

            //when mouse enters a the Quit button change the color of button
            else if(x >= 144 && x <= 144+241 && y >= 154 && y <= 154+56 && quitButton == false)
            {
                quitButton = true;
                repaint();
                //repaint(144, 154,144+241, 154+56);
            }
            else if(!(x >= 144 && x <= 144+241 && y >= 27 && y <= 27+56) &&
            !(x >= 144 && x <= 144+241 && y >= 91 && y <= 75+56) &&
            !(x >= 144 && x <= 144+241 && y >= 154 && y <= 154+56))
            {
                playButton = false;
                rulesButton = false;
                quitButton = false;
                repaint();
                //repaint(144, 27,144+241, 27+56);
                //repaint(144, 91,144+241, 75+56);
                //repaint(144, 154,144+241, 154+56);
            }

        }

        //only if StartMenuSelectAGameScreen == true
        if(StartMenuSelectAGameScreen == true)
        {
            //             //when the mouse enters the BACK button
            //             if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30 &&
            //             backButton == false)
            //             {
            //                 backButton = true;
            //                 repaint(5, 5, 5+80, 5+30);
            //             }
            // 
            //             //when the mouse enters the CLASSIC button
            //             else if(x >= 35 && x <= 35+100 && y >= 90 && y <= 90+50 &&
            //             buttonNotSelected == true &&
            //             classicButton == false)
            //             {
            //                 classicButton = true;
            //                 repaint(35, 90, 35+100, 90+50);
            //             }
            // 
            //             //when the mouse enters the DYNASTY button
            //             else if(x >= 35 && x <= 35+100 && y >= 190 && y <= 190+50 &&
            //             dynastyButton == false)
            //             {
            //                 dynastyButton = true;
            //                 repaint(35, 190, 35+100, 190+50);
            //             }
            // 
            //             //when the mouse enters the IMHOTEP button
            //             else if(x >= 35 && x <= 35+100 && y >= 290 && y <= 290+50 &&
            //             imhotepButton == false)
            //             {
            //                 imhotepButton = true;
            //                 repaint(35, 290, 35+100, 290+50);
            //             }
            // 
            //             //when the mouse enters the CUSTOM button
            //             else if(x >= 35 && x <= 35+100 && y >= 390 && y <= 390+50 &&
            //             customButton == false)
            //             {
            //                 customButton = true;
            //                 repaint(35, 390, 35+100, 390+50);
            //             }
            //             else if(!(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30) &&
            //             !(x >= 35 && x <= 35+100 && y >= 90 && y <= 90+50) &&
            //             !(x >= 35 && x <= 35+100 && y >= 190 && y <= 190+50) &&
            //             !(x >= 35 && x <= 35+100 && y >= 290 && y <= 290+50) &&
            //             !(x >= 35 && x <= 35+100 && y >= 390 && y <= 390+50) &&
            //             gameToPlay.equals(""))
            //             {
            //                 backButton = false;
            //                 classicButton = false;
            //                 dynastyButton = false;
            //                 imhotepButton = false;
            //                 customButton = false;
            //                 repaint();
            //             }

            //             //when the mouse enters the START GAME button
            //             if(starGameButtonVisibile == true)
            //             {
            //                 if(x >= 745 && x <= 745+100 && y >= 445 && y <= 445+50 &&
            //                 startGameButton == false)
            //                 {
            //                     startGameButton = true;
            //                     repaint();
            //                 }
            //                 else if(!(x >= 745 && x <= 745+100 && y >= 445 && y <= 445+50))
            //                 {
            //                     startGameButton = false;
            //                     repaint();
            //                 }
            //             }

        }

        //only if setUpRulesScreenPage1 == true
        if(RulesScreenPage1 == true)
        {
            //When the mouse enters the BACK button
            if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30 &&
            backButton == false)
            {
                backButton = true;
                repaint();
            }

            //When the mouse enters the NEXT button
            else if(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30 &&
            RulesScreenNextButton == false)
            {
                RulesScreenNextButton = true;
                repaint();
            }
            else if(!(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30) &&
            !(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30))
            {
                backButton = false;
                RulesScreenNextButton = false;
                repaint();
            }

        }

        //only if setUpRulesScreenPage2 == true
        if(RulesScreenPage2 == true)
        {
            //When the mouse enters the BACK button
            if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30 &&
            backButton == false)
            {
                backButton = true;
                repaint();
            }

            //When the mouse enters the PREVIOUS button
            else if(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30 &&
            RulesScreenPreviousButton == false)
            {
                RulesScreenPreviousButton = true;
                repaint();
            }

            //When the mouse enters the NEXT button
            else if(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30 &&
            RulesScreenNextButton == false)
            {
                RulesScreenNextButton = true;
                repaint();
            }
            else if(!(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30) &&
            !(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30) &&
            !(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30)
            )
            {
                backButton = false;
                RulesScreenPreviousButton = false;
                RulesScreenNextButton = false;
                repaint();
            }

        }

        //only if setUpRulesScreenPage3 == true
        if(RulesScreenPage3 == true)
        {
            //When the mouse enters the BACK button
            if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30 &&
            backButton == false)
            {
                backButton = true;
                repaint();
            }

            //When the mouse enters the PREVIOUS button
            else if(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30 &&
            RulesScreenPreviousButton == false)
            {
                RulesScreenPreviousButton = true;
                repaint();
            }
            else if (!(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30) &&
            !(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30))
            {
                backButton = false;
                RulesScreenPreviousButton = false;
                repaint();
            }

        }

        e.consume();
    }

    /**
     * method to find the location of press of the mouse
     */
    public void mousePressed( MouseEvent e ) {
        //when the mouse is pushed show the images for pushed buttons
        int x = e.getX();
        int y = e.getY();

        Graphics g = getGraphics();

        if(playGame == true)
        {
            xClickA = e.getX();
            yClickA = e.getY();
        }

        //only if StartMenuMainScreen == true
        if(StartMenuMainScreen == true)
        {

            if (e.getButton() == MouseEvent.BUTTON1)
            {
                if(x >= 144 && x <= 144+241 && y >= 27 && y <= 27+56)
                {
                    //if you press the 1st mouse button 
                    //on the PLAY GAME button it shows
                    //a PRESSED button image
                    paintPressedPlayGameButton(g);
                }

                if(x >= 144 && x <= 144+241 && y >= 91 && y <= 75+56)
                {
                    //if you press the 1st mouse button 
                    //on the RULES button it shows
                    //a PRESSED button image
                    paintPressedRulesButton(g);
                }

                if(x >= 144 && x <= 144+241 && y >= 154 && y <= 154+56)
                {
                    //if you press the 1st mouse button  
                    //on the QUIT button it shows
                    //a PRESSED button image
                    paintPressedQuitButton(g);
                }

            }

        }

        //only if StartMenuSelectAGameScreen == true
        if(StartMenuSelectAGameScreen == true)
        {

            if (e.getButton() == MouseEvent.BUTTON1) {
                if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
                {
                    //if you press the 1st mouse button 
                    //on the BACK button it shows
                    //a PRESSED button image
                    paintPressedBackButton(g);
                }

                if(x >= 35 && x <= 35+100 && y >= 90 && y <= 90+50)
                {
                    //if you press the 1st mouse button  
                    //on the CLASSIC button it shows
                    //a PRESSED button image
                    paintPressedClassicButton(g);
                }

                if(x >= 35 && x <= 35+100 && y >= 190 && y <= 190+50)
                {
                    //if you press the 1st mouse button 
                    //on the DYNASTY button it shows
                    //a PRESSED button image
                    paintPressedDynastyButton(g);
                }

                if(x >= 35 && x <= 35+100 && y >= 290 && y <= 290+50)
                {
                    //if you press the 1st mouse button 
                    //on the IMHOTEP button it shows
                    //a PRESSED button image
                    paintPressedImhotepButton(g);
                }

                if(x >= 35 && x <= 35+100 && y >= 390 && y <= 390+50)
                {
                    //if you press the 1st mouse button 
                    //on the CUSTOM button it shows
                    //a PRESSED button image
                    paintPressedCustomButton(g);
                }

                if(starGameButtonVisibile == true){
                    if(x >= 745 && x <= 745+100 && y >= 445 && y <= 445+50)
                    {
                        //if you press the 1st mouse button  
                        //on the START GAME button it shows
                        //a PRESSED button image
                        paintPressedStartGameButton(g);
                    }
                }

            }

        }

        //only if setUpRulesScreenPage1 == true
        if(RulesScreenPage1 == true)
        {

            if (e.getButton() == MouseEvent.BUTTON1) {
                if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
                {
                    //if you press the 1st mouse button  
                    //on the BACK button it shows
                    //a PRESSED button image
                    paintPressedBackButton(g);
                }

                //When the mouse enters the NEXT button
                else if(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30)
                {
                    //if you press the 1st mouse button  
                    //on the NEXT button it shows
                    //a PRESSED button image
                    paintPressedNextButton(g);
                }
            }

        }

        //only if setUpRulesScreenPage2 == true
        if(RulesScreenPage2 == true)
        {

            if (e.getButton() == MouseEvent.BUTTON1) {
                if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
                {
                    //if you press the 1st mouse button  
                    //on the BACK button it shows
                    //a PRESSED button image
                    paintPressedBackButton(g);

                }
                else if(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30)
                {
                    //if you press the 1st mouse button  
                    //on the Previous button it shows
                    //a PRESSED button image
                    paintPressedPreviousButton(g);

                }
                else if(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30)
                {
                    //if you press the 1st mouse button  
                    //on the NEXT button it shows
                    //a PRESSED button image
                    paintPressedNextButton(g);

                }
            }

        }

        //only if setUpRulesScreenPage3 == true
        if(RulesScreenPage3 == true)
        {

            if (e.getButton() == MouseEvent.BUTTON1) {
                if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
                {
                    //if you press the 1st mouse button  
                    //on the BACK button it shows
                    //a PRESSED button image
                    paintPressedBackButton(g);

                }
                //Plays a button sound when you press the PREVIOUS button
                //only plays the sound if it is the left mouse button
                else if(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30)
                {
                    //if you press the 1st mouse button  
                    //on the Previous button it shows
                    //a PRESSED button image
                    paintPressedPreviousButton(g);

                }
            }

        }

        e.consume();
    }

    /**
     * method to find the released location of the mouse and perform and action based on the diffrence between the press and the release
     */
    public void mouseReleased( MouseEvent e ) {
        //when the mouse is released show the images for highlighted button
        int x = e.getX();
        int y = e.getY();

        Graphics g = getGraphics();

        if(playGame ==true)
        {
            xClickB = e.getX();
            yClickB = e.getY();
            int[] tileA = getTile(xClickA,yClickA);
            int[] tileB = getTile(xClickB,yClickB);
            //lastPointLBL.setText("["+tileA[0]+","+tileA[1]+"] ["+tileB[0]+","+tileB[1]+"]");
            //lastPointLBL.setText("["+xClickA+","+yClickA+"] ["+xClickB+","+yClickB+"]");
            if(customSetup){
                if((tileA[0] == tileB[0] && tileA[1] == tileB[1])){
                    if (e.getButton() == MouseEvent.BUTTON1)
                        rotateGamePiece(tileA,true);
                    else
                        rotateGamePiece(tileA,false);
                }else if(checkGoodTile(tileA,tileB)){
                    board.movePiece(tileA,tileB);
                    repaint();
                }
            }else if(!gameOver){
                if(!turnEnded){
                    if( tileA[0] == tileB[0] && tileA[1] == tileB[1]){//rotate
                        if (checkOwnership(board.getPiece(tileA[0],tileA[1])) && e.getButton() == MouseEvent.BUTTON1)
                            rotateGamePiece(tileA,true);
                        else
                            rotateGamePiece(tileA,false);
                    }else if(checkOwnership(board.getPiece(tileA[0],tileA[1])) && checkRangeOfMove(tileA,tileB)){//move
                        movingGamePiece(tileA,tileB);
                    }
                }else{
                    informUserPopup("you have no more moves left this turn \n end your turn","Error");
                }
            }else{
                informUserPopup("the game is over thanks for playing", "Game Over");
            }
        }

        //only if StartMenuMainScreen == true
        if(StartMenuMainScreen == true)
        {

            if (e.getButton() == MouseEvent.BUTTON1)
            {
                if(x >= 144 && x <= 144+241 && y >= 27 && y <= 27+56)
                {
                    //if you release the 1st mouse button 
                    //on the PLAY GAME button it shows
                    //a UNSELECTED button image
                    paintUnselectedPlayGameButton(g);
                }

                else if(x >= 144 && x <= 144+241 && y >= 91 && y <= 75+56)
                {
                    //if you release the 1st mouse button 
                    //on the RULES button it shows
                    //a UNSELECTED button image
                    paintUnselectedRulesButton(g);
                }

                else if(x >= 144 && x <= 144+241 && y >= 154 && y <= 154+56)
                {
                    //if you release the 1st mouse button 
                    //on the QUIT button it shows
                    //a UNSELECTED button image
                    paintUnselectedQuitButton(g);
                }

            }

        }

        //only if StartMenuSelectAGameScreen == true
        if(StartMenuSelectAGameScreen == true)
        {

            if (e.getButton() == MouseEvent.BUTTON1) {

                if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
                {
                    //if you release the 1st mouse button 
                    //on the BACK button it shows
                    //a UNSELECTED button image
                    paintUnselectedBackButton(g);
                }

                if(x >= 35 && x <= 35+100 && y >= 90 && y <= 90+50)
                {
                    //if you release the 1st mouse button 
                    //on the CLASSIC button it shows
                    //a HIGHLIGHTED button image
                    paintHighlightedClassicButton(g);
                }

                if(x >= 35 && x <= 35+100 && y >= 190 && y <= 190+50)
                {
                    //if you release the 1st mouse button 
                    //on the DYNASTY button it shows
                    //a HIGHLIGHTED button image
                    paintHighlightedDynastyButton(g);
                }

                if(x >= 35 && x <= 35+100 && y >= 290 && y <= 290+50)
                {
                    //if you release the 1st mouse button 
                    //on the IMHOTEP button it shows
                    //a HIGHLIGHTED button image
                    paintHighlightedImhotepButton(g);
                }

                if(x >= 35 && x <= 35+100 && y >= 390 && y <= 390+50)
                {
                    //if you release the 1st mouse button 
                    //on the CUSTOM button it shows
                    //a HIGHLIGHTED button image
                    paintHighlightedCustomButton(g);
                }

                if(starGameButtonVisibile == true){
                    if(x >= 745 && x <= 745+100 && y >= 445 && y <= 445+50)
                    {
                        //if you release the 1st mouse button 
                        //on the START GAME button it shows
                        //a UNSELECTED button image
                        paintUnselectedStartGameButton(g);
                    }
                }

            }

        }

        //only if setUpRulesScreenPage1 == true
        if(RulesScreenPage1 == true)
        {

            if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
            {
                //if you press the 1st mouse button  
                //on the BACK button it shows
                //a UNSELECTED button image
                paintUnselectedBackButton(g);

            }
            else if(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30)
            {
                //if you press the 1st mouse button  
                //on the NEXT button it shows
                //a UNSELECTED button image
                paintUnselectedNextButton(g);

            }

        }

        //only if setUpRulesScreenPage2 == true
        if(RulesScreenPage2 == true)
        {

            if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
            {
                //if you press the 1st mouse button  
                //on the BACK button it shows
                //a UNSELECTED button image
                paintUnselectedBackButton(g);

            }
            else if(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30)
            {
                //if you press the 1st mouse button  
                //on the Previous button it shows
                //a UNSELECTED button image
                paintPressedPreviousButton(g);

            }
            else if(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30)
            {
                //if you press the 1st mouse button  
                //on the NEXT button it shows
                //a UNSELECTED button image
                paintPressedNextButton(g);

            }

        }

        //only if setUpRulesScreenPage3 == true
        if(RulesScreenPage3 == true)
        {

            if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
            {
                //if you press the 1st mouse button  
                //on the BACK button it shows
                //a UNSELECTED button image
                paintUnselectedBackButton(g);

            }
            else if(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30)
            {
                //if you press the 1st mouse button  
                //on the Previous button it shows
                //a UNSELECTED button image
                paintPressedPreviousButton(g);

            }

        }

        //repaint();

        e.consume();

    }

    /**
     * public void mouseClicked creates an event when a mouse is clicked
     * @param MouseEvent e is the event that the mouse creates
     */
    public void mouseClicked( MouseEvent e ) 
    {
        Graphics g = getGraphics();
        int x = e.getX();
        int y = e.getY();

        //only if StartMenuMainScreen == true
        if(StartMenuMainScreen == true)
        {

            if (e.getButton() == MouseEvent.BUTTON1) {
                //Plays a button sound when you press PLAY GAME
                //only plays the sound if it is the left mouse button
                if(x >= 144 && x <= 144+241 && y >= 27 && y <= 27+56)
                {
                    //play the button sound
                    playButtonSound();
                    //set StartMenuMainScreen to false
                    //set StartMenuSelectAGameScreen to true
                    StartMenuMainScreen = false;
                    StartMenuSelectAGameScreen = true;

                    //reset the Select a game menu
                    gameToPlay = "";
                    classicBoard = false;
                    dynastyBoard = false;
                    imhotepBoard = false;
                    emptyBoard = false;

                    starGameButtonVisibile = false;

                }

                //Plays a button sound when you press the Rules button
                else if(x >= 144 && x <= 144+241 && y >= 91 && y <= 75+56)
                {
                    //play the button sound
                    playButtonSound();
                    //set StartMenuMainScreen to false
                    //set StartMenuRulesScreen to true
                    StartMenuMainScreen = false;
                    RulesScreenPage1 = true;

                }

                //Plays a button sound when you press the Quit button
                else if(x >= 144 && x <= 144+241 && y >= 154 && y <= 154+56)
                {
                    //play the button sound
                    playButtonSound();

                    //set StartMenuMainScreen to false
                    //set StartMenuQuitScreen to true
                    StartMenuMainScreen = false;
                    StartMenuQuitScreen = true;

                }

            }

        }

        //only if StartMenuSelectAGameScreen == true
        if(StartMenuSelectAGameScreen == true)
        {

            if (e.getButton() == MouseEvent.BUTTON1) {
                //Plays a button sound when you press BACK
                //only plays the sound if it is the left mouse button
                if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
                {
                    //move to the main menu
                    //play the button sound
                    playButtonSound();
                    //tell the program we are back on the StartMenuMainScreen
                    StartMenuMainScreen = true;
                    StartMenuSelectAGameScreen = false;
                    StartMenuQuitScreen = false;
                    //set the size of the applet to the mainMenu background
                    setSize(530, 265);

                }

                //Plays a button sound when you press CLASSIC
                else if(x >= 35 && x <= 35+100 && y >= 90 && y <= 90+50)
                {
                    //play the button sound
                    playButtonSound();
                    //show an image of the selected board
                    //classicBoard = true;
                    //dynastyBoard = false;
                    //imhotepBoard = false;
                    //emptyBoard = false;

                    //Set the gameToPlay equal to classic
                    gameToPlay = "classic";
                    //show start game button
                    starGameButtonVisibile = true;

                }

                //Plays a button sound when you press DYNASTY
                else if(x >= 35 && x <= 35+100 && y >= 190 && y <= 190+50)
                {
                    //play the button sound
                    playButtonSound();
                    //show an image of the selected board
                    classicBoard = false;
                    dynastyBoard = true;
                    imhotepBoard = false;
                    emptyBoard = false;

                    //Set the gameToPlay equal to dynasty
                    gameToPlay = "dynasty";
                    //show start game button
                    starGameButtonVisibile = true;

                }

                //Plays a button sound when you press IMHOTEP
                else if(x >= 35 && x <= 35+100 && y >= 290 && y <= 290+50)
                {
                    //play the button sound
                    playButtonSound();
                    //show an image of the selected board
                    classicBoard = false;
                    dynastyBoard = false;
                    imhotepBoard = true;
                    emptyBoard = false;

                    //Set the gameToPlay equal to imhotep
                    gameToPlay = "imhotep";
                    //show start game button
                    starGameButtonVisibile = true;

                }

                //Plays a button sound when you press CUSTOM
                else if(x >= 35 && x <= 35+100 && y >= 390 && y <= 390+50)
                {
                    //play the button sound
                    playButtonSound();
                    //show an image of the selected board
                    classicBoard = false;
                    dynastyBoard = false;
                    imhotepBoard = false;
                    emptyBoard = true;

                    //Set the gameToPlay equal to custom
                    gameToPlay = "custom";
                    //show start game button
                    starGameButtonVisibile = true;

                }

                //Plays a button sound when you press START GAME
                else if(starGameButtonVisibile == true){
                    if(x >= 745 && x <= 745+100 && y >= 445 && y <= 445+50)
                    {   
                        //play the button sound
                        playStartGameButtonSound();
                        //launches the game with settings provided
                        if(gameToPlay.equals("classic"))
                        {
                            ////launch game with classic configuration
                            playGame = true;
                            board.setBoardClassic();
                        }
                        else if(gameToPlay.equals("dynasty"))
                        {
                            //launch game with dynasty configuration
                            playGame = true;
                            board.setBoardDynasty();
                        }
                        else if(gameToPlay.equals("imhotep"))
                        {
                            //launch game with imhotep configuration
                            playGame = true;
                            board.setBoardImhotep();
                        }
                        else if(gameToPlay.equals("custom"))
                        {
                            //launch game with custom configuration
                            playGame = true;
                            customSetup = true;
                            continueButton.setEnabled(true);
                            endTurnButton.setEnabled(false);
                            board.setBoardCustom();
                            informUserPopup("Move pieces to starting positions","Move pieces");
                        }

                    }

                }

            }

        }

        //only if setUpRulesScreenPage1 == true
        if(RulesScreenPage1 == true)
        {
            //Plays a button sound when you press the BACK button
            //only plays the sound if it is the left mouse button
            if (e.getButton() == MouseEvent.BUTTON1) {
                if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
                {
                    //play the button sound
                    playButtonSound();
                    //set StartMenuMainScreen to false
                    //set StartMenuRulesScreen to true
                    StartMenuMainScreen = true;
                    RulesScreenPage1 = false;
                    //set the size of the applet to the mainMenu background
                    setSize(530, 265);

                }
                //Plays a button sound when you press the NEXT button
                //only plays the sound if it is the left mouse button
                else if(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30)
                {
                    //play the button sound
                    playPageTurnButtonSound();
                    //set StartMenuMainScreen to false
                    //set StartMenuRulesScreen to true
                    RulesScreenPage1 = false;
                    RulesScreenPage2 = true;

                }
            }

        }
        //only if setUpRulesScreenPage2 == true
        else if(RulesScreenPage2 == true)
        {
            //Plays a button sound when you press the BACK button
            //only plays the sound if it is the left mouse button
            if (e.getButton() == MouseEvent.BUTTON1) {
                if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
                {
                    //play the button sound
                    playButtonSound();
                    //set StartMenuMainScreen to false
                    //set StartMenuRulesScreen to true
                    StartMenuMainScreen = true;
                    RulesScreenPage2 = false;
                    //set the size of the applet to the mainMenu background
                    setSize(530, 265);

                }
                //Plays a button sound when you press the PREVIOUS button
                //only plays the sound if it is the left mouse button
                else if(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30)
                {
                    //play the button sound
                    playPageTurnButtonSound();
                    //set StartMenuMainScreen to false
                    //set StartMenuRulesScreen to true
                    RulesScreenPage2 = false;
                    RulesScreenPage1 = true;

                }
                //Plays a button sound when you press the NEXT button
                //only plays the sound if it is the left mouse button
                else if(x >= 483 && x <= 483+80 && y >= 818 && y <= 818+30)
                {
                    //play the button sound
                    playPageTurnButtonSound();
                    //set  RulesScreenPage2 to false
                    //set RulesScreenPage3 to true
                    RulesScreenPage2 = false;
                    RulesScreenPage3 = true;

                }
            }

        }
        //only if setUpRulesScreenPage3 == true
        else if(RulesScreenPage3 == true)
        {
            //only plays the sound if it is the left mouse button
            if (e.getButton() == MouseEvent.BUTTON1) {
                //Plays a button sound when you press the BACK button
                if(x >= 5 && x <= 5+80 && y >= 5 && y <= 5+30)
                {
                    //play the button sound
                    playButtonSound();
                    //set StartMenuMainScreen to false
                    //set StartMenuRulesScreen to true
                    StartMenuMainScreen = true;
                    RulesScreenPage3 = false;
                    //set the size of the applet to the mainMenu background
                    setSize(530, 265);

                }

                //Plays a button sound when you press the PREVIOUS button
                else if(x >= 5 && x <= 5+80 && y >= 819 && y <= 819+30)
                {
                    //play the button sound
                    playPageTurnButtonSound();
                    //set StartMenuMainScreen to false
                    //set StartMenuRulesScreen to true
                    RulesScreenPage3 = false;
                    RulesScreenPage2 = true;

                }

            }

        }
        //repaint with the new Booleans
        repaint();

        e.consume();
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
                if(!((Obelisk) gpB).stacked){
                    board.stackObelisk(tileA,tileB);
                    turnEnded = true;
                    repaint();
                }
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
        GamePiece gpA = board.getPiece(a[0],a[1]);
        GamePiece gpB = board.getPiece(b[0],b[1]);

        if(!checkGoodTile(a,b))
            return false;

        if(!(gpA instanceof Djed) && !(gpB instanceof NullPiece)){
            informUserPopup("only djed can swap","Error");
            return false;
        }else if((gpA instanceof Djed) && (gpB instanceof Pharaoh || gpB instanceof Djed)){
            informUserPopup("djed cant swap with pharaoh's or other djed's","Error");
            return false;
        }

        return true;
    }

    /**
     * 
     * method to check if a tile is able to be moved into. i.e. the tile to be moved to is not exclusively a red or white tile
     * @param a an int array of size 2 that holds the position of the piece to be moved
     * @param b an int array of size 2 that holds the position of the tile the peice is to be moved to
     * @return true if the tile is able to be moved to false if there is a problem with the tile
     * 
     */
    public boolean checkGoodTile(int[] a, int[] b){
        GamePiece gpA = board.getPiece(a[0],a[1]);
        GamePiece gpB = board.getPiece(b[0],b[1]);
        if(gpA.team.equals("white")){
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
        return true;
    }

    /**
     * method that checks to see who owns a given GamePiece
     * 
     * @piece the GamePiece to be checked
     * @return true if current player owns the piece, false if they do not
     */
    public boolean checkOwnership(GamePiece piece){
        if(piece.team.equals(board.currentPlayer)){
            return true;
        }else{
            informUserPopup("not your unit","Error");
            return false;
        }
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
        if(playGame == true)
        {
            // simple text displayed on applet
            if(firstRun ==1)
            {
                setUpPlayGame();
            }
            if(runGame == true)
            {
                clearWindow(g);
                board.repaint();
                textPanel.repaint();
                buttonPanel.repaint();
            }

        }

        if(StartMenuMainScreen == true)
        {
            setUpMainMenuScreen();

            if(playButton == true)
            {
                //visibility of the play button is true
                paintHighlightPlayGameButton(g);
            }
            else
            {
                //visibility of the play button icon is false
                paintUnselectedPlayGameButton(g);
            }

            if(rulesButton == true)
            {
                //visibility of the rules button icon is true
                paintHighlightRulesButton(g);
            }
            else
            {
                //visibility of the rules button icon is false
                paintUnselectedRulesButton(g);
            }

            if(quitButton == true)
            {
                //visibility of the quit button icon is true
                paintHighlightQuitButton(g);
            }
            else
            {
                //visibility of the quit button icon is false
                paintUnselectedQuitButton(g);
            }

        }
        else if(StartMenuSelectAGameScreen == true)
        {
            setUpSelectAGameScreen();

            if(backButton == true)
            {
                //visibility of the back button is true
                paintHighlightedBackButton(g);
            }
            else
            {
                //visibility of the back button is false
                paintUnselectedBackButton(g);
            }

            if(classicButton == true)
            {
                //visibility of the classic button is true
                paintHighlightedClassicButton(g);
            }
            else
            {
                //visibility of the classic button is false
                paintUnselectedClassicButton(g);
            }

            if(dynastyButton == true)
            {
                //visibility of the dynastyButton is true
                paintHighlightedDynastyButton(g);
            }
            else
            {
                //visibility of the dynastyButton is false
                paintUnselectedDynastyButton(g);
            }

            if(imhotepButton == true)
            {
                //visibility of the imhotepButton is true
                paintHighlightedImhotepButton(g);
            }
            else
            {
                //visibility of the imhotepButton is false
                paintUnselectedImhotepButton(g);
            }

            if(customButton == true)
            {
                //visibility of the cusomButton is true
                paintHighlightedCustomButton(g);
            }
            else
            {
                //visibility of the cusomButton is false
                paintUnselectedCustomButton(g);
            }

            if(starGameButtonVisibile == true)
            {
                if(startGameButton == true)
                {
                    //visibility of the startGameButton is true
                    paintHighlightedStartGameButton(g);
                }
                else
                {
                    //visibility of the startGameButton is false
                    paintUnselectedStartGameButton(g);
                }
            }

            //Current Selected Board

            //             if(classicBoard == true)
            //             {
            //                 paintPressedClassicButton(g);
            //                 //paint the classicBoard
            //                 //paintClassicBoard(g);
            //             }
            //             else if(dynastyBoard == true)
            //             {
            //                 paintPressedDynastyButton(g);
            //                 //paint the classicBoard
            //                 //paintDynastyBoard(g);
            //             }
            //             else if(imhotepBoard == true)
            //             {
            //                 paintPressedImhotepButton(g);
            //                 //paint the classicBoard
            //                 //paintImhotepBoard(g);
            //             }
            //             else if(emptyBoard == true)
            //             {
            //                 paintPressedCustomButton(g);
            //                 //paint the classicBoard
            //                 //paintEmptyBoard(g);
            //             }

            if(gameToPlay.equals("classic"))
            {
                paintPressedClassicButton(g);
                //paint the classicBoard
                //we only want to paint this in once
                //conditional that says
                //hey if you allrady drew this dont draw
                paintClassicBoard(g);
            }
            else if(gameToPlay.equals("dynasty"))
            {
                paintPressedDynastyButton(g);
                //paint the classicBoard
                paintDynastyBoard(g);
            }
            else if(gameToPlay.equals("imhotep"))
            {
                paintPressedImhotepButton(g);
                //paint the classicBoard
                paintImhotepBoard(g);
            }
            else if(gameToPlay.equals("custom"))
            {
                paintPressedCustomButton(g);
                //paint the classicBoard
                paintEmptyBoard(g);
            }

        }
        else if(RulesScreenPage1 == true)
        {
            setUpRulesScreenPage1();

            if(backButton == true)
            {
                paintHighlightedBackButton(g);
            }

            if(RulesScreenNextButton == true)
            {
                paintHighlightedNextButton(g);
            }

        }
        else if(RulesScreenPage2 == true)
        {
            setUpRulesScreenPage2();

            if(backButton == true)
            {
                paintHighlightedBackButton(g);
            }

            if(RulesScreenNextButton == true)
            {
                paintHighlightedNextButton(g);
            }

            if(RulesScreenPreviousButton == true)
            {
                paintHighlightedPreviousButton(g);
            }

        }
        else if(RulesScreenPage3 == true)
        {
            setUpRulesScreenPage3();

            if(backButton == true)
            {
                paintHighlightedBackButton(g);
            }

            if(RulesScreenPreviousButton == true)
            {
                paintHighlightedPreviousButton(g);
            }

        }
        else if(StartMenuQuitScreen == true)
        {
            setUpQuitScreen();
        }

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
                String winner;
                if(gp.team.equals("white")){
                    winner = "RED WINS";
                }else{
                    winner = "WHITE WINS";
                }
                endGameSounds();
                informUserPopup(winner,"GAME OVER");
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

    //===============================================
    //      Seting up the game screens
    //===============================================

    /**
     * Draws the main menu to the applet
     */
    public void setUpMainMenuScreen()
    {
        Graphics g = getGraphics();
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File backgroundSounds = new File(getClass().getResource("resources\\images\\backgrounds").toURI());
            File[] backgroundArray = backgroundSounds.listFiles();
            Arrays.sort(backgroundArray);
            Image background = ImageIO.read(backgroundArray[0]);
            g.drawImage(background, 0, 0, this);

            //print Version number in the bottom right
            String version = "Version 1.2";
            int y = getSize().height;
            int c1 = getSize().width;
            int c2 = y/2;
            g.setColor(Color.blue);
            g.drawString(version, c1 - 79, y - 4);   // width, height
        }
        catch(Exception e){e.printStackTrace();}
    }

    /**
     * Draws the select a game menu to the applet
     */
    public void setUpSelectAGameScreen()
    {
        Graphics g = getGraphics();

        //set the size of the SelectAGameScreen
        setSize(850, 500);
        //show background of SelectAGameScreen
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File backgroundSounds = new File(getClass().getResource("resources\\images\\backgrounds").toURI());
            File[] backgroundArray = backgroundSounds.listFiles();
            Arrays.sort(backgroundArray);
            Image background = ImageIO.read(backgroundArray[1]);
            g.drawImage(background, 0, 0, this);
        }
        catch(Exception e){e.printStackTrace();}

    }

    /**
     * Draws the quit game screen to the applet
     */
    public void setUpQuitScreen()
    {
        Graphics g = getGraphics();

        //set the size of the SelectAGameScreen
        setSize(850, 500);
        //show background of SelectAGameScreen
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File backgroundSounds = new File(getClass().getResource("resources\\images\\backgrounds").toURI());
            File[] backgroundArray = backgroundSounds.listFiles();
            Arrays.sort(backgroundArray);
            Image background = ImageIO.read(backgroundArray[5]);
            g.drawImage(background, 0, 0, this);
        }
        catch(Exception e){e.printStackTrace();}

    }

    /**
     * Initilizes the game once all
     * options are selected and the
     * player selects the start game
     * button
     */
    public void setUpPlayGame()
    {
        int appletWidth = (((GameBoard.tileSize+GameBoard.borderSize)*10)+GameBoard.tileSize*2);
        int appletHeight = (((GameBoard.tileSize+GameBoard.borderSize)*8)+GameBoard.tileSize*2);

        this.setSize(new Dimension(appletWidth,appletHeight));

        board.setBorder(BorderFactory.createTitledBorder("Game Board"));

        //         lastPointLBL = new JLabel("");
        //         infoLBL = new JLabel("");
        //         currentPlayerLBL = new JLabel("");
        //         moveUnitLBL = new JLabel("");
        // 
        //         textPanel = new JPanel(new FlowLayout());
        //         textPanel.add(lastPointLBL);
        //         textPanel.add(infoLBL);
        //         textPanel.add(currentPlayerLBL);
        //         textPanel.add(moveUnitLBL);
        // 
        //         endTurnButton = new JButton("End Turn");
        //         endTurnButton.setActionCommand("EndTurn");
        //         continueButton = new JButton("Continue");
        //         continueButton.setActionCommand("Continue");
        //         continueButton.setEnabled(false);
        //         instructionsButton = new JButton("instructions");
        //         instructionsButton.setActionCommand("Instructions");
        // 
        //         buttonPanel = new JPanel(new FlowLayout());
        //         buttonPanel.add(endTurnButton);
        //         buttonPanel.add(continueButton);
        //         buttonPanel.add(instructionsButton);

        this.add(buttonPanel,BorderLayout.NORTH);
        this.add(textPanel,BorderLayout.SOUTH);
        this.add(board,BorderLayout.CENTER);

        setPlayerWhite();//defaults to white
        //selectStartingPlayer();

        firstRun++;

        runGame = true;

        endTurnButton.addActionListener( this );
        continueButton.addActionListener( this );
        instructionsButton.addActionListener( this );
    }

    /**
     * Draws the first page
     * of the rules screen
     * to the applet
     */
    public void setUpRulesScreenPage1()
    {
        Graphics g = getGraphics();

        //set the size of the RulesScreen
        setSize(568, 853);
        //show background of setUpRulesScreenPage1
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File backgroundSounds = new File(getClass().getResource("resources\\images\\backgrounds").toURI());
            File[] backgroundArray = backgroundSounds.listFiles();
            Arrays.sort(backgroundArray);
            Image background = ImageIO.read(backgroundArray[2]);
            g.drawImage(background, 0, 0, this);
        }
        catch(Exception e){e.printStackTrace();}
    }

    /**
     * Draws the second page
     * of the rules screen
     * to the applet
     */
    public void setUpRulesScreenPage2()
    {
        Graphics g = getGraphics();

        //set the size of the RulesScreen
        setSize(568, 853);
        //show background of setUpRulesScreenPage2
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File backgroundSounds = new File(getClass().getResource("resources\\images\\backgrounds").toURI());
            File[] backgroundArray = backgroundSounds.listFiles();
            Arrays.sort(backgroundArray);
            Image background = ImageIO.read(backgroundArray[3]);
            g.drawImage(background, 0, 0, this);
        }
        catch(Exception e){e.printStackTrace();}
    }

    /**
     * Draws the third page
     * of the rules screen
     * to the applet
     */
    public void setUpRulesScreenPage3()
    {
        Graphics g = getGraphics();

        //set the size of the RulesScreen
        setSize(568, 853);
        //show background of setUpRulesScreenPage3
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File backgroundSounds = new File(getClass().getResource("resources\\images\\backgrounds").toURI());
            File[] backgroundArray = backgroundSounds.listFiles();
            Arrays.sort(backgroundArray);
            Image background = ImageIO.read(backgroundArray[4]);
            g.drawImage(background, 0, 0, this);
        }
        catch(Exception e){e.printStackTrace();}
    }

    //         //initializes the backButton
    //         backButton.setLocation(5,5);
    //         backButton.setSize(80,30);
    // 
    //         //initializes the classicButton
    //         classicButton.setLocation(35,500/5 - 10); //(x,y) left is  - and right is +
    //         classicButton.setSize(100,50);  //lenght,height
    // 
    //         //initializes the DynastyButton
    //         DynastyButton.setLocation(35,190);
    //         DynastyButton.setSize(100,50);
    // 
    //         //initializes the ImhotepButton
    //         ImhotepButton.setLocation(35,500/5 - 10 + 200);
    //         ImhotepButton.setSize(100,50);;
    // 
    //         initializes the customButton
    //         customButton.setLocation(35,500/5 - 10 + 300);
    //         customButton.setSize(100,50);
    // 
    //         initializes the startGameButton
    //         startGameButton.setLocation(745,445);
    //         startGameButton.setSize(100,50);

    /**
     * Generate a random number for the button sounds and sets the sound to play
     */
    public void playButtonSound() {
        //set the random number to play random button sound
        String buttonSound = "resources/sounds/buttons/button1.wav";
        //plays random button sound from button names
        play( getDocumentBase(), buttonSound );
    }

    /**
     * Plays the start game button sound
     */
    public void playStartGameButtonSound() {
        //set the random number to play random button sound
        String buttonSound = "resources/sounds/buttons/soundgong.au";
        //plays random button sound from button names
        play( getDocumentBase(), buttonSound );
    }

    /**
     * Plays the pageTurning button sound
     */
    public void playPageTurnButtonSound() {
        //set the random number to play random button sound
        String buttonSound = "resources/sounds/buttons/PageTurn.wav";
        //plays random button sound from button names
        play( getDocumentBase(), buttonSound );
    }
    
    /**
     * Plays some fun audio when the game ends
     */
    public void endGameSounds()
    {
        String endGameAudio = "resources/sounds/backgroundSounds/endgame.wav";
        //plays end game sound from file location
        AudioClip endAC = getAudioClip(getCodeBase(), endGameAudio);
        endAC.play();//plays the audio once
    }

    /**
     * Generate a random number for the 
     * background sounds and sets the sound to play
     */
    public void playBackgroundSound() {
        //random call
        Random num = new Random();
        //set diceOne to random # between 1 and 3
        int diceNum = num.nextInt(3) + 1;
        //set the random number to play random button sound
        if(diceNum == 1)
        {
            menuAudio = "resources/sounds/backgroundSounds/pharaoh ramses ii.au";
        }
        if(diceNum == 2)
        {
            menuAudio = "resources/sounds/backgroundSounds/prince of egypt.au";
        }
        if(diceNum == 3)
        {
            menuAudio = "resources/sounds/backgroundSounds/tomb raiders.au";
        }

        //plays random background sound from filelocation
        AudioClip ac = getAudioClip(getCodeBase(), menuAudio);
        ac.loop();  //plays audio on loop
    }

    //====================
    //  Paint boards
    //====================

    /**
     * fetches the Classic board
     * to be used for painting
     */
    public void paintClassicBoard(Graphics g)
    {
        try
        {
            File backgroundSounds = new File(getClass().getResource("resources\\images\\boards\\scaled").toURI());
            File[] boardArray = backgroundSounds.listFiles();
            Arrays.sort(boardArray);
            Image classicBoard = ImageIO.read(boardArray[0]);
            g.drawImage(classicBoard, 165, 40, this);
        }
        catch(Exception ex){ex.printStackTrace();}
    }

    /**
     * fetches the Dynasty board
     * to be used for painting
     */
    public void paintDynastyBoard(Graphics g)
    {
        try
        {
            File backgroundSounds = new File(getClass().getResource("resources\\images\\boards\\scaled").toURI());
            File[] boardArray = backgroundSounds.listFiles();
            Arrays.sort(boardArray);
            Image dynastyBoard = ImageIO.read(boardArray[1]);
            g.drawImage(dynastyBoard, 165, 40, this);
        }
        catch(Exception ex){ex.printStackTrace();}
    }

    /**
     * fetches the Imhotep board
     * to be used for painting
     */
    public void paintImhotepBoard(Graphics g)
    {
        try
        {
            File backgroundSounds = new File(getClass().getResource("resources\\images\\boards\\scaled").toURI());
            File[] boardArray = backgroundSounds.listFiles();
            Arrays.sort(boardArray);
            Image imhotepBoard = ImageIO.read(boardArray[2]);
            g.drawImage(imhotepBoard, 165, 40, this);
        }
        catch(Exception ex){ex.printStackTrace();}
    }

    /**
     * fetches the empty board
     * to be used for painting
     */
    public void paintEmptyBoard(Graphics g)
    {
        try
        {
            File backgroundSounds = new File(getClass().getResource("resources\\images\\boards\\scaled").toURI());
            File[] boardArray = backgroundSounds.listFiles();
            Arrays.sort(boardArray);
            Image customBoard = ImageIO.read(boardArray[3]);
            g.drawImage(customBoard, 165, 40, this);
        }
        catch(Exception ex){ex.printStackTrace();}
    }

    /**
     * fetches the Unselected
     * Play game button
     * to be used for painting
     */
    public void paintUnselectedPlayGameButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image playButtonReleased = ImageIO.read(backgroundArray[6]);
            g.drawImage(playButtonReleased, 144, 27, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Unselected
     * Rules Button
     * to be used for painting
     */
    public void paintUnselectedRulesButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image rulesButtonReleased = ImageIO.read(backgroundArray[7]);
            g.drawImage(rulesButtonReleased, 144, 91, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Unselected
     * quit button
     * to be used for painting
     */
    public void paintUnselectedQuitButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image quitButtonReleased = ImageIO.read(backgroundArray[8]);
            g.drawImage(quitButtonReleased, 144, 154, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Highlighted
     * Play game button
     * to be used for painting
     */
    public void paintHighlightPlayGameButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image playButtonHighlight = ImageIO.read(backgroundArray[0]);
            g.drawImage(playButtonHighlight, 144, 27, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Highlighted
     * rules button
     * to be used for painting
     */
    public void paintHighlightRulesButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image rulesHighlightButton = ImageIO.read(backgroundArray[1]);
            g.drawImage(rulesHighlightButton, 144, 91, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Highlighted
     * quit button
     * to be used for painting
     */
    public void paintHighlightQuitButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image quitButtonHighlight = ImageIO.read(backgroundArray[2]);
            g.drawImage(quitButtonHighlight, 144, 154, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * play game button
     * to be used for painting
     */
    public void paintPressedPlayGameButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image playButtonPressed = ImageIO.read(backgroundArray[3]);
            g.drawImage(playButtonPressed, 144, 27, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * rules button
     * to be used for painting
     */
    public void paintPressedRulesButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image rulesButtonPressed = ImageIO.read(backgroundArray[4]);
            g.drawImage(rulesButtonPressed, 144, 91, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * quit button
     * to be used for painting
     */
    public void paintPressedQuitButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image quitButtonPressed = ImageIO.read(backgroundArray[5]);
            g.drawImage(quitButtonPressed, 144, 154, this);    //image, width, height

        }
        catch(Exception a){}
    }

    //==============================
    //  SelectAGameMenu pictures
    //==============================

    /**
     * fetches the unselected
     * back button
     * to be used for painting
     */
    public void paintUnselectedBackButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image BackButton = ImageIO.read(backgroundArray[0]);
            g.drawImage(BackButton, 5, 5, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the unselected
     * classic button
     * to be used for painting
     */
    public void paintUnselectedClassicButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image classicButton = ImageIO.read(backgroundArray[1]);
            g.drawImage(classicButton, 35, 90, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the unselected
     * Dynasty button
     * to be used for painting
     */
    public void paintUnselectedDynastyButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image dynastyButton = ImageIO.read(backgroundArray[2]);
            g.drawImage(dynastyButton, 35, 190, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the unselected
     * Imhotep button
     * to be used for painting
     */
    public void paintUnselectedImhotepButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image imhotepButton = ImageIO.read(backgroundArray[3]);
            g.drawImage(imhotepButton, 35, 290, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the unselected
     * Custom button
     * to be used for painting
     */
    public void paintUnselectedCustomButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image CustomButton = ImageIO.read(backgroundArray[4]);
            g.drawImage(CustomButton, 35, 390, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the unselected
     * start game button
     * to be used for painting
     */
    public void paintUnselectedStartGameButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image StartButton = ImageIO.read(backgroundArray[5]);
            g.drawImage(StartButton, 745, 445, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Highlighted
     * back button
     * to be used for painting
     */
    public void paintHighlightedBackButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image BackButton = ImageIO.read(backgroundArray[6]);
            g.drawImage(BackButton, 5, 5, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Highlighted
     * classic button
     * to be used for painting
     */
    public void paintHighlightedClassicButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image classicButton = ImageIO.read(backgroundArray[7]);
            g.drawImage(classicButton, 35, 90, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Highlighted
     * dynasty button
     * to be used for painting
     */
    public void paintHighlightedDynastyButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image dynastyButton = ImageIO.read(backgroundArray[8]);
            g.drawImage(dynastyButton, 35, 190, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Highlighted
     * Imhotep button
     * to be used for painting
     */
    public void paintHighlightedImhotepButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image imhotepButton = ImageIO.read(backgroundArray[9]);
            g.drawImage(imhotepButton, 35, 290, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Highlighted
     * custom button
     * to be used for painting
     */
    public void paintHighlightedCustomButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image CustomButton = ImageIO.read(backgroundArray[10]);
            g.drawImage(CustomButton, 35, 390, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Highlighted
     * start game button
     * to be used for painting
     */
    public void paintHighlightedStartGameButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image StartButton = ImageIO.read(backgroundArray[11]);
            g.drawImage(StartButton, 745, 445, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * back button
     * to be used for painting
     */
    public void paintPressedBackButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image BackButton = ImageIO.read(backgroundArray[12]);
            g.drawImage(BackButton, 5, 5, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * classic button
     * to be used for painting
     */
    public void paintPressedClassicButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image classicButton = ImageIO.read(backgroundArray[13]);
            g.drawImage(classicButton, 35, 90, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * Dynasty button
     * to be used for painting
     */
    public void paintPressedDynastyButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image dynastyButton = ImageIO.read(backgroundArray[14]);
            g.drawImage(dynastyButton, 35, 190, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * Imhotep button
     * to be used for painting
     */
    public void paintPressedImhotepButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image imhotepButton = ImageIO.read(backgroundArray[15]);
            g.drawImage(imhotepButton, 35, 290, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * custom button
     * to be used for painting
     */
    public void paintPressedCustomButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image CustomButton = ImageIO.read(backgroundArray[16]);
            g.drawImage(CustomButton, 35, 390, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * start game button
     * to be used for painting
     */
    public void paintPressedStartGameButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\SelectAGameMenu").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image StartButton = ImageIO.read(backgroundArray[17]);
            g.drawImage(StartButton, 745, 445, this);    //image, width, height

        }
        catch(Exception a){}
    }

    //==================================
    //  Buttons for the rules menu
    //==================================
    /**
     * fetches the Unselected
     * next button
     * to be used for painting
     */
    public void paintUnselectedNextButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\RulesScreen").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image nextButton = ImageIO.read(backgroundArray[0]);
            g.drawImage(nextButton, 483, 818, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Unselected
     * previous button
     * to be used for painting
     */
    public void paintUnselectedPreviousButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\RulesScreen").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image previousButton = ImageIO.read(backgroundArray[1]);
            g.drawImage(previousButton, 5, 819, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the highlighted
     * next button
     * to be used for painting
     */
    public void paintHighlightedNextButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\RulesScreen").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image previousButton = ImageIO.read(backgroundArray[3]);
            g.drawImage(previousButton, 483, 818, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the highlighted
     * previous button
     * to be used for painting
     */
    public void paintHighlightedPreviousButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\RulesScreen").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image previousButton = ImageIO.read(backgroundArray[2]);
            g.drawImage(previousButton, 5, 819, this);    //image, width, height

        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * next button
     * to be used for painting
     */
    public void paintPressedNextButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\RulesScreen").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image previousButton = ImageIO.read(backgroundArray[4]);
            g.drawImage(previousButton, 483, 818, this);    //image, width, height
        }
        catch(Exception a){}
    }

    /**
     * fetches the Pressed
     * Previous button
     * to be used for painting
     */
    public void paintPressedPreviousButton(Graphics g)
    {
        try
        {
            //takes file path and puts contents into an array
            //then we sort the array lexiographically
            //draw the image we want from the array
            File pressedButton = new File(getClass().getResource("resources\\images\\buttons\\RulesScreen").toURI());
            File[] backgroundArray = pressedButton.listFiles();
            Arrays.sort(backgroundArray);
            Image previousButton = ImageIO.read(backgroundArray[5]);
            g.drawImage(previousButton, 5, 819, this);    //image, width, height

        }
        catch(Exception a){}
    }

}

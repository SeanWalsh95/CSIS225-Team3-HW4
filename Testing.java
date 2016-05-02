import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * Write a description of class testing here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Testing
{
    public static void main(String args[]){
        Testing test = new Testing();
        //test.board[4][9].rotate();
        //test.printList();
        test.simPaint();
        test.printList();
        test.printList();
    }

    GamePiece[][] board = new GamePiece[8][10];
    Laser laser = new Laser();
    String currentPlayer = "white";

    public Testing(){
        for(int row=0; row < 8; row++){
            for(int col=0; col < 10; col++){
                board[row][col] = new NullPiece();
            }
        }

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

    public void simPaint(){
        int topBorder = (600-408)/2;
        int leftBorder = (700-510)/2; 
        for(int row=0; row < 8; row++){
            for(int col=0; col < 10; col++){
                board[row][col].
				setXYpos(leftBorder+(col*51),topBorder+(row*51));
            }
        }
    }

    public void printPos(){
        for(int row=0; row < 8; row++){
            for(int col=0; col < 10; col++){
                Point pt = board[row][col].getCenterPoint();
                System.out.print("("+pt.getX()+","+pt.getY()+")");
            }
            System.out.println("");
        }
    }

    public void printList(){
        ArrayList<int[]> tiles = 
		new ArrayList<int[]>(laser.getTraversedTiles(board,currentPlayer));
        System.out.println("enter print list");
        for(int i=0; i< tiles.size();i++){
            int[] set = tiles.get(i);
            Point pt = board[set[0]][set[1]].getCenterPoint();
            System.out.println("("+pt.getX()+","+pt.getY()+")");
        }
        System.out.println(laser.getDetails());
    }

    public void printTiles(){
        ArrayList<int[]> pos = laser.getTraversedTiles(board,"white");
        System.out.println("enter print tiles");
        for(int i=0; i<pos.size();i++){
            int tmp[] = pos.get(i);
            System.out.println("["+tmp[0]+","+tmp[1]+"]");
        }
        System.out.println(laser.getDetails());
    }
}

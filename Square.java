import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * A Class extending JButton for the squares on the graphical board display. Their postion
 * on the board is stored, and the icon appearing on each is updated as moves are made.
 * 
 * @author Lewis Lloyd 
 * @version 1
 */
public class Square extends JButton
{
    int xPos;
    int yPos;

    /**
     * Constructor for objects of class Square
     * @param int x the row on the board this square appears in
     * @param int y the column on the board this square appears in
     * @param Color background the colour this square should be
     */
    public Square(int x, int y, Color background)
    {
        xPos = x;
        yPos = y;
        super.setBackground(background);
        super.setOpaque(true);
        super.setBorderPainted(false);
    }

    /**
     * Add an icon to the square.
     * @param int[][] the current state
     */
    public void addPiece(int[][] current)
    {
        ImageIcon blackPiece = new ImageIcon("bPiece.png"); //image of a black piece
        ImageIcon whitePiece = new ImageIcon("wPiece.png"); //image of a white piece
        ImageIcon blackKing = new ImageIcon("bPieceK.png"); //image of a black king
        ImageIcon whiteKing = new ImageIcon("wPieceK.png"); //image of a white king
        if (current[xPos][yPos]==1) { //if a black is here in the current game state
            super.setIcon(blackPiece);
        }
        else if (current[xPos][yPos]==2) { //if a white piece is here
            super.setIcon(whitePiece);
        }
        else if (current[xPos][yPos]==3) { //if a black king is here
            super.setIcon(blackKing); 
        }
        else if (current[xPos][yPos]==4) { //if a white king is here
            super.setIcon(whiteKing);
        }
        else { //if this square is unoccupied
            super.setIcon(null);
        }
    }
}

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * The Game class initialises the GUI and runs the game.
 * 
 * @author Lewis Lloyd 
 * @version 1
 */
public class Game
{
    private JFrame frame; //the GUI frame
    Board board; //a board to play the game on!

    /**
     * The constructor calls a method to make the frame, creates a new board,
     * and calls a method to pop up an introductory dialogue box.
     */
    public Game()
    {
        makeFrame();
        //frame passed as a parameter when making board, so board representation can be added to it in Board class
        board = new Board(frame); 
        intro();
    }
    
    /**
     * Create the Swing frame to add stuff to. Calls makeMenu() to add a menu bar to it.
     */
    private void makeFrame()
    {
        frame = new JFrame("Draughts");
        makeMenu(frame);
    }
    
    /**
     * Add a menu bar with a couple of items to the frame.
     * @param JFrame the frame to add a menu bar to.
     */
    private void makeMenu(JFrame frame)
    {
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        
        JMenu fileMenu = new JMenu("File");
        menubar.add(fileMenu);
        
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(e -> quit());
        fileMenu.add(quitItem);
        
        JMenu helpMenu = new JMenu("Help");
        menubar.add(helpMenu);
        
        JMenuItem rulesItem = new JMenuItem("Rules");
        rulesItem.addActionListener(e -> rulesDialogue());
        helpMenu.add(rulesItem);
    }
    
    /**
     * Show an introductory dialogue, with a welcome message, the rules, and a choice of difficulty.
     */
    private void intro()
    {
        Object[] options = {"feeble", "alright", "solid", "strong", "bonkers"}; //the difficulty options
        int n = JOptionPane.showOptionDialog(frame, //the difficulty selection will be registered as int n
                    "Welcome to the wonderful world of Draughts!\n" +
                    "\n" +
                    "The aim is to 'take' all of your opponent's pieces.\n" +
                    "You move by clicking on a piece, then on the square you would like to move it to. \n" +
                    "Only diagonal moves forward are allowed.\n" +
                    "\n" +
                    "You take one of your opponent's pieces by jumping over it into an empty square.\n" +
                    "Otherwise, you can only move forward by one square.\n" +
                    "If you can take a piece, you are forced to.\n" +
                    "If you can take another piece straight after that piece, you are forced to.\n" +
                    "\n" +
                    "A piece that reaches the end of the board becomes a King. Kings can move backwards.\n" +
                    "\n" +
                    "You will be playing with the white pieces.\n" +
                    "The above rules also apply to the computer. The computer (black) will start.\n" +
                    "\n" +
                    "How good would you like the computer to be?",
                    "Welcome!",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null, options, options[1]); //'alright' set as default choice
        board.setLimit(n); //set the limit on minimax depth in Board class according to difficulty level selected 
        play(); //let's get started
    }

    /**
     * Start the game and keep it rolling.
     */
    private void play()
    {
        while (!board.wWins(board.currentState) && !board.bWins(board.currentState)) { //while nobody has won...
            board.getAIMove(); //get the computer's move first
            board.moveMade=false; //make sure the system operates on basis that human has yet to make their move
            while (board.moveMade==false) { //while human has yet to make move...
                wait(1000); //just wait
            }
        }
        
        if (board.wWins(board.currentState)) { //if the human has won
            System.out.println("*******");
            System.out.println("You won! Now try a harder computer setting...");
            System.out.println("");
        }
        if (board.bWins(board.currentState)) { //if the computer has won
            System.out.println("*******");
            System.out.println("The computer won! Ha.");
            System.out.println("");
        }
    }
    
    /**
     * Have a messageDialog pop up with the rules of the game.
     */
    private void rulesDialogue()
    {
        JOptionPane.showMessageDialog(frame,
                    "The aim is to 'take' all of your opponent's pieces.\n" +
                    "You move by clicking on a piece, then on the \n" +
                    "square you would like to move it to.\n" +
                    "Only diagonal moves forward are allowed.\n" +
                    "\n" +
                    "You take one of your opponent's pieces by jumping\n" +
                    "over it into an empty square.\n" +
                    "Otherwise, you can only move forward by one square.\n" +
                    "If you can take a piece, you are forced to.\n" +
                    "If you can take another piece straight after that piece,\n" +
                    "you are forced to.\n" +
                    "\n" +
                    "A piece that reaches the end of the board\n" +
                    "becomes a King. Kings can move backwards.\n" +
                    "You will be playing with the white pieces.\n" +
                    "The computer (black) will start.",
                    "Rules of the Game",
                    JOptionPane.PLAIN_MESSAGE);
    }
                    
    /**
     * Exit the game if the user has selected file->quit.
     */
    public void quit()
    {
        System.exit(0);
    }
    
    /**
     * Wait for a specified number of milliseconds before doing the next thing.
     * @param  milliseconds  the amount of time to wait 
     */
    public void wait(int milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        } 
        catch (Exception e)
        {
        }
    }
}
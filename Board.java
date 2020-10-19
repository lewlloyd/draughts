import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;
/**
 * The Board class is really the main class in the game - it's where all the fun stuff happens. A board 
 * is created for the purposes of the GUI and added to the frame created in the Game class; user moves are
 * processed and accepted or rejected; the computer's moves are decided upon; and so on.
 * 
 * @author Lewis Lloyd
 * @version 1
 */
public class Board
{
    int[][] currentState; //representing the current state of the board as a 2D array
    int limit; //the limit on how deep minimax can go, based on the difficulty level selected by the user
    JFrame frame; //the GUI frame, to be received from the Game class
    ArrayList<Square> squares; //a container to store all the squares on the board in
    boolean pieceSelected; //whether or not the user has selected a piece to move
    boolean jumpRequest; //whether or not the user is trying to jump with a piece
    int fromX; //the x position the user wants to move a piece from
    int fromY; //the y position the user wants to move a piece from
    boolean moveMade; //whether or not the user has successfully made a move this turn
    ArrayList<StatesAndScores> successorEvaluations; //a container for the possible next states and their values

    /**
     * Constructor for objects of class Board
     * @param JFrame the frame created in the Game class, passed here to have a board added to it
     */
    public Board(JFrame frame)
    {
        getStartState(); //this will give us an int[][] for our initial currentState
        this.frame = frame; //the frame to add the graphical board display to
        makeBoardRep(frame); //make the board display
        pieceSelected = false; //no piece selected by the user, initially
        jumpRequest = false; //no jump requested as yet
        moveMade = false; //no move made as yet
        successorEvaluations = new ArrayList<>();
    }
    
    /**
     * Initialise the game's currentState, with the board represented by an 8x8 2D array of integers. 
     * 1's indicate that a black piece is occupying the corresponding square, and 2's represent white pieces. 
     * A position with a 0 value is unoccupied.
     */
    public void getStartState()
    {
        currentState = new int[][]{
            {0,1,0,1,0,1,0,1},
            {1,0,1,0,1,0,1,0},
            {0,1,0,1,0,1,0,1},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {2,0,2,0,2,0,2,0},
            {0,2,0,2,0,2,0,2},
            {2,0,2,0,2,0,2,0}};
    }
    
    /**
     * Create the JPanel for the board representation and its contents, including all of the squares
     * as Objects of the Class Square (which extends JButton).
     * @param JFrame the frame created in the Game class that we want to add the board display to
     */
    public void makeBoardRep(JFrame frame)
    {
        JPanel boardRep = new JPanel();
        boardRep.setLayout(new GridLayout(8,8)); //opting for an 8x8 grid, matching our 2D array above
        boardRep.setSize(600,600);
        
        squares = new ArrayList<>(); //an ArrayList to hold all the squares of the board in
        
        for(int i=0; i<8; i++) {
            if (i % 2 == 0) { //if this is an even numbered row (including 0)
                for(int j=0; j<8; j++) {
                    if (j % 2 == 0) { //if this is an even numbered column (including 0)
                        Square lightSquare = new Square(i, j, Color.lightGray); //create a light-coloured square (a JButton)
                        lightSquare.addActionListener(e -> moveRequest(lightSquare)); //add a way of it recognising when it has been clicked on
                        squares.add(lightSquare); //add the square to the ArrayList of squares
                        boardRep.add(lightSquare); //add the square to the board display
                    }
                    else { //if this is an odd numbered column, do the above but with a dark-coloured square
                        Square darkSquare = new Square(i, j, Color.gray);
                        darkSquare.addActionListener(e -> moveRequest(darkSquare));
                        squares.add(darkSquare);
                        boardRep.add(darkSquare);
                    }
                }
            }
            else { //if this is an odd numbered row
                for(int j=0; j<8; j++) {
                    if (j % 2 == 0) { //if even numbered column, add a dark square
                        Square darkSquare = new Square(i, j, Color.gray);
                        darkSquare.addActionListener(e -> moveRequest(darkSquare));
                        squares.add(darkSquare);
                        boardRep.add(darkSquare);
                    }
                    else { //if odd numbered column, add a light square
                        Square lightSquare = new Square(i, j, Color.lightGray);
                        lightSquare.addActionListener(e -> moveRequest(lightSquare));
                        squares.add(lightSquare);
                        boardRep.add(lightSquare);
                    }
                }
            }
        }
        
        placePieces(); //given the current state, place the correct pieces on any occupied squares
        
        frame.add(boardRep); //add the board display to the frame
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Match the currentState of the game (which positions are occupied by which pieces), with what is 
     * displayed on the graphical board, making sure pieces are visible where they should be.
     */
    public void placePieces()
    {
        for (Square square : squares) {
            square.addPiece(currentState);
        }
    }
    
    /**
     * When a square has been clicked on by the user, come here to work out what move is being requested,
     * and, if the move is valid, to make it happen.
     * @param Square the square that has been clicked on
     */
    public void moveRequest(Square square)
    {
        int x = square.xPos; //the x position of the square on the board
        int y = square.yPos; //the y position of the square on the board
        boolean king; //to store whether or not the piece on the square is a king
        if (currentState[x][y]==4) { //checking if the square is occupied by a king
            king = true; }
        if (!pieceSelected) { //if the user has yet to select a piece to move (until now)
            if (takesPossState(2,currentState)) { //if the user could take a piece in the current game state
                //if it is not possible to take a piece in any direction from the square selected
                if (!jumpPoss(2,currentState,x,y,x-1,y+1,x-2,y+2) && !jumpPoss(2,currentState,x,y,x-1,y-1,x-2,y-2) && !jumpPoss(2,currentState,x,y,x+1,y+1,x+2,y+2) && !jumpPoss(2,currentState,x,y,x+1,y-1,x+2,y-2)) {
                    //get the user to select a different square
                    System.out.println("But there's a take on the cards...pick a different piece.");
                    System.out.println("");
                }
                else { //if a jump/take is possible from the position selected, move on
                    fromX = x; //set the x position of the selected square as the x position to move from
                    fromY = y; //set the y position of the selected square as the y position to move from
                    pieceSelected = true; //a piece to move has been successfully selected
                    jumpRequest = true; //a jump has been successfully requested
                }
            }
            //if a take isn't possible, but the square is occupied by one of the player's pieces
            else if (currentState[x][y]==2 || currentState[x][y]==4) {
                fromX = x; //x as position to move from
                fromY = y; //y as position to move from
                pieceSelected = true;
            }
            else { //if the player does not have a piece in this square
                System.out.println("You don't have a piece to move in this square!");
                System.out.println("");
            }
        }
        else if (pieceSelected) { //if a piece to move has already been selected
            if (jumpRequest) { //if a jump should be made with this piece
                if (x==fromX-2 && y==fromY+2) { //is a jump possible in this direction?
                    if (jumpPoss(2,currentState,fromX,fromY,fromX-1,fromY+1,fromX-2,fromY+2)) {
                        currentState = makeJump(currentState,2,fromX,fromY,fromX-1,fromY+1,fromX-2,fromY+2);
                        placePieces(); //if yes, make the jump and update the display
                        if (takesPossPosition(2,currentState,x,y)) { //if another take is possible from the current position
                            System.out.println("You can take again! Pick the next square to move to.");
                            System.out.println("");
                            //keep pieceSelected and jumpRequested as true, and wait for the player to select the square to move to
                            fromX = x; //moving from where we just moved to
                            fromY = y;
                        }
                        else { //if another take is not possible from the position moved to
                            moveMade = true; //a move has been successfully made. Turn over
                            pieceSelected = false; //returning these values to false before next moveRequest() call
                            jumpRequest = false;
                        }
                    }
                    else { //if a jump isn't possible in the direction specified by the player
                        System.out.println("That jump isn't possible! Try a different move.");
                        System.out.println("");
                    }
                }
                else if (x==fromX-2 && y==fromY-2) { //same as above, but in a different direction
                    if (jumpPoss(2,currentState,fromX,fromY,fromX-1,fromY-1,fromX-2,fromY-2)) {
                        currentState = makeJump(currentState,2,fromX,fromY,fromX-1,fromY-1,fromX-2,fromY-2);
                        placePieces();
                        if (takesPossPosition(2,currentState,x,y)) {
                            System.out.println("You can take again! Pick the next square to move to.");
                            System.out.println("");
                            fromX = x;
                            fromY = y;
                        }
                        else {
                            moveMade = true;
                            pieceSelected = false;
                            jumpRequest = false;
                        }
                    }
                    else {
                        System.out.println("That jump isn't possible! Try a different move.");
                        System.out.println("");
                    }
                }
                else if (x==fromX+2 && y==fromY-2) { //same as above, but in a different direction
                    if (jumpPoss(2,currentState,fromX,fromY,fromX+1,fromY-1,fromX+2,fromY-2)) {
                        currentState = makeJump(currentState,2,fromX,fromY,fromX+1,fromY-1,fromX+2,fromY-2);
                        placePieces();
                        if (takesPossPosition(2,currentState,x,y)) {
                            System.out.println("You can take again! Pick the next square to move to.");
                            System.out.println("");
                            fromX = x;
                            fromY = y;
                        }
                        else {
                            moveMade = true;
                            pieceSelected = false;
                            jumpRequest = false;
                        }
                    }
                    else {
                        System.out.println("That jump isn't possible! Try a different move.");
                        System.out.println("");
                    }
                }
                else if (x==fromX+2 && y==fromY+2) { //same as above, but in a different direction
                    if (jumpPoss(2,currentState,fromX,fromY,fromX+1,fromY+1,fromX+2,fromY+2)) {
                        currentState = makeJump(currentState,2,fromX,fromY,fromX+1,fromY+1,fromX+2,fromY+2);
                        placePieces();
                        if (takesPossPosition(2,currentState,x,y)) {
                            System.out.println("You can take again! Pick the next square to move to.");
                            System.out.println("");
                            fromX = x;
                            fromY = y;
                        }
                        else {
                            moveMade = true;
                            pieceSelected = false;
                            jumpRequest = false;
                        }
                    }
                    else {
                        System.out.println("That jump isn't possible! Try a different move.");
                        System.out.println("");
                    }
                }
                else { //if the jump the user has tried to make is not possible
                     System.out.println("You can't move here! Try jumping over a neighbouring black piece.");
                     System.out.println("");
                     pieceSelected = false;
                     jumpRequest = false;
                }
            }
            else if ((x==fromX-1 || x==fromX+1) && (y==fromY-1 || y==fromY+1)) { //we're not working with a jump request
                if (stepPoss(2,currentState,fromX,fromY,x,y)) { //if it's possible to step in the given direction, do so
                    currentState = makeStep(currentState,2,fromX,fromY,x,y);
                    moveMade = true;
                    pieceSelected = false;
                    placePieces();
                }
                else {
                    System.out.println("You can only move forwards and into empty squares.");
                    System.out.println("");
                    pieceSelected = false;
                }
            }
            else { //if the user has tried to move a piece somewhere illegal
                    System.out.println("You can't move here! Have you read the rules?");
                    System.out.println("");
                    pieceSelected = false;
            }
        }
    }
    
    /**
     * Called from the play() method of the Game class, a method to get the computer's next move and update
     * the board accordingly.
     */
    public void getAIMove()
    {
        if (limit==0) { //if the lowest difficulty level has been selected
            Random rand = new Random();
            ArrayList<int[][]> availableStates = getPossibleStates(currentState,1); //get the possible next states
            if (availableStates.size()==0) { //if no move is possible, do nothing
                currentState = currentState;
            }
            else { //pick a random state from the possible options and make that the currentState
                int i = rand.nextInt(availableStates.size());
                currentState = availableStates.get(i);
            }
        }
        else { //if any other difficulty level has been selected
            minimax(currentState, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE); //call minimax
            int[][] bestState = getBestState(); //get the bestState in the wake of the minimax call
            currentState = bestState; //make that best state the currentState
        }
        placePieces(); //update the board
    }
    
    /**
     * A method to get the possible next states for either player - although this only applies to player 2
     * (the user) for the purposes of the minimax evaluation.
     * @param int[][] the state you want to get the next states for
     * @param int the player in question
     * @return ArrayList<int[][]> an ArrayList of the possible next states
     */
    public ArrayList<int[][]> getPossibleStates(int[][] state, int player)
    {
        int king = setKing(player); //set an appropriate number (3/4) for the king, given the player
        ArrayList<int[][]> possibleStates = new ArrayList<>(); //initialise the ArrayList
        possibleStates = getJumpStatesState(state, possibleStates, player, king); //get any next states resulting from a jump/take
        if (possibleStates.size()==0) { //if a jump is not possible from the given state
            possibleStates = getStepStates(state, possibleStates, player, king); //get states resulting from a step
        }
        return possibleStates; //return the possible states
    }
    
    /**
     * A method to get the possible next states from the current state that would occur as a result of a jump.
     * @param int[][] state the state to get the next states for
     * @param ArrayList<int[][]> possibleStates an ArrayList of the possible next states
     * @param int player the player in question
     * @param int king the number corresponding to a king for the player in question
     * @return ArrayList<int[][]> an ArrayList of the possible next states
     */
    public ArrayList<int[][]> getJumpStatesState(int[][] state, ArrayList<int[][]> possibleStates, int player, int king)
    {
        for (int i = 0; i < 8; i++) { //for each row on the board
            for (int j = 0; j < 8; j++) { //for each column on the board
                if (state[i][j]==player || state[i][j]==king) { //if the player has a piece in this position
                    if (jumpPoss(player,state,i,j,i+1,j+1,i+2,j+2)) { //if they can make a jump in this direction
                        int[][] clone = makeJump(state,player,i,j,i+1,j+1,i+2,j+2); //record the resulting state as a new state
                        if (takesPossPosition(player, clone, i+2, j+2)) { //if the same piece can make another jump, get those possible states
                            possibleStates = getJumpStatesPosition(clone, i+2, j+2, possibleStates, player, king); }
                        else { //add the new state to possibleStates
                            possibleStates.add(clone); }
                    }
                    if (jumpPoss(player,state,i,j,i+1,j-1,i+2,j-2)) { //the same as above, but in a different direction
                        int[][] clone = makeJump(state,player,i,j,i+1,j-1,i+2,j-2);
                        if (takesPossPosition(player, clone, i+2, j-2)) {
                            possibleStates = getJumpStatesPosition(clone, i+2, j-2, possibleStates, player, king); }
                        else {
                            possibleStates.add(clone); }
                    }
                    if (jumpPoss(player,state,i,j,i-1,j+1,i-2,j+2)) { //the same as above, but in a different direction
                        int[][] clone = makeJump(state,player,i,j,i-1,j+1,i-2,j+2);
                        if (takesPossPosition(player, clone, i-2, j+2)) {
                            possibleStates = getJumpStatesPosition(clone, i-2, j+2, possibleStates, player, king); }
                        else {
                            possibleStates.add(clone); }
                    }
                    if (jumpPoss(player,state,i,j,i-1,j-1,i-2,j-2)) { //the same as above, but in a different direction
                        int[][] clone = makeJump(state,player,i,j,i-1,j-1,i-2,j-2);
                        if (takesPossPosition(player, clone, i-2, j-2)) {
                            possibleStates = getJumpStatesPosition(clone, i-2, j-2, possibleStates, player, king); }
                        else {
                            possibleStates.add(clone); }
                    }
                }
            }
        }
        return possibleStates; //return the possible next states that were found
    }
    
    /**
     * A method to get the possible next states in the wake of a jump that would occur as the result of another jump.
     * @param int[][] state the state to get the next states for
     * @param int i the row the piece that has just made a jump is in
     * @param int j the column the piece that has just made a jump is in
     * @param ArrayList<int[][]> possibleStates an ArrayList of the possible next states
     * @param int player the player in question
     * @param int king the number corresponding to a king for the player in question
     * @return ArrayList<int[][]> an ArrayList of the possible next states
     */
    public ArrayList<int[][]> getJumpStatesPosition(int[][] state, int i, int j, ArrayList<int[][]> possibleStates, int player, int king)
    {
        //follows the same process as the body of the method above, checking for possible jumps from the given state in each direction
        if (jumpPoss(player,state,i,j,i+1,j+1,i+2,j+2)) { 
            int[][] clone = makeJump(state,player,i,j,i+1,j+1,i+2,j+2);
            if (takesPossPosition(player, clone, i+2, j+2)) {
                possibleStates = getJumpStatesPosition(clone, i+2, j+2, possibleStates, player, king); } //recursive call if yet another jump is possible
            else {
                possibleStates.add(clone); }
        }
        if (jumpPoss(player,state,i,j,i+1,j-1,i+2,j-2)) {
            int[][] clone = makeJump(state,player,i,j,i+1,j-1,i+2,j-2);
            if (takesPossPosition(player, clone, i+2, j-2)) {
                possibleStates = getJumpStatesPosition(clone, i+2, j-2, possibleStates, player, king); }
            else {
                possibleStates.add(clone); }
        }
        if (jumpPoss(player,state,i,j,i-1,j+1,i-2,j+2)) {
            int[][] clone = makeJump(state,player,i,j,i-1,j+1,i-2,j+2);
            if (takesPossPosition(player, clone, i-2, j+2)) {
                possibleStates = getJumpStatesPosition(clone, i-2, j+2, possibleStates, player, king); }
            else {
                possibleStates.add(clone); }
        }
        if (jumpPoss(player,state,i,j,i-1,j-1,i-2,j-2)) {
            int[][] clone = makeJump(state,player,i,j,i-1,j-1,i-2,j-2);
            if (takesPossPosition(player, clone, i-2, j-2)) {
                possibleStates = getJumpStatesPosition(clone, i-2, j-2, possibleStates, player, king); }
            else {
                possibleStates.add(clone); }
        }
        return possibleStates;
    }
    
    /**
     * A method to get the possible next states from the current state that would occur as a result of a step.
     * @param int[][] state the state to get the next states for
     * @param ArrayList<int[][]> possibleStates an ArrayList of the possible next states
     * @param int player the player in question
     * @param int king the number corresponding to a king for the player in question
     * @return ArrayList<int[][]> an ArrayList of the possible next states
     */
    public ArrayList<int[][]> getStepStates(int[][] state, ArrayList<int[][]> possibleStates, int player, int king)
    {
        if (possibleStates.size()==0) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (state[i][j]==player || state[i][j]==king) { //if the player has a piece in this square
                        if (stepPoss(player,state,i,j,i+1,j+1)) { //if it is possible to step in this direction
                            int[][] clone = makeStep(state,player,i,j,i+1,j+1); //record the resulting state as a new state
                            possibleStates.add(clone); } //add that state to possible states
                        if (stepPoss(player,state,i,j,i+1,j-1)) {
                            int[][] clone = makeStep(state,player,i,j,i+1,j-1);
                            possibleStates.add(clone); }
                        if (stepPoss(player,state,i,j,i-1,j+1)) {
                            int[][] clone = makeStep(state,player,i,j,i-1,j+1);
                            possibleStates.add(clone); }
                        if (stepPoss(player,state,i,j,i-1,j-1)) {
                            int[][] clone = makeStep(state,player,i,j,i-1,j-1);
                            possibleStates.add(clone); }
                    }
                }
            }
        }
        return possibleStates; //return the possible states
    }
    
    /**
     * Method to see whether a jump is possible from one given position to another.
     * @param int player the player who would be making the jump
     * @param int[][] state the game state the jump would be made from
     * @param int i the row the piece is jumping from
     * @param int j the column the piece is jumping from
     * @param int i1 the row the piece is jumping over
     * @param int j1 the column the piece is jumping over
     * @param int i2 the row the piece is jumping to
     * @param int j2 the column the piece is jumping to
     * @return boolean whether or not the jump is possible
     */
    public boolean jumpPoss(int player, int[][] state, int i, int j, int i1, int j1, int i2, int j2)
    {
        int king = setKing(player); //get the appropriate number representing a king for the given player
        if (i2<0 || i2>7 || j2<0 || j2>7) { //if the square to jump to does not exist on the board
            return false;
        }
        if (state[i][j]!=player && state[i][j]!=king) { //if the square being jumped from is not occupied by the player
            return false; 
        }
        if (state[i2][j2]!=0) { //if the square to jump to is not empty
            return false;
        }
        if (player==1) { //if the computer is moving
            if (i2 < i && state[i][j]!=king) { //if the move is up the board, and the piece is not a king
                return false; }
            else if (state[i1][j1]!=2 && state[i1][j1]!=4) { //if the square being jumped over is not occupied by the opponent
                return false; }
            else {
                return true; }
        }
        else { //if the user is moving
            if (i2 > i && state[i][j]!=king) { //if the move is down the board, and the piece is not a king
                return false; }
            else if (state[i1][j1]!=1 && state[i1][j1]!=3) { //if the square being jumped over is not occupied by the opponent
                return false; }
            else {
                return true; }
        }
    }
    
    /**
     * A method to determine whether a given player can make a take from the current game state.
     * @param int player the player in question
     * @param int[][] state the state in question
     * @return boolean whether or not a take is possible
     */
    public boolean takesPossState(int player, int[][] state)
    {
        boolean takePoss = false;
        //check to see if jump is possible from each square, in each direction
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (jumpPoss(player,state,i,j,i-1,j+1,i-2,j+2)) {
                    takePoss = true; }
                if (jumpPoss(player,state,i,j,i-1,j-1,i-2,j-2)) {
                    takePoss = true; }
                if (jumpPoss(player,state,i,j,i+1,j+1,i+2,j+2)) {
                    takePoss = true; }
                if (jumpPoss(player,state,i,j,i+1,j-1,i+2,j-2)) {
                    takePoss = true; }
            }
        }
        return takePoss;
    }
    
    /**
     * A method to determine whether a given player can make a take with a piece in a given position.
     * @param int player the player in question
     * @param int[][] state the state in question
     * @param int i the row the selected piece is in
     * @param int j the column the selected piece is in
     * @return boolean whether or not a take is possible
     */
    public boolean takesPossPosition(int player, int[][] state, int i, int j)
    {
        boolean takePoss = false;
        //from the given position, check whether a jump is possible in each direction
        if (jumpPoss(player,state,i,j,i-1,j+1,i-2,j+2)) {
            takePoss = true; }
        if (jumpPoss(player,state,i,j,i-1,j-1,i-2,j-2)) {
            takePoss = true; }
        if (jumpPoss(player,state,i,j,i+1,j+1,i+2,j+2)) {
            takePoss = true; }
        if (jumpPoss(player,state,i,j,i+1,j-1,i+2,j-2)) {
            takePoss = true; }
        return takePoss;
    }
    
    
    /**
     * Method to see whether a step is possible from one given position to another.
     * @param int player the player who would be making the step
     * @param int[][] state the game state the step would be made from
     * @param int i the row the piece is stepping from
     * @param int j the column the piece is stepping from
     * @param int i1 the row the piece is moving to
     * @param int j1 the column the piece is moving to
     * @return boolean whether or not the step is possible
     */
    public boolean stepPoss(int player, int[][] state, int i, int j, int i1, int j1)
    {
        int king = setKing(player);
        if (i1<0 || i1>7 || j1<0 || j1>7) { //if trying to move to square that does not exist
            return false;
        }
        if (state[i][j]!=player && state[i][j]!=king) { //if moving from square not occupied by player
            return false; 
        }
        if (state[i1][j1]!=0) { //if moving to square that is not empty
            return false;
        }
        if (player==1) { //if computer
            if (i1 < i && state[i][j]!=king) { //if stepping up the board and piece is not a king
                return false; }
            else {
                return true; }
        }
        else { //if user
            if (i1 > i && state[i][j]!=king) { //if moving down the board and piece is not a king
                return false; }
            else {
                return true; }
        }
    }
    
    /**
     * Method to make a jump, then return the resulting state as a new state.
     * @param int[][] state the game state the jump is happening from
     * @param int player the player making the jump
     * @param int i the row the piece is jumping from
     * @param int j the column the piece is jumping from
     * @param int i1 the row the piece is jumping over
     * @param int j1 the column the piece is jumping over
     * @param int i2 the row the piece is jumping to
     * @param int j2 the column the piece is jumping to
     * @return int[][] the resulting state
     */
    public int[][] makeJump(int[][] state, int player, int i, int j, int i1, int j1, int i2, int j2)
    {
        int[][] clone = cloneState(state);
        int king = setKing(player);
        if (state[i][j]==king) {
            player = king; }
        clone[i][j] = 0;
        clone[i1][j1] = 0;
        if(kingCheck(player,i2)) {
            clone[i2][j2] = king; }
        else {
            clone[i2][j2] = player; }
        return clone;
    }
    
    /**
     * Method to make a step, then return the resulting state as a new state
     * @param int[][] state the game state the step is being made from
     * @param int player the player making the step
     * @param int i the row the piece is stepping from
     * @param int j the column the piece is stepping from
     * @param int i1 the row the piece is moving to
     * @param int j1 the column the piece is moving to
     * @return int[][] the resulting state
     */
    public int[][] makeStep(int[][] state, int player, int i, int j, int i1, int j1)
    {
        int[][] clone = cloneState(state);
        int king = setKing(player);
        if (state[i][j]==king) {
            player = king; }
        clone[i][j] = 0;
        if(kingCheck(player,i1)) {
            clone[i1][j1] = king; }
        else {
            clone[i1][j1] = player; }
        return clone;
    }
    
    /**
     * A method to clone a given state.
     * @param int[][] the state to be cloned
     * @return int[][] the clone
     */
    public int[][] cloneState(int[][] state)
    {
        int[][] clone = new int[8][8]; //initialising the clone state
        int temp; //an int to keep a record of numbers being passed from one int[][] to the other
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                temp = state[i][j];
                clone[i][j] = temp;
            }
        }
        return clone;
    }
    
    /**
     * A method to return the appropriate number representing a king piece for a given player.
     * @param int player the player in question
     * @return int the number representing a king piece
     */
    public int setKing(int player)
    {
        int king;
        if (player==1) {
            king = 3; }
        else {
            king = 4; }
        return king;
    }
    
    /**
     * Checking for when a piece reaches the end of the board and needs to be come a king.
     * @param int player the player who's piece it is
     * @param x the row on the board the piece has reached
     * @return boolean whether or not the piece is now a king
     */
    public boolean kingCheck(int player, int x)
    {
        if (player==1 && x==7) {
            return true; }
        else if (player==2 && x==0) {
            return true; }
        else {
            return false; }
    }
    
    /**
     * Checking to see if the user has won the game in a given state.
     * @param int[][] the state we want to check for
     * @return boolean whether or not the user wins in this state
     */
    public boolean wWins(int[][] state)
    {
        boolean wins = false;
        int bCount = 0; //to count the number of black pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(state[i][j]==1 || state[i][j]==3) { //include the presence of kings
                    bCount++; }
            }
        }
        if (bCount==0) { //if there are no black pieces
            wins = true; }
        return wins;
    }
    
    /**
     * Checking to see if the computer has won the game in a given state.
     * @param int[][] the state we want to check for
     * @return boolean whether or not the computer wins in this state
     */
    public boolean bWins(int[][] state)
    {
        boolean wins = false;
        int wCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(state[i][j]==2 || state[i][j]==4) {
                    wCount++; }
            }
        }
        if (wCount==0) {
            wins = true; }
        return wins;
    }
    
    /**
     * The minimax algorithm that is called by getAIMove that calls itself recursively to get a value for each possible next state. 
     * Goes to a depth specified by a limit set by the difficulty level (see setLimit() below), passing a value up the tree when that depth is reached,
     * when a state is reached in which one of the players wins, or when a state is reached in which the player cannot make any more moves.
     * @param int[][] state the state from which it is being called
     * @param int depth how far it is going down the search tree
     * @param int player the player who is making the next (imaginary) move
     * @param int alpha the value of alpha at the previous state
     * @param int beta the value of beta at the previous state
     * @return int a value corresponding to the result reached
     */
    public int minimax(int[][] state, int depth, int player, int alpha, int beta)
    {
        int bestScore;
        if(player==1) {
            bestScore = -24; } //the worst possible score for the computer (that isn't even possible), when there are 12 white kings on the board and nothing else
        else {
            bestScore = 24; } //the worst possible score for the human (that isn't even possible), when there are 12 black kings on the board and nothing else
        ArrayList<int[][]> availableStates = getPossibleStates(state, player); //the available states from the given state
        
        if (depth==0) { //if this call has come from getAIMove(), start the evaluations afresh
            successorEvaluations.clear();
        }

        if (bWins(state)) { //give a high value for the computer winning - this is the ultimate goal, after all!
            return 24;
        }
        if (wWins(state)) { //give a low value for the human winning - this is the ultimate goal, after all!
            return -24;
        }
        if (availableStates.isEmpty()) { //if no moves can be made, a draw
            return 0;
        }
        if (depth==limit) { //if the depth limit has been hit, evaluate the state reached
            int value = evaluateState(state);
            return value;
        }
        
        for (int i=0; i < availableStates.size(); i++) { //going down the search tree, depth-first
            int[][] s = availableStates.get(i);
            if (player==1) {
                int currentScore = minimax(s, depth + 1, 2, alpha, beta); //get the minimax value for the other player at the next level down
                bestScore = Math.max(bestScore, currentScore); //if currentScore from minimax evaluation just carried out is better than bestScore, update bestScore
                alpha = Math.max(currentScore, alpha); //if currentScore from minimax evaluation just carried out is better than alpha, update alpha
                if (depth==0) { //when we've run through all the recursive calls and reached the top again, store the value found with the next state in question
                    successorEvaluations.add(new StatesAndScores(s, currentScore)); } 
            }
            else if (player==2) {
                int currentScore = minimax(s, depth + 1, 1, alpha, beta); //get the minimax value for the other player at the next level down
                bestScore = Math.min(bestScore, currentScore); //if currentScore from minimax evaluation just carried out is better (lower) than bestScore, update bestScore
                beta = Math.min(currentScore, beta); //if currentScore from minimax evaluation just carried out is better (lower) than beta, update beta
            }
            if (alpha>=beta) { //there is no point going any further, so break out of the for loop
                break;
            }
        }
        return bestScore; //pass the best score found at this depth up a level
    }
    
    /**
     * A method to return a value for the state reached when the depth limit has been hit in minimax. This operates as a heuristic, with the value calculated
     * by subtracting the number of white pieces from the number of black pieces, then adding the number of black kings (multiplied by 2, to give kings more
     * weight), and subtracting the number of white kings (multiplied by 2, again). In the minimax algorithm, the player who represents "MAX" (player 1 / the
     * computer) wants this to be a high score, and "MIN" (player 2 / the user) wants it to be low.
     * @param int[][] the state reached
     * @return int the value given
     */
    public int evaluateState(int[][] state)
    {
        int bCount = 0; //to monitor the number of black pieces
        int wCount = 0; //to monitor the number of white pieces
        int bKCount = 0; //to monitor the number of black kings
        int wKCount = 0; //to monitor the number of white kings
        //search through each square in the current state, updating the counts when we encounter corresponding pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state[i][j]==1) {
                    bCount++; }
                else if (state[i][j]==2) {
                    wCount++; }
                else if (state[i][j]==3) {
                    bKCount++; }
                else if (state[i][j]==4) {
                    wKCount++; }
            }
        }
        int value = bCount - wCount + (2*bKCount) - (2*wKCount); //giving extra weight to kings
        return value;
    }
    
    /**
     * Searches through our ArrayList of successorEvaluations and returns the best next state given the current state.
     * @return int[][] the best next state
     */
    public int[][] getBestState()
    {
        int maxScore = Integer.MIN_VALUE;
        int[][] bestState = null;
        
        for (int i=0; i<successorEvaluations.size(); i++) {
            if (maxScore < successorEvaluations.get(i).score) {
                maxScore = successorEvaluations.get(i).score;
                bestState = successorEvaluations.get(i).state;
            }
        }
        return bestState;
    }
    
    /**
     * Set a limit on the number of static evaluations the minimax algorithm can make, based on the difficulty
     * level selected by the user before starting the game.
     * @param int the difficulty level selected.
     */
    public void setLimit(int difficulty)
    {
        if (difficulty==0) {
            limit = 0; }
        else if (difficulty==1) {
            limit = 1; }
        else if (difficulty==2) {
            limit = 2; }
        else if (difficulty==3) {
            limit = 5; }
        else {
            limit = 10; }
    }
}
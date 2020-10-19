
/**
 * A very simple class, with objects intended to store states alongside values found for them in the 
 * evaluation methods of the Board class.
 * 
 * @author Lewis Lloyd 
 * @version 1
 */
public class StatesAndScores
{
    int score; //the score found for the given state
    int[][] state; //the state in question
    
    public StatesAndScores(int[][] state, int score) 
    {
        this.score = score;
        this.state = state;
    }
}
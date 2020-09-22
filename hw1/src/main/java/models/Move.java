package models;

/**
 * Represents a move in the game. 
 * @author kenyaplenty
 *
 */
public class Move {

  private Player player;

  private int moveX;

  private int moveY;
  
  /** Creates a new instance of the Move class. 
   * @param player represents the player making the move
   * @param moveX represents the x coordinate of the move
   * @param moveY represents the y coordinate of the move
   */
  
  public Move(Player player, int moveX, int moveY) {
    this.player = player; 
    this.moveX = moveX;
    this.moveY = moveY; 
  }

  /** Returns the player making the move. 
   * @return the player
   */
  public Player getPlayer() {
    return player;
  }
  
  /**
   * Sets a move's player to the input player.
   * @param player represents the player making the move
   */
  public void setPlayer(Player player) {
    this.player = player; 
  }
 

  /** Returns the x coordinate of the move.
   * @return the moveX
   */
  public int getMoveX() {
    return moveX;
  }
  
  /*
   * Sets the x coordinate of the the move to the input moveX
   * @param moveX represents the x coordinate of the move
   */
  public void setMoveX(int moveX) {
    this.moveX = moveX; 
  }

  /** Returns the y coordinate of the move. 
   * @return the moveY
   */
  public int getMoveY() {
    return moveY;
  }
  
  /*
  * Sets the y coordinate of the move to the input moveY.
  * @param moveY represents the y coordinate of the move
  */
  public void setMoveY(int moveY) {
    this.moveY = moveY; 
  }
}

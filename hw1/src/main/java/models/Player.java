package models;

/**
 * This class represents a player in the game.
 * @author kenyaplenty
 *
 */
public class Player {

  private char type;

  private int id;
  
  public Player(char playerSymbol, int playerId) {
    this.type = playerSymbol; 
    this.id = playerId; 
  }

  /** Returns a player's symbol in the game.
   * @return the type
   */
  public char getType() {
    return type;
  }

  /** Sets a player's symbol for the game.
   * @param type is a char that represents a player's symbol
   */
  public void setType(char type) {
    this.type = type;
  }

  /** Returns a player's id. 
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**Sets a player's id.
   * @param id an integer representing what the player's id should be
   */
  public void setId(int id) {
    this.id = id;
  }

}

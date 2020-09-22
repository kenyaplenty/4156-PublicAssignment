package models;

/** 
 * This class represents messages sent during the game.
 * @author kenyaplenty
 *
 */

public class Message {

  private boolean moveValidity;

  private int code;

  private String message;
  
  /** Creates a new instance of the Message class.
   * @param moveValidity is a boolean representing if the move was valid
   * @param code is an integer representing the error status associated with the message
   * @param message is a string representing the contents of the message
   */
  public Message(boolean moveValidity, int code, String message) {
    this.moveValidity = moveValidity; 
    this.code = code; 
    this.message = message; 
  }

  /** 
   * Returns whether or not the move is valid. 
   * @return the moveValidity
   */
  public boolean isMoveValidity() {
    return moveValidity;
  }

  /** 
   * Sets the value of moveValidity to the parameter moveValidity.
   * @param moveValidity the moveValidity to set
   */
  public void setMoveValidity(boolean moveValidity) {
    this.moveValidity = moveValidity;
  }

  /** 
   * Returns the error status of the message.
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /** 
   * Sets the error status of the message to the parameter code. 
   * @param code the code to set
   */
  public void setCode(int code) {
    this.code = code;
  }

  /**
   * Returns the string or body of the message.
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /** Sets the string or body of the message to the input message.
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

}

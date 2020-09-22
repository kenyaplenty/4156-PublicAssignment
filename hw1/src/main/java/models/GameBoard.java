package models;

/**
 * GameBoard adds moves to the board and updates game status. 
 * @author kenyaplenty
 *
 */

public class GameBoard {

  private Player p1;

  private Player p2;

  private boolean gameStarted;

  private int turn;

  private char[][] boardState;

  private int winner;

  private boolean isDraw;
  
  /** Creates a new instance of the GameBoard.
   */
  public GameBoard() {
    this.gameStarted = false;
    this.turn = 1; 
    this.boardState = new char[3][3];
    this.winner = 0; 
    this.isDraw = false; 

  }
  
  /** Returns an instance of Player 1.
   * @return the p1
   */
  
  public Player getP1() {
    return p1;
  }

  /** Returns an instance of Player 2. 
   * @param p1 the p1 to set
  */
  public void setP1(Player p1) {
    this.p1 = p1;
  }

  /** Returns an instance of Player 2. 
   * @return the p2
   */
  public Player getP2() {
    return p2;
  }

  /** Sets p2 to a passed in instance of player.
  * @param p2 the p2 to set
  */
  public void setP2(Player p2) {
    this.p2 = p2;
  }
  
  /** Sets the value of gameStarted.
   * @param gameStarted a boolean indicating whether or not the game has started
   * 
   */
  public void setGameStarted(boolean gameStarted) {
    this.gameStarted = gameStarted; 
  }
  
  /** Returns the current turn.
   * @return the turn 
   */
  public int getTurn() {
    return this.turn; 
  }
  
  /** 
   * Sets the turn field to the integer passed in using the turn parameter.
   * @param turn represents the current player's turn as an integer
   */
  public void setTurn(int turn) {
    this.turn = turn; 
  }
  
  /**
   *  Checks if a move can be made at this time.
   * @param move represents a Move instance 
   */
  public boolean isValidMove(Move move) {
    if (this.gameStarted && isSpaceFree(move) && isPlayerTurn(move)) {
      return true; 
    }
    return false; 
  }
  
  /**
   * Checks to see if it is time for a player to make a turn. 
   * @return boolean stating whether or not the current player can make a move
   * 
   */
  private boolean isPlayerTurn(Move move) {
    return this.turn == move.getPlayer().getId();
  }
  
  /** 
   * Checks if the space described in the move is free.
   * @param move represents a Move object
   * @return a boolean representing whether or not the space is free
   */
  private boolean isSpaceFree(Move move) {
    int moveX = move.getMoveX(); 
    int moveY = move.getMoveY(); 
    if (boardState[moveX][moveY] == 'X' || boardState[moveX][moveY] == 'O') {
      return false; 
    }
    return true; 
  }
  
  /** 
   * Adds a new Move to the game board and switches it over to the other player's turn. 
   * @param move is a move object that represents the move that will be added to the board
   */
  public void addMoveToBoardAndSwitchesTurns(Move move) {
    int moveX = move.getMoveX();
    int moveY = move.getMoveY(); 
    char symbol = move.getPlayer().getType();
    this.boardState[moveX][moveY] = symbol;
    this.turn = move.getPlayer().getId() == 1 ? 2 : 1; 
  }
 
  
  /**
   * Checks to see if a specific player has won the game.
   * @param player represents the player whose victory we are assessing
   * @return
   */
  public boolean playerWonGame(Player player) {
    if (playerWonGameWithDiagonalLine(player) || playerWonGameWithHorizontalLine(player)
        || playerWonGameWithVerticalLine(player)) {
      return true; 
    }
    return false; 
    
  }
  
  /**
   * Checks whether or not the game has ended in a draw. 
   * @return boolean indicating whether or not the game has been won
   */
  public boolean isGameDraw() {
    if (this.winner == 0 && isBoardFull() && this.gameStarted) {
      return true; 
    }
    return false; 
  }
  
  /**
   * Changes the necessary parameters so the game can end in a draw.
   */
  
  public void setGameDraw() {
    this.gameStarted = false; 
    this.winner = 0; 
    this.isDraw = true; 
  }
  
  /** 
   * Checks to see if a player has won with a diagonal line.
   * @param player represents the player whose victory we are assessing
   * @return whether a player has won with a diagonal line
   */
  private boolean playerWonGameWithDiagonalLine(Player player) {
    char playerSymbol = player.getType();
    if (boardState[0][0] == playerSymbol && boardState[1][1] == playerSymbol 
        && boardState[2][2] == playerSymbol) {
      return true; 
    } else if (boardState[0][2] == playerSymbol && boardState[1][1] == playerSymbol 
        && boardState[2][0] == playerSymbol) {
      return true; 
    }
    return false; 
  }
  
  /**
   * Checks to see if a player has won with a horizontal line. 
   * @param player represents the player whose victory we are assessing
   * @return whether a player has won with a horizontal line
   */
  private boolean playerWonGameWithHorizontalLine(Player player) {
    char playerSymbol = player.getType();
    for (int i = 0; i < 3; i++) {
      if (boardState[i][0] == playerSymbol && boardState[i][1] == playerSymbol 
          && boardState[i][2] == playerSymbol) {
        return true; 
      }
    
    }
    return false;  
  }
  
  /**
   * Checks to see if a player has won with a vertical line.
   * @param player represents the player whose victory we are assessing
   * @return whether a player has won with a vertical line
   * 
   */
  private boolean playerWonGameWithVerticalLine(Player player) {
    char playerSymbol = player.getType(); 
    for (int i = 0; i < 3; i++) {
      if  (boardState[0][i] == playerSymbol && boardState[1][i] == playerSymbol 
          && boardState[2][i] == playerSymbol) {
        return true; 
      }
    
    }
    return false; 
  }
  
  /**
   * Checks to see if all the spaces on the board are full. 
   * @return a boolean representing whether or not all the spaces on the board are full
   */
  private boolean isBoardFull() {
    for (int i = 0; i < 3; i++) {
      for  (int j = 0; j < 3; j++) {
        if (boardState[i][j] != 'X' && boardState[i][j] != 'O') {
          return false; 
        }
      }
    }
    return true; 
  }
  
  /**
   *  Designates the winner of the game and ends the game.
   *  @param player represents the player that has won the game
   */
  
  public void endsGameAndSetsWinner(Player player) {
    this.winner = player.getId(); 
    this.gameStarted = false; 
  
  }
 
  
  /** 
   * Generates a message telling the user that they successfully made a move.
   * @return a message indicating a good move
   */
  public Message generateValidMoveMessage() {
    return new Message(true, 100, "");
  }

  /**
   * Generates a message telling the users that the game has ended in a draw. 
   * @return
   */
  public Message generateDrawMessage() {
    return new Message(false, -100, "Game Over. It was a DRAW!"); 
  }
  
  /**
   * Generates a message telling the user that their move was unsuccessful.
   * @return a message indicating that a move was bad
   */
  
  public Message generateInvalidMoveMessage() {
    Message moveMessage = new Message(false, -1,
        "ERROR: It is not your turn or the space you have chosen is full.");
    return moveMessage; 
  }
  
  
}

/*
 * The isGameDraw method in my game checks that 
 * there has not been a winner assigned to the game, 
 * that the game board is full and that 
 */

package unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import models.GameBoard;
import models.Move;
import models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



/** 
 * This is a class that unit tests all of the important methods in the 
 * GameBoard class.
 * @author kenyataplenty
 *
 */
public class GameBoardTest {
  GameBoard gameBoard;
  Player testPlayer1;
  Player testPlayer2;

  /**
   * This method resets the game board, initializes the players and starts the game.
   */
  @BeforeEach
  public void init() {
    gameBoard = new GameBoard();
    testPlayer1 = new Player('X', 1);
    testPlayer2 = new Player('O', 2);
    gameBoard.setGameStarted(true);
  }

  
  /**
   * This method checks that the gameBoard will think that a move is valid as long
   * as: its the current player's turn, the space is free, and the game is not
   * over yet.
   */
  @Test
  public void testIsValidMoveWithAllCriteriaMet() {
    Move currentMove = new Move(testPlayer1, 0, 0);
    assertTrue(gameBoard.isValidMove(currentMove));
  }

  @Test
  public void testIsValidMoveWithFullSpace() {
    Move currentMove = new Move(testPlayer1, 0, 0);
    gameBoard.addMoveToBoardAndSwitchesTurns(currentMove);
    Move duplicateMove = new Move(testPlayer2, 0, 0);
    assertFalse(gameBoard.isValidMove(duplicateMove));
  }

  @Test
  public void testIsValidMoveWithGameNotStarted() {
    gameBoard.setGameStarted(false);
    Move currentMove = new Move(testPlayer1, 0, 0);
    assertFalse(gameBoard.isValidMove(currentMove));
  }

  @Test
  public void testIsValidMoveWhenPlayer2TriesToStartGame() {
    Move currentMove = new Move(testPlayer2, 1, 2);
    assertFalse(gameBoard.isValidMove(currentMove));
  }

  @Test
  public void testIsValidMoveWhenNotPlayersTurn() {
    Move firstMove = new Move(testPlayer1, 0, 0);
    gameBoard.addMoveToBoardAndSwitchesTurns(firstMove);
    Move newMove = new Move(testPlayer1, 1, 1);
    assertFalse(gameBoard.isValidMove(newMove));
  }

  @Test
  public void testplayerWonWithVerticalLine() {
    for (int i = 0; i < 3; i++) {
      Move currentMove = new Move(testPlayer1, i, 0);
      gameBoard.addMoveToBoardAndSwitchesTurns(currentMove);
      gameBoard.setTurn(1);
    }
    assertTrue(gameBoard.playerWonGame(testPlayer1));

  }

  @Test
  public void testPlayerWonWithHorizontalLine() {
    for (int i = 0; i < 3; i++) {
      Move currentMove = new Move(testPlayer1, 0, i);
      gameBoard.addMoveToBoardAndSwitchesTurns(currentMove);
      gameBoard.addMoveToBoardAndSwitchesTurns(currentMove);
    }

    assertTrue(gameBoard.playerWonGame(testPlayer1));
  }

  @Test
  public void testPlayerWonWithAlmostVerticalLine() {
    Move firstMove = new Move(testPlayer1, 0, 0);
    gameBoard.addMoveToBoardAndSwitchesTurns(firstMove);
    Move secondMove = new Move(testPlayer2, 1, 0);
    gameBoard.addMoveToBoardAndSwitchesTurns(secondMove);
    Move finalMove = new Move(testPlayer1, 2, 0);
    gameBoard.addMoveToBoardAndSwitchesTurns(finalMove);
    assertFalse(gameBoard.playerWonGame(testPlayer1));
  }

  @Test
  public void testPlayerWonWithAlmostHorizontalLine1() {
    Move firstMove = new Move(testPlayer1, 0, 0);
    gameBoard.addMoveToBoardAndSwitchesTurns(firstMove);
    Move secondMove = new Move(testPlayer2, 0, 1);
    gameBoard.addMoveToBoardAndSwitchesTurns(secondMove);
    Move finalMove = new Move(testPlayer1, 0, 2);
    gameBoard.addMoveToBoardAndSwitchesTurns(finalMove);
    assertFalse(gameBoard.playerWonGame(testPlayer1));
  }

  @Test
  public void testPlayerWonWithAlmostHorizontalLine() {
    Move firstMove = new Move(testPlayer1, 0, 0);
    gameBoard.addMoveToBoardAndSwitchesTurns(firstMove);
    Move secondMove = new Move(testPlayer2, 1, 1);
    gameBoard.addMoveToBoardAndSwitchesTurns(secondMove);
    Move finalMove = new Move(testPlayer1, 2, 2);
    gameBoard.addMoveToBoardAndSwitchesTurns(finalMove);
    assertFalse(gameBoard.playerWonGame(testPlayer1));
  }

  @Test
  public void testPlayerWonwithDrawBoard() {
    String[] player1Moves = { "00", "02", "10", "21", "22" };
    String[] player2Moves = { "01", "11", "12", "20" };
    for (int i = 0; i < player1Moves.length; i++) {
      int moveX = Character.getNumericValue(player1Moves[i].charAt(0));
      int moveY = Character.getNumericValue(player1Moves[i].charAt(1));
      Move currentMove = new Move(testPlayer1, moveX, moveY);
      gameBoard.addMoveToBoardAndSwitchesTurns(currentMove);
      gameBoard.setTurn(1);
    }

    for (int j = 0; j < player2Moves.length; j++) {
      int moveX = Character.getNumericValue(player2Moves[j].charAt(0));
      int moveY = Character.getNumericValue(player2Moves[j].charAt(1));
      Move currentMove = new Move(testPlayer2, moveX, moveY);
      gameBoard.addMoveToBoardAndSwitchesTurns(currentMove);
      gameBoard.setTurn(2);
    }

    assertFalse(gameBoard.playerWonGame(testPlayer1));

  }

  @Test
  public void testIsGameDrawWithDrawBoard() {
    String[] player1Moves = { "00", "02", "10", "21", "22" };
    String[] player2Moves = { "01", "11", "12", "20" };
    for (int i = 0; i < player1Moves.length; i++) {
      int moveX = Character.getNumericValue(player1Moves[i].charAt(0));
      int moveY = Character.getNumericValue(player1Moves[i].charAt(1));
      Move currentMove = new Move(testPlayer1, moveX, moveY);
      gameBoard.addMoveToBoardAndSwitchesTurns(currentMove);
      gameBoard.setTurn(1);
    }

    for (int j = 0; j < player2Moves.length; j++) {
      int moveX = Character.getNumericValue(player2Moves[j].charAt(0));
      int moveY = Character.getNumericValue(player2Moves[j].charAt(1));
      Move currentMove = new Move(testPlayer2, moveX, moveY);
      gameBoard.addMoveToBoardAndSwitchesTurns(currentMove);
      gameBoard.setTurn(2);
    }

    assertTrue(gameBoard.isGameDraw());
  }

  @Test
  public void testIsDrawWithEmptyBoard() {
    assertFalse(gameBoard.isGameDraw());
  }

  @Test
  public void testIsDrawWithGameNotStarted() {
    gameBoard.setGameStarted(false);
    assertFalse(gameBoard.isGameDraw());
  }
}
package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import controllers.PlayGame;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import models.GameBoard;
import models.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;



/**
 * The following sets of test specifically ensures that the
 * back end of the game will reload after 
 * a simulated crash. From my understanding, 
 * we do not have to deal with the 
 * front end keeping up with what changes were
 * made after the reboot, so I have not worried
 * about this in my code. 
 * 
 * @author kenyataplenty
 *
 */

class GameRebootTest {
  
  Gson gson = new Gson(); 
  
  /**
   * Runs only once before the test starts.
   */
  @BeforeAll
  public static void init() {
    //Start Server
    PlayGame.main(null);
    System.out.println("Before All"); 
  }
  
  /**
   * This method starts a new game before every test run. It will run every time before a test.
   */
  @BeforeEach
  public void startNewGame() {
    // Test if server is running. You need to have an endpoint /
    // If you do not wish to have this end point, it is okay to not have anything in this method.
    HttpResponse response = Unirest.get("http://localhost:8080/").asString();
    int restStatus = response.getStatus();

    System.out.println("Before Each");
  }

  /**
   * This test case ensures that the GameBoard is
   * correctly reloaded from the database 
   * if Player1 has joined and nothing else has 
   * happened.
   */
  @Test 
  @Order(1)
  public void checkGameBoardRecoversCorrectlyAfterPlayer1Joins() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    
    PlayGame.stop(); 
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
  
      e.printStackTrace();
    } 
    
    PlayGame.startGame();
    
    HttpResponse getBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString(); 
    
    String boardResponse = (String) getBoardResponse.getBody(); 
    
    JSONObject board = new JSONObject(boardResponse);
    
    GameBoard gameBoard = gson.fromJson(board.toString(), GameBoard.class); 
    
    Player player1 = gameBoard.getP1(); 
    
    assertEquals('X', player1.getType()); 
    assertEquals(1, player1.getId()); 
    
    System.out.println("---------- Test After Player1 Joins ----------"); 
  }
  
  /**
   * This test case ensures that the GameBoard is
   * correctly reloaded from the database 
   * if Player1 and Player2 have joined and nothing else has 
   * happened.
   */
  @Test 
  @Order(2)
  public void checkGameBoardRecoverCorrectlyAfterPlayer2Joins() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString(); 
    
    PlayGame.stop(); 
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
  
      e.printStackTrace();
    } 
    
    PlayGame.startGame();
    
    HttpResponse getBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString(); 
    
    String boardResponse = (String) getBoardResponse.getBody(); 
    
    JSONObject board = new JSONObject(boardResponse);
    
    GameBoard gameBoard = gson.fromJson(board.toString(), GameBoard.class); 
    
    Player player1 = gameBoard.getP1(); 
    Player player2 = gameBoard.getP2(); 
    
    assertEquals('X', player1.getType()); 
    assertEquals(1, player1.getId()); 
    assertEquals('O', player2.getType()); 
    assertEquals(2, player2.getId()); 
    assertEquals(board.get("gameStarted"), true); 

    System.out.println("---------- Test After Player2 Joins ----------"); 
  }
  
  /**
   * This test case ensures that the GameBoard is
   * correctly reloaded after some moves 
   * have been put onto the board. 
   */
  @Test 
  @Order(3)
  public void checkGameBoardRecoverCorrectlyAfterSomeMoves() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=1&y=0").asString();
   
    PlayGame.stop(); 
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
  
      e.printStackTrace();
    } 
    
    PlayGame.startGame();
    
    HttpResponse getBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString(); 
    
    String boardResponse = (String) getBoardResponse.getBody(); 
    
    JSONObject board = new JSONObject(boardResponse);
    
    GameBoard gameBoard = gson.fromJson(board.toString(), GameBoard.class); 
    
    Player player1 = gameBoard.getP1(); 
    Player player2 = gameBoard.getP2(); 
    
    assertEquals('X', player1.getType()); 
    assertEquals(1, player1.getId()); 
    assertEquals('O', player2.getType()); 
    assertEquals(2, player2.getId()); 
    assertEquals(2, gameBoard.getTurn()); 
   
    System.out.println("---------- Test After Some Moves Made ----------"); 
  }
  
  /**
   * This test case ensures that the GameBoard is
   * correctly reloaded from the database
   * after Player1 has won the game.
   */
  @Test
  @Order(4)
  public void checkGameBoardRebootsCorrectlyAfterP1Victory() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
    Unirest.get("http://localhost:8080/joingame").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=0&y=1").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=1&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=1&y=1").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=2&y=0").asString(); 
    
    PlayGame.stop();
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
  
      e.printStackTrace();
    } 
    
    PlayGame.startGame();
    
    HttpResponse getGameBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString();
    String boardResponse = (String) getGameBoardResponse.getBody();
    JSONObject boardResponseObject = new JSONObject(boardResponse);
    
    assertEquals(false, boardResponseObject.get("gameStarted"));
    assertEquals(1, boardResponseObject.get("winner"));
    
    System.out.println("------- Test Player1 Can Win Game -------");
  }
  
  /**
   * This test case ensures that the GameBoard is
   * correctly reloaded from the database
   * after Player2 has won the game.
   */
  @Test
  @Order(5)
  public void checkGameBoardRebootsCorrectlyAfterP2Victory() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=1&y=1").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=1&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=2&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=2&y=1").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=0&y=2").asString(); 
    
    PlayGame.stop(); 
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
  
      e.printStackTrace();
    } 
    
    PlayGame.startGame();
    
    HttpResponse getGameBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString(); 
    
    String boardResponse = (String) getGameBoardResponse.getBody();
    JSONObject boardResponseObject = new JSONObject(boardResponse); 
    
    assertEquals(false, boardResponseObject.get("gameStarted"));
    assertEquals(2, boardResponseObject.get("winner"));
    
    System.out.println("------ Test After Player2 Wins -------"); 
  }
  
  /**
   * This test case ensures that the GameBoard is
   * correctly reloaded from the database
   * after the game has ended in a tie. 
   */
  @Test
  @Order(6)
  public void checkGameBoardRebootsCorrectlyAfterDraw() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=0&y=1").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=2").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=1&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=2&y=2").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=1&y=2").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=2&y=1").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=2&y=0").asString();
    
    
    PlayGame.stop(); 
    
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
  
      e.printStackTrace();
    } 
    
    PlayGame.startGame();
    
    HttpResponse getGameBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString(); 
    
    String boardResponse = (String) getGameBoardResponse.getBody(); 
    
    JSONObject board = new JSONObject(boardResponse);
    
    assertEquals(board.get("isDraw"), true); 
    
    
    
    System.out.println("---- Testing After Game Ends With Draw ---");
  }
  
  /**
   * This test case ensures that the GameBoard is
   * correctly reloaded from the database
   * after an invalid move has been made.
   */
  @Test
  @Order(7)
  public void checkGameBoardRebootsAfterInvalidMove() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
    Unirest.get("http://localhost:8080/joingame").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=1&y=0").asString(); 
    
    PlayGame.stop(); 
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
  
      e.printStackTrace();
    } 
    
    PlayGame.startGame();
    
    HttpResponse getGameBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString(); 
    
    String boardResponse = (String) getGameBoardResponse.getBody(); 
    
    JSONObject board = new JSONObject(boardResponse);
    
    GameBoard gameBoard = gson.fromJson(board.toString(), GameBoard.class); 
    
    assertEquals(gameBoard.getTurn(), 2); 
    
    System.out.println("--- Testing After Invalid Move ---");
  }
  
  

  /**
   * This will run every time after a test has finished.
   */
  @AfterEach
  public void finishGame() {
    System.out.println("After Each");
  }

  /**
   * This method runs only once after all the test cases have been executed.
   */
  @AfterAll
  public static void close() {
    // Stop Server
    PlayGame.stop();
    System.out.println("After All");
  }
  
  
}

 



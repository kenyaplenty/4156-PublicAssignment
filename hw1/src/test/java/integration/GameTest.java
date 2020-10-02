package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import controllers.PlayGame;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import models.GameBoard;
import models.Message;
import models.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


class GameTest {
  
  Gson gson = new Gson(); 

  /**
   * Runs only once before the testing starts.
   */
  @BeforeAll
  public static void init() {
    // Start Server
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
   * This is a test case to evaluate the newgame endpoint.
   */
  @Test
  @Order(1)
  public void newGameTest() {

    // Create HTTP request and get response
    HttpResponse response = Unirest.get("http://localhost:8080/newgame").asString();
    int restStatus = response.getStatus();

    // Check assert statement (New Game has started)
    assertEquals(restStatus, 200);
    System.out.println("Test New Game");
  }

  /**
   * This is a test case to evaluate the startgame endpoint.
   */
  @Test
  @Order(2)
  public void startGameTest() {

    // Create a POST request to startgame endpoint and get the body
    // Remember to use asString() only once for an endpoint call. 
    //Every time you call asString(), a new request will be sent to the endpoint. 
    //Call it once and then use the data in the object.
    HttpResponse response = Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    String responseBody = (String) response.getBody();

    // --------------------------- JSONObject Parsing ----------------------------------

    System.out.println("Start Game Response: " + responseBody);

    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);

    // Check if game started after player 1 joins: Game should not start at this point
    assertEquals(false, jsonObject.get("gameStarted"));

    // ---------------------------- GSON Parsing -------------------------

    // GSON use to parse data to object
    Gson gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    Player player1 = gameBoard.getP1();

    // Check if player type is correct
    assertEquals('X', player1.getType());

    System.out.println("Test Start Game");
  }
  
  /**
   * This method checks that the first player cannot make a move
   * without the second player joining the game.
   */
  @Test
  @Order(3)
  public void testPlayer1CannotMakeMoveWithoutPlayer2() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    HttpResponse<?> move1Response = Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString(); 
    
    String moveResponse = (String) move1Response.getBody();
    
    JSONObject moveResponseObject = new JSONObject(moveResponse);
    
    Message moveMessage = gson.fromJson(moveResponseObject.toString(), Message.class);
    
    assertEquals(moveMessage.getCode(), -1);
    assertFalse(moveMessage.getMoveValidity());
    
   
    System.out.println("----- Test Player 1 Cannot Make The First Move Without Player2 -----"); 
  }
  
  /**
   * This method checks that the second player was successfully added to the tic
   * tac toe game. 
   */
  
  @Test
  @Order(4)
  public void testPlayer2JoinsGame() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString(); 
    HttpResponse getBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString(); 
    
    String boardResponse = (String) getBoardResponse.getBody(); 
    
    JSONObject board = new JSONObject(boardResponse);
    
    GameBoard gameBoard = gson.fromJson(board.toString(), GameBoard.class); 
    Player player2 = gameBoard.getP2(); 
    
    //Check if the player type is correct 
    assertEquals('O', player2.getType()); 
    
    //Check if the player id is correct 
    assertEquals(2, player2.getId()); 
    
    System.out.println("---------- Test Player2 Joins Game ----------"); 
    
  }
  
  /**
   * This method tests that the first player's move 
   * shows up successfully on the board.
   * Because I did not make a public method to get the 
   * state of the game board, I am relying on the correct
   * message response to ensure that 
   */
  
  @Test
  @Order(5)
  public void testPlayer1CanMakeFirstMove() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
    Unirest.get("http://localhost:8080/joingame").asString(); 
    HttpResponse move1Response = Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString(); 
    
    String moveResponse = (String) move1Response.getBody(); 
    JSONObject moveResponseObject = new JSONObject(moveResponse); 
    
    Message moveMessage = gson.fromJson(moveResponseObject.toString(), Message.class);
    
    assertEquals(moveMessage.getCode(), 100); 
    assertTrue(moveMessage.getMoveValidity()); 
    assertEquals(moveMessage.getMessage(), ""); 
    
    
    
    System.out.println("---------- Test Player 1 Makes First Move -----------"); 
  }
  
  /**
   * This method checks that Player 2 cannot make the first move of the game.
   * Because I do not expose the game board at any time, I am relying 
   * on the message responses from different moves. 
   */
  
  @Test
  @Order(6)
  public void testPlayer2CannotMakeFirstMove() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString(); 
    HttpResponse move1Response = Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=0&y=0").asString(); 
    
    String moveResponse = (String) move1Response.getBody(); 
    JSONObject moveResponseObject = new JSONObject(moveResponse); 
    
    Message moveMessage = gson.fromJson(moveResponseObject.toString(), Message.class);
    
    assertFalse(moveMessage.getMoveValidity());
    assertEquals(moveMessage.getCode(), -1);
    
    System.out.println("---------- Test Player2 First Move ---------"); 
  }
  
  /**
   * This method ensures that a player cannot 
   * make two turns in the same turn. 
   */
  
  @Test 
  @Order(7)
  public void testPlayerCannotMakeTwoMovesInOneTurn() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
    Unirest.get("http://localhost:8080/joingame").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString(); 
    HttpResponse move1Response = Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=1&y=0").asString(); 
    
    String moveResponse = (String) move1Response.getBody();
    JSONObject moveResponseObject = new JSONObject(moveResponse); 
    
    Message moveMessage = gson.fromJson(moveResponseObject.toString(), Message.class); 
    
    assertFalse(moveMessage.getMoveValidity());
    assertEquals(moveMessage.getCode(), -1); 
    
    System.out.println("--------- Test Player Cannot Make 2 Moves In 1 Turn"); 
  }
  
  /**
   * This method ensures that Player 1 can win the game. 
   */
  
  @Test
  @Order(8)
  public void testPlayer1CanWinGame() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
    Unirest.get("http://localhost:8080/joingame").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=0&y=1").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=1&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=1&y=1").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=2&y=0").asString(); 
    HttpResponse getGameBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString(); 
    
    String boardResponse = (String) getGameBoardResponse.getBody();
    JSONObject boardResponseObject = new JSONObject(boardResponse);
    
    assertEquals(false, boardResponseObject.get("gameStarted"));
    System.out.println("------- Test Player1 Can Win Game -------");
    
  }
  
  /**
   * This method ensures that Player2 can win the game. 
   */
  
  @Test
  @Order(9)
  public void testPlayer2CanWinGame() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=0&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=1&y=1").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=1&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=2&y=0").asString(); 
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "1").body("x=2&y=1").asString();
    Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=0&y=2").asString(); 
    HttpResponse getGameBoardResponse = Unirest.get("http://localhost:8080/getgameboard").asString(); 
    
    String boardResponse = (String) getGameBoardResponse.getBody();
    JSONObject boardResponseObject = new JSONObject(boardResponse); 
    
    assertEquals(false, boardResponseObject.get("gameStarted"));
    
    System.out.println("------ Test Player 2 Can Win Game -------"); 
    
  }
  
  /**
   * This method ensures that the game can end in a draw.
   */
  
  @Test
  @Order(10)
  public void testGameEndsInDraw() {
    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
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
    HttpResponse finalMoveResponse = Unirest.post("http://localhost:8080/move/{playerId}").routeParam("playerId", "2").body("x=2&y=0").asString();
    
    String moveResponse = (String) finalMoveResponse.getBody();
    JSONObject moveResponseObject = new JSONObject(moveResponse);
    
    Message moveMessage = gson.fromJson(moveResponseObject.toString(), Message.class); 
    
    
 
    
    System.out.println("---- Test Game Ends in Draw ------"); 
   
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
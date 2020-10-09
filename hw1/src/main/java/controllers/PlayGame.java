package controllers;

import com.google.gson.Gson;
import io.javalin.Javalin;
import java.io.IOException;
import java.sql.Connection;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import org.eclipse.jetty.websocket.api.Session;
import utils.GameDatabase;

public class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;
  
  private static Gson gson = new Gson(); 
  
  private static GameBoard gameBoard; 
  
  private static Player player1;
  
  private static Player player2; 

  /** Main method of the application.
   * @param args Command line arguments
   */
  public static void main(final String[] args) {

    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });
    
    Connection conn = GameDatabase.createConnection(); 
    gameBoard = GameDatabase.restoreGameBoard(conn, gameBoard);
    System.out.println(gameBoard); 
        
    // Redirects the user to a new game
    app.get("/newgame", ctx -> {
      GameDatabase.resetTables(conn);
      gameBoard = null; 
      ctx.redirect("/tictactoe.html"); 
    }); 
    
    // Adds player 1 to the game and sends a link for player to join
    app.post("/startgame", ctx -> {
      char player1Symbol = ctx.body().charAt(ctx.body().length() - 1); 
      player1 = new Player(player1Symbol, 1); 
      gameBoard = new GameBoard(); 
      gameBoard.setP1(player1);
      GameDatabase.addPlayer(conn, player1); 
      ctx.result(gson.toJson(gameBoard)); 
    });
    
    
    // Adds player 2 to the game and starts the game
    app.get("/joingame", ctx -> {
      char player2Symbol = gameBoard.getP1().getType() == 'X' ? 'O' : 'X';
      player2 = new Player(player2Symbol, 2); 
      gameBoard.setP2(player2);
      gameBoard.setGameStarted(true); 
      GameDatabase.addPlayer(conn, player2);
      sendGameBoardToAllPlayers(gson.toJson(gameBoard));
      ctx.redirect("/tictactoe.html?p=2");
    });
    
    //Adds moves to the board and ends the game when necessary
    app.post("/move/:playerId", ctx -> {
      int playersTurn = Integer.parseInt(ctx.pathParam("playerId")); 
      int moveX = Character.getNumericValue(ctx.body().charAt(2)); 
      int moveY = Character.getNumericValue(ctx.body().charAt(ctx.body().length() - 1));
      
      Move currentMove; 
      
      if (playersTurn == 1) {
        currentMove = new Move(gameBoard.getP1(), moveX, moveY);
      } else {
        currentMove = new Move(gameBoard.getP2(), moveX, moveY);
      }
      
      Message moveMessage; 
      if (gameBoard.isValidMove(currentMove)) { 
        gameBoard.addMoveToBoardAndSwitchesTurns(currentMove); 
        
        if (gameBoard.playerWonGame(gameBoard.getP1())) {
          gameBoard.endsGameAndSetsWinner(gameBoard.getP1());
        }  
        if (gameBoard.playerWonGame(gameBoard.getP2())) {
          gameBoard.endsGameAndSetsWinner(gameBoard.getP2());
        }
        
        moveMessage = gameBoard.generateValidMoveMessage();
       
        
      } else if (gameBoard.isGameDraw()) {
        gameBoard.setGameDraw();
        moveMessage = gameBoard.generateDrawMessage();
        
      } else {
        moveMessage = gameBoard.generateInvalidMoveMessage();
      }
      sendGameBoardToAllPlayers(gson.toJson(gameBoard));
      GameDatabase.addMoveData(conn, currentMove);
      
      ctx.result(gson.toJson(moveMessage));
    }); 
    
    app.get("/getgameboard", ctx -> {
      ctx.result(gson.toJson(gameBoard));
    });

    // Web sockets - DO NOT DELETE or CHANGE
    app.ws("/gameboard", new UiWebSocket());
  }

  /** Send message to all players.
   * @param gameBoardJson Gameboard JSON
   * @throws IOException Websocket message send IO Exception
   */
  private static void sendGameBoardToAllPlayers(final String gameBoardJson) {
    Queue<Session> sessions = UiWebSocket.getSessions();
    for (Session sessionPlayer : sessions) {
      try {
        sessionPlayer.getRemote().sendString(gameBoardJson);
      } catch (IOException e) {
        // Add logger here
      }
    }
  }
  
  // Restarts the app
  public static void startGame() {
    main(null); 
    
  }
  
  
  // Simulates the app crashing
  public static void stop() {
    app.stop();
  }
}

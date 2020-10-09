package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import models.GameBoard;
import models.Move;
import models.Player;
 

/**
 * This class is responsible for retrieving and 
 * restoring information about each individual 
 * tic tac toe game from the SQLite Database, ase.db. 
 * This information includes what moves were made
 * in what order as well as each player's id 
 * and symbol in the game. 
 * @author kenyataplenty
 *
 */
public class GameDatabase { 
  
  /**
   * Creates a new connection to the game's SQLite Database, ase.db.
   * @return a Connection object
   */
  public static Connection createConnection() {
    Connection conn = null; 
    
    try {
      Class.forName("org.sqlite.JDBC"); 
      conn = DriverManager.getConnection("jdbc:sqlite:ase.db"); 
      
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
    }
    
    System.out.println("Opened database connection successfully"); 
    return conn;
  }
  
  /**
   * Creates a Moves table that holds all the moves done
   * during the game. 
   * @param conn is a Connection Object 
   * @return boolean indicating the success of the table creation
   */
  public static boolean createMovesTable(Connection conn) {
    Statement stmt = null; 
    
    try {
      stmt = conn.createStatement(); 
      String sql = "CREATE TABLE IF NOT EXISTS Moves " 
                   + "(playerID INT NOT NULL, " 
                   + "moveX INT NOT NULL, "
                   + "moveY INT NOT NULL);"; 
      stmt.executeUpdate(sql);
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage());
      return false; 
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
        } 
      }
    }
    System.out.println("Table created successfully"); 
    return true; 
  }
  
  /**
   * Creates the Players table in the ase.db.
   * @param conn is a Connection object
   * @return boolean indicating whether or not the operation was successful
   */
  public static boolean createPlayersTable(Connection conn) {
    Statement stmt = null; 
    
    try {
      stmt = conn.createStatement(); 
      String sql = "CREATE TABLE IF NOT EXISTS Players"
                   + "(playerID INT NOT NULL, "
                   + "symbol CHAR(1) NOT NULL);"; 
      stmt.executeUpdate(sql);  
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage());
      return false; 
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
        } 
      }
    }
    System.out.println("Players table successfully created.");
    return true; 
  }
  
  /**
   * Adds a successful move to the Moves table in the ase.db.
   * @param conn is a Connection object that allows us to access the database
   * @param move is a Move object which will provide the information to update the database with
   * @return Boolean that indicates whether or not the move was successfully added
   */
  public static boolean addMoveData(Connection conn, Move move) {
    Statement stmt = null; 
    
    try {
      conn.setAutoCommit(false);
      System.out.println("Opened database successfully.");
      
      stmt = conn.createStatement(); 
      String sql = "INSERT INTO Moves(playerID, moveX, moveY) "
                   + "VALUES (" + move.getPlayer().getId() + ", " 
                   + move.getMoveX() + ", " 
                   + move.getMoveY() + " );";
      stmt.executeUpdate(sql); 
      conn.commit(); 
      
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
      return false; 
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
        } 
      }
    }
    System.out.println("Move successfully added to the table."); 
    return true; 
  }
  
  /**
   * Adds a player to the Players table in the ase.db.
   * @param conn is a Connection object 
   * @param player is a Player object representing the Player that you are adding to the table 
   * @return Boolean indicating whether or not the addition was successful
   */
  public static boolean addPlayer(Connection conn, Player player) {
    Statement stmt = null; 
    try {
      conn.setAutoCommit(false);
      stmt = conn.createStatement(); 
      String sql = "INSERT INTO Players( playerID, symbol) "
                   + "VALUES (" + player.getId() + ", " 
                   + "\'" + player.getType() + "\'" + ");"; 
      stmt.executeUpdate(sql); 
      conn.commit(); 
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
      return false; 
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
        } 
      }
    }
    System.out.println("Successfully added Player"); 
    return true; 
  }
  
  /**
   * Returns the id and symbol for Player1.
   * @param conn is a Connection object 
   * @return Player object that contains all the information about Player 1
   */
  public static Player retrievePlayer1(Connection conn) {
    Statement stmt = null; 
    Player savedPlayer = null; 
    try {
      stmt = conn.createStatement(); 
      String sql = "SELECT * from Players where playerID = 1;";
      ResultSet playerData = stmt.executeQuery(sql); 
      try {
        while (playerData.next()) {
          savedPlayer = new Player(playerData.getString("symbol").charAt(0),
              playerData.getInt("playerID"));
        }
      } catch (Exception e) {
        System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
      } finally {
        if (playerData != null) {
          try {
            playerData.close();
          } catch (Exception e2) {
            System.out.println(e2.getClass().getName() + ":" + e2.getMessage()); 
          }
        }
      }
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
      return null; 
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
        } 
      } 
    }
    System.out.println("Successfully retrieved player");
    return savedPlayer; 
  }
  
  /**
   * Returns the id and symbol for Player 2.
   * @param conn is a Connection object 
   * @return Player object that contains all the information about Player 2
   */
  public static Player retrievePlayer2(Connection conn) {
    Statement stmt = null; 
    Player savedPlayer = null; 
    try {
      stmt = conn.createStatement(); 
      String sql = "SELECT * from Players where playerID = 2;";
      ResultSet playerData = stmt.executeQuery(sql); 
      try {
        while (playerData.next()) {
          savedPlayer = new Player(playerData.getString("symbol").charAt(0),
              playerData.getInt("playerID"));
        }
      } catch (Exception e) {
        System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
      } finally {
        if (playerData != null) {
          try {
            playerData.close();
          } catch (Exception e2) {
            System.out.println(e2.getClass().getName() + ":" + e2.getMessage()); 
          }
        }
      }
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
      return null; 
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
        } 
      } 
    }
    System.out.println("Successfully retrieved Player 2"); 
    return savedPlayer; 
  }
  
  /**
   * Retrieves all the moves that have been made during the tic tac toe game so far.
   * If it is a valid move, then it is added to the valid moves array that will be
   * used to reconstruct the game board with. 
   * @param conn is a Connection object 
   * @param gameBoard is the GameBoard object that you are retrieving moves for
   * @return ArrayList containing all the valid played moves
   */
  public static ArrayList<Move> retrieveMoves(Connection conn, GameBoard gameBoard) {
    Statement stmt = null;
    ArrayList<Move> savedMoves = new ArrayList<Move>(); 
    
    try {
      stmt = conn.createStatement();
      String sql = "SELECT * from Moves"; 
      ResultSet moveData = stmt.executeQuery(sql); 
      try {
        while (moveData.next()) {
          Move currentRestoredMove = null; 
          if (moveData.getInt("playerID") == 1) {
            currentRestoredMove = new Move(gameBoard.getP1(),
                                   moveData.getInt("moveX"), 
                                   moveData.getInt("moveY")); 
          }
          if (moveData.getInt("playerID") == 2) {
            currentRestoredMove = new Move(gameBoard.getP2(),
                                  moveData.getInt("moveX"), 
                                  moveData.getInt("moveY")); 
          }
          savedMoves.add(currentRestoredMove); 
        }
        
      } catch (Exception e) {
        System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
      } finally {
        if (moveData != null) {
          try {
            moveData.close(); 
          } catch (Exception e2) {
            System.out.println(e2.getClass().getName() + ":" + e2.getMessage()); 
          }
        }
      }
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
      return null; 
    } finally {
      if (stmt != null) {
        try {
          stmt.close(); 
        } catch (Exception e) {
          System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
        }
      }
    }
    System.out.println("Successfully retrieved all of the moves from the table"); 
    return savedMoves; 
  }
 
  /**
   * Removes the specified table from the database. 
   * @param conn is a Connection object that allows us to access the database
   * @param tableName is a String representing the table to be dropped
   * @return Boolean indicating whether or not the sql query was successful
   */
  public static boolean dropTable(Connection conn, String tableName) {
    Statement stmt = null; 
    try {
      stmt = conn.createStatement(); 
      String sql =  "DROP Table IF EXISTS " + tableName;  
      stmt.execute(sql); 
      conn.commit(); 
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
      return false; 
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (Exception e2) {
          System.out.println(e2.getClass().getName() + ":" + e2.getMessage()); 
        }
      }
    }
    System.out.println("Table successfully dropped"); 
    return true; 
  }
  
  /**
   * Restores a GameBoard object after there has been a "crash."
   * @param conn is a Connection Object 
   * @param gameBoard is a GameBoard object that needs to be restored 
   * @return GameBoard that has been fully restored
   */
  public static GameBoard restoreGameBoard(Connection conn, GameBoard gameBoard) {
    try {
      Player savedPlayer1 = retrievePlayer1(conn); 
      gameBoard = new GameBoard(); 
      if (savedPlayer1 != null) {
        gameBoard.setP1(savedPlayer1);
      }
      
      Player savedPlayer2 = retrievePlayer2(conn); 
      if (savedPlayer2 != null) {
        gameBoard.setP2(savedPlayer2); 
        gameBoard.setGameStarted(true); 
      }
      
      if (gameBoard.getP1() != null && gameBoard.getP2() != null) {
        ArrayList<Move> storedMoves = retrieveMoves(conn, gameBoard); 
        
        if (storedMoves.size()  > 0) {
          for (Move currentMove: storedMoves) { 
            if (gameBoard.isValidMove(currentMove)) {
              gameBoard.addMoveToBoardAndSwitchesTurns(currentMove);
              
              if (gameBoard.playerWonGame(savedPlayer1)) {
                gameBoard.endsGameAndSetsWinner(savedPlayer1);
              }
              
              if (gameBoard.playerWonGame(savedPlayer2)) {
                gameBoard.endsGameAndSetsWinner(savedPlayer2);
              }
              
            } else if (gameBoard.isGameDraw()) {
              gameBoard.setGameDraw();
            }
          }
        }
        
      }
      
    } catch (Exception e) {
      System.out.println(e.getClass().getName() + ":" + e.getMessage()); 
    }
    return gameBoard; 
  }
  
  /**
   * Deletes all the tables and creates them fresh. 
   * @param conn is a Connection object
   */
  public static void resetTables(Connection conn) {
    dropTable(conn, "Moves"); 
    dropTable(conn, "Players"); 
    createPlayersTable(conn); 
    createMovesTable(conn); 
  }

}

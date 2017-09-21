package com.pharvey.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.pharvey.model.Game;
import com.pharvey.model.GameStatus;
import com.pharvey.model.Move;
import com.pharvey.model.Player;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TictactoeServiceIntTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void createGame() {

		Game createdGame = startNewGame();		
		assertThat(createdGame.getGameStatus(), equalTo(GameStatus.IN_PROGRESS));
		assertThat(createdGame.getGameWinner(), equalTo(null));

	}

	@Test
	public void getGameSuccess() {

		// Start new game, and retrieve ID
		Game createdGame = startNewGame();
		// Retrieve the new game from the rest service
		ResponseEntity<Game> getResponse = this.restTemplate.getForEntity("/game/{gameId}", Game.class, createdGame.getGameId());
		Game retrievedGame = getResponse.getBody();
		assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(createdGame, equalTo(retrievedGame));
 
	}

	@Test
	public void getGameNotFound() {

		// Attempt to get the game with a non-existent gameId
		ResponseEntity<String> getResponse = this.restTemplate.getForEntity("/game/{gameId}", String.class, 9999);
		String message = getResponse.getBody();
		assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
		assertThat(message, equalTo("Game with ID 9999 not found in list of current games"));

	}

	@Test
	public void makeMoveGameNotFound() {

		Move move = new Move(Player.CROSS, 1, 1);
		// Attempt to make a move with a non-existent gameId
		ResponseEntity<String> putResponse = this.restTemplate.exchange("/game/9999/move", HttpMethod.PUT, new HttpEntity<Move>(move), String.class);
		String message = putResponse.getBody();
		assertThat(putResponse.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
		assertThat(message, equalTo("Game with ID 9999 not found in list of current games"));

	}

	@Test
	public void makeMoveWrongPlayersGo() {

		Game createdGame = startNewGame();
		
		Move move = new Move(Player.NOUGHT, 1, 1);
		// Attempt to make a move withthe wrong player
		ResponseEntity<String> putResponse = this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(move), String.class);
		String message = putResponse.getBody();
		assertThat(putResponse.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		assertThat(message, equalTo("Illegal move : The next valid player is CROSS, and not NOUGHT"));

	}

	@Test
	public void makeMoveInvalidRow() {

		Game createdGame = startNewGame();
		
		Move move = new Move(Player.CROSS, 4, 1);
		// Attempt to make a move with the wrong player
		ResponseEntity<String> putResponse = this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(move), String.class);
		String message = putResponse.getBody();
		assertThat(putResponse.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		assertThat(message, equalTo("Illegal move : Row number must be between 1, 2 or 3. You supplied 4"));
	}
	
	@Test
	public void makeMoveInvalidColumn() {

		Game createdGame = startNewGame();
		
		Move move = new Move(Player.CROSS, 1, 4);
		// Attempt to make a move with the wrong player
		ResponseEntity<String> putResponse = this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(move), String.class);
		String message = putResponse.getBody();
		assertThat(putResponse.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		assertThat(message, equalTo("Illegal move : Column number must be between 1, 2 or 3. You supplied 4"));
	}
	
	@Test
	public void makeMoveOccupiedCell() {

		Game createdGame = startNewGame();
		
		Move successFullmove = new Move(Player.CROSS, 1, 2);
		ResponseEntity<Game> successfulMoveResponse = this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(successFullmove), Game.class);
		assertThat(successfulMoveResponse.getStatusCode(), equalTo(HttpStatus.OK));
		
		Move illegalMove = new Move(Player.NOUGHT, 1, 2);
		ResponseEntity<String> illegalMoveResponse = this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(illegalMove), String.class);
		String message = illegalMoveResponse.getBody();
		assertThat(illegalMoveResponse.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		assertThat(message, equalTo("Illegal move : Position row 1 column 2 is already occupied with a CROSS"));
	}

	@Test
	public void makeMoveGameInWrongState() {

		Game createdGame = startNewGame();

		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 1, 1)), Game.class);
		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.NOUGHT, 1, 2)), Game.class);
		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 2, 1)), Game.class);
		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.NOUGHT, 2, 2)), Game.class);
        this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 3, 1)), Game.class);
        //Cross has now won the game. Try and make another move to get the game in wrong state error
        ResponseEntity<String> illegalMoveResponse = this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.NOUGHT, 3, 2)), String.class);
        String message = illegalMoveResponse.getBody();
        assertThat(illegalMoveResponse.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		assertThat(message, equalTo(String.format("Illegal move : Game ID %s is not currently in progress. Its status is WON",createdGame.getGameId())));
		
	}
	

	@Test
	public void winGame() {
		
		Game createdGame = startNewGame();
		//Before starting, check that no winner is defined
		assertThat(createdGame.getGameWinner(), equalTo(null));

		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 1, 1)), Game.class);
		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.NOUGHT, 1, 2)), Game.class);
		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 2, 1)), Game.class);
		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.NOUGHT, 2, 2)), Game.class);
        this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 3, 1)), Game.class);
        
        //Game now won. Get details to chck that CROSS won it
        ResponseEntity<Game> getResponse = this.restTemplate.getForEntity("/game/{gameId}", Game.class, createdGame.getGameId());
		Game retrievedGame = getResponse.getBody();
		assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(retrievedGame.getGameWinner(), equalTo(Player.CROSS));
		assertThat(retrievedGame.getGameStatus(), equalTo(GameStatus.WON));
        

	}
	
	@Test
	public void drawGame() {

		Game createdGame = startNewGame();
		//Before starting, check that no winner is defined
		assertThat(createdGame.getGameWinner(), equalTo(null));

		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 3, 3)), Game.class);
		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.NOUGHT, 1, 1)), Game.class);
		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 1, 3)), Game.class);
		this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.NOUGHT, 3, 1)), Game.class);
        this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 2, 2)), Game.class);
        this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.NOUGHT, 1, 2)), Game.class);
        this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 2, 1)), Game.class);
        this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.NOUGHT, 3, 2)), Game.class);
        this.restTemplate.exchange("/game/" + createdGame.getGameId() + "/move", HttpMethod.PUT, new HttpEntity<Move>(new Move(Player.CROSS, 2, 3)), Game.class);
        
        //Game now drawn. Get details to verify
        ResponseEntity<Game> getResponse = this.restTemplate.getForEntity("/game/{gameId}", Game.class, createdGame.getGameId());
		Game retrievedGame = getResponse.getBody();
		assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(retrievedGame.getGameWinner(), equalTo(null));
		assertThat(retrievedGame.getGameStatus(), equalTo(GameStatus.DRAW));
 

	}
	
	private Game startNewGame() {
		ResponseEntity<Game> postResponse = this.restTemplate.exchange("/game", HttpMethod.POST, null, Game.class);
		Game createdGame = postResponse.getBody();
		assertThat(postResponse.getStatusCode(), equalTo(HttpStatus.OK));
		return createdGame;
	}

	

}

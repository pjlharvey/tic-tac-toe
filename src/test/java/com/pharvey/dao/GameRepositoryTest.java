package com.pharvey.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.pharvey.Application;
import com.pharvey.dao.GameRepository;
import com.pharvey.model.Game;
import com.pharvey.model.Move;
import com.pharvey.model.Player;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { Application.class })
public class GameRepositoryTest {

	@Autowired
	GameRepository gameRepository;

	@Test
	public void getGameTestGameFound() {

		// Create a game in the repository
		Game expectedGame = new Game();
		Game game = gameRepository.createGame(expectedGame);

		expectedGame.setGameId(game.getGameId());

		assertEquals(expectedGame, game);

		// Double check it is persisted by getting the game
		Game retrievedGame = gameRepository.getGame(game.getGameId());
		assertEquals(retrievedGame, game);

	}

	@Test
	public void getGameTestGameNotFound() {

		// Attempt to retrieve a non-existent game
		Game game = gameRepository.getGame(9999);
		assertNull("Game should be returned as null if not found in the repository", game);

	}

	@Test
	public void updateGameTest() {
		
		Game expectedGame = new Game();
		Game game = gameRepository.createGame(expectedGame);		
		Game retrievedGame = gameRepository.getGame(game.getGameId());	
		Move move = new Move(Player.CROSS, 1, 1);		
		retrievedGame.applyMove(move);		
		gameRepository.updateGame(retrievedGame);
		
		Game updatedGame = gameRepository.getGame(game.getGameId());
		assertEquals(updatedGame.getNextPlayer(), Player.NOUGHT);
		

	}

}


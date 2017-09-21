package com.pharvey.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.pharvey.Application;
import com.pharvey.dao.GameRepository;
import com.pharvey.exception.IllegalMoveException;
import com.pharvey.model.Game;
import com.pharvey.model.Move;
import com.pharvey.model.Player;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { Application.class })
public class GameControllerTest {

	@InjectMocks
	GameController gameController;
	
	@Mock
	GameRepository gameRepository;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test(expected = IllegalMoveException.class)
	public void invalidMoveWrongPlayerTest() throws IllegalMoveException {
		
		Game game = new Game();
		game.setGameId(1);
		
		Move move = new Move(Player.NOUGHT, 1 ,1);
		
		Mockito.when(gameRepository.getGame((Integer)anyObject())).thenReturn(game);

		gameController.makeMove(1, move);
		
		expectedEx.expect(IllegalMoveException.class);
	    expectedEx.expectMessage("Illegal move : The next valid player is CROSS, and not NOUGHT");

	}
	
	@Test(expected = IllegalMoveException.class)
	public void invalidMoveArgumentsOutOfRangeTest() throws IllegalMoveException {
		
		Game game = new Game();
		game.setGameId(1);
		
		Move move = new Move(Player.CROSS, 5 ,1);
		
		Mockito.when(gameRepository.getGame((Integer)anyObject())).thenReturn(game);

		gameController.makeMove(1, move);
		
		expectedEx.expect(IllegalMoveException.class);
	    expectedEx.expectMessage("Illegal move : Row number must be between 1, 2 or 3. You supplied 5");

	}

	@Test
	public void validMoveTest() throws IllegalMoveException {

		Game game = new Game();
		game.setGameId(1);
		
		Move move = new Move(Player.CROSS, 1 ,1);
		
		Mockito.when(gameRepository.getGame((Integer)anyObject())).thenReturn(game);

		Game updatedGame = gameController.makeMove(1, move);
		
		assertEquals(Player.NOUGHT, updatedGame.getNextPlayer());

	}


}

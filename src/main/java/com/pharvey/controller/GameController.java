package com.pharvey.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pharvey.dao.GameRepository;
import com.pharvey.exception.IllegalMoveException;
import com.pharvey.model.Game;
import com.pharvey.model.GameStatus;
import com.pharvey.model.Move;
import com.pharvey.model.Player;

@Component
public class GameController {
	
	Logger logger = LoggerFactory.getLogger(GameController.class);
	
	@Autowired
	GameRepository gameRepository;
	
	/**
	 * Starts a new game between player 1 and player 2
	 * @return The game that has been started, including the ID of the game required for future interactions
	 * 
	 */
	public Game startNewGame() {
		
		logger.debug("Entry");		
		
		Game newGame = new Game();
		Game savedGame = gameRepository.createGame(newGame);				

		logger.info("New game created and saved : {}", savedGame);		
		
		return savedGame;		
	}

	/**
	 * Return the state of the game represented by the parameter gameID
	 * 
	 * @param gameId
	 * @return the game represented by the gameId, null if no game found for this ID
	 */
	public Game getGame(Integer gameId) {
		
		logger.debug("Entry: {}", gameId);
		
		Game game = gameRepository.getGame(gameId);
		
		logger.debug("Exit: {}", gameId);
		
		return game;		
	}

	/**
	 * Update the game board with a player's move. 
	 * Before updating the board, validate that the game exists, is still in progress, it is the player's go and 
	 * the move is legal within the rules of noughts and crosses.
	 * 
	 * @param gameId The game to make the move in
	 * @param move The player and position of the move
	 * @return The game details after the move has taken place, including game status. Null if the game cannot be found in the repository
	 */
	public Game makeMove(Integer gameId, Move move) throws IllegalMoveException {
		
		Game game = gameRepository.getGame(gameId);
		
		if (game == null) {
			return null;
		}
		
		// Synchronize on key to prevent one thread inserting into this particular game after another has validated. Syncing on key ensures only one game is locked keeping the program performant
		synchronized (game.getGameId()) {
			validateMove(move, game);
			game.applyMove(move);
			game.refreshStatus(move.getPlayer());
			gameRepository.updateGame(game);
		}

		return game;
	}

	private void validateMove(Move move, Game game) throws IllegalMoveException {
		
		
		if (game.getGameStatus() != GameStatus.IN_PROGRESS) {
			throw new IllegalMoveException(String.format("Illegal move : Game ID %s is not currently in progress. Its status is %s", game.getGameId(), game.getGameStatus()));
		}
		
		Player nextValidPlayer = game.getNextPlayer();
		if (nextValidPlayer != move.getPlayer()) {
			throw new IllegalMoveException(String.format("Illegal move : The next valid player is %s, and not %s", nextValidPlayer, move.getPlayer()));
		}
		
		if (move.getRow() < 1 || move.getRow() > 3) {
			throw new IllegalMoveException(String.format("Illegal move : Row number must be between 1, 2 or 3. You supplied %s", move.getRow()));
		}
		
		if (move.getColumn() < 1 || move.getColumn() > 3) {
			throw new IllegalMoveException(String.format("Illegal move : Column number must be between 1, 2 or 3. You supplied %s", move.getColumn()));
		}
		
		Player player = game.getCellState(move);
		if (player != null) {
			throw new IllegalMoveException(String.format("Illegal move : Position row %s column %s is already occupied with a %s", move.getRow(), move.getColumn(), player));
		}		
	}
}

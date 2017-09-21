package com.pharvey.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pharvey.controller.GameController;
import com.pharvey.exception.IllegalMoveException;
import com.pharvey.model.Game;
import com.pharvey.model.Move;

@RestController
@RequestMapping(value = "/game")
public class TictactoeService {

	Logger logger = LoggerFactory.getLogger(TictactoeService.class);

	@Autowired
	GameController gameController;

	@RequestMapping(method = RequestMethod.POST)
	public Game startNewGame() {

		return gameController.startNewGame();
	}

	@RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
	public ResponseEntity<?> getGame(@PathVariable("gameId") Integer gameId) {

		ResponseEntity<?> response = null;

		Game game = gameController.getGame(gameId);

		if (game != null) {
			response = new ResponseEntity<Game>(game, HttpStatus.OK);

		} else {
			String message = String.format("Game with ID %s not found in list of current games", gameId);
			logger.error(message);
			response = new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
		}
		return response;

	}

	@RequestMapping(value = "/{gameId}/move", method = RequestMethod.PUT)
	public ResponseEntity<?> makeMove(@PathVariable("gameId") Integer gameId, @RequestBody Move move) {

		ResponseEntity<?> response = null;
		try {
			Game game = gameController.makeMove(gameId, move);
			if (game != null) {
				response = new ResponseEntity<Game>(game, HttpStatus.OK);
			} else {
				String message = String.format("Game with ID %s not found in list of current games", gameId);
				logger.error(message);
				response = new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
			}

		} catch (IllegalMoveException ime) {
			logger.error(String.format("Problem making move %s for game %s : %s", move, gameId, ime.getMessage()));
			response = new ResponseEntity<String>(ime.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return response;

	}

}

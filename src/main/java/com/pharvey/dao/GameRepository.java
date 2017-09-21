package com.pharvey.dao;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pharvey.model.Game;

/**
 * 
 * Class responsible for storing game information, including public CRUD operations to allow its retrieval. Uses locks to prevent reads or writes occurring during an update.
 * 
 * Clones results of the read operation to ensure this class is the only one that update data in the repository.
 * 
 * @author PJLH
 * 
 */
@Component
public class GameRepository {

	Logger logger = LoggerFactory.getLogger(GameRepository.class);

	private static ConcurrentHashMap<Integer, Game> games = new ConcurrentHashMap<Integer, Game>();
	//Game ID counter represented by AtomicInteger to make increment thread safe 
	private static AtomicInteger latestGameID = new AtomicInteger(0);

	/**
	 * Create a game in the repository. Assign the gameId from an incremental sequence held in memory.
	 * 
	 * @param game .Game to save to the repository
	 * @return. Game that was saved, including gameId that was assigned
	 * 
	 */
	public Game createGame(Game game) {

		logger.debug("Entry: {}", game);

		/*
		 * There is a possible race condition in this block. It could be synchronised but the worst (rare) case is that the games are created with non-sequential IDs.
		 */		
		int nextGameId = latestGameID.incrementAndGet();
		latestGameID = new AtomicInteger(nextGameId);
		game.setGameId(nextGameId);
		games.put(nextGameId, game);

		logger.debug("Exit: {}", game);

		return game;
	}

	/**
	 * Get a game from the games repository, using the gameId as a key.
	 * 
	 * @param gameId The key of the game to return
	 * @return The game object keyed by the provided gamedId. Null if no game found.
	 * 
	 */
	public Game getGame(Integer gameId) {

		return games.get(gameId);
	}

	public void updateGame(Game game) {

		//Games repo represented by a ConcurrentHashMap, so no threading issues during update 
		games.replace(game.getGameId(), game);

	}

}

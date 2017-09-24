package com.pharvey.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {
	
	Logger logger = LoggerFactory.getLogger(Game.class);

	private Integer gameId;
	private Board board;
	private GameStatus gameStatus;
	private Player gameWinner;

	public Game() {

		this.board = new Board();
		gameStatus = GameStatus.IN_PROGRESS;

	}

	/**
	 * Set the game status based for a player (win or draw, and the winner if any) based on the current board position
	 * @param player whose position is being checked for a possible game status update
	 */
	public void refreshStatus(Player player) {
		
		logger.debug("Entry: {}", player);
		
		if (board.checkForDraw()) {
			gameStatus = GameStatus.DRAW;
		} else if (board.checkIfPlayerHasWon(player)) {
			gameStatus = GameStatus.WON;
			gameWinner = player;
		}	
		
		logger.debug("Exit");
	}

	/**
	 * Take a move in x,y co-ordinate format, and apply to the current board cell state, in its simple 9 element array format
	 * @param move To apply to the board
	 */
	public void applyMove(Move move) {
		
		board.applyMove(move);
				
	}
	/**
	 * Get the next player in the rules of noughts and crosses
	 * @return The player whose turn it is next
	 */
	public Player getNextPlayer() {
		
		return board.getNextPlayer();
	}
	
	/**
	 * Get the current state of the cells on the board
	 * @return The state of the cells on the board
	 */	
	public Player getCellState(Move move) {
		
		return board.getCellState(move);
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	public Player getGameWinner() {
		return gameWinner;
	}

	public void setGameWinner(Player gameWinner) {
		this.gameWinner = gameWinner;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result + ((gameId == null) ? 0 : gameId.hashCode());
		result = prime * result + ((gameStatus == null) ? 0 : gameStatus.hashCode());
		result = prime * result + ((gameWinner == null) ? 0 : gameWinner.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (gameId == null) {
			if (other.gameId != null)
				return false;
		} else if (!gameId.equals(other.gameId))
			return false;
		if (gameStatus != other.gameStatus)
			return false;
		if (gameWinner != other.gameWinner)
			return false;
		return true;
	}
	

}

package com.pharvey.model;

import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharvey.dao.GameRepository;

public class Board {

	Logger logger = LoggerFactory.getLogger(Board.class);
	
	Player[] cellStates;
	int[][] winnableLines = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 }, { 2, 4, 6 } };

	/**
	 * Zero argument constructor, just creates new initialised board
	 */
	public Board() {

		cellStates = new Player[9];

	}
	/**
	 * Constructor to allow a game to start in a specified state.
	 * @param cellStates The initial state of the board
	 */
	public Board(Player[] cellStates) {

		this.cellStates = cellStates;

	}

	/**
	 * Take a move in x,y co-ordinate format, and apply to the current board cell state, in its simple 9 element array format
	 * @param move To apply to the board
	 */
	public void applyMove(Move move) {
		
		logger.debug("Entry: {}", move);
		
		int arrayIndex = convertMoveToArrayIndex(move);
		cellStates[arrayIndex] = move.getPlayer();
		
		logger.debug("Exit");
		
	}
	
	/**
	 * Check if the game is in a draw state, by seeing if all moves have been made and no player has won 
	 * @return a boolean saying if the game is a draw
	 */
	public boolean checkForDraw() {

		logger.debug("Entry");
		
		boolean isDraw = false;

		MarkCounts markCounts = countMarks();
		if  (markCounts.getNumberOfCrossMarks() + markCounts.getNumberOfNoughtMarks() == cellStates.length &&
				!checkIfPlayerHasWon(Player.NOUGHT) &&
				!checkIfPlayerHasWon(Player.CROSS) 
				
			) {
			isDraw = true;
		}
		
		logger.debug("Exit: {}", isDraw);
		
		return isDraw;
	}

	/**
	 * Check if the specified player has won
	 * @param player against who the win check is being performed
	 * @return Boolean saying if the specified player has won
	 */
	public boolean checkIfPlayerHasWon(Player player) {

		logger.debug("Entry: {}", player);
		
		boolean isWinner = false;

		for (int[] line : winnableLines) {

			if (checkLineForWin(line, player)) {
				return true;
			}
		}
		logger.debug("Exit: {}", isWinner);
		return isWinner;
	}

	private boolean checkLineForWin(int[] line, Player player) {

		boolean isLineWon = true;
		
		for (int i : line) {
			if (cellStates[i] != player) {
				isLineWon = false;
			}
		}
		return isLineWon;

	}

	/**
	 * Return the current state of the cell specified in the move object
	 * @param move whose position must be checked
	 * @return The current state of the specified move
	 */
	public Player getCellState(Move move) {

		logger.debug("Entry: {}", move);
		
		int arrayIndex = convertMoveToArrayIndex(move);
		
		Player player = cellStates[arrayIndex];
		logger.debug("Exit: {}", player);
		
		return player;

	}

	private int convertMoveToArrayIndex(Move move) {
		int offset = (move.getRow() - 1) * 3;
		return offset + move.getColumn() - 1;
	}

	/**
	 * Get the next player in the rules of noughts and crosses
	 * @return The player whose turn it is next
	 */
	public Player getNextPlayer() {

		logger.debug("Entry");
		Player nextPlayer;
		
		MarkCounts markCounts = countMarks();
		if (markCounts.getNumberOfCrossMarks() == markCounts.getNumberOfNoughtMarks()) {
			nextPlayer =  Player.CROSS;
		} else {
			nextPlayer = Player.NOUGHT;
		}
		
		logger.debug("Exit: {}", nextPlayer);
		return nextPlayer;
	}

	private MarkCounts countMarks() {
		
		int numberOfCrossMarks = 0;
		int numberOfNoughtMarks = 0;

		for (int i = 0; i < cellStates.length; i++) {
			Player cell = cellStates[i];
			if (cell != null) {
				switch (cell) {
				case CROSS:
					numberOfCrossMarks++;
					break;
				case NOUGHT:
					numberOfNoughtMarks++;
					break;
				}
			}
		}
		return new MarkCounts(numberOfCrossMarks, numberOfNoughtMarks);
	}

	/*
	 * Private class to hold the number of NOUGHT goes and number of CROSS goes on the board at any time
	 */
	private class MarkCounts {

		private int numberOfCrossMarks = 0;
		private int numberOfNoughtMarks = 0;

		public MarkCounts(int numberOfCrossMarks, int numberOfNoughtMarks) {
			this.numberOfCrossMarks = numberOfCrossMarks;
			this.numberOfNoughtMarks = numberOfNoughtMarks;

		}

		public int getNumberOfCrossMarks() {
			return numberOfCrossMarks;
		}

		public int getNumberOfNoughtMarks() {
			return numberOfNoughtMarks;
		}

	}
	
	/**
	 * Get the current state of the cells on the board
	 * @return The state of the cells on the board
	 */			
	public Player[] getCellStates() {
		return cellStates;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(cellStates);
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
		Board other = (Board) obj;
		if (!Arrays.equals(cellStates, other.cellStates))
			return false;
		return true;
	}
}

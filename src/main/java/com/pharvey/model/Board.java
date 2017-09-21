package com.pharvey.model;

import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Board {

	Player[] cellStates;
	int[][] winnableLines = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 }, { 2, 4, 6 } };

	public Board() {

		cellStates = new Player[9];

	}
	public Board(Player[] cellStates) {

		this.cellStates = cellStates;

	}

	public void applyMove(Move move) {
		int arrayIndex = convertMoveToArrayIndex(move);
		cellStates[arrayIndex] = move.getPlayer();
		//could validate board state at this point, with more time

	}

	public boolean checkForDraw() {

		boolean isDraw = false;

		MarkCounts markCounts = countMarks();
		if (markCounts.getNumberOfCrossMarks() + markCounts.getNumberOfNoughtMarks() == cellStates.length) {
			isDraw = true;
		}
		return isDraw;
	}

	public boolean checkIfPlayerHasWon(Player player) {

		boolean isWinner = false;

		for (int[] line : winnableLines) {

			if (checkLineForWin(line, player)) {
				return true;
			}
		}
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

	public Player getCellState(Move move) {

		int arrayIndex = convertMoveToArrayIndex(move);
		return cellStates[arrayIndex];

	}

	private int convertMoveToArrayIndex(Move move) {
		int offset = (move.getRow() - 1) * 3;
		return offset + move.getColumn() - 1;
	}

	public Player getNextPlayer() {

		MarkCounts markCounts = countMarks();
		if (markCounts.getNumberOfCrossMarks() == markCounts.getNumberOfNoughtMarks()) {
			return Player.CROSS;
		} else {
			return Player.NOUGHT;
		}
	}

	public Player[] getCellStates() {
		return cellStates;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
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

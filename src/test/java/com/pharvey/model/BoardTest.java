package com.pharvey.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BoardTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
		// Row
		{ Player.CROSS, true, false, Player.CROSS, new Player[] { Player.CROSS, Player.CROSS, Player.CROSS, Player.NOUGHT, null, Player.NOUGHT, null, Player.NOUGHT, null } },
		// Diagonal
		{ Player.CROSS, true, false, Player.CROSS, new Player[] { Player.CROSS, null, Player.NOUGHT, null, Player.CROSS, null, Player.NOUGHT, Player.NOUGHT, Player.CROSS } },
		// Column
		{ Player.CROSS, true, false, Player.NOUGHT, new Player[] { Player.CROSS, null, Player.NOUGHT, Player.CROSS, Player.CROSS, Player.NOUGHT, Player.CROSS, Player.NOUGHT, null } },
		// No winner
		{ Player.CROSS, false, false, Player.CROSS, new Player[] { null, Player.NOUGHT, null, Player.NOUGHT, null, null, Player.CROSS, null, Player.CROSS } },
		// Row
		{ Player.NOUGHT, true, false, Player.NOUGHT, new Player[] { Player.CROSS, null, Player.CROSS, Player.NOUGHT, Player.NOUGHT, Player.NOUGHT, Player.CROSS, Player.CROSS, null } },
		// Diagonal
		{ Player.NOUGHT, true, false, Player.CROSS, new Player[] { Player.CROSS, Player.CROSS, Player.NOUGHT, Player.CROSS, Player.NOUGHT, null, Player.NOUGHT, Player.NOUGHT, Player.CROSS } },
		// Column
		{ Player.NOUGHT, true, false, Player.CROSS, new Player[] { Player.CROSS, Player.NOUGHT, Player.CROSS, Player.CROSS, Player.NOUGHT, Player.CROSS, null, Player.NOUGHT, Player.NOUGHT } },
		// No winner
		{ Player.NOUGHT, false, false, Player.NOUGHT, new Player[] { Player.CROSS, Player.NOUGHT, Player.CROSS, Player.NOUGHT, Player.CROSS, Player.NOUGHT, null, Player.NOUGHT, null } },
		// Draw
		{ Player.NOUGHT, false, true, Player.NOUGHT, new Player[] { Player.CROSS, Player.NOUGHT, Player.NOUGHT, Player.NOUGHT, Player.CROSS, Player.CROSS, Player.CROSS, Player.NOUGHT, Player.CROSS } },
		// Draw
		{ Player.NOUGHT, false, true, Player.NOUGHT, new Player[] { Player.CROSS, Player.NOUGHT, Player.CROSS, Player.NOUGHT, Player.CROSS, Player.NOUGHT, Player.CROSS, Player.NOUGHT, Player.CROSS } }

		});
	}

	private Player player;
	private boolean expectedGameWin;
	private boolean expectedDraw;
	private Player expectedNextPlayer;
	private Player[] boardState;

	public BoardTest(Player player, boolean expectedGameWin, boolean expectedDraw, Player expectedNextPlayer, Player[] boardState) {

		this.player = player;
		this.expectedGameWin = expectedGameWin;
		this.expectedDraw = expectedDraw;
		this.expectedNextPlayer = expectedNextPlayer;
		this.boardState = boardState;
	}

	@Test
	public void gameWonTest() {

		Board board = new Board(boardState);
		boolean actualGameWin = board.checkIfPlayerHasWon(player);
		assertEquals("A board state of " + Arrays.toString(boardState) + " should result in a win result of " + expectedGameWin + " for player " + player, expectedGameWin, actualGameWin);

	}

	@Test
	public void gameDrawnTest() {

		Board board = new Board(boardState);
		boolean actualGameDrawn = board.checkForDraw();
		assertEquals("A board state of " + Arrays.toString(boardState) + " should result in a draw result of " + expectedDraw, expectedDraw, actualGameDrawn);

	}

	@Test
	public void gameNextPlayerTest() {

		Board board = new Board(boardState);
		Player actualnextPlayer = board.getNextPlayer();
		assertEquals("A board state of " + Arrays.toString(boardState) + " should result in a next player of " + expectedNextPlayer, expectedNextPlayer, actualnextPlayer);

	}

}

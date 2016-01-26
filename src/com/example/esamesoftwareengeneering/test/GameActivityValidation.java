package com.example.esamesoftwareengeneering.test;

import com.example.esamesoftwareengeneering.R;
import com.example.esamesoftwareengeneering.game.Game;
import com.example.esamesoftwareengeneering.game.GameActivity;
import com.example.esamesoftwareengeneering.game.board.Board;
import com.example.esamesoftwareengeneering.game.board.CellAdapter;
import com.example.esamesoftwareengeneering.game.board.cells.Cell;
import com.example.esamesoftwareengeneering.game.board.cells.Square;
import com.example.esamesoftwareengeneering.game.board.pieces.Piece;
import com.example.esamesoftwareengeneering.game.board.pieces.Pieces;
import com.example.esamesoftwareengeneering.game.board.pieces.behaviours.PieceBehaviour.Color;
import com.example.esamesoftwareengeneering.game.board.pieces.behaviours.PieceBehaviour.Type;
import com.example.esamesoftwareengeneering.game.board.position.Position;

import android.annotation.TargetApi;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class GameActivityValidation extends
		ActivityInstrumentationTestCase2<GameActivity> {
	
	private GameActivity gameActivity;
	private TextView labelTextView;
	private Board board;
	private Button confirmMoveButton;
	private Button playAgainButton;
	private CellAdapter cellAdapter;
	private Game game;
	

	public GameActivityValidation() {
		super(GameActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		gameActivity = getActivity();
		labelTextView = (TextView) gameActivity.findViewById(R.id.textView_label);
		board = (Board) gameActivity.findViewById(R.id.board);
		confirmMoveButton = (Button) gameActivity.findViewById(R.id.button_confirm_move);
		playAgainButton = (Button) gameActivity.findViewById(R.id.button_play_again);
		cellAdapter = (CellAdapter) board.getAdapter();
		game = gameActivity.getGame();
	}
	
	public void testPreconditions() {
		gameActivity = getActivity();
		assertNotNull(gameActivity);
		assertNotNull(labelTextView);
		assertNotNull(board);
		assertNotNull(confirmMoveButton);
		assertNotNull(playAgainButton);
		assertNotNull(cellAdapter);
	}
	
	public void testLabelTextViewTextIsTurnWhite() {
		assertEquals("LabelTextView text should be \"Turn: White\"",
				"Turn: White",
				labelTextView.getText().toString());
	}
	
	public void testConfirmMoveButtonVisibilityIsGone() {
		assertTrue("ConfirmMoveButton visibility should be \"View.GONE\"",
				confirmMoveButton.getVisibility() == View.GONE);
	}
	
	public void testPlayAgainButtonVisibilityIsGone() {
		assertTrue("PlayAgainButton visibility should be \"View.GONE\"",
				playAgainButton.getVisibility() == View.GONE);
	}
	
	@UiThreadTest
	public void testMovingAPiece() throws InterruptedException {
		Position startPosition = new Position('2', 'a');
		Position endPosition = new Position('3', 'a');
		
		Piece startPositionPiece = cellAdapter.getPieces().getPiece(startPosition);
		Piece endPositionPiece = cellAdapter.getPieces().getPiece(endPosition);
		
		final Cell startPositionCell = cellAdapter.getCell(startPosition);
		final Cell endPositionCell = cellAdapter.getCell(endPosition);
		
		assertTrue("startPositionCell should not be null and be a Square",
				startPositionCell != null && startPositionCell instanceof Square);
		assertTrue("endPositionCell should not be null and be a Square",
				endPositionCell != null	&& endPositionCell instanceof Square);	
		
		assertTrue("startPositionPiece should be a white pawn",
				startPositionPiece != null
				&& startPositionPiece.getColor() == Color.WHITE
				&& startPositionPiece.getType() == Type.PAWN);
		assertNull("endPositionPiece should be null", endPositionPiece);
		assertTrue("startPositionPiece should be able to move to endPosition",
				startPositionPiece.isMoveValid(endPosition));
		
		int startPositionCellViewPosition = startPosition.getRank().getRow() * Board.COLUMNS + startPosition.getFile().getColumn();
		int endPositionCellViewPosition = endPosition.getRank().getRow() * Board.COLUMNS + endPosition.getFile().getColumn();
		
		board.performItemClick(startPositionCell,
				startPositionCellViewPosition,
				startPositionCellViewPosition);
		assertEquals("startPositionCell should be selected",
				Square.Selection.PIECE,
				((Square) startPositionCell).getSelection());
		
		board.performItemClick(endPositionCell,
				endPositionCellViewPosition,
				endPositionCellViewPosition);
		assertEquals("endPositionCell should be selected",
				Square.Selection.DESTINATION,
				((Square) endPositionCell).getSelection());
		assertTrue("ConfirmMoveButton visibility should be \"View.VISIBLE\"",
				confirmMoveButton.getVisibility() == View.VISIBLE);

		confirmMoveButton.performClick();
		endPositionPiece = cellAdapter.getPieces().getPiece(endPosition);
		assertTrue("endPositionPiece now should be the piece that was in startPosition",
				startPositionPiece == endPositionPiece);
	}
	
	@UiThreadTest
	public void testCheckmate() {
		Pieces pieces = Pieces.getCheckmateConfiguration();
		cellAdapter.setPieces(pieces);
		
    	game.changeTurn();
    	assertTrue("Game state should be \"State.END\"",
    			game.getState() == Game.State.END);
		assertEquals("LabelTextView text should be \"Checkmate: White wins\"",
				"Checkmate: White wins",
				labelTextView.getText().toString());
		assertTrue("PlayAgainButton visibility should be \"View.VISIBLE\"",
				playAgainButton.getVisibility() == View.VISIBLE);			
	}
	
	@UiThreadTest
	public void testStalemate() {
		Pieces pieces = Pieces.getStalemateConfiguration();
		cellAdapter.setPieces(pieces);
    	
    	game.changeTurn();
    	assertTrue("Game state should be \"State.END\"",
    			game.getState() == Game.State.END);
		assertEquals("LabelTextView text should be \"Draw (stalemate)\"",
				"Draw (stalemate)",
				labelTextView.getText().toString());
		assertTrue("PlayAgainButton visibility should be \"View.VISIBLE\"",
				playAgainButton.getVisibility() == View.VISIBLE);
	}
	
}

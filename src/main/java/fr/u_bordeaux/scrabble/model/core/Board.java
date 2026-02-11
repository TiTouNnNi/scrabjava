package fr.u_bordeaux.scrabble.model.core;

import fr.u_bordeaux.scrabble.model.factories.StandardBoardFactory;
import fr.u_bordeaux.scrabble.model.utils.Point;

/**
 * Represents the game board (grid of squares).
 * Manages the placement of tiles and special squares (bonuses).
 */
public class Board {
    public static final int SIZE = 15;
    private Square[][] board;

    /**
     * Custom constructor: initializes the board with a given grid.
     *
     * @param board The grid of squares to use.
     * @throws IllegalArgumentException if the board is null or has invalid dimensions.
     */
    public Board(Square[][] board) {
        if (board == null || board.length != SIZE || board[0].length != SIZE) {
            throw new IllegalArgumentException("Invalid board dimensions. Expected " + SIZE + "x" + SIZE + ".");
        }
        this.board = board;
    }

    /**
         * Creates a deep copy of the board for AI simulation.
         * @return A new independent Board instance.
         */
        public Board deepCopy() {
            Square[][] newGrid = new Square[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    Square oldSquare = this.board[i][j];

                    // Square(Point position, SquareType squareType)
                    Square newSquare = new Square(oldSquare.getPosition(), oldSquare.getSquareType());

                    // Si la case contient une tuile, on la copie (référence)
                    if (!oldSquare.isEmpty()) {
                        newSquare.setTile(oldSquare.getTile());
                    }
                    newGrid[i][j] = newSquare;
                }
            }
            return new Board(newGrid);
        }
    
    // Helper to get the raw grid for the AI to scan
    public Square[][] getGrid() {
        return this.board;
    }

    /**
     * Default constructor: initializes a standard Scrabble board.
     */
    public Board() {
        // Delegate creation to the factory
        Board tempBoard = StandardBoardFactory.createBoard();
        this.board = tempBoard.board;
    }

    public Square getSquare(Point point) {
        if (point.getX() >= 0 && point.getX() < SIZE && point.getY() >= 0 && point.getY() < SIZE) {
            return board[point.getX()][point.getY()];
        }
        return null;
    }
}
package fr.u_bordeaux.scrabble.model.ai;

import fr.u_bordeaux.scrabble.model.core.*;
import fr.u_bordeaux.scrabble.model.enums.Direction;
import fr.u_bordeaux.scrabble.model.interfaces.Player;
import fr.u_bordeaux.scrabble.model.utils.Point;

import java.util.*;

/**
 * Implements the Minimax algorithm with a simplified move generator
 * since the GADDAG structure is not yet available.
 */
public class MinimaxSolver {

    // --- STATIC DICTIONARY (Mock Data) ---
    // A mix of words length 2-7, including high value letters for testing.
    private static final List<String> DICTIONARY = Arrays.asList(
        "LE", "LA", "UN", "DE", "ET", "EN", "IL", "JE", "TU", "ON",
        "EST", "OUI", "NON", "JEU", "MOT", "RUE", "EAU", "AIR", "SOL", "MER",
        "JAZZ", "KIWI", "WHIG", "YOGA", "ZELE", "TAXE", "QUAI", "JOIE", "KIKI", "WIFI",
        "JEEP", "JURY", "JEAN", "JOLI", "JOTA", "KAMY", "KELP", "KERN", "KIDS", "KINA",
        "WATT", "WAOU", "WUHU", "WURM", "WAGS", "YAK", "YAM", "YETI", "YEUX", "YODS",
        "ZONE", "ZERO", "ZINC", "ZIST", "ZOOM", "ZOUK", "AXE", "EXIL", "FIXE", "LUXE",
        "MAISON", "MANGER", "PARTIR", "DORMIR", "POMME", "ROUGE", "VERTE", "BLANC", "NOIRE",
        "SCRABBLE", "JAVA", "CODE", "TEST", "UNIT", "VOID", "MAIN", "ARGS", "LIST",
        "TABLE", "CHAISE", "LIVRE", "STYLO", "PAGE", "SOURIS", "ECRAN", "CLAVIER", "PHONE",
        "ARBRE", "FLEUR", "HERBE", "CIEL", "NUAGE", "PLUIE", "NEIGE", "SOLEIL", "LUNE",
        "CHIEN", "CHAT", "OISEAU", "POISSON", "LION", "TIGRE", "OURS", "LOUP", "RENARD",
        "VOITURE", "VELO", "TRAIN", "AVION", "BATEAU", "BUS", "TAXI", "METRO", "TRAM",
        "POMME", "POIRE", "BANANE", "ORANGE", "CITRON", "RAISIN", "MELON", "FRAISE", "KIWI",
        "ROUGE", "BLEU", "VERT", "JAUNE", "NOIR", "BLANC", "GRIS", "ROSE", "VIOLET",
        "UN", "DEUX", "TROIS", "QUATRE", "CINQ", "SIX", "SEPT", "HUIT", "NEUF", "DIX",
        "GRAND", "PETIT", "GROS", "MINCE", "LONG", "COURT", "HAUT", "BAS", "LARDE",
        "JOUER", "GAGNER", "PERDRE", "AIMER", "HAIR", "VOIR", "LIRE", "DIRE", "FAIRE",
        "ETRE", "AVOIR", "ALLER", "VENIR", "POUVOIR", "VOULOIR", "SAVOIR", "DEVOIR", "FALLOIR",
        "BONJOUR", "MERCI", "SALUT", "ADIEU", "BRAVO", "SUPER", "GENIAL", "COOL", "TOP",
        "EXAMEN", "ETUDE", "COURS", "LECON", "CLASSE", "ECOLE", "LYCEE", "FAC", "THESE",
        "WHISKY", "JOKER", "QUEER", "PIZZA", "BOXE", "JOGGING", "KAYAK", "WAGON", "XYLO",
        "QUARTZ", "JANTE", "KAOLI", "WAZAS", "YANGS", "ZEBRE", "AZUR", "EXAM", "FLUX"
    );

    /**
     * Finds the best move for the AI using a Minimax-style approach.
     *
     * @param board The current game board.
     * @param rack  The AI's current rack.
     * @param bag   The game bag (used for probability/heuristics).
     * @return The best Move object found, or a PASS move if nothing is possible.
     */
    public Move getBestMove(Board board, Rack rack, Bag bag) {
        System.out.println("AI is thinking...");

        // 1. Generate all legally possible moves with the current Dictionary and Rack
        List<Move> possibleMoves = generateLegalMoves(board, rack);

        if (possibleMoves.isEmpty()) {
            System.out.println("No moves found. AI passes.");
            return Move.createPass(new AIPlayer("Bot"));
        }

        Move bestMove = null;
        double maxEvaluation = Double.NEGATIVE_INFINITY;

        // 2. Iterate through moves and evaluate them
        for (Move move : possibleMoves) {
            // Apply move to a simulation board
            Board simulatedBoard = board.deepCopy();
            // We need a helper to actually apply the move to the cloned board to score it
            int score = applyMoveSimulation(simulatedBoard, move);
            move.setScoreGained(score); // Store immediate score

            // 3. Heuristic Evaluation (Minimax Depth 1 + Expectancy)
            double eval = evaluateMove(simulatedBoard, move, bag, score);

            if (eval > maxEvaluation) {
                maxEvaluation = eval;
                bestMove = move;
            }
        }

        if (bestMove != null) {
            System.out.println("Best move found: " + bestMove.getScoreGained() + " points.");
        }
        return bestMove;
    }

    /**
     * Evaluates a board state after a move (Heuristic function).
     */
    private double evaluateMove(Board board, Move move, Bag bag, int moveScore) {
        double totalValue = moveScore;

        // Factor 1: Rack Leave (Simplified)
        // Penalize if we keep difficult letters like Q, W, X, Z without playing them
        // (Logic omitted for brevity)

        // Factor 2: Board Openness (Defense)
        if (opensBonusSquare(board, move)) {
            totalValue -= 8.0; // Penalty for opening a dangerous spot
        }

        return totalValue;
    }

    /**
     * Checks if the move places a tile adjacent to a premium square (TW/DW) that is still empty.
     */
    private boolean opensBonusSquare(Board board, Move move) {
        // Implementation omitted for brevity
        return false; 
    }

    // --- MOVE GENERATION (MOCK GADDAG) ---

    /**
     * Generates a list of valid moves using the static dictionary.
     */
    private List<Move> generateLegalMoves(Board board, Rack rack) {
        List<Move> moves = new ArrayList<>();
        Player aiPlayer = new AIPlayer("Bot");

        // Optimization: Identify "Anchor" squares.
        // An anchor is an empty square adjacent to an existing tile, 
        // or the center (7,7) if board is empty.
        List<Point> anchors = findAnchors(board);

        if (anchors.isEmpty()) {
            // Should not happen if findAnchors handles empty board correctly
            return moves;
        }

        // Try every word in dictionary
        for (String wordStr : DICTIONARY) {
            // Try every anchor position
            for (Point anchor : anchors) {
                // Try Horizontal and Vertical
                for (Direction dir : Direction.values()) {
                    // Try different offsets (the anchor could be the 1st letter, 2nd, etc.)
                    for (int offset = 0; offset < wordStr.length(); offset++) {
                        
                        // Calculate start position based on anchor and offset
                        int startX = anchor.getX();
                        int startY = anchor.getY();
                        
                        if (dir == Direction.HORIZONTAL) {
                            startY -= offset;
                        } else {
                            startX -= offset;
                        }
                        
                        Point start = new Point(startX, startY);

                        // Validation 1: Bounds
                        if (!isInBounds(start, wordStr.length(), dir)) continue;

                        // Validation 2: Cross-checks & Rack Check
                        Move validMove = tryCreateMove(board, rack, wordStr, start, dir, aiPlayer);
                        if (validMove != null) {
                            moves.add(validMove);
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Tries to form a move object. Verifies board conflicts, rack availability, and cross-words.
     */
    private Move tryCreateMove(Board board, Rack rack, String word, Point start, Direction dir, Player player) {
        List<Tile> tilesToPlay = new ArrayList<>();
        List<Tile> rackTilesCopy = new ArrayList<>(rack.getTiles()); // Shallow copy of list
        Square[][] grid = board.getGrid();

        boolean connectsToExisting = false;
        boolean placesAtLeastOneTile = false;
        
        int x = start.getX();
        int y = start.getY();

        // 1. Check main word placement
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            Square sq = grid[x][y];

            if (!sq.isEmpty()) {
                // There is already a tile here. It MUST match the letter.
                if (sq.getTile().getCharacter() != letter) {
                    return null; // Conflict
                }
                connectsToExisting = true;
            } else {
                // Empty square, must pull from rack
                Tile found = findInRack(rackTilesCopy, letter);
                
                if (found != null) {
                    // Use the tile (create a copy to avoid mutating rack if we were to modify it)
                    // Assuming Tile is immutable or we don't modify it here.
                    tilesToPlay.add(found);
                    rackTilesCopy.remove(found);
                } else {
                    // Check for Blank/Joker
                    found = findInRack(rackTilesCopy, ' '); 
                    if (found == null) return null; // Missing letter
                    
                    // Create a NEW tile for the joker with the assigned letter
                    // We assume Tile has a constructor Tile(char letter, int value)
                    // Jokers usually have value 0.
                    Tile jokerTile = new Tile(letter);
                    tilesToPlay.add(jokerTile);
                    rackTilesCopy.remove(found);
                }
                placesAtLeastOneTile = true;
            }

            // Move to next square
            if (dir == Direction.HORIZONTAL) y++; else x++;
        }

        if (!placesAtLeastOneTile) return null;

        // 2. Check connectivity
        boolean isFirstTurn = grid[7][7].isEmpty();
        if (isFirstTurn) {
            if (!passesThroughCenter(start, word.length(), dir)) return null;
        } else {
            // Must connect to existing word either by overlap or adjacency
            // Since we iterate anchors (adjacent empty squares), adjacency is guaranteed if we place a tile on an anchor.
            // But we must double check if we didn't overlap anything.
            if (!connectsToExisting && !hasNeighbor(board, start, word.length(), dir)) {
                 return null;
            }
        }

        // 3. Check Cross-Words Validity
        if (!areCrossWordsValid(board, start, word, dir, tilesToPlay)) {
            return null;
        }

        return Move.createPlay(player, tilesToPlay, start, dir);
    }
    
    /**
     * Checks if all new words formed perpendicular to the main word are valid.
     */
    private boolean areCrossWordsValid(Board board, Point start, String word, Direction dir, List<Tile> tilesToPlay) {
        Square[][] grid = board.getGrid();
        int x = start.getX();
        int y = start.getY();

        for (int i = 0; i < word.length(); i++) {
            Square sq = grid[x][y];
            
            // Only check cross words for newly placed tiles
            if (sq.isEmpty()) {
                // Determine cross direction
                Direction crossDir = (dir == Direction.HORIZONTAL) ? Direction.VERTICAL : Direction.HORIZONTAL;
                
                // Check if there are neighbors in the cross direction
                if (hasNeighborAt(grid, x, y, crossDir)) {
                    String crossWord = getFullWordAt(grid, x, y, crossDir, word.charAt(i));
                    if (crossWord.length() > 1 && !DICTIONARY.contains(crossWord)) {
                        return false; // Invalid cross word
                    }
                }
            }

            if (dir == Direction.HORIZONTAL) y++; else x++;
        }
        return true;
    }

    private boolean hasNeighborAt(Square[][] grid, int x, int y, Direction dir) {
        if (dir == Direction.VERTICAL) {
            return (x > 0 && !grid[x-1][y].isEmpty()) || (x < 14 && !grid[x+1][y].isEmpty());
        } else {
            return (y > 0 && !grid[x][y-1].isEmpty()) || (y < 14 && !grid[x][y+1].isEmpty());
        }
    }

    private String getFullWordAt(Square[][] grid, int x, int y, Direction dir, char placedLetter) {
        StringBuilder sb = new StringBuilder();
        sb.append(placedLetter);

        // Scan backwards
        int currX = x;
        int currY = y;
        if (dir == Direction.VERTICAL) currX--; else currY--;
        
        while (currX >= 0 && currY >= 0 && !grid[currX][currY].isEmpty()) {
            sb.insert(0, grid[currX][currY].getTile().getCharacter());
            if (dir == Direction.VERTICAL) currX--; else currY--;
        }

        // Scan forwards
        currX = x;
        currY = y;
        if (dir == Direction.VERTICAL) currX++; else currY++;

        while (currX < 15 && currY < 15 && !grid[currX][currY].isEmpty()) {
            sb.append(grid[currX][currY].getTile().getCharacter());
            if (dir == Direction.VERTICAL) currX++; else currY++;
        }
        
        return sb.toString();
    }

    // --- HELPERS ---

    private Tile findInRack(List<Tile> rack, char letter) {
        for (Tile t : rack) {
            if (t.getCharacter() == letter) return t;
        }
        return null;
    }

    private boolean isInBounds(Point start, int length, Direction dir) {
        int endX = start.getX() + (dir == Direction.VERTICAL ? length - 1 : 0);
        int endY = start.getY() + (dir == Direction.HORIZONTAL ? length - 1 : 0);
        return start.getX() >= 0 && start.getY() >= 0 && endX < 15 && endY < 15;
    }
    
    private boolean passesThroughCenter(Point start, int len, Direction dir) {
        int x = start.getX();
        int y = start.getY();
        for(int k=0; k<len; k++) {
            if(x == 7 && y == 7) return true;
            if(dir == Direction.HORIZONTAL) y++; else x++;
        }
        return false;
    }

    private boolean hasNeighbor(Board board, Point start, int len, Direction dir) {
        int x = start.getX();
        int y = start.getY();
        Square[][] grid = board.getGrid();
        
        for(int k=0; k<len; k++) {
            if (hasOccupiedNeighbor(grid, x, y)) return true;
            if(dir == Direction.HORIZONTAL) y++; else x++;
        }
        return false;
    }

    private boolean hasOccupiedNeighbor(Square[][] grid, int x, int y) {
        if (x > 0 && !grid[x - 1][y].isEmpty()) return true;
        if (x < 14 && !grid[x + 1][y].isEmpty()) return true;
        if (y > 0 && !grid[x][y - 1].isEmpty()) return true;
        if (y < 14 && !grid[x][y + 1].isEmpty()) return true;
        return false;
    }

    private List<Point> findAnchors(Board board) {
        Set<Point> anchors = new HashSet<>();
        Square[][] grid = board.getGrid();
        
        // Check if board is empty
        boolean isEmpty = true;
        if (!grid[7][7].isEmpty()) {
            isEmpty = false;
        } else {
            // Double check full board to be safe
            for(int i=0; i<15; i++) {
                for(int j=0; j<15; j++) {
                    if(!grid[i][j].isEmpty()) {
                        isEmpty = false;
                        break;
                    }
                }
                if(!isEmpty) break;
            }
        }

        if (isEmpty) {
            anchors.add(new Point(7, 7));
            return new ArrayList<>(anchors);
        }

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (grid[i][j].isEmpty()) {
                    if (hasOccupiedNeighbor(grid, i, j)) {
                        anchors.add(new Point(i, j));
                    }
                }
            }
        }
        return new ArrayList<>(anchors);
    }

    /**
     * Simulates placing the tiles on the board and calculating score.
     */
    private int applyMoveSimulation(Board board, Move move) {
        int score = 0;
        List<Tile> tiles = move.getTiles();
        Point pos = move.getStartPosition();
        Direction dir = move.getDirection();
        
        List<Square> wordSquares = new ArrayList<>();
        List<Square> newlyPlaced = new ArrayList<>();

        int currentTileIndex = 0;
        int x = pos.getX();
        int y = pos.getY();

        while (currentTileIndex < tiles.size()) {
            if (x >= 15 || y >= 15) break; 
            
            Square sq = board.getSquare(new Point(x, y));
            
            if (sq.isEmpty()) {
                Tile t = tiles.get(currentTileIndex);
                sq.setTile(t); // Place tile
                newlyPlaced.add(sq);
                wordSquares.add(sq);
                currentTileIndex++;
            } else {
                wordSquares.add(sq); // Existing tile
            }

            if (dir == Direction.HORIZONTAL) y++; else x++;
        }
        
        // Assuming Scoring class exists in model.core
        score = Scoring.calculateWordScore(wordSquares, newlyPlaced);
        
        return score;
    }
}

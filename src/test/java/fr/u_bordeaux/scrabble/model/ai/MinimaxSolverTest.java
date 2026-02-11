package fr.u_bordeaux.scrabble.model.ai;

import fr.u_bordeaux.scrabble.model.core.Bag;
import fr.u_bordeaux.scrabble.model.core.Board;
import fr.u_bordeaux.scrabble.model.core.Move;
import fr.u_bordeaux.scrabble.model.core.Rack;
import fr.u_bordeaux.scrabble.model.core.Tile;
import fr.u_bordeaux.scrabble.model.enums.Direction;
import fr.u_bordeaux.scrabble.model.enums.MoveType;
import fr.u_bordeaux.scrabble.model.utils.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinimaxSolverTest {

    private MinimaxSolver solver;
    private Board board;
    private Rack rack;
    private Bag bag;

    @BeforeEach
    void setUp() {
        solver = new MinimaxSolver();
        board = new Board(); // Initialise un plateau vide
        rack = new Rack();
        bag = new Bag(); // Initialise un sac plein
    }

    @Test
    void testBestMoveFirstTurn() {
        // Scénario : Premier tour de jeu.
        // On remplit le chevalet avec des lettres permettant de former "JEU" 
        // (mot présent dans le dictionnaire statique de MinimaxSolver).
        rack.addTile(new Tile('J'));
        rack.addTile(new Tile('E'));
        rack.addTile(new Tile('U'));
        // On complète avec d'autres lettres
        rack.addTile(new Tile('A'));
        rack.addTile(new Tile('B'));
        rack.addTile(new Tile('C'));
        rack.addTile(new Tile('D'));

        System.out.println("Test: Recherche du meilleur coup au premier tour...");
        Move move = solver.getBestMove(board, rack, bag);

        // Vérifications
        assertNotNull(move, "L'IA devrait trouver un coup valide.");
        assertEquals(MoveType.PLAY, move.getType(), "Le coup devrait être de type PLAY.");
        assertTrue(move.getScoreGained() > 0, "Le score du coup devrait être positif.");
        
        System.out.println("Coup trouvé par l'IA : " + move.getScoreGained() + " points.");
    }

    @Test
    void testNoMovePossible() {
        // Scénario : L'IA a des lettres qui ne permettent de former aucun mot du dictionnaire
        // sur un plateau vide (il faut au moins 2 lettres pour un mot).
        // On donne trois 'Z'. Dans le dictionnaire mocké, il n'y a pas de mot "ZZ" ou "ZZZ".
        rack.addTile(new Tile('Z'));
        rack.addTile(new Tile('Z'));
        rack.addTile(new Tile('Z'));
        
        System.out.println("Test: Vérification du comportement sans coup possible...");
        Move move = solver.getBestMove(board, rack, bag);
        
        // L'IA doit choisir de PASSER son tour
        assertNotNull(move, "L'IA doit retourner un coup (PASS).");
        assertEquals(MoveType.PASS, move.getType(), "L'IA devrait passer (PASS) si aucun mot n'est possible.");
    }

    @Test
    void testGameSequence() {
        System.out.println("\n--- Test: Simulation de 3 tours de jeu ---");

        // --- TOUR 1 : IA ---
        // Rack: J, E, U (permet de faire JEU)
        rack = new Rack();
        rack.addTile(new Tile('J'));
        rack.addTile(new Tile('E'));
        rack.addTile(new Tile('U'));
        rack.addTile(new Tile('A')); 
        rack.addTile(new Tile('B'));

        Move move1 = solver.getBestMove(board, rack, bag);
        
        assertNotNull(move1, "Tour 1: L'IA doit jouer au premier tour.");
        assertEquals(MoveType.PLAY, move1.getType());
        System.out.println("Tour 1 (IA) : Joue " + move1.getTiles() + " pour " + move1.getScoreGained() + " pts.");

        // On applique le coup sur le plateau pour simuler la suite de la partie
        applyMoveToBoard(board, move1);

        // --- TOUR 2 : ADVERSAIRE (Simulé) ---
        // Supposons que l'IA a joué JEU en 7,7 (Horizontal). Le U est donc en 7,9.
        // L'adversaire joue "UN" verticalement en utilisant ce U.
        // Il pose donc juste un 'N' en dessous du U (position 8,9).
        System.out.println("Tour 2 (Adversaire) : Joue 'N' pour former 'UN' verticalement.");
        
        // On triche un peu pour le test : on pose manuellement la tuile sur le plateau
        // Note : Assurez-vous que les coordonnées correspondent à la réalité du placement de l'IA
        // Si JEU est en 7,7 -> J(7,7), E(7,8), U(7,9).
        // On place N en 8,9.
        board.getSquare(new Point(8, 9)).setTile(new Tile('N'));

        // --- TOUR 3 : IA ---
        // L'IA a maintenant O, N dans son jeu (et d'autres lettres).
        // Elle peut profiter du 'N' posé par l'adversaire en (8,9) pour faire "NON" ou "ON".
        rack = new Rack();
        rack.addTile(new Tile('O'));
        rack.addTile(new Tile('N'));
        rack.addTile(new Tile('S')); // Lettres de remplissage
        rack.addTile(new Tile('T'));
        rack.addTile(new Tile('R'));

        Move move3 = solver.getBestMove(board, rack, bag);

        assertNotNull(move3, "Tour 3: L'IA doit trouver un coup en s'appuyant sur les mots existants.");
        assertEquals(MoveType.PLAY, move3.getType());
        System.out.println("Tour 3 (IA) : Joue " + move3.getTiles() + " pour " + move3.getScoreGained() + " pts.");
        
        assertTrue(move3.getScoreGained() > 0, "Le score doit être positif.");
    }

    /**
     * Méthode utilitaire pour appliquer un coup (Move) sur le plateau (Board)
     * car le Solver retourne un coup mais ne le joue pas automatiquement.
     */
    private void applyMoveToBoard(Board board, Move move) {
        if (move.getType() != MoveType.PLAY) return;

        int x = move.getStartPosition().getX();
        int y = move.getStartPosition().getY();
        Direction dir = move.getDirection();
        
        for (Tile tile : move.getTiles()) {
            // Si la case est déjà occupée, on saute (c'est une lettre déjà présente sur le plateau)
            while (!board.getSquare(new Point(x, y)).isEmpty()) {
                if (dir == Direction.HORIZONTAL) y++; else x++;
            }
            // On pose la tuile
            board.getSquare(new Point(x, y)).setTile(tile);
            
            // On avance pour la prochaine lettre
            if (dir == Direction.HORIZONTAL) y++; else x++;
        }
    }
}

package fr.u_bordeaux.scrabble.model.ai;

import fr.u_bordeaux.scrabble.model.core.Bag;
import fr.u_bordeaux.scrabble.model.core.Board;
import fr.u_bordeaux.scrabble.model.core.Move;
import fr.u_bordeaux.scrabble.model.core.Rack;
import fr.u_bordeaux.scrabble.model.core.Tile;

public class AITester {

    public static void main(String[] args) {
        System.out.println("--- Lancement du testeur d'IA ---");

        // Scénario 1: Premier tour, coup possible
        testFirstTurn();

        System.out.println("\n-----------------------------------\n");

        // Scénario 2: Pas de coup possible
        testNoMovePossible();

        System.out.println("\n--- Fin du testeur d'IA ---");
    }

    private static void testFirstTurn() {
        System.out.println(">>> Scénario 1: Recherche du meilleur coup au premier tour.");

        MinimaxSolver solver = new MinimaxSolver();
        Board board = new Board(); // Plateau vide
        Rack rack = new Rack();
        Bag bag = new Bag(); // Sac plein

        // On remplit le chevalet avec des lettres permettant de former "JEU"
        rack.addTile(new Tile('J'));
        rack.addTile(new Tile('E'));
        rack.addTile(new Tile('U'));
        rack.addTile(new Tile('A'));
        rack.addTile(new Tile('B'));
        rack.addTile(new Tile('C'));
        rack.addTile(new Tile('D'));

        System.out.println("Chevalet de l'IA: " + rack.getTiles());

        Move bestMove = solver.getBestMove(board, rack, bag);

        if (bestMove != null) {
            System.out.println("Coup trouvé par l'IA :");
            System.out.println("  - Type: " + bestMove.getType());
            if (bestMove.getType() == fr.u_bordeaux.scrabble.model.enums.MoveType.PLAY) {
                System.out.println("  - Mot joué (lettres du chevalet): " + bestMove.getTiles());
                System.out.println("  - Position: " + bestMove.getStartPosition());
                System.out.println("  - Direction: " + bestMove.getDirection());
                System.out.println("  - Score: " + bestMove.getScoreGained() + " points.");
            }
        } else {
            System.out.println("L'IA n'a trouvé aucun coup.");
        }
    }

    private static void testNoMovePossible() {
        System.out.println(">>> Scénario 2: Vérification du comportement sans coup possible.");

        MinimaxSolver solver = new MinimaxSolver();
        Board board = new Board(); // Plateau vide
        Rack rack = new Rack();
        Bag bag = new Bag();

        // On donne des lettres qui ne permettent de former aucun mot du dictionnaire
        rack.addTile(new Tile('Z'));
        rack.addTile(new Tile('W'));
        rack.addTile(new Tile('K'));

        System.out.println("Chevalet de l'IA: " + rack.getTiles());

        Move bestMove = solver.getBestMove(board, rack, bag);

        if (bestMove != null) {
            System.out.println("Coup trouvé par l'IA :");
            System.out.println("  - Type: " + bestMove.getType());
            if (bestMove.getType() == fr.u_bordeaux.scrabble.model.enums.MoveType.PASS) {
                System.out.println("  - L'IA a correctement choisi de passer son tour.");
            }
        } else {
            System.out.println("L'IA n'a retourné aucun coup (ce qui est inattendu, elle devrait passer).");
        }
    }
}

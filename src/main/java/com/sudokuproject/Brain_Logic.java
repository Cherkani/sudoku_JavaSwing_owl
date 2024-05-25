package com.sudokuproject;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class Brain_Logic {
    private static final String ONTOLOGY_FILE = "entology_owl.rdf";
    private OntModel model;
    
    public Brain_Logic() {

        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        InputStream in = FileManager.get().open(ONTOLOGY_FILE);
        if (in == null) {
            throw new IllegalArgumentException("Fichier ontologique non trouvé : " + ONTOLOGY_FILE);
        }
        model.read(in, null);
    }
    
    public boolean solve(int[][] board) {
        for (int row = 1; row <= 9; row++) {
            for (int col = 1; col <= 9; col++) {
                String cellIRI = "http://projet.org#c" + row + "_" + col;
                Individual cell = model.getIndividual(cellIRI);
                if (cell != null) {
                    Property hasNumber = model.getProperty("http://projet.org#hasNumber");
                    if (cell.hasProperty(hasNumber)) {
                        String number = cell.getPropertyValue(hasNumber).asLiteral().getString();
                        board[row - 1][col - 1] = Integer.parseInt(number);
                    }
                }
            }
        }
        return solveSudoku(board, 0, 0);
    }
    
    private boolean solveSudoku(int[][] board, int row, int col) {
        if (row == 9) {
            return true;
        }
        
        if (board[row][col] != 0) {
            return solveSudoku(board, col == 8 ? row + 1 : row, (col + 1) % 9);
        }
        
        for (int num = 1; num <= 9; num++) {
            if (isSafe(board, row, col, num)) {
                board[row][col] = num;
                if (solveSudoku(board, col == 8 ? row + 1 : row, (col + 1) % 9)) {
                    return true;
                }
                board[row][col] = 0;
            }
        }
        
        return false;
    }
    
    private boolean isSafe(int[][] board, int row, int col, int num) {
        for (int x = 0; x < 9; x++) {
            if (board[row][x] == num || board[x][col] == num ||
                board[row - row % 3 + x / 3][col - col % 3 + x % 3] == num) {
                return false;
            }
        }
        return true;
    }
}

package com.sudokuproject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Design_Interface extends JFrame {
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private Color userInputColor = new Color(250, 146, 49);
    private Color solverInputColor = new Color(255, 255, 186);
    private Color subgridColor1 = new Color(214, 233, 255);
    private Color subgridColor2 = new Color(186, 228, 255);
    private Color buttonColor = new Color(171, 171, 245);
    private Color backgroundColor = new Color(255, 255, 255);
    private Color borderColor = new Color(0, 0, 0);
    private Image backgroundImage;

    public Design_Interface() {
        setTitle("Sudoku Enigme : Résolvez le mystère des grilles");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Charger l'image de fond
        try {
            backgroundImage = ImageIO.read(new File("chemin_vers_image_de_fond.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        JLabel titleLabel = new JLabel("Sudoku Enigme : Résolvez le mystère des grilles", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(SUBGRID_SIZE, SUBGRID_SIZE, 2, 2)) {
            @Override
            public Insets getInsets() {
                return new Insets(10, 10, 10, 10);
            }
        };
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new LineBorder(borderColor, 2));

        for (int rowBlock = 0; rowBlock < SUBGRID_SIZE; rowBlock++) {
            for (int colBlock = 0; colBlock < SUBGRID_SIZE; colBlock++) {
                JPanel subgridPanel = new JPanel(new GridLayout(SUBGRID_SIZE, SUBGRID_SIZE));
                subgridPanel.setBorder(new LineBorder(borderColor, 1));
                subgridPanel.setOpaque(false);

                for (int row = 0; row < SUBGRID_SIZE; row++) {
                    for (int col = 0; col < SUBGRID_SIZE; col++) {
                        int actualRow = rowBlock * SUBGRID_SIZE + row;
                        int actualCol = colBlock * SUBGRID_SIZE + col;

                        cells[actualRow][actualCol] = new JTextField();
                        cells[actualRow][actualCol].setHorizontalAlignment(JTextField.CENTER);
                        cells[actualRow][actualCol].setFont(new Font("Arial", Font.BOLD, 20));
                        cells[actualRow][actualCol].setBorder(new RoundedBorder(10));
                        cells[actualRow][actualCol].addFocusListener(new FocusAdapter() {
                            public void focusLost(FocusEvent evt) {
                                JTextField source = (JTextField) evt.getSource();
                                if (!source.getText().isEmpty()) {
                                    try {
                                        Integer.parseInt(source.getText());
                                        source.setBackground(userInputColor);
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(Design_Interface.this, "Veuillez entrer un chiffre entre 1 et 9.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                                        source.setText("");
                                    }
                                }
                            }
                        });
                        if ((actualRow / SUBGRID_SIZE + actualCol / SUBGRID_SIZE) % 2 == 0) {
                            cells[actualRow][actualCol].setBackground(subgridColor1);
                        } else {
                            cells[actualRow][actualCol].setBackground(subgridColor2);
                        }
                        subgridPanel.add(cells[actualRow][actualCol]);
                    }
                }
                gridPanel.add(subgridPanel);
            }
        }

        JButton solveButton = new JButton("Résoudre");
        styleButton(solveButton);

        JButton resetButton = new JButton("Réinitialiser");
        styleButton(resetButton);

        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGrid();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(solveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(resetButton);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.setOpaque(false);

        mainPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void styleButton(JButton button) {
        button.setBackground(buttonColor);
        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setPreferredSize(new Dimension(120, 60));
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(25));
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);
    }


    private void solveSudoku() {
        int[][] sudoku = new int[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = cells[row][col].getText();
                if (!text.isEmpty()) {
                    sudoku[row][col] = Integer.parseInt(text);
                } else {
                    sudoku[row][col] = 0;
                }
            }
        }

        if (!isValidSudoku(sudoku)) {
            JOptionPane.showMessageDialog(this, "La grille est invalide. Veuillez vérifier votre saisie.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Brain_Logic solver = new Brain_Logic();
        if (solver.solve(sudoku)) {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (cells[row][col].getBackground() != userInputColor) {
                        cells[row][col].setText(String.valueOf(sudoku[row][col]));
                        cells[row][col].setBackground(solverInputColor);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Impossible de résoudre le Sudoku avec cette configuration.", "Erreur", JOptionPane.ERROR_MESSAGE);
            resetGrid();
        }
    }

    private void resetGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText("");
                if ((row / SUBGRID_SIZE + col / SUBGRID_SIZE) % 2 == 0) {
                    cells[row][col].setBackground(subgridColor1);
                } else {
                    cells[row][col].setBackground(subgridColor2);
                }
            }
        }
    }

    private boolean isValidSudoku(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            Set<Integer> rowSet = new HashSet<>();
            Set<Integer> colSet = new HashSet<>();
            for (int j = 0; j < SIZE; j++) {
                if (rowSet.contains(board[i][j]) && board[i][j] != 0) {
                    return false;
                }
                if (colSet.contains(board[j][i]) && board[j][i] != 0) {
                    return false;
                }
                rowSet.add(board[i][j]);
                colSet.add(board[j][i]);
            }
        }

        for (int i = 0; i < SIZE; i += SUBGRID_SIZE) {
            for (int j = 0; j < SIZE; j += SUBGRID_SIZE) {
                Set<Integer> subgridSet = new HashSet<>();
                for (int k = i; k < i + SUBGRID_SIZE; k++) {
                    for (int l = j; l < j + SUBGRID_SIZE; l++) {
                        if (subgridSet.contains(board[k][l]) && board[k][l] != 0) {
                            return false;
                        }
                        subgridSet.add(board[k][l]);
                    }
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Design_Interface().setVisible(true);
            }
        });
    }
}

class RoundedBorder implements Border {
    private int radius;

    RoundedBorder(int radius) {
        this.radius = radius;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}

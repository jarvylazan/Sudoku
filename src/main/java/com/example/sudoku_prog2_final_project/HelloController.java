package com.example.sudoku_prog2_final_project;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HelloController {
    @FXML
    private GridPane sudokuGrid; // Ensure this GridPane is properly referenced in your FXML

    private final TextField[][] gridFields = new TextField[9][9]; // Array to store text fields for Sudoku grid

    public void initialize() {
        // Initialize the grid with text fields
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = new TextField();
                tf.setPrefHeight(50);
                tf.setPrefWidth(50);
                gridFields[row][col] = tf;
                sudokuGrid.add(tf, col, row);
            }
        }
    }

    @FXML
    protected void onLoadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Sudoku File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            loadSudokuFromFile(file);
        }
    }

    private void loadSudokuFromFile(java.io.File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < 9) {
                String[] cells = line.split(",");
                for (int col = 0; col < Math.min(cells.length, 9); col++) {
                    gridFields[row][col].setText(cells[col].trim());
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

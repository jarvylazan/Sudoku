package com.example.sudoku_prog2_final_project;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SudokuHelper {

    // Init button, grid and 2d array of text fields, as well as the sudoku validator class.
    @FXML
    private Button finishedSetupButton;
    @FXML
    private GridPane mainGrid;
    private final TextField[][] sudokuFields = new TextField[9][9];
    SudokuValidator validate = new SudokuValidator();

    // This method initializes the program by inserting text fields into individual blocks in the mainGrid.
    public void initialize() {
        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                GridPane block = new GridPane();
                block.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        TextField tf = new TextField();
                        tf.setPrefWidth(50);
                        tf.setPrefHeight(50);
                        tf.setAlignment(Pos.CENTER);
                        block.add(tf, col, row);
                        validate.attachListeners(tf);
                        // Calculate the index in the sudokuFields array
                        int indexRow = blockRow * 3 + row;
                        int indexCol = blockCol * 3 + col;
                        sudokuFields[indexRow][indexCol] = tf; // Store the text field reference in the array
                    }
                }
                mainGrid.add(block, blockCol, blockRow);
            }
        }
    }

    // Assigned method to import csv sudoku file.
    @FXML
    protected void onImportButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Sudoku File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            loadSudokuFromFile(file);
        }
    }

    // Makes the filled text fields un-editable
    @FXML
    protected void onFinishedSetupButtonClick() {
        try {
            validateBoard();
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = sudokuFields[row][col];
                if (!tf.getText().trim().isEmpty()) {
                    tf.setEditable(false);
                }
            }
        }
    }

    // Void method that validates the current state of the board and throws specific exception messages based on the type of error in the board
    private void validateBoard() {
        boolean[] seen;

        // Check rows and columns
        for (int i = 0; i < 9; i++) {
            seen = new boolean[10];  // Index 0 is unused
            for (int j = 0; j < 9; j++) {
                if (!validate.validateCell(sudokuFields[i][j], seen)) {
                    FormatInvalidCell(j, i);
                    throw new NumberFormatException("There is either an error or a duplicate in your rows. Please correct.");
                }
            }
            seen = new boolean[10];  // Reset for column check
            for (int j = 0; j < 9; j++) {
                if (!validate.validateCell(sudokuFields[j][i], seen)) {
                    FormatInvalidCell(j, i);
                    throw new NumberFormatException("There is an error or a duplicate in your rows. Please correct.");
                }
            }
        }

        // Check 3x3 sub-grids
        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                seen = new boolean[10];
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        int x = blockRow * 3 + row;
                        int y = blockCol * 3 + col;
                        try {
                            if (!validate.validateCell(sudokuFields[x][y], seen)) {
                                FormatInvalidCell(x, y);
                                throw new NumberFormatException("One of your sub-grids has an error or a duplicate. Please correct.");
                            }
                        } catch (NumberFormatException e) {
                            FormatInvalidCell(x, y);
                            throw new NumberFormatException("There is invalid input in one of your sub-grids. Please correct.");
                        }
                    }
                }
            }
        }
    }

    // This method is called to format the invalid texts.
    private void FormatInvalidCell(int x, int y) {
        sudokuFields[x][y].setEditable(true);
        sudokuFields[x][y].setStyle("-fx-text-fill: red;");
    }

    // Method to import the csv sudoku file.
    private void loadSudokuFromFile(java.io.File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int rowCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cells = line.split(",");
                if (cells.length != 9) {
                    throw new InvalidFileFormatException("The file does not have exactly 9 inputs per row.");
                }
                rowCount++;
                if (rowCount > 9) {
                    throw new InvalidFileFormatException("The file has more than 9 rows.");
                }
                if (!validate.validateRow(cells)) {
                    throw new InvalidFileFormatException("The file contains invalid input.");
                }
                try {
                    for (int col = 0; col < cells.length; col++) {
                        TextField tf = sudokuFields[rowCount - 1][col];
                        if (tf != null) {
                            String value = cells[col].trim();
                            if (!value.isEmpty()) {
                                if (!value.matches("[1-9]")) { // Check if the value is not a digit from 1 to 9
                                    throw new NumberFormatException("Invalid input detected in the file.");
                                }
                                tf.setText(value);
                                tf.setEditable(false); // Make the field non-editable only if it's filled
                            } else {
                                tf.setEditable(true); // Ensure empty fields are editable
                                tf.setStyle("-fx-opacity: 1.0;"); // Reset the style for empty cells
                            }
                        }
                    }
                } catch (NumberFormatException ex) { // Catch error if value is invalid.
                    Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    alert.showAndWait();
                }
            }
            if (rowCount != 9) {
                throw new InvalidFileFormatException("The file does not have exactly 9 rows.");
            }
            validateBoard();
        } catch (InvalidFileFormatException ex) { // Catch error if file format is invalid (less/more than 9 rows)
            onClearButtonClick();
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        } catch (IOException ex) { // Catch error if importing file fails
            throw new RuntimeException(ex);
        }
    }

    // Clear the entire board
    @FXML
    protected void onClearButtonClick() {
        for (TextField[] row : sudokuFields) {
            for (TextField tf : row) {
                tf.setText(""); // Clear the text
                tf.setEditable(true); // Make the field editable again
                tf.setStyle("-fx-opacity: 1.0;"); // Reset any style if needed
            }
        }
        finishedSetupButton.setVisible(true);
    }

    // Method to solve the entire board.
    @FXML
    protected void onSolveBoardButtonClick() {
        try {
            validateBoard();
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
        solveSudoku();
    }

    // Solve the entire sudoku board.
    private boolean solveSudoku() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = sudokuFields[row][col];
                if (tf.getText().isEmpty()) {  // Find an empty cell
                    for (int num = 1; num <= 9; num++) {  // Try possible numbers
                        if (isValid(row, col, num)) {
                            tf.setText(String.valueOf(num));  // Place the number
                            tf.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");  // Color it green
                            if (solveSudoku()) {  // Recursively continue to solve
                                return true;
                            } else {
                                tf.setText("");  // Backtrack
                            }
                        }
                    }
                    return false;  // Trigger backtrack
                }
            }
        }
        return true;  // Puzzle solved
    }


    // Create a suggestion box for the entire board.
    @FXML
    private void GetSuggestions() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = sudokuFields[row][col];
                if (tf.getText().isEmpty()) {  // Only update if the cell is empty
                    List<Integer> possibleNumbers = new ArrayList<>();
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(row, col, num)) {
                            possibleNumbers.add(num);
                        }
                    }
                    // Format and display suggestions without commas
                    if (!possibleNumbers.isEmpty()) {
                        String suggestion = possibleNumbers.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining());  // No separator used
                        tf.setText(suggestion);
                        tf.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        tf.setFont(Font.font("-fx-text-fill: green; -fx-font-weight: bold;", 10));
                        tf.setAlignment(Pos.TOP_LEFT);
                    }
                }
            }
        }
    }

    // Compared to the ValidateBoard() method, this method is used by the SolveSudoku() method to verify its solutions.
    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            // Row check
            if (!sudokuFields[row][i].getText().isEmpty() &&
                    Integer.parseInt(sudokuFields[row][i].getText()) == num) {
                return false;
            }
            // Column check
            if (!sudokuFields[i][col].getText().isEmpty() &&
                    Integer.parseInt(sudokuFields[i][col].getText()) == num) {
                return false;
            }
        }
        // Subgrid check
        int startRow = row / 3 * 3;
        int startCol = col / 3 * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (!sudokuFields[i][j].getText().isEmpty() &&
                        Integer.parseInt(sudokuFields[i][j].getText()) == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private static class InvalidFileFormatException extends Throwable {
        public InvalidFileFormatException(String s) {
            super(s);
        }
    }

    //TODO Validate the imported csv file. (Too many commas, too little, etc etc).
}

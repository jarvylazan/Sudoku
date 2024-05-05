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

    @FXML
    private Button finishedSetupButton;
    @FXML
    private GridPane mainGrid; // Ensure this GridPane is properly referenced inFXML

    private TextField[][] sudokuFields = new TextField[9][9]; // Array to store text fields for Sudoku grid

    SudokuValidator validate = new SudokuValidator();

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

    @FXML
    protected void onFinishedSetupButtonClick() {
        validateBoard();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = sudokuFields[row][col];
                if (!tf.getText().trim().isEmpty()) {
                    tf.setEditable(false);
                }
            }
        }
    }


    private void validateBoard() {
        boolean[] seen;
        boolean valid = true;
        Alert alert = new Alert(Alert.AlertType.ERROR, "This is not a valid input for the Sudoku table setup.\n" +
                "Please use integers from 1 to 9.");

        // Check rows and columns
        for (int i = 0; i < 9; i++) {
            seen = new boolean[10];  // Index 0 is unused
            for (int j = 0; j < 9; j++) {
                if (!validate.validateCell(sudokuFields[i][j], seen)) {
                    valid = false;
                    FormatInvalidCell(j,i);
                }
            }

            seen = new boolean[10];  // Reset for column check
            for (int j = 0; j < 9; j++) {
                if (!validate.validateCell(sudokuFields[j][i], seen)) {
                    valid = false;
                    FormatInvalidCell(j,i);
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
                                valid = false;
                                FormatInvalidCell(x,y);
                            }
                        } catch (NumberFormatException e) {
                            valid = false;
                            FormatInvalidCell(x,y);
                        }
                    }
                }
            }
        }
        if (!valid) {
            alert.showAndWait();
        }
    }

    private void FormatInvalidCell(int x, int y) {
        sudokuFields[x][y].setEditable(true);
        sudokuFields[x][y].setStyle("-fx-text-fill: red;");
    }

    private void loadSudokuFromFile(java.io.File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            boolean valid = true; // Flag to track overall validity
            int rowCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cells = line.split(",");
                if (cells.length != 9) {
                    valid = false;
                    break; // Exit loop immediately
                }
                rowCount++;
                if (rowCount > 9) {
                    valid = false;
                    break;
                }
                if (!validateRow(cells)) {
                    valid = false;
                    break;
                }
                for (int col = 0; col < cells.length; col++) {
                    TextField tf = sudokuFields[rowCount - 1][col];
                    if (tf != null) {
                        String value = cells[col].trim();
                        if (!value.isEmpty()) {
                            if (!value.matches("[1-9]")) { // Check if the value is not a digit from 1 to 9
                                valid = false;
                                break;
                            }
                            tf.setText(value);
                            tf.setEditable(false); // Make the field non-editable only if it's filled
                        } else {
                            tf.setEditable(true); // Ensure empty fields are editable
                            tf.setStyle("-fx-opacity: 1.0;"); // Reset the style for empty cells
                        }
                    }
                }
                if (!valid) {
                    break;
                }
            }
            if (!valid || rowCount != 9) {
                // Incorrect number of rows or invalid row length
                onClearButtonClick();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your imported csv file is wrong.");
                alert.showAndWait();
            }
            validateBoard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateRow(String[] cells) {
        for (String cell : cells) {
            if (!cell.trim().isEmpty() && !cell.matches("[1-9]")) {
                return false;
            }
        }
        return true;
    }


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

    @FXML
    protected void onGetHelpButtonClick() {
        validateBoard();
        solveSudoku();
    }

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


    // Create a suggestion box for a single cell.
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

    //TODO Validate the imported csv file. (Too many commas, too little, etc etc).
}

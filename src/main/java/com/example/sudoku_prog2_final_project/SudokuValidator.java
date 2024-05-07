package com.example.sudoku_prog2_final_project;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class SudokuValidator {

    public void attachListeners(TextField textField) {
        // Adding a change listener to each TextField
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) { // Allow only digits
                textField.setText(newValue.replaceAll("\\D", ""));
            }
            if (!newValue.isEmpty() && !isValidInput(newValue)) {
                textField.setStyle("-fx-text-fill: red;"); // Set text color to red if input is invalid
            } else {
                textField.setStyle("-fx-text-fill: black;"); // Reset text color to black if input is valid
                textField.setAlignment(Pos.CENTER);
                textField.setFont(Font.font("-fx-text-fill: black;", 12));
            }
        });
    }

    private boolean isValidInput(String input) {
        try {
            int value = Integer.parseInt(input);
            return value >= 1 && value <= 9;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateCell(TextField cell, boolean[] seen) {
        if (!cell.getText().isEmpty()) {
            try {
                int num = Integer.parseInt(cell.getText());
                if (num < 1 || num > 9 || seen[num]) {
                    return false;  // Invalid number or duplicate
                }
                seen[num] = true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}

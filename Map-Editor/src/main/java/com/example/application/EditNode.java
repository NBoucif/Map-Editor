package com.example.application;


import com.example.application.models.Shelf;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class EditNode {

    @FXML private Button confirm_button;

    @FXML private TextField textfield_height;

    @FXML private TextField textfield_id;

    @FXML private TextField textfield_object;

    @FXML  private TextField textfield_width;

    private static Shelf shelf;

    @FXML
    private void initialize() {
        textfield_object.setText("Shelf");
        buildHandlers();
    }

    public void setTextFields(DraggableNode node) throws IOException {
        textfield_id.setText(String.valueOf(node.getShelf().getId()));
        textfield_object.setText(node.getShelf().getIdentifier());
        textfield_height.setText(String.valueOf(node.getShelf().getHeight()));
        textfield_width.setText(String.valueOf(node.getShelf().getWidth()));
    }

    public void buildHandlers(){
        confirm_button.setOnMouseClicked( new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(Double.parseDouble(textfield_height.getText()));
                shelf = new Shelf(Long.parseLong(textfield_id.getText()),textfield_object.getText(),Double.parseDouble(textfield_width.getText()),Double.parseDouble(textfield_height.getText()));
                Stage stage = (Stage) confirm_button.getScene().getWindow();
                stage.close();
            }
        });
    }

    public Shelf getValues() {
        if(shelf ==null){
            shelf = new Shelf(Long.parseLong(textfield_id.getText()),textfield_object.getText(),Double.parseDouble(textfield_width.getText()),Double.parseDouble(textfield_height.getText()));
        }
        return shelf;
    }
}

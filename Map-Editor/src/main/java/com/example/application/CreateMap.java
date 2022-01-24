package com.example.application;

import com.example.application.models.Map;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateMap {

    @FXML private TextField textfield_height;

    @FXML private TextField textfield_id;

    @FXML  private TextField textfield_width;

    @FXML private Button confirm_button;

    private static Map map;

    @FXML
    private void initialize() {
        buildHandlers();
    }

    public void setTextFields(Map map) throws IOException {
        textfield_id.setText(String.valueOf(map.getId()));
        textfield_height.setText(String.valueOf(map.getHeight()));
        textfield_width.setText(String.valueOf(map.getWidth()));
    }

    public void buildHandlers(){
        confirm_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) confirm_button.getScene().getWindow();
                stage.close();
            }
        });
    }

    public Map getValues() {
        if(map ==null){
            map = new Map(Long.parseLong(textfield_id.getText()),Double.parseDouble(textfield_width.getText()),Double.parseDouble(textfield_height.getText()));
        }

        return map;
    }
}

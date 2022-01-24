package com.example.application.icon.click;

import com.example.application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ClickIcon extends AnchorPane {

    private ClickIconType mType;

    public ClickIcon() {

        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("ClickIcon.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {}

    public ClickIconType getType() { return mType;}

    public void setType(ClickIconType type) {

        mType = type;

        getStyleClass().clear();
        getStyleClass().add("icon");
        switch (mType) {
            case map:
                getStyleClass().add("icon-map");
                break;
            default:
                break;
        }
    }
}
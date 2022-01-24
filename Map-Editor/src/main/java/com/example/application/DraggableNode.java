package com.example.application;

import com.example.application.draggable.DragContainer;
import com.example.application.icon.drag.DragIconType;
import com.example.application.models.Shelf;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;


import java.io.IOException;

public class DraggableNode extends AnchorPane {



    @FXML AnchorPane root_pane;
    @FXML ImageView close_button;
    @FXML ImageView rotate_button;
    @FXML ImageView edit_button;
    @FXML AnchorPane right_handle;
    @FXML AnchorPane left_handle;
    @FXML AnchorPane node_body;
    @FXML Label draggableZone;
    @FXML Label label_height;
    @FXML Label label_width;
    @FXML Line lineHeight;
    @FXML Line lineWidth;

    private Shelf shelf;
    private final DraggableNode self;
    private DragIconType mType = null;



    private Point2D mDragOffset = new Point2D(0.0, 0.0);
    private int degree =0;


    public DraggableNode(Long id) {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("DraggableNode.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        self = this;
        shelf = new Shelf(id, "Shelf",70.0,96.0);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Point2D getmDragOffset() {
        return mDragOffset;
    }

    public void setmDragOffset(Point2D mDragOffset) {
        this.mDragOffset = mDragOffset;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public DragIconType getType() { return mType;}

    public void setType(DragIconType type) {

        mType = type;

        getStyleClass().clear();
        getStyleClass().add("dragicon");
        switch (mType) {
            case shelf:
                getStyleClass().add("icon-shelf");
                break;
            default:
                break;
        }
    }

    public void relocateToPoint (Point2D p) {
        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);

        relocate (
                (int) (localCoords.getX() - mDragOffset.getX()),
                (int) (localCoords.getY() - mDragOffset.getY())
        );
    }

    @FXML
    private void initialize() {
        System.out.println(root_pane.getBoundsInLocal().getHeight());
        draggableZone.setText(String.valueOf(shelf.getId()));
        setElementsVisible(false);
    }

    @FXML
    void showActions(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            setElementsVisible(true);
        }else if (event.getButton() == MouseButton.SECONDARY) {
            setElementsVisible(false);
        }
    }

    public void setElementsVisible(boolean visible){
        label_height.setText(String.valueOf(self.getHeight()));
        label_width.setText(String.valueOf(self.getWidth()));
        label_width.setLayoutX((root_pane.getWidth()/2)-8);
        label_height.setLayoutY((root_pane.getHeight()/2)-8);
        label_height.setVisible(visible);
        label_width.setVisible(visible);
        close_button.setVisible(visible);
        rotate_button.setVisible(visible);
        edit_button.setVisible(visible);
        lineHeight.setVisible(visible);
        lineWidth.setVisible(visible);
    }

    public void resizeNode(Double newWidth, Double newHeight){
        self.setMinHeight(newHeight);
        self.setPrefHeight(newHeight);
        self.setMinWidth(newWidth);
        self.setPrefWidth(newWidth);

        self.draggableZone.setMinHeight(newHeight-10);
        self.draggableZone.setPrefHeight(newHeight-10);
        self.draggableZone.setMinWidth(newWidth-10);
        self.draggableZone.setPrefWidth(newWidth-10);

        self.label_width.setLayoutX((newWidth/2)-8);
        self.label_height.setLayoutY((newHeight/2)-8);
        self.label_height.setText(String.valueOf(newHeight));
        self.label_width.setText(String.valueOf(newWidth));

        self.lineHeight.setEndY(newHeight-8);
        self.lineWidth.setEndX((self.lineWidth.getStartX()+newWidth)-15);
    }
}
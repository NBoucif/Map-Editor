package com.example.application;

import com.example.application.draggable.*;
import com.example.application.icon.drag.DragIcon;
import com.example.application.icon.drag.DragIconType;
import com.example.application.draggable.node.DragResize;
import com.example.application.icon.click.ClickIcon;
import com.example.application.icon.click.ClickIconType;
import com.example.application.models.Map;
import com.example.application.sequenceGenerator.AtomicSequenceGenerator;
import com.example.application.models.Shelf;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LongStringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RootLayout extends AnchorPane {
    @FXML private SplitPane base_pane;
    @FXML private AnchorPane right_pane;
    @FXML private VBox left_pane;
    @FXML private TableView<Shelf> table;
    @FXML private TableColumn<Shelf, Long> id;
    @FXML private TableColumn<Shelf, String> identifier;
    @FXML private TableColumn<Shelf, Double> width;
    @FXML private TableColumn<Shelf, Double> depth;
    @FXML private TableColumn<Shelf, Double> height;

    private final int size =  900;
    private final int spots = 18;
    private final int squareSize = size / spots;
    private boolean [][] grid;
    private boolean editMap;

    private List<DraggableNode> listOfNodes;
    private Map map;
    private ObservableList<Shelf> list;
    private final AtomicSequenceGenerator sequenceGenerator;

    private DragIcon mDragOverIcon = null;

    private EventHandler mIconDragOverRoot=null;
    private EventHandler mIconDragDropped=null;
    private EventHandler mIconDragOverRightPane=null;

    //EventHandler Node
    private EventHandler <MouseEvent> mLinkHandleDragDetected;
    private EventHandler <DragEvent> mLinkHandleDragDropped;
    private EventHandler <DragEvent> mContextLinkDragOver;
    private EventHandler <DragEvent> mContextLinkDragDrop;

    private EventHandler mContextDragOver;
    private EventHandler mContextDragDropped;
    private EventHandler rotateDragOver;
    private EventHandler rotateDragDropped;



    public RootLayout() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("RootLayout.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            sequenceGenerator = new AtomicSequenceGenerator();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        //Add one icon that will be used for the drag-drop process
        //This is added as a child to the root AnchorPane so it can be
        //visible on both sides of the split pane.

        listOfNodes = new ArrayList<DraggableNode>();
        mDragOverIcon = new DragIcon();
        grid = new boolean[size][size];
        map = new Map();

        editMap = false;

        mDragOverIcon.setVisible(false);
        mDragOverIcon.setOpacity(0.65);
        getChildren().add(mDragOverIcon);

        for (ClickIconType iconType : ClickIconType.values() ) {
            ClickIcon icn = new ClickIcon();
            addClickDetection(icn);
            icn.setType(iconType);
            left_pane.getChildren().add(icn);
        }

        //populate left pane with multiple colored icons for testing
        for(DragIconType iconType : DragIconType.values() ) {
            DragIcon icn = new DragIcon();
            addDragDetection(icn);
            icn.setType(iconType);
            left_pane.getChildren().add(icn);
        }

        for(int i=0;i<size;i+=squareSize){
            for(int j=0; j<size;j+=squareSize){
                Rectangle r = new Rectangle(i,j, squareSize,squareSize);
                r.setFill(Color.WHITE);
                r.setStroke(Color.DIMGREY);
                right_pane.getChildren().add(r);
            }
        }
        buildDragHandlers();
        setUpTable();
    }

    public List<DraggableNode> getMap() {
        return listOfNodes;
    }

    public void setUpTable(){
        list = FXCollections.observableArrayList();

        id.setCellValueFactory(new PropertyValueFactory<Shelf, Long>("id"));
        id.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        id.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Shelf, Long>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Shelf, Long> event) {
                Shelf shelf = event.getRowValue();

                for(DraggableNode node : listOfNodes) {
                    if (shelf.getId() == node.getShelf().getId()) {
                        node.getShelf().setId(event.getNewValue());
                        shelf.setId(event.getNewValue());
                    }
                }
            }
        });

        identifier.setCellValueFactory(new PropertyValueFactory<Shelf, String>("identifier"));
        identifier.setCellFactory(TextFieldTableCell.forTableColumn());
        identifier.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Shelf, String>>(){
            @Override
            public void handle(TableColumn.CellEditEvent<Shelf, String> event) {
                Shelf shelf = event.getRowValue();
                for(DraggableNode node : listOfNodes) {
                    if (shelf.getId() == node.getShelf().getId()) {
                        node.getShelf().setIdentifier(event.getNewValue());
                        shelf.setIdentifier(event.getNewValue());
                    }
                }
            }
        });

        width.setCellValueFactory(new PropertyValueFactory<Shelf, Double>("width"));
        width.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        width.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Shelf, Double>>(){
            @Override
            public void handle(TableColumn.CellEditEvent<Shelf, Double> event) {
                Shelf shelf = event.getRowValue();
                for(DraggableNode node : listOfNodes) {
                    if (shelf.getId() == node.getShelf().getId()) {
                        node.getShelf().setWidth(event.getNewValue());
                        shelf.setWidth(event.getNewValue());
                        node.resizeNode(node.getShelf().getWidth(),node.getShelf().getHeight());
                    }
                }
            }
        });

        depth.setCellValueFactory(new PropertyValueFactory<Shelf, Double>("depth"));
        depth.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        depth.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Shelf, Double>>(){
            @Override
            public void handle(TableColumn.CellEditEvent<Shelf, Double> event) {
                Shelf shelf = event.getRowValue();
                for(DraggableNode node : listOfNodes) {
                    if (shelf.getId() == node.getShelf().getId()) {
                        node.getShelf().setDepth(event.getNewValue());
                        shelf.setDepth(event.getNewValue());
                    }
                }
            }
        });

        height.setCellValueFactory(new PropertyValueFactory<Shelf, Double>("height"));
        height.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        height.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Shelf, Double>>(){
            @Override
            public void handle(TableColumn.CellEditEvent<Shelf, Double> event) {
                Shelf shelf = event.getRowValue();
                for(DraggableNode node : listOfNodes) {
                    if (shelf.getId() == node.getShelf().getId()) {
                        node.getShelf().setHeight(event.getNewValue());
                        shelf.setHeight(event.getNewValue());
                        node.resizeNode(node.getShelf().getWidth(),node.getShelf().getHeight());
                    }
                }
            }
        });

        table.setItems(list);
        table.setEditable(true);
    }

    private void addDragDetection(DragIcon dragIcon) {

        dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                // set the other drag event handles on their respective objects
                base_pane.setOnDragOver(mIconDragOverRoot);
                right_pane.setOnDragOver(mIconDragOverRightPane);
                right_pane.setOnDragDropped(mIconDragDropped);

                // get a reference to the clicked DragIcon object
                DragIcon icn = (DragIcon) event.getSource();

                //begin drag ops
                mDragOverIcon.setType(icn.getType());
                mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData ("type", mDragOverIcon.getType().toString());
                content.put(DragContainer.AddNode, container);

                mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
                mDragOverIcon.setVisible(true);
                mDragOverIcon.setMouseTransparent(true);

                event.consume();
            }
        });
    }

    private void addClickDetection(ClickIcon clickIcon) {

        clickIcon.setOnMouseClicked (new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event)  {
                try {
                    System.out.println(getClass().getResource("CreateMap.fxml"));
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateMap.fxml"));
                    Parent root =  loader.load();
                    CreateMap mapController = loader.getController();
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);


                    if(event.getButton().equals(MouseButton.PRIMARY)){
                        stage.setTitle("Create Map");
                    }else if(event.getButton().equals(MouseButton.SECONDARY)){
                        stage.setTitle("Edit Map");
                        editMap=true;
                    }

                    if(editMap){
                        System.out.println(map.toString());
                        mapController.setTextFields(map);
                        editMap=false;
                    }
                    stage.showAndWait();
                    map = mapController.getValues();
                    event.consume();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void buildDragHandlers() {

        //drag over transition to move widget form left pane to right pane
        mIconDragOverRoot = new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());
                if (!right_pane.boundsInLocalProperty().get().contains(p)) {
                    mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                    return;
                }
                event.consume();
            }
        };

        mIconDragOverRightPane = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                mDragOverIcon.relocateToPoint(
                        new Point2D(event.getSceneX(), event.getSceneY())
                );
                event.consume();
            }
        };

        mIconDragDropped = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {

                DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

                container.addData("scene_coords",
                        new Point2D(event.getSceneX()-32, event.getSceneY()-32));

                ClipboardContent content = new ClipboardContent();
                content.put(DragContainer.AddNode, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
            }
        };

        this.setOnDragDone (new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
                right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
                base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);
                mDragOverIcon.setVisible(false);
                DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

                if (container != null) {
                    if (container.getValue("scene_coords") != null) {

                        DraggableNode node = new DraggableNode(sequenceGenerator.getNext());
                        DragResize.makeResizable(node);
                        addNodeDetection(node);

                        node.setType(DragIconType.valueOf(container.getValue("type")));
                        right_pane.getChildren().add(node);
                        Point2D cursorPoint = container.getValue("scene_coords");

                        node.relocateToPoint(
                                new Point2D(cursorPoint.getX() , cursorPoint.getY() )
                        );

                        listOfNodes.add(node);

                        table.getItems().add(new Shelf(node.getShelf().getId(),node.getShelf().getIdentifier(), node.getShelf().getWidth(),node.getShelf().getHeight()));
                    }
                }
                event.consume();
            }
        });


    }

    private void addNodeDetection(DraggableNode node) {

        //drag detection for node dragging
        node.draggableZone.setOnDragDetected ( new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                node.setElementsVisible(true);

                getParent().setOnDragOver (mContextDragOver);
                getParent().setOnDragDropped (mContextDragDropped);

                //begin drag ops
                node.setmDragOffset(new Point2D(event.getX(), event.getY()));

                node.relocateToPoint (new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData ("type", node.getType().toString());
                content.put(DragContainer.DragNode, container);

                startDragAndDrop (TransferMode.ANY).setContent(content);
                event.consume();
            }
        });

        mContextDragOver = new EventHandler <DragEvent> () {
            //dragover to handle node dragging in the right pane view
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                node.relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        };

        //dragdrop for node dragging
        mContextDragDropped = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                node.getParent().setOnDragOver(null);
                node.getParent().setOnDragDropped(null);

                event.setDropCompleted(true);

                event.consume();
            }
        };

        node.rotate_button.setOnMouseClicked( new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(node.getWidth());
            }
        });

        node.left_handle.setOnMouseClicked( new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if( node.right_handle.getStyleClass().get(0) == "left-link-handleClicked"){
                    node.right_handle.getStyleClass().clear();
                    node.right_handle.getStyleClass().add("right-link-handle");
                }
                node.left_handle.getStyleClass().clear();
                node.left_handle.getStyleClass().add("left-link-handleClicked");
                node.getShelf().setOrientation("LEFT");
            }
        });

        node.right_handle.setOnMouseClicked( new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(node.left_handle.getStyleClass().get(0));
                if( node.left_handle.getStyleClass().get(0) == "left-link-handleClicked"){
                    node.left_handle.getStyleClass().clear();
                    node.left_handle.getStyleClass().add("left-link-handle");
                }
                node.right_handle.getStyleClass().clear();
                node.right_handle.getStyleClass().add("left-link-handleClicked");
                node.getShelf().setOrientation("RIGHT");
            }
        });

        node.rotate_button.setOnDragDetected ( new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver (rotateDragDropped);
                getParent().setOnDragDropped (rotateDragOver);


                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData ("type", node.getType().toString());
                content.put(DragContainer.DragNode, container);

                startDragAndDrop (TransferMode.ANY).setContent(content);
                event.consume();
            }
        });

        rotateDragDropped = new EventHandler <DragEvent> () {
            //dragover to handle node dragging in the right pane view
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                node.setDegree((int) event.getSceneX()%360);
                //System.out.println(event.getSceneX()%360);

                if(node.getDegree()>=80 && node.getDegree()<=100){
                    node.setRotate(90);
                } else if(node.getDegree()>=170 && node.getDegree()<=190){
                    node.setRotate(180);
                }else if(node.getDegree()>=260 && node.getDegree()<=280){
                    node.setRotate(270);
                }else if(node.getDegree()>=350 || node.getDegree()<=10){
                    node.setRotate(360);
                }else{
                    node.setRotate(event.getSceneX());
                }
                event.consume();
            }
        };

        //dragdrop for node dragging
        rotateDragOver = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                event.setDropCompleted(true);
                event.consume();
            }
        };

        //close button click
        node.close_button.setOnMouseClicked( new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                AnchorPane parent  = (AnchorPane) node.getParent();
                parent.getChildren().remove(node);

                ObservableList<Shelf> allShelf, SelectedShelf;
                allShelf=table.getItems();

                SelectedShelf = table.getSelectionModel().getSelectedItems();
                SelectedShelf.forEach(allShelf::remove);
            }
        });

        node.edit_button.setOnMouseClicked( new EventHandler <MouseEvent> ()  {
            //send to scene 2
            @Override
            public void handle(MouseEvent event)  {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("EditShelf.fxml"));
                    Parent root = loader.load();

                    EditNode scene2Controller = loader.getController();
                    scene2Controller.setTextFields(node);
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.showAndWait();

                    Shelf editShelf = scene2Controller.getValues();

                    System.out.println(node.getShelf().toString());
                    node.setShelf(editShelf);
                    System.out.println(node.getShelf().toString());
                    node.resizeNode(editShelf.getWidth(), editShelf.getHeight());

                    event.consume();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mLinkHandleDragDetected = new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver(mContextLinkDragOver);
                getParent().setOnDragDropped(mLinkHandleDragDropped);

                event.consume();
            }
        };
    }

    public void updateTable(Shelf shelf) {
        System.out.println(shelf);
    }
}

module com.example.demo2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.application to javafx.fxml;
    exports com.example.application;
    exports com.example.application.sequenceGenerator;
    opens com.example.application.sequenceGenerator to javafx.fxml;
    exports com.example.application.models;
    opens com.example.application.models to javafx.fxml;
    exports com.example.application.draggable;
    opens com.example.application.draggable to javafx.fxml;
    exports com.example.application.draggable.node;
    opens com.example.application.draggable.node to javafx.fxml;
    opens com.example.application.icon.drag to javafx.fxml;
    exports com.example.application.icon.drag;

    exports com.example.application.icon.click;
    opens com.example.application.icon.click to javafx.fxml;
}
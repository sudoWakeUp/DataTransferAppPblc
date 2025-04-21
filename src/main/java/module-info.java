module ge.edu.sangu.giorgi.datatransferapp {
    requires javafx.fxml;
    requires jsch;
    requires jdk.compiler;
    requires de.jensd.fx.glyphs.fontawesome;
    requires javafx.controls;
    requires com.h2database;
    requires java.sql;
    requires java.naming;
    //requires fontawesomefx;

    opens ge.edu.sangu.giorgi.datatransferapp to javafx.fxml;
    exports ge.edu.sangu.giorgi.datatransferapp;
    exports ge.edu.sangu.giorgi.datatransferapp.scenes;
    opens ge.edu.sangu.giorgi.datatransferapp.scenes to javafx.fxml;
    exports ge.edu.sangu.giorgi.datatransferapp.controllers;
    opens ge.edu.sangu.giorgi.datatransferapp.controllers to javafx.fxml;
}
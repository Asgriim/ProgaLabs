module com.example.lab8_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml.bind;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens utility to java.xml.bind;
    opens com.example.lab8_2 to javafx.fxml;
    exports com.example.lab8_2;
}
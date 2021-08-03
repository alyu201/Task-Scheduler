package Controller;

import Model.Visualiser;
import javafx.fxml.FXML;

import java.io.IOException;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        Visualiser.setRoot("../View/primary");
    }
}
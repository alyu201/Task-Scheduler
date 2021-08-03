package Controller;

import Model.Visualiser;
import javafx.fxml.FXML;

import java.io.IOException;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        Visualiser.setRoot("../View/secondary");
    }
}

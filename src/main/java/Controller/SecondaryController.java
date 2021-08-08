package Controller;

import Model.Visualiser;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * This class handles all the elements in the secondary scene.
 * @author Amy Lyu
 *
 */

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        Visualiser.setRoot("../View/primary");
    }
}
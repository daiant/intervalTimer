/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaltimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author daiant
 */
public class IntervalTimer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLIntervalController.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        FXMLIntervalControllerController controller = loader.getController();
        stage.setOnHidden(event -> controller.exitApplication());
        scene.getStylesheets().add(IntervalTimer.class.getResource("bootstrap3.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

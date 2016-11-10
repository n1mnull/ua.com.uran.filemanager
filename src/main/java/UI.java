import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UI extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  public void start(Stage stage) {

    Scene scene = new Scene(new Group(), 600, 600);
    Group root = (Group) scene.getRoot();
    try {
      root.getChildren().add(new UranFileManager().configurePane());
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    stage.setTitle("UranFileManager");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
    stage.sizeToScene();
  }
}
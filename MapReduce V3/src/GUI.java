import Views.HomeScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        HomeScreen homeScreen = new HomeScreen();

        Scene s = homeScreen.getScreen();
        primaryStage.setScene(s);
        primaryStage.setTitle("MapReduce System");
        primaryStage.show();

    }

}


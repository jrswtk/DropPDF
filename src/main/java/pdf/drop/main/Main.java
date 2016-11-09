package pdf.drop.main;

import java.io.File;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pdf.drop.application.AppFiles;
import pdf.drop.application.AppImageLoader;
import pdf.drop.files.FilePath;

public class Main extends Application {
    
    private static Scene scene = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(
                "/fxml/MainView.fxml"));
        primaryStage.setTitle("DropPDF");
        primaryStage.setMinWidth(800.0);
        primaryStage.setMinHeight(500.0);
        scene = new Scene(root,
                primaryStage.getMinWidth(),
                primaryStage.getMinHeight());
        primaryStage.setScene(scene);
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                File dFile = new File(FilePath.TEMP_DIRECTORY);
                if(dFile.exists()) {
                    for(File file : dFile.listFiles()) {
                        file.delete();
                    }
                    dFile.delete();
                }
            }
        });
        
        primaryStage.getIcons().add(new AppImageLoader().getImage("icon"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Scene getScene() {
        return scene;
    }    
    
}

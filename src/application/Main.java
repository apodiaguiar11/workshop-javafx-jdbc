package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		//stage significa palco
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			Parent parent = loader.load();
			
			//Criando a cena
			Scene mainScene = new Scene(parent);
			
			//Setando a cena mainScene como a cena principal
			primaryStage.setScene(mainScene);
			
			//Definindo um título para o palco (stage)
			primaryStage.setTitle("Sample JavaFX application");
			
			//Mostrar o palco
			primaryStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

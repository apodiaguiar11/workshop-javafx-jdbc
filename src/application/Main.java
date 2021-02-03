package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		//stage significa palco
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			
			//Comando para deixar o ScrollPane ajustado a janela			
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			//Criando a cena
			mainScene = new Scene(scrollPane);
			
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

	public static Scene getMainScene() {
		return mainScene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

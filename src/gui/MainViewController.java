package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable {

	// Atributos dos itens do Menu
	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDeparment;

	@FXML
	private MenuItem menuItemAbout;

	// Métodos para tratar os eventos do Menu
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}

	@FXML
	public void onMenuItemDepartmentAction() {

		// Chamando o método que vai abrir a outra tela quando esse comando for acionado
		// Usando expressão lâmbida
		// Essa é uma solução para não ter que criar duas versões do método loadView
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});

	}

	@FXML
	public void onMenuItemAboutAction() {

		// Chamando o método que vai abrir a outra tela quando esse comando for acionado
		loadView("/gui/About.fxml", x -> {});

	}

	// Método da interface Initializable
	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	// A palavra synchronized garante que todo esse processamento não será
	// interrompido durante
	// multi-thread
	// Usando o Consumer, vamos evitar de ter que fazer outro método loadView para o caso
	// de ser um Department.
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {

		try {

			// Instanciando o FXMLLoader
			// Carregando a View
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();

			// Pegando a Cena princial, que seria um ScrollPane
			Scene mainScene = Main.getMainScene();

			// Pegando a referência do VBox principal
			// Dar para conseguir enxegar esse comando, visualizando o MainView
			// Pois, ((ScrollPane) mainScene.getRoot()) retorna o primeiro elemento da View
			// Que nesse caso, seria o ScrollPane
			// Depois, na hierarquia, usando o comando getContent(), retorna o Content
			// E por último faz um casting para transformar em VBox que é o próximo da
			// hierarquia
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			// Pegando o primeiro filho da VBox principal, que seria o Menu (MenuBar)
			// Olhando pelo arquivo MainView, podemos perceber essa hierarquia
			// Guardando esse filho para ser adicionado depois
			Node mainMenu = mainVBox.getChildren().get(0);

			// Limpar todos os filhos (Children) do mainVBox (que seria a cópia do VBox
			// principal)
			mainVBox.getChildren().clear();

			// Adicionando os filhos um por um no mainVBox
			mainVBox.getChildren().add(mainMenu);

			// Adicionando uma coleção, neste caso, a coleção seria os filhos do newVBox
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			//Comando para ativar a função que é passada como parâmetro
			T controller = loader.getController();
			initializingAction.accept(controller);
			

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}

	}

	

}

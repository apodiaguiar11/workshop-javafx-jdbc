package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	
	private DepartmentService service;
	
	//Atributos das colunas da tabela; atributo do button
	
	//Criando a tabela primeiro
	@FXML
	private TableView<Department> tableViewDepartment;
	
	//Seria o atributo para coluna do Id, por isso, usamos como segundo argumento
	//o tipo Integer.
	//O primeiro argumento seria o tipo da tabela
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	//Seria o atributo para coluna do Name
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	//Atributo para o button	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm(obj,"/gui/DepartmentForm.fxml", parentStage);
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		//Para iniciar algum componente na tela
		initializeNodes();
		
	}



	private void initializeNodes() {
		
		//Comando para iniciar apropriadamente o comportamento das colunas da tabela
		//Padrão do JavaFx para iniciar o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		//Comando para a tabela acompanhar a altura da janela	
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		
		
	}
	
	//Método responsável por acessar o serviço, carregar os departamentos,
	//e jogar tudo dentro da obsList
	public void updateTableView() {
		
		//Usado para o caso do programador esquecer de injetar a dependência (método setDepartmentService)
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		//Colocando a lista carregada no serviço dentro de list
		List<Department> list = service.findAll();
		
		//Instancia o obsList pegando os valores de list
		obsList = FXCollections.observableArrayList(list);
		
		//Carregar os itens na TableView e mostrar na tela
		tableViewDepartment.setItems(obsList);
	}
	
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		
		//Lógica para abrir a janelinha de formulário
		try {
			
			//Carregando a tela
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			//Pegando o controlador da tela que acabei de carregar 			
			DepartmentFormController controller = loader.getController();
			
			//Quando pressiono o button new, passo um obj vazio
			//por isso, não mostra nada no TextField
			//Até por é óbvio, pois estou criando algo
			//Injetando um Department
			controller.setDepartment(obj);
			
			//Injetando um DepartmentService
			controller.setDepartmentService(new DepartmentService());
			
			//Inscrever para escutar o evento do onDataChange
			//Aqui inicia o padrão Observer (Primeiro passo)
			//Está mandando um objeto do tipo dele mesmo
			controller.subscribeDataChangeListener(this);
			
			controller.updateFormData();
						
			
			//Quando quero mostrar uma janelinha na frente de um Stage (um palco)
			//É preciso criar outro palco, pois será um palco em cima de outro			
			Stage dialogStage = new Stage();
			
			//Título da janelinha
			dialogStage.setTitle(" Enter Department Data ");
			
			//Determinando quem será a cena
			dialogStage.setScene(new Scene(pane));
			
			//Fazer com que a janelinha não seja redimensionada
			dialogStage.setResizable(false);
			
			//Determinar quem é o Stage pai dessa janelinha
			dialogStage.initOwner(parentStage);
			
			//Determina se a janela vai ser modal ou vai ter outro comportamento
			//Assim, a janelinha vai ficar travada para que não possa acessar outra coisa
			//enquanto essa janelinha estiver aberta
			dialogStage.initModality(Modality.WINDOW_MODAL);
			
			dialogStage.showAndWait();
			
		}
		catch(IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		//Quinto passo do padrão Observer
		//Antes, da janelinha fechar, o método notifyDataChangeListeners(), vai chamar
		//esse método, que vai ter a responsabilidade de atualizar a tabela com o novo valor inserido
		//Só depois de executar esse método que a janelinha será fechada (Pelo outro método que está dentro
		//do método onBtSaveAction da classe DepartmentFormController)
		updateTableView();		
	}
	
	
}

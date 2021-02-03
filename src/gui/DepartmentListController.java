package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

	
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
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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
	
}

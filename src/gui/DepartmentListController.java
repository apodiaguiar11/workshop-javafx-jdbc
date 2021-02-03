package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable {

	
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
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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
	
	
}

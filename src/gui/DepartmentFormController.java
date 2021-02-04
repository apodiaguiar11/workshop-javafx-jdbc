package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	
	private Department entity;
	
	private DepartmentService service;
	
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		
		//Os dois ifs seria para o caso do programador esquecer de 
		//injetar a depend�ncia
		if(entity == null) {
			throw new IllegalStateException("Entity was null");			
		}
		
		if(service == null) {
			throw new IllegalStateException("Service was null");			
		}
		try {
			//Instanciando o objeto
			entity = getFormData();
			
			//Salvando no Banco de Dados
			service.saveOrUpdate(entity);
			
			//Para fechar a janela, depois de salvar no Banco de dados
			Utils.currentStage(event).close();
			
		}
		catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
		
		
		
	}
	
	//M�todo para pegar as informa��es que est�o no TextField e
	//instanciar um Department. 
	private Department getFormData() {		
		Department obj = new Department();
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		
		//Fechar a janela
		Utils.currentStage(event).close();
		
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		initializeNodes();
		
	}

	private void initializeNodes() {
		//Usando regras para a digita��o na janelinha de inser��o de Departamento
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
}

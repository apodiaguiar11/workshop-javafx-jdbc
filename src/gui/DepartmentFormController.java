package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
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
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	
	private Department entity;
	
	private DepartmentService service;
	
	//Como se fosse uma lista de observadores
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
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
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		//Adicionando o observador do evento dentro da lista 
		//Segundo passo do padr�o Observer
		dataChangeListeners.add(listener);
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
			
			//M�todo para executar o m�todo da interface DataChangeListener
			//Terceiro passo do padr�o Observer
			notifyDataChangeListeners();
			
			//Para fechar a janela, depois de salvar no Banco de dados
			Utils.currentStage(event).close();
			
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
		
		
		
	}
	
	private void notifyDataChangeListeners() {
		
		//Quarto passo do padr�o Observer
		//Vai notificar ao observador (a classe DepartmentListController) que est� dentro da lista, 
		//que houve uma altera��o, pois no momento que esse m�todo � chamado,
		//o conte�do digitado pelo usu�rio j� foi salvo no Banco de dados
		for(DataChangeListener listener: dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}

	//M�todo para pegar as informa��es que est�o no TextField e
	//instanciar um Department. 
	private Department getFormData() {		
		
		Department obj = new Department();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		//Verificando se a String est� vazia
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			//Exce��o manda uma mensagem dizendo que este campo n�o pode ser vazio
			exception.addError("name", "Field can't be empty");
		}
		
		//Mesmo vazio, mas ainda vou setar
		obj.setName(txtName.getText());
		
		//Testando se na cole��o de erros tem pelo menos um, caso tenha,
		//Vai lan�ar a exce��o
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
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
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> field = errors.keySet();
		
		if(field.contains("name")) {
			labelErrorName.setText(errors.get("name"));;
		}
		
	}
	
}

package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	
	private Seller entity;
	
	private SellerService service;
	
	//Como se fosse uma lista de observadores
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;

	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		//Adicionando o observador do evento dentro da lista 
		//Segundo passo do padrão Observer
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		
		//Os dois ifs seria para o caso do programador esquecer de 
		//injetar a dependência
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
			
			//Método para executar o método da interface DataChangeListener
			//Terceiro passo do padrão Observer
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
		
		//Quarto passo do padrão Observer
		//Vai notificar ao observador (a classe SellerListController) que está dentro da lista, 
		//que houve uma alteração, pois no momento que esse método é chamado,
		//o conteúdo digitado pelo usuário já foi salvo no Banco de dados
		for(DataChangeListener listener: dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}

	//Método para pegar as informações que estão no TextField e
	//instanciar um Seller. 
	private Seller getFormData() {		
		
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		//Verificando se a String está vazia
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			//Exceção manda uma mensagem dizendo que este campo não pode ser vazio
			exception.addError("name", "Field can't be empty");
		}
		
		//Mesmo vazio, mas ainda vou setar
		obj.setName(txtName.getText());
		
		//Testando se na coleção de erros tem pelo menos um, caso tenha,
		//Vai lançar a exceção
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
		//Usando regras para a digitação na janelinha de inserção de Departamento
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 55);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
	}
	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		
		//Seria para mostrar a data no formato local (do formato do computador que está usando)
		//ZoneId.systemDefault(), seria a zona, ou seja, neste caso estamos usando o fuso horário da máquina local do usuário
		if(entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> field = errors.keySet();
		
		if(field.contains("name")) {
			labelErrorName.setText(errors.get("name"));;
		}
		
	}
	
}

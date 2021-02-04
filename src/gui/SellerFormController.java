package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;

	// Como se fosse uma lista de observadores
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
	private ComboBox<Department> comboBoxDepartment;

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

	private ObservableList<Department> obsList;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;

	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		// Adicionando o observador do evento dentro da lista
		// Segundo passo do padr�o Observer
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {

		// Os dois ifs seria para o caso do programador esquecer de
		// injetar a depend�ncia
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}

		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			// Instanciando o objeto
			entity = getFormData();

			// Salvando no Banco de Dados
			service.saveOrUpdate(entity);

			// M�todo para executar o m�todo da interface DataChangeListener
			// Terceiro passo do padr�o Observer
			notifyDataChangeListeners();

			// Para fechar a janela, depois de salvar no Banco de dados
			Utils.currentStage(event).close();

		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifyDataChangeListeners() {

		// Quarto passo do padr�o Observer
		// Vai notificar ao observador (a classe SellerListController) que est� dentro
		// da lista,
		// que houve uma altera��o, pois no momento que esse m�todo � chamado,
		// o conte�do digitado pelo usu�rio j� foi salvo no Banco de dados
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	// M�todo para pegar as informa��es que est�o no TextField e
	// instanciar um Seller.
	private Seller getFormData() {

		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		// Verificando se a String est� vazia
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			// Exce��o manda uma mensagem dizendo que este campo n�o pode ser vazio
			exception.addError("name", "Field can't be empty");
		}

		// Mesmo vazio, mas ainda vou setar
		obj.setName(txtName.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {			
			exception.addError("email", "Field can't be empty");
		}
		
		obj.setEmail(txtEmail.getText());
		
		if(dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		}else {
			//Assim que pego um valor que est� no Date Picker
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			
			obj.setBirthDate(Date.from(instant));
		}
		
		
		
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {			
			exception.addError("baseSalary", "Field can't be empty");
		}
		
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
				
		obj.setDepartment(comboBoxDepartment.getValue());		
		
		// Testando se na cole��o de erros tem pelo menos um, caso tenha,
		// Vai lan�ar a exce��o
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {

		// Fechar a janela
		Utils.currentStage(event).close();

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		initializeNodes();

	}

	private void initializeNodes() {
		// Usando regras para a digita��o na janelinha de inser��o de Departamento
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 55);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();

	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		// Seria para mostrar a data no formato local (do formato do computador que est�
		// usando)
		// ZoneId.systemDefault(), seria a zona, ou seja, neste caso estamos usando o
		// fuso hor�rio da m�quina local do usu�rio
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if(entity.getDepartment() == null) {
			//Para selecionar o primeiro Departamento
			comboBoxDepartment.getSelectionModel().selectFirst();
		}else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> field = errors.keySet();
		
		labelErrorName.setText(field.contains("name") ? errors.get("name") : "");
		labelErrorEmail.setText(field.contains("email") ? errors.get("email") : "");
		labelErrorBaseSalary.setText(field.contains("baseSalary") ? errors.get("baseSalary") : "");
		labelErrorBirthDate.setText(field.contains("birthDate") ? errors.get("birthDate") : "");		
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
	
}

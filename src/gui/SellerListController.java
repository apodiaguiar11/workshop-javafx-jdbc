package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;

	// Atributos das colunas da tabela; atributo do button

	// Criando a tabela primeiro
	@FXML
	private TableView<Seller> tableViewSeller;

	// Seria o atributo para coluna do Id, por isso, usamos como segundo argumento
	// o tipo Integer.
	// O primeiro argumento seria o tipo da tabela
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	// Seria o atributo para coluna do Name
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	//Seria o atributo para coluna do Email
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	//Seria o atributo para coluna da Data de anivers�rio
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	//Seria o atributo para coluna do S�lario base
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	//Atributo para coluna EDIT
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	//Atributo para coluna REMOVE
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	
	
	// Atributo para o button
	@FXML
	private Button btNew;

	private ObservableList<Seller> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		// Para iniciar algum componente na tela
		initializeNodes();

	}

	private void initializeNodes() {

		// Comando para iniciar apropriadamente o comportamento das colunas da tabela
		// Padr�o do JavaFx para iniciar o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		// Comando para a tabela acompanhar a altura da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());

	}

	// M�todo respons�vel por acessar o servi�o, carregar os departamentos,
	// e jogar tudo dentro da obsList
	public void updateTableView() {

		// Usado para o caso do programador esquecer de injetar a depend�ncia (m�todo
		// setSellerService)
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		// Colocando a lista carregada no servi�o dentro de list
		List<Seller> list = service.findAll();

		// Instancia o obsList pegando os valores de list
		obsList = FXCollections.observableArrayList(list);

		// Carregar os itens na TableView e mostrar na tela
		tableViewSeller.setItems(obsList);

		// Acrescentar o button EDIT em cada linha da tabela
		// E toda vez que clicar nesse bot�o, vai abrir o formul�rio de edi��o
		initEditButtons();
		
		//M�todo para mostrar um button de delete
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {

		// L�gica para abrir a janelinha de formul�rio
		try {

			// Carregando a tela
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// Pegando o controlador da tela que acabei de carregar
			SellerFormController controller = loader.getController();

			// Quando pressiono o button new, passo um obj vazio
			// por isso, n�o mostra nada no TextField
			// At� por � �bvio, pois estou criando algo
			// Injetando um Seller
			controller.setSeller(obj);

			// Injetando um SellerService
			controller.setServices(new SellerService(), new DepartmentService());
			
			//Carregando os departamentos
			controller.loadAssociatedObjects();
			
			// Inscrever para escutar o evento do onDataChange
			// Aqui inicia o padr�o Observer (Primeiro passo)
			// Est� mandando um objeto do tipo dele mesmo
			controller.subscribeDataChangeListener(this);

			controller.updateFormData();

			// Quando quero mostrar uma janelinha na frente de um Stage (um palco)
			// � preciso criar outro palco, pois ser� um palco em cima de outro
			Stage dialogStage = new Stage();

			// T�tulo da janelinha
			dialogStage.setTitle(" Enter Seller Data ");

			// Determinando quem ser� a cena
			dialogStage.setScene(new Scene(pane));

			// Fazer com que a janelinha n�o seja redimensionada
			dialogStage.setResizable(false);

			// Determinar quem � o Stage pai dessa janelinha
			dialogStage.initOwner(parentStage);

			// Determina se a janela vai ser modal ou vai ter outro comportamento
			// Assim, a janelinha vai ficar travada para que n�o possa acessar outra coisa
			// enquanto essa janelinha estiver aberta
			dialogStage.initModality(Modality.WINDOW_MODAL);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		// Quinto passo do padr�o Observer
		// Antes, da janelinha fechar, o m�todo notifyDataChangeListeners(), vai chamar
		// esse m�todo, que vai ter a responsabilidade de atualizar a tabela com o novo
		// valor inserido
		// S� depois de executar esse m�todo que a janelinha ser� fechada (Pelo outro
		// m�todo que est� dentro
		// do m�todo onBtSaveAction da classe SellerFormController)
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
			
		});
	}
	
	//M�todo para remover um departamento
	private void removeEntity(Seller obj) {		
		
		//Est� pegando a resposta do Alert para saber se aceita ou n�o deletar
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				//Excluindo o Departament
				service.remove(obj);
				
				//Atualizando a tabela depois da exclus�o
				updateTableView();
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
}

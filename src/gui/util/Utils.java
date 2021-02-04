package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

	//Se caso clico em um botão, o mesmo vai pegar o Stage daquele botão
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}
	
	//Método para converter o conteúdo (String) em Inteiro.
	//Mas, como sabemos, o conteúdo pode não ser um inteiro válido.
	//E caso, não seja um inteiro válido, esse método irá retornar um 
	//valor null
	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		}
		catch(NumberFormatException e) {
			return null;
		}		
	}
	
	
}

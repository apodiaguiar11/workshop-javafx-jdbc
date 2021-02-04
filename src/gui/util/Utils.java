package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

	//Se caso clico em um bot�o, o mesmo vai pegar o Stage daquele bot�o
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}
	
	//M�todo para converter o conte�do (String) em Inteiro.
	//Mas, como sabemos, o conte�do pode n�o ser um inteiro v�lido.
	//E caso, n�o seja um inteiro v�lido, esse m�todo ir� retornar um 
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

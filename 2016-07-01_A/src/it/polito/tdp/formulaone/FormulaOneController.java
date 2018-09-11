package it.polito.tdp.formulaone;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import it.polito.tdp.formulaone.model.Season;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FormulaOneController {
	
	Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Season> boxAnno;

    @FXML
    private TextField textInputK;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	try {
    		
    		Season s = boxAnno.getValue();
    		if(s == null) {
    			txtResult.setText("Selezionare una stagione!");
    			return;
    		}
    		
    	model.creaGrafo(s);	
    		Driver d = model.getBestDriver();
    		txtResult.setText(d.toString());
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    		txtResult.setText("Errore di connessione al DB");
    	}
    }

    @FXML
    void doTrovaDreamTeam(ActionEvent event) {
    	try {
    		try {
    			int k = Integer.parseInt(textInputK.getText());
    			if(k <= 0) {
    				txtResult.setText("inserire k > 0");
    			return;
    		}
    			List<Driver> drivers = model.getDreamTeam(k);
    			txtResult.setText(drivers.toString());
    			
    		} catch (NumberFormatException e){
    			txtResult.setText("inserire K > 0");
    		return;
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		txtResult.setText("Errore di connessione al DB");
    	}
    		
    		
    		
    }

    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }
    
    public void setModel(Model model){
    	this.model = model;
    	boxAnno.getItems().addAll(model.getAllSeasons());
    }
}

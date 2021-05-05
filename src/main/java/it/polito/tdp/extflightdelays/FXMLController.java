package it.polito.tdp.extflightdelays;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController 
{
	private Model model;

    @FXML 
    private ResourceBundle resources;

    @FXML 
    private URL location;

    @FXML 
    private TextArea txtResult; 

    @FXML 
    private TextField compagnieMinimo; 

    @FXML 
    private ComboBox<Airport> cmbBoxAeroportoPartenza; 

    @FXML 
    private ComboBox<Airport> cmbBoxAeroportoDestinazione; 

    @FXML 
    private Button btnAnalizza; 

    @FXML 
    private Button btnConnessione; 

    @FXML
    void doAnalizzaAeroporti(ActionEvent event) 
    {
    	int x;
    	
    	try
		{
			String input = this.compagnieMinimo.getText();
			x = Integer.parseInt(input);
		}
		catch(NumberFormatException nfe)
		{
			this.txtResult.setText("Inserisci un numero intero!");
			return;
		}
    	
    	this.model.creaGrafo(x);
    	
    	Set<Airport> allVertices = this.model.getVertici();
    	
    	this.cmbBoxAeroportoPartenza.getItems().addAll(allVertices);
    	this.cmbBoxAeroportoDestinazione.getItems().addAll(allVertices);
    	
    	this.txtResult.setText("Grafo creato!");
    	this.txtResult.appendText("\n# Vertici: " + this.model.getVertici().size());
    	this.txtResult.appendText("\n# Archi: " + this.model.getArchi().size());
    	
    	for(var v : this.model.getArchi())
    		this.txtResult.appendText("\n" + v);
    }

    @FXML
    void doTestConnessione(ActionEvent event) 
    {
    	Airport origine = this.cmbBoxAeroportoPartenza.getValue();
    	Airport destinazione = this.cmbBoxAeroportoDestinazione.getValue();

    	if(origine == null || destinazione == null)
    	{
    		this.txtResult.setText("Selezionare sia Aeroporto Partenza che Aeroporto Destinazione");
    		return;
    	}
    	
    	List<Airport> percorso = this.model.trovaPercorso(origine, destinazione);
    	
    	if(percorso == null)
    	{
    		this.txtResult.setText("Non esiste alcun percorso dall'aeroporto \"" +
    								origine + "\" all'aeroporto \"" + destinazione  +"\"");
    		return;
    	}
    	
    	this.txtResult.setText("Esiste un percorso dall'aeroporto \"" +
    								origine + "\" all'aeroporto \"" + destinazione  +"\":\n\n");
    	
    	int count = 1;
    	for(Airport a : percorso)
    	{
    		this.txtResult.appendText((count++) + ") " + a.toString() + "\n");
    	}
    }

    @FXML 
    void initialize() 
    {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert compagnieMinimo != null : "fx:id=\"compagnieMinimo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbBoxAeroportoDestinazione != null : "fx:id=\"cmbBoxAeroportoDestinazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnConnessione != null : "fx:id=\"btnConnessione\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) 
    {
    	this.model = model;
    }
}

package it.polito.tdp.extflightdelays;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;

public class FXMLController 
{
    @FXML 
    private ResourceBundle resources;

    @FXML 
    private URL location;

    @FXML 
    private TextArea txtResult; 

    @FXML 
    private TextField compagnieMinimo; 
    private final int MAX_CHARS_COMPAGNIE = 2;

    @FXML 
    private ComboBox<Airport> cmbBoxAeroportoPartenza; 

    @FXML 
    private ComboBox<Airport> cmbBoxAeroportoDestinazione; 

    @FXML 
    private Button btnAnalizza; 

    @FXML 
    private Button btnConnessione; 
    
	private Model model;


    @FXML
    void doAnalizzaAeroporti(ActionEvent event) 
    {
		String input = this.compagnieMinimo.getText();
    	int minAirlines;
    	
    	try
		{
			minAirlines = Integer.parseInt(input);
		}
		catch(NumberFormatException nfe)
		{
			this.txtResult.setText("Errore input: inserisci un numero intero!");
			return;
		}
    	
    	this.model.createGraph(minAirlines);
    	
    	Collection<Airport> allVertices = this.model.getVertices();
    	
    	this.cmbBoxAeroportoPartenza.setValue(null);
    	this.cmbBoxAeroportoPartenza.getItems().clear();
    	this.cmbBoxAeroportoPartenza.getItems().addAll(allVertices);
    	this.cmbBoxAeroportoPartenza.setDisable(false);

    	
    	this.cmbBoxAeroportoDestinazione.setValue(null);
    	this.cmbBoxAeroportoDestinazione.getItems().clear();
    	this.cmbBoxAeroportoDestinazione.getItems().addAll(allVertices);
    	this.cmbBoxAeroportoDestinazione.setDisable(false);

    	int numVertices = allVertices.size();
    	Collection<DefaultWeightedEdge> allEdges = this.model.getEdges();
    	int numEdges = allEdges.size();
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("Grafo creato!\n");
    	sb.append("#Vertici: " + numVertices).append("\n");
    	sb.append("#Archi: " + numEdges).append("\n\n");
    	
    	for(var v : allEdges)
    		sb.append(v).append("\n");
    	
    	this.txtResult.setText(sb.toString());
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
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("Esiste un percorso dall'aeroporto \"").append(origine);
    	sb.append("\" all'aeroporto \"").append(destinazione).append("\":\n\n");
    	
    	int count = 1;
    	for(Airport a : percorso)
    	{
    		sb.append(count++).append(") ").append(a.toString()).append("\n");
    	}
    	
    	this.txtResult.setText(sb.toString());
    }
    
    @FXML
    void handleOriginAirportSelection(ActionEvent event) 
    {
    	this.checkAirports();
    }
    
    @FXML
    void handleDestinationAirportSelection(ActionEvent event) 
    {
    	this.checkAirports();
    }
    
    private void checkAirports()
    {
    	if( this.cmbBoxAeroportoPartenza.getValue() != null &&
        	this.cmbBoxAeroportoDestinazione.getValue() != null)
        {
        	this.btnConnessione.setDisable(false);
        }
        else
        {
        	this.btnConnessione.setDisable(true);
        }
    }

    @FXML
    void handleMinAirlinesTyping(KeyEvent event) 
    {
    	if(this.compagnieMinimo.getText().isBlank())
    	{
    		this.btnAnalizza.setDisable(true);
    	}
    	else
    	{
    		this.btnAnalizza.setDisable(false);
    	}
    }

    @FXML 
    void initialize() 
    {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene_FlightDelays.fxml'.";
        assert compagnieMinimo != null : "fx:id=\"compagnieMinimo\" was not injected: check your FXML file 'Scene_FlightDelays.fxml'.";
        assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'Scene_FlightDelays.fxml'.";
        assert cmbBoxAeroportoDestinazione != null : "fx:id=\"cmbBoxAeroportoDestinazione\" was not injected: check your FXML file 'Scene_FlightDelays.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'Scene_FlightDelays.fxml'.";
        assert btnConnessione != null : "fx:id=\"btnConnessione\" was not injected: check your FXML file 'Scene_FlightDelays.fxml'.";
    
        this.compagnieMinimo.setTextFormatter(new TextFormatter<>(change -> 
        {        	
        	String text = change.getText();
        	if(text == null || text.isEmpty())
        		return change;
        	
        	int alreadyPresentChars = this.compagnieMinimo.getText().length();
        	int maxNumCharsLeft = MAX_CHARS_COMPAGNIE - alreadyPresentChars;
        	
        	if(text.length() > maxNumCharsLeft)
        		text = text.substring(0, maxNumCharsLeft);
        	
        	if(!text.matches("[\\d]+"))
        		text = text.replaceAll("\\D", "");
        	
        	if(alreadyPresentChars == 0 && text.matches("(0)+(.)*"))
        		text = text.replaceFirst("(0)+", "");
        	
        	change.setText(text);
        	        	
        	return change;
        }));
    }
    
    public void setModel(Model model) 
    {
    	this.model = model;
    }
}

package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model 
{
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> airportsIdMap;
	
	
	public Model()
	{
		this.dao = new ExtFlightDelaysDAO();
		this.airportsIdMap = new HashMap<>();
		this.dao.loadAllAirports(this.airportsIdMap);
	}
	
	public void createGraph(int minAirlines)
	{
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo solo i vertici filtrati
		Set<Airport> vertices = this.dao.getVertici(airportsIdMap, minAirlines);
		Graphs.addAllVertices(this.grafo, vertices);
		
		//aggiungo gli archi
		Set<Rotta> rotte = this.dao.getRotte(airportsIdMap);
		
		for(Rotta r : rotte)
		{
			Airport a1 = r.getA1();
			Airport a2 = r.getA2();
			int numRotte = r.getN();
			
			if(!this.grafo.containsVertex(a1) || !this.grafo.containsVertex(a2))
				continue;
			
			if(this.grafo.containsEdge(a1, a2))
			{
				DefaultWeightedEdge edge = this.grafo.getEdge(a1, a2);
				
				double oldNumRotte = this.grafo.getEdgeWeight(edge);
				double newNumRotte = oldNumRotte + (double)numRotte;
				
				this.grafo.setEdgeWeight(edge, newNumRotte);
			}
			else
				Graphs.addEdgeWithVertices(this.grafo, a1, a2, numRotte);
		}
	}

	public Set<Airport> getVertices()
	{
		return this.grafo.vertexSet();
	}
	
	public Set<DefaultWeightedEdge> getEdges()
	{
		return this.grafo.edgeSet();
	}
	
	public List<Airport> trovaPercorso(Airport origin, Airport destination)
	{
		LinkedList<Airport> percorso = new LinkedList<>();
		
		BreadthFirstIterator<Airport, DefaultWeightedEdge> iterator = 
												new BreadthFirstIterator<>(this.grafo, origin); 
		
		Map<Airport, Airport> visita = new HashMap<>();
		visita.put(origin, null);
		
		iterator.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>()
		{
			
			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> e) { }
			
			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> e) { }
			
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e)
			{
				DefaultWeightedEdge edgeTraversed = e.getEdge();
				
				Airport a1 = grafo.getEdgeSource(edgeTraversed);
				Airport a2 = grafo.getEdgeTarget(edgeTraversed);
				
				if(visita.containsKey(a1) && !visita.containsKey(a2))
				{
					visita.put(a2, a1);
				}
				else if(visita.containsKey(a2) && !visita.containsKey(a1))
				{
					visita.put(a1, a2);
				}
			}
			
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) { }
			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) { }
		});
		
		while(iterator.hasNext())
			iterator.next();
		
		Airport a = destination;
		
		while(a != null)
		{
			percorso.addFirst(a);
			a = visita.get(a);
		}
		
		if(!percorso.getFirst().equals(origin))
			return null; //non esiste un percorso da 'origin' a 'destination'
			
		return percorso;
	}

}

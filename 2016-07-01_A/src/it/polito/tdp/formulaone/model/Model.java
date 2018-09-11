package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {

	private FormulaOneDAO fonedao;
	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> grafo;
	private DriverIdMap driverIdmap;
	
	private List<Driver> bestDreamTeam;
	private int bestDreamTeamValue;
	
	public Model() {
		fonedao = new FormulaOneDAO();
		driverIdmap = new DriverIdMap();
		
	}
	
	public List<Season> getAllSeasons() {
		return fonedao.getAllSeasons();
	}

	public void creaGrafo(Season s) {
		grafo = new SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		List<Driver> drivers = fonedao.getAllDriversBySeason(s, driverIdmap);
		Graphs.addAllVertices(grafo, drivers);
		for(DriverSeasonResult dsr : fonedao.getDriverSeasonResult(s, driverIdmap)) {
			Graphs.addEdge(grafo, dsr.getD1(), dsr.getD2(), dsr.getCounter());
		}
		
		System.out.format("Grafo creato %d archi, %d nodi", grafo.edgeSet().size(), grafo.vertexSet().size());
	}
	
	public Driver getBestDriver() {
		if(grafo == null)
			new RuntimeException("Creare il grafo!");
		
		//Inizializzazione
		Driver bestDriver = null;
		int best = Integer.MIN_VALUE;
		
		for(Driver d : grafo.vertexSet()) {
			int sum = 0; 
			
			//Itero sugli archi uscenti 
			for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(d)) {
				sum += grafo.getEdgeWeight(e);
			}
			
			//Itero sugli archi entranti 
			for(DefaultWeightedEdge e : grafo.incomingEdgesOf(d)) {
				sum -= grafo.getEdgeWeight(e);
			}
			
			if(sum > best || bestDriver == null) {
				bestDriver = d;
				best = sum; 
			}
		}
		
		if(bestDriver== null) {
			new RuntimeException("BestDriver not found");
		}
		return bestDriver;
	}

	public List<Driver> getDreamTeam(int k){
		bestDreamTeam = new ArrayList<>();
		bestDreamTeamValue = Integer.MAX_VALUE;
		recursive(0, new ArrayList<Driver>(), k);
		return bestDreamTeam;
		
	}
	
private int evaluate(ArrayList<Driver> tempDreamTeam) {
		int sum = 0;
		Set<Driver> tempDreamTeamSet = new HashSet<>(tempDreamTeam);
		for (DefaultWeightedEdge e : grafo.edgeSet()) {
			if(tempDreamTeamSet.contains(grafo.getEdgeTarget(e))) {
				sum += grafo.getEdgeWeight(e);
			}
		}
		return sum;
	}

	private void recursive(int step, ArrayList<Driver> tempDreamTeam, int k) { //k è la dimensione del dream team
		
		// condizione di terminazione 
		if(step >= k) {
			if(evaluate(tempDreamTeam) < bestDreamTeamValue) {
				bestDreamTeamValue = evaluate(tempDreamTeam);
				bestDreamTeam = new ArrayList<>(tempDreamTeam);
				return;
			}
		}
		
		// generazione soluzione parziale
		for(Driver d : grafo.vertexSet()) {
			if(!tempDreamTeam.contains(d)) {
				tempDreamTeam.add(d);
				recursive(step+1, tempDreamTeam, k);
				tempDreamTeam.remove(d);
			}
		}
	}

	

}

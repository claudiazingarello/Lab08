package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	ExtFlightDelaysDAO dao;
	SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	List<Airport> listAereoporti = new ArrayList<>();
	Map<Integer, Airport> idMap;

	public Model () {
		dao = new ExtFlightDelaysDAO();
		idMap = new HashMap<>();
	}

	public void creaGrafo(int distanza) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		dao.loadAllAirports(idMap);

		//Aggiungo i vertici
		Graphs.addAllVertices(grafo, idMap.values());

		for(Rotta r :dao.getRotte(idMap, distanza)) {
			//controllo se esiste già un arco
			//Se sì, aggiorno il peso
			DefaultWeightedEdge edge = grafo.getEdge(r.getA1(), r.getA2());
			if(edge == null) {
				grafo.addEdge(r.getA1(), r.getA2());
			} else {
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = (peso + r.getPeso())/2;
				grafo.setEdgeWeight(edge, newPeso);
			}
		}
	}

	public int getVertici() {
		return this.grafo.vertexSet().size();
	}

	public int getArchi() {
		return this.grafo.edgeSet().size();
	}

	public List<Rotta> getRotte() {
		List<Rotta> listaRotte = new ArrayList<>();
			for(DefaultWeightedEdge edge : this.grafo.edgeSet()) {
				listaRotte.add(new Rotta(grafo.getEdgeSource(edge), grafo.getEdgeTarget(edge), grafo.getEdgeWeight(edge)));
			}
		return listaRotte;
	}
}

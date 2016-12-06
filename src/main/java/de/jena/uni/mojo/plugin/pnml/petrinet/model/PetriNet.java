/**
 * Copyright 2016 mojo Friedrich Schiller University Jena
 * 
 * This file is part of mojo.
 * 
 * mojo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * mojo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with mojo. If not, see <http://www.gnu.org/licenses/>.
 */
package de.jena.uni.mojo.plugin.pnml.petrinet.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents a petri net with all its places, transitions, arcs and
 * markings.
 * 
 * @author Dipl.-Inf. Thomas M. Prinz
 * 
 */
public class PetriNet {

	/**
	 * A final list of places.
	 */
	private final ArrayList<Place> places = new ArrayList<Place>();

	/**
	 * A final list of transitions.
	 */
	private final ArrayList<Transition> transitions = new ArrayList<Transition>();

	/**
	 * A final list of arcs.
	 */
	private final ArrayList<Arc> arcs = new ArrayList<Arc>();

	/**
	 * A map that maps each name of an arc to its arc.
	 */
	private final HashMap<String, Arc> arcMap = new HashMap<String, Arc>();

	/**
	 * The initial places of the petri net.
	 */
	private HashSet<Place> initialPlaces = new HashSet<Place>();

	/**
	 * The end nodes of this petri net.
	 */
	private HashSet<Place> endNodes = new HashSet<Place>();

	/**
	 * Is the petri closed or open?
	 */
	private boolean closed = true;

	/**
	 * The id of the petri net (since it is final, it can be public).
	 */
	public final String id;

	/**
	 * The constructor needs the id of this petri net.
	 * 
	 * @param id
	 *            The id of the petri net.
	 */
	public PetriNet(String id) {
		this.id = id;
	}

	/**
	 * Add a place.
	 * 
	 * @param place
	 *            The place to add.
	 */
	public void add(Place place) {
		places.add(place);
	}

	/**
	 * Add a transition.
	 * 
	 * @param transition
	 *            The transition to add.
	 */
	public void add(Transition transition) {
		transitions.add(transition);
	}

	/**
	 * Add a arbitrary node and let this method decide whether it is a place or
	 * transition.
	 * 
	 * @param node
	 *            The petri net node to add.
	 */
	public void add(PNode node) {
		if (node instanceof Place) {
			add((Place) node);
		} else {
			add((Transition) node);
		}
	}

	/**
	 * Remove a petri net node.
	 * 
	 * @param node
	 *            The node to remove.
	 */
	public void remove(PNode node) {
		if (node instanceof Place) {
			places.remove(node);
		} else {
			transitions.remove(node);
		}
	}

	/**
	 * Add an arc.
	 * 
	 * @param arc
	 *            The arc to add.
	 */
	public void add(Arc arc) {
		arcs.add(arc);
		arcMap.put(arc.toString(), arc);
	}

	/**
	 * Remove the arc with the given id.
	 * 
	 * @param id
	 *            The id of the arc to remove.
	 */
	public void removeArc(String id) {
		Arc arc = arcMap.remove(id);

		arcs.remove(arc);
	}

	/**
	 * Get all transitions.
	 * 
	 * @return A list of transitions.
	 */
	public List<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * Get all places.
	 * 
	 * @return A list of places.
	 */
	public List<Place> getPlaces() {
		return places;
	}

	/**
	 * Get all arcs.
	 * 
	 * @return A list of arcs.
	 */
	public List<Arc> getArcs() {
		return arcs;
	}

	/**
	 * Get all nodes (as a copy list).
	 * 
	 * @return A list of all transitions and places.
	 */
	public List<PNode> getNodes() {
		ArrayList<PNode> list = new ArrayList<PNode>(transitions);
		list.addAll(places);

		return list;
	}

	/**
	 * Get the end nodes.
	 * 
	 * @return A set of end nodes.
	 */
	public Set<Place> getEndNodes() {
		if (!endNodes.isEmpty()) {
			endNodes = new HashSet<Place>();
		}
		for (Place p : places) {
			if (p.successors.isEmpty()) {
				endNodes.add(p);
			}
		}
		return endNodes;
	}

	/**
	 * Add an initial place.
	 * 
	 * @param place
	 *            The initial place.
	 */
	public void addInitial(Place place) {
		initialPlaces.add(place);
	}

	/**
	 * Get the initial places.
	 * 
	 * @return The initial places.
	 */
	public HashSet<Place> getInitialPlaces() {
		return initialPlaces;
	}

	/**
	 * Produces a dot representation of the petri net.
	 * 
	 * @return A string containing the dot representation of the petri net.
	 */
	public String toGraph() {
		String tempId = id.replace("-", "");

		String graph = "digraph " + tempId + " {\n";

		// Print all edges
		for (Arc arc : arcs) {
			graph += arc.toGraph();
		}

		// Print all places
		for (Place p : places) {
			graph += p.toGraph();
		}

		// Print all transitions
		for (Transition t : transitions) {
			graph += t.toGraph();
		}

		graph += "}";

		return graph;
	}

	/**
	 * Whether this petri net is closed or not.
	 * 
	 * @return Whether this petri net is open or closed.
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Set closed.
	 * 
	 * @param closed
	 *            Closed.
	 */
	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	@Override
	public String toString() {
		String s = "PetriNet:\n";
		s += "\tPlaces: " + places + "\n";
		s += "\tTransitions: " + transitions + "\n";
		s += "\tArcs: " + arcs + "\n";
		s += "\tInitial: " + initialPlaces + "\n";
		s += "\tEnd: " + getEndNodes() + "\n";

		return s;
	}
}
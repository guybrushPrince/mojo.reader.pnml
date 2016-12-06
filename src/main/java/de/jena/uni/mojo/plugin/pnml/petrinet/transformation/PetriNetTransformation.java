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
package de.jena.uni.mojo.plugin.pnml.petrinet.transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.jena.uni.mojo.Mojo;
import de.jena.uni.mojo.plugin.pnml.parser.pnml.PetriNetContext;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.Arc;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.PNode;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.PetriNet;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.Place;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.Transition;

/**
 * Transforms and repairs a petri net so that we can transform it later into a
 * workflow graph.
 * 
 * @author Dipl.-Inf. Thomas M. Prinz
 * 
 */
public class PetriNetTransformation {

	/**
	 * All path nodes (for combining different exit nodes to a single one).
	 */
	private HashMap<PNode, Set<PNode>> pathNodes = new HashMap<PNode, Set<PNode>>();

	/**
	 * Transform and repair the petri net by:
	 * 
	 * 1. Extend isolated places.
	 * 
	 * 2. Set places without incoming arcs as initial place.
	 * 
	 * 3. Add places after transitions without outgoing arcs.
	 * 
	 * 4. Combine different end places to a single one.
	 * 
	 * 5. Combine different initial places to a single one.
	 * 
	 * 6. Split nodes with more than one incoming AND outcoming arc.
	 * 
	 * @param net
	 *            The petri net to transform.
	 * @param context
	 *            The context of the petri net.
	 * @return The repaired petri net.
	 */
	public PetriNet transform(PetriNet net, PetriNetContext context) {
		repair(net, context);
		if (Mojo.getCommand("SIMPLE_END_PLACE").asBooleanValue()) {
			simpleEndTransform(net, context);
		} else {
			endTransform(net, context);
		}
		transitionAsStartTransform(net, context);
		startTransform(net, context);
		uniqueTypeTransform(net, context);

		net.setClosed(true);

		return net;
	}

	/**
	 * Performs the steps:
	 * 
	 * 1. Extend isolated places.
	 * 
	 * 2. Set places without incoming arcs as initial place.
	 * 
	 * 3. Add places after transitions without outgoing arcs.
	 * 
	 * @param net
	 *            The petri net.
	 * @param context
	 *            The petri net context.
	 * @return The repaired petri net.
	 */
	private PetriNet repair(PetriNet net, PetriNetContext context) {
		if (net.getPlaces().size() == 1) {
			// The net has only one place with an initial marking
			Place p = net.getPlaces().get(0);
			// Create a new end place
			Place e = new Place(context.placeCounter++);
			// And a new transition
			Transition t = new Transition(context.transitionCounter++);

			p.successors.add(t);
			t.predecessors.add(p);
			e.predecessors.add(t);
			t.successors.add(e);

			// Change the model
			net.add(e);
			net.add(t);
			net.add(new Arc(context.arcCounter++, p, t, 1));
			net.add(new Arc(context.arcCounter++, t, e, 1));
		}
		for (Place p : net.getPlaces()) {
			if (p.predecessors.isEmpty()) {
				// Add it as an initial place
				net.addInitial(p);
			}
		}
		for (Transition t : net.getTransitions()) {
			if (t.successors.isEmpty()) {
				// Add a place as final place
				Place p = new Place(context.placeCounter++);

				t.successors.add(p);
				p.predecessors.add(t);

				// Change the model
				net.add(p);
				net.add(new Arc(context.arcCounter++, t, p, 1));
			}
		}

		return net;
	}

	/**
	 * Places a place before each transition without incoming edges.
	 * 
	 * @param net
	 *            The petri net.
	 * @param context
	 *            The petri net context.
	 * @return The repaired petri net.
	 */
	private PetriNet transitionAsStartTransform(PetriNet net,
			PetriNetContext context) {
		// Take a look at every transition
		// if one transition has no predecessor
		// insert a new place
		for (Transition trans : net.getTransitions()) {
			if (trans.predecessors.isEmpty()) {
				// Create a new start place
				Place s = new Place(context.placeCounter++);
				s.setMarking(1);

				s.successors.add(trans);
				trans.predecessors.add(s);

				// Change the model
				net.add(s);
				net.add(new Arc(context.arcCounter++, s, trans, 1));
				net.addInitial(s);
			}
		}
		return net;
	}

	/**
	 * Performs the step:
	 * 
	 * 5. Combine different initial places to a single one.
	 * 
	 * @param net
	 *            The petri net.
	 * @param context
	 *            The petri net context.
	 * @return The repaired petri net.
	 */
	private PetriNet startTransform(PetriNet net, PetriNetContext context) {
		// For all the initial places with predecessors
		// transform them
		HashSet<Place> start = new HashSet<Place>();

		for (Place p : net.getInitialPlaces()) {
			if (p.predecessors.size() > 0 || p.successors.size() > 1) {

				// Create a new start place
				Place s = new Place(context.placeCounter++);
				s.setMarking(1);
				start.add(s);

				// Create a new transition
				Transition trans = new Transition(context.transitionCounter++);
				trans.predecessors.add(s);
				trans.successors.add(p);
				s.successors.add(trans);
				p.predecessors.add(trans);

				// Update the model
				net.add(s);
				net.add(trans);
				net.add(new Arc(context.arcCounter++, s, trans, 1));
				net.add(new Arc(context.arcCounter++, trans, p, 1));

				p.setMarking(0);
			} else {
				start.add(p);
			}
		}

		// Now create one single start place
		Place s = new Place(context.placeCounter++);
		Transition trans = new Transition(context.transitionCounter++);
		trans.predecessors.add(s);
		s.successors.add(trans);
		net.add(new Arc(context.arcCounter++, s, trans, 1));
		net.add(s);
		net.add(trans);

		for (Place st : start) {
			// Create a new transition
			trans.successors.add(st);
			st.predecessors.add(trans);

			net.add(new Arc(context.arcCounter++, trans, st, 1));
		}

		net.getInitialPlaces().clear();
		net.getInitialPlaces().add(s);

		return net;
	}

	/**
	 * Performs a fast and simple unique end place transformation (by simply
	 * generating an or transition.
	 * 
	 * @param net
	 *            The petri net.
	 * @param context
	 *            The petri net context.
	 * @return The petri net with a single end place.
	 */
	private PetriNet simpleEndTransform(PetriNet net, PetriNetContext context) {
		// Create a new transition
		Transition trans = new Transition(context.transitionCounter++);
		trans.setOrTransition(true);

		// Connect all end places with the transition
		for (Place end : net.getEndNodes()) {
			end.successors.add(trans);
			trans.predecessors.add(end);

			net.add(new Arc(context.arcCounter++, end, trans, 1));
		}

		net.add(trans);

		// New end place
		Place end = new Place(context.placeCounter++);
		trans.successors.add(end);
		end.predecessors.add(trans);

		net.add(end);
		net.add(new Arc(context.arcCounter++, trans, end, 1));

		net.getEndNodes().clear();
		net.getEndNodes().add(end);

		return net;
	}

	/**
	 * Performs the step:
	 * 
	 * 4. Combine different end places to a single one.
	 * 
	 * @param net
	 *            The petri net.
	 * @param context
	 *            The petri net context.
	 * @return The petri net with a single end place.
	 */
	private PetriNet endTransform(PetriNet net, PetriNetContext context) {
		// Determine all the nodes lying on the path
		// to the node
		determinePathNodes(net);

		// Get the end nodes
		Set<Place> ends = net.getEndNodes();

		// For every end node, take a look to the nodes lying
		// on the path between the start and the end node.
		// If there is a split and one successor of the split
		// isn't on this path, then we have to create a fork
		// after this split and put it to the end node.
		// After all, the end node is translated to a merge.
		for (Place end : ends) {
			// Get the nodes on the path
			Set<PNode> paths = pathNodes.get(end);

			for (PNode pathNode : paths) {
				if (pathNode instanceof Place) {
					for (PNode suc : pathNode.successors) {
						if (suc instanceof Transition) {
							if (!paths.contains(suc)) {
								// Update the nodes
								suc.successors.add(end);
								end.predecessors.add(suc);

								// Update the model
								net.add(new Arc(context.arcCounter++, suc, end,
										1));
							}
						}
					}
				}
			}
		}

		// Create a new transition
		Transition trans = new Transition(context.transitionCounter++);

		// Connect all end places with the transition
		for (Place end : ends) {
			end.successors.add(trans);
			trans.predecessors.add(end);

			net.add(new Arc(context.arcCounter++, end, trans, 1));
		}

		net.add(trans);

		// New end place
		Place end = new Place(context.placeCounter++);
		trans.successors.add(end);
		end.predecessors.add(trans);

		net.add(end);
		net.add(new Arc(context.arcCounter++, trans, end, 1));

		return net;
	}

	/**
	 * Performs the step:
	 * 
	 * 6. Split nodes with more than one incoming AND outcoming arc.
	 * 
	 * @param net
	 *            The petri net.
	 * @param context
	 *            The petri net context.
	 * @return The petri net with unique node types.
	 */
	private PetriNet uniqueTypeTransform(PetriNet net, PetriNetContext context) {

		// For all the nodes do:
		for (PNode node : net.getNodes()) {
			if (node instanceof Place) {
				// Has the place more then one predecessor and more then one
				// successor?
				if (node.predecessors.size() > 1 && node.successors.size() > 1) {
					// We create a new transition
					Transition trans = new Transition(
							context.transitionCounter++);

					// Create a new place
					Place place = new Place(context.placeCounter++);

					// Add all the successors of the old place to the new
					place.successors.addAll(node.successors);

					// Update the arcs and successors
					for (PNode suc : place.successors) {
						suc.predecessors.remove(node);
						net.removeArc(Arc.toString(node, suc));
						suc.predecessors.add(place);
						net.add(new Arc(context.arcCounter++, place, suc, 1));
					}

					// Connect the old place with the transition
					node.successors.clear();
					node.successors.add(trans);
					trans.predecessors.add(node);

					net.add(new Arc(context.arcCounter++, node, trans, 1));

					// Connect the transition with the new place
					trans.successors.add(place);
					place.predecessors.add(trans);

					net.add(new Arc(context.arcCounter++, trans, place, 1));

					// Put the nodes to the model
					net.add(place);
					net.add(trans);
				}
			} else {
				// Has the place more then one predecessor and more then one
				// successor?
				if (node.predecessors.size() > 1 && node.successors.size() > 1) {
					// We create a new transition
					Transition trans = new Transition(
							context.transitionCounter++);

					// Create a new place
					Place place = new Place(context.placeCounter++);

					// Add all the successors of the old transition to the new
					trans.successors.addAll(node.successors);

					// Update the arcs and successors
					for (PNode suc : trans.successors) {
						suc.predecessors.remove(node);
						net.removeArc(Arc.toString(node, suc));
						suc.predecessors.add(trans);
						net.add(new Arc(context.arcCounter++, trans, suc, 1));
					}

					// Connect the old place with the transition
					node.successors.clear();
					node.successors.add(place);
					place.predecessors.add(node);

					net.add(new Arc(context.arcCounter++, node, place, 1));

					// Connect the transition with the new place
					place.successors.add(trans);
					trans.predecessors.add(place);

					net.add(new Arc(context.arcCounter++, place, trans, 1));

					// Put the nodes to the model
					net.add(place);
					net.add(trans);
				}
			}
		}

		return net;
	}

	/**
	 * Determines the path nodes for the petri nets.
	 * 
	 * @param net
	 *            The petri net.
	 * @return false
	 */
	private boolean determinePathNodes(PetriNet net) {
		boolean stable;

		/*
		 * INITIALIZE
		 */
		for (PNode node : net.getNodes()) {
			pathNodes.put(node, new HashSet<PNode>());
		}

		do {
			stable = true;

			// Take a look at every node and determine the signal classes
			for (PNode node : net.getNodes()) {

				/*
				 * OUT
				 */

				// A new hash set
				HashSet<PNode> nef = new HashSet<PNode>();

				for (PNode pre : node.predecessors) {
					try {
						nef.addAll(pathNodes.get(pre));
					} catch (NullPointerException ex) {
						System.out.println(pre + " " + pathNodes + " " + nef);
						throw ex;
					}
				}

				nef.add(node);

				/*
				 * Stable check
				 */
				if (pathNodes.get(node).size() == nef.size()) {
					continue;
				}

				// Unstable
				stable = false;
				pathNodes.get(node).addAll(nef);
			}

		} while (!stable);

		return false;
	}

}

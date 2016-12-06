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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.jena.uni.mojo.model.WGNode;
import de.jena.uni.mojo.model.WGNode.Type;
import de.jena.uni.mojo.model.WorkflowGraph;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.Arc;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.PNode;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.PetriNet;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.Place;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.Transition;

/**
 * Transforms a petri net into a semantic equivalent workflow graph.
 * 
 * @author Dipl.-Inf. Thomas M. Prinz
 * 
 */
public class PetriNetWorkflowGraphTransformation {

	/**
	 * A list of end nodes of the workflow graph.
	 */
	private final List<WGNode> ends = new ArrayList<WGNode>();

	/**
	 * A map that handles the transformation.
	 */
	private final HashMap<PNode, WGNode> transformationMap = new HashMap<PNode, WGNode>();

	/**
	 * Transform the given petri net into a workflow graph.
	 * 
	 * @param net
	 *            The petri net.
	 * @return The semantic equivalent workflow graph.
	 */
	public WorkflowGraph transform(PetriNet net) {
		int nodeCounter = 0;
		WorkflowGraph graph = new WorkflowGraph();

		// Transform every place and transition to a workflow process
		// node
		for (PNode pNode : net.getNodes()) {
			if (pNode instanceof Place) {
				// If the place has exact one predecessor and one successor
				// it is activity
				if (pNode.predecessors.size() == 1
						&& pNode.successors.size() == 1) {
					WGNode node = new WGNode(nodeCounter++, Type.ACTIVITY);
					String name = ((Place) pNode).getName();
					if (name.startsWith("$$")) {
						name = name.substring(2);
					}

					node.addProcessElement(pNode);
					node.setCode(name);

					graph.addNode(node);

					transformationMap.put(pNode, node);
				} else if (pNode.predecessors.size() == 0
						&& pNode.successors.size() == 1) {
					if (net.getInitialPlaces().contains(pNode)) {
						// It's a start node
						WGNode node = new WGNode(nodeCounter++, Type.START);

						node.addProcessElement(pNode);
						graph.setStart(node);

						transformationMap.put(pNode, node);

					} else {
						WGNode node = new WGNode(nodeCounter++, Type.ACTIVITY);

						node.addProcessElement(pNode);
						graph.addNode(node);

						transformationMap.put(pNode, node);
					}

				} else if (pNode.predecessors.size() == 1
						&& pNode.successors.size() == 0) {
					// It's an end node
					WGNode node = new WGNode(nodeCounter++, Type.ACTIVITY);

					node.addProcessElement(pNode);
					graph.addNode(node);
					ends.add(node);

					transformationMap.put(pNode, node);
				} else if (pNode.predecessors.size() > 1) {
					// It's a merge
					WGNode node = new WGNode(nodeCounter++, Type.MERGE);

					node.addProcessElement(pNode);
					graph.addNode(node);

					transformationMap.put(pNode, node);
				} else if (pNode.successors.size() > 1) {
					// It's a split
					WGNode node = new WGNode(nodeCounter++, Type.SPLIT);

					node.addProcessElement(pNode);
					graph.addNode(node);

					transformationMap.put(pNode, node);
				} else {

				}
			} else {
				if (pNode.predecessors.size() > 1) {
					// It's a join, however, when its semantics should be
					// interpreted
					// as an or-join, we have to handle it as a or-join node.
					WGNode node;
					if (!((Transition) pNode).isOrTransition()) {
						node = new WGNode(nodeCounter++, Type.JOIN);
					} else {
						node = new WGNode(nodeCounter++, Type.OR_JOIN);
					}

					node.addProcessElement(pNode);
					graph.addNode(node);

					transformationMap.put(pNode, node);
				} else if (pNode.successors.size() > 1) {
					WGNode node;
					if (!((Transition) pNode).isOrTransition()) {
						node = new WGNode(nodeCounter++, Type.FORK);
					} else {
						node = new WGNode(nodeCounter++, Type.OR_FORK);
					}

					node.addProcessElement(pNode);
					graph.addNode(node);

					transformationMap.put(pNode, node);
				} else {
					// We translate them to an activity
					// It's easier to handle
					WGNode node = new WGNode(nodeCounter++, Type.ACTIVITY);

					node.addProcessElement(pNode);
					graph.addNode(node);

					transformationMap.put(pNode, node);
				}
			}
		}

		// Transform the arcs
		for (Arc arc : net.getArcs()) {
			WGNode source = transformationMap.get(arc.source);
			WGNode target = transformationMap.get(arc.target);

			// Link the nodes
			source.addSuccessor(target);
			target.addPredecessor(source);
		}

		// Create an unique end node
		WGNode end = new WGNode(nodeCounter++, Type.END);
		graph.setEnd(end);

		if (ends.size() == 1) {
			WGNode node = ends.get(0);
			node.addSuccessor(end);
			end.addPredecessor(node);
			end.addProcessElements(node.getProcessElements());
		} else {
			WGNode orjoin = new WGNode(nodeCounter++, Type.OR_JOIN);
			graph.addNode(orjoin);
			for (WGNode node : ends) {
				node.addSuccessor(orjoin);
				orjoin.addPredecessor(node);
				end.addProcessElements(node.getProcessElements());
				orjoin.addProcessElements(node.getProcessElements());
			}
			orjoin.addSuccessor(end);
			end.addPredecessor(orjoin);
		}

		// The transformation is done, return the workflow graph
		return graph;
	}

}

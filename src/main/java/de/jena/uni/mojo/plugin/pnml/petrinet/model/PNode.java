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

/**
 * An abstract class which stores common properties of places and transitions.
 * Since these properties are final, we decided to make them public.
 * 
 * @author Dipl.-Inf. Thomas Prinz
 * 
 */
public abstract class PNode implements Comparable<PNode> {

	/**
	 * The id of the petri net node.
	 */
	public final String id;

	/**
	 * A list of direct predecessors.
	 */
	public final ArrayList<PNode> predecessors = new ArrayList<PNode>();

	/**
	 * A list of direct successors.
	 */
	public final ArrayList<PNode> successors = new ArrayList<PNode>();

	/**
	 * Whether this node was visited or not.
	 */
	public boolean visited = false;

	/**
	 * The constructor.
	 * 
	 * @param id
	 *            The id.
	 */
	protected PNode(String id) {
		this.id = id;
	}

	@Override
	public int compareTo(PNode node) {
		return this.id.compareTo(node.id);
	}
}

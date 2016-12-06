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

/**
 * A simple representation of a transition. It inherits from the {@link PNode}.
 * 
 * @author Dipl.-Inf. Thomas Prinz
 * 
 */
public class Transition extends PNode {

	/**
	 * The name of the transition. It hides the PNode.name field.
	 */
	private String name = "";

	/**
	 * Whether this transition should be handled with or semantics.
	 */
	private boolean orTransition = false;

	/**
	 * The constructor.
	 * 
	 * @param id
	 *            The id of the transition.
	 */
	public Transition(String id) {
		super(id);
	}

	/**
	 * When the id is an integer value.
	 * 
	 * @param id
	 *            The id of the transition.
	 */
	public Transition(int id) {
		this("tx" + id);
	}

	/**
	 * Set the name of this transition.
	 * 
	 * @param name
	 *            The name of this transition.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the name of this transition.
	 * 
	 * @return The name of this transition.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Whether this transition should be handled with or semantics or not.
	 * 
	 * @return Whether this transition should be handled with or semantics or
	 *         not.
	 */
	public boolean isOrTransition() {
		return orTransition;
	}

	/**
	 * Set whether this transition should be handled with or semantics or not.
	 * 
	 * @param orTransition
	 *            Whether this transition should be handled with or semantics or
	 *            not.
	 */
	public void setOrTransition(boolean orTransition) {
		this.orTransition = orTransition;
	}

	/**
	 * Produces a string dot representation.
	 * 
	 * @return A string dot representation.
	 */
	public String toGraph() {
		String ret = id + "[label=\"" + id + "\\n";

		ret += "\", shape=square];\n";

		return ret;
	}

	@Override
	public String toString() {
		return id + "(" + name + ")";
	}
}

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
 * This class represents a simple place of a petri net. It extends a
 * {@link PNode}.
 * 
 * @author Dipl.-Inf. Thomas M. Prinz
 * 
 */
public class Place extends PNode {

	/**
	 * The name of the place.
	 */
	private String name = "";

	/**
	 * The start marking of the place.
	 */
	private int marking = 0;

	/**
	 * Constructor with a string id.
	 * 
	 * @param id
	 *            The id of the place.
	 */
	public Place(String id) {
		super(id);
	}

	/**
	 * The constructor with an integer id.
	 * 
	 * @param id
	 *            The id of the place.
	 */
	public Place(int id) {
		this("px" + id);
	}

	/**
	 * Set the name of the place.
	 * 
	 * @param name
	 *            The name of the place.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the place.
	 * 
	 * @return The name of the place.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the marking of the place.
	 * 
	 * @param marking
	 *            The marking of the place.
	 */
	public void setMarking(int marking) {
		this.marking = marking;
	}

	/**
	 * Get the marking of the place.
	 * 
	 * @return The marking of the place.
	 */
	public int getMarking() {
		return marking;
	}

	/**
	 * Generates a string dot representation of the place.
	 * 
	 * @return A string dot representation of the place.
	 */
	public String toGraph() {
		String ret = id + "[label=\"" + id + "(" + marking + ")" + "\\n";

		ret += "\", shape=circle];\n";

		return ret;
	}

	@Override
	public String toString() {
		return id + "(" + marking + ", " + name + ")";
	}
}

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
 * A class containing all information about a petri net arc. Since all fields
 * are final, they can be public.
 * 
 * @author Dipl.-Inf. Thomas M. Prinz
 * 
 */
public class Arc {

	/**
	 * The id of the arc.
	 */
	public final String id;

	/**
	 * The source node of the arc.
	 */
	public final PNode source;

	/**
	 * The target node of the arc.
	 */
	public final PNode target;

	/**
	 * The start marking of the arc.
	 */
	public final int marking;

	/**
	 * The constructor defines the arc finally.
	 * 
	 * @param id
	 *            The id.
	 * @param source
	 *            The source node.
	 * @param target
	 *            The target node.
	 * @param marking
	 *            The marking.
	 */
	public Arc(String id, PNode source, PNode target, int marking) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.marking = marking;
	}

	/**
	 * A constructor that also accepts integer ids.
	 * 
	 * @param id
	 *            The id.
	 * @param source
	 *            The source node.
	 * @param target
	 *            The target node.
	 * @param marking
	 *            The marking.
	 */
	public Arc(int id, PNode source, PNode target, int marking) {
		this("ax" + id, source, target, marking);
	}

	/**
	 * Creates a dot graph representation of this arc.
	 * 
	 * @return A string dot representation of this arc.
	 */
	public String toGraph() {
		String ret = toString() + "[label=\"";
		ret += "";
		ret += "\"];\n";

		return ret;
	}

	@Override
	public String toString() {
		return source.id + "->" + target.id;
	}

	/**
	 * A static to string method.
	 * 
	 * @param source
	 *            The source node.
	 * @param target
	 *            The target node.
	 * @return A string representation.
	 */
	public static String toString(PNode source, PNode target) {
		return source.id + "->" + target.id;
	}
}

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
package de.jena.uni.mojo.plugin.pnml.parser.pnml;

/**
 * A simple (and fast) context for generating a petri net.
 * 
 * @author Dipl.-Inf. Thomas M. Prinz
 * 
 */
public class PetriNetContext {

	/**
	 * A arc counter.
	 */
	public int arcCounter = 0;

	/**
	 * A transition counter.
	 */
	public int transitionCounter = 0;

	/**
	 * A place counter.
	 */
	public int placeCounter = 0;
}

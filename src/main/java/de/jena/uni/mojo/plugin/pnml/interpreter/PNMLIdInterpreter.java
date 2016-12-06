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
package de.jena.uni.mojo.plugin.pnml.interpreter;

import java.util.Collection;
import java.util.List;

import de.jena.uni.mojo.interpreter.AbstractEdge;
import de.jena.uni.mojo.interpreter.IdInterpreter;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.PNode;

/**
 * A special pnml node Id interpreter.
 * 
 * @author Dipl.-Inf. Thomas M. Prinz
 * 
 */
public class PNMLIdInterpreter extends IdInterpreter {

	@Override
	public String extractId(Object obj) {
		PNode pnode = (PNode) obj;
		return pnode.id;
	}

	@Override
	public String extractPath(Collection<Object> nodes) {
		return "";
	}

	@Override
	public String extractPath(List<AbstractEdge> edges) {
		return "";
	}
}

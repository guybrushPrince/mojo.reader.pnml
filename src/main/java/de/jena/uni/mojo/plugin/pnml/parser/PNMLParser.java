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
package de.jena.uni.mojo.plugin.pnml.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.jena.uni.mojo.plugin.pnml.petrinet.model.Arc;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.PNode;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.PetriNet;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.Place;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.Transition;

/**
 * This is a simple (inofficial) PNML file parser. Since PNML is XML based, we
 * read each file as an XML stream and interpret each information.
 * 
 * @author Dipl.-Inf. Thomas M. Prinz
 * 
 */
public class PNMLParser {

	/**
	 * An array list of petri nets which were parsed.
	 */
	private final ArrayList<PetriNet> nets = new ArrayList<PetriNet>();

	/**
	 * A map that stores for each id of a node a corresponding petri net node.
	 */
	private final HashMap<String, PNode> nodes = new HashMap<String, PNode>();

	/**
	 * A list of temporary arcs between the nodes.
	 */
	private final ArrayList<TemporaryArc> tempArcs = new ArrayList<TemporaryArc>();

	/**
	 * Starts to parse the petri net which is given by the XML stream.
	 * 
	 * @param stream
	 *            The XML stream.
	 * @return A petri net.
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 * @throws ParseException
	 *             If the PNML file contains an error.
	 */
	public PetriNet parse(XMLStreamReader stream) throws XMLStreamException,
			ParseException {

		// Parse the file till the end is reached
		try {
			while (stream.hasNext()) {
				next(stream);

				if ("pnml".equalsIgnoreCase(stream.getLocalName())) {
					next(stream);
					nets.add(parsePNML(stream));
				}
			}
		} catch (NoSuchElementException ex) {
			// ex.printStackTrace();
		} catch (XMLStreamException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		if (nets.size() > 1)
			throw new ParseException();

		PetriNet net = nets.get(0);

		// Create real arcs
		for (TemporaryArc temp : tempArcs) {
			PNode source = nodes.get(temp.source);
			PNode target = nodes.get(temp.target);

			net.add(new Arc(temp.id, source, target, temp.marking));

			source.successors.add(target);
			target.predecessors.add(source);
		}

		tempArcs.clear();

		return net;
	}

	/**
	 * Parses a PNML XML tag.
	 * 
	 * @param stream
	 *            The XML stream.
	 * @return A petri net since in each PNML tag is a petri net.
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 * @throws ParseException
	 *             If the PNML file contains an error.
	 */
	private PetriNet parsePNML(XMLStreamReader stream)
			throws XMLStreamException, ParseException {
		PetriNet petriNet = null;

		while ("net".equalsIgnoreCase(stream.getLocalName())) {
			String id = stream.getAttributeValue(null, "id");
			next(stream);
			petriNet = parseNet(stream, id);
		}

		return petriNet;
	}

	/**
	 * Parses the net XML tag.
	 * 
	 * @param stream
	 *            The XML stream.
	 * @param id
	 *            The id of the net.
	 * @return A petri net since each petri net is within its own net tag.
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 * @throws ParseException
	 *             If the PNML file contains an error.
	 */
	private PetriNet parseNet(XMLStreamReader stream, String id)
			throws XMLStreamException, ParseException {
		PetriNet petriNet = new PetriNet(id);

		while ("place".equalsIgnoreCase(stream.getLocalName())
				|| "transition".equalsIgnoreCase(stream.getLocalName())
				|| "arc".equalsIgnoreCase(stream.getLocalName())) {

			if ("place".equalsIgnoreCase(stream.getLocalName())) {
				petriNet.add(parsePlace(petriNet, stream));
			} else if ("transition".equalsIgnoreCase(stream.getLocalName())) {
				petriNet.add(parseTransition(stream));
			} else if ("arc".equalsIgnoreCase(stream.getLocalName())) {
				parseArc(stream);
			} else {
				throw new ParseException();
			}
		}

		return petriNet;
	}

	/**
	 * Parses an petri net arc.
	 * 
	 * @param stream
	 *            The XML stream.
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 * @throws ParseException
	 *             If the PNML file contains an error.
	 */
	private void parseArc(XMLStreamReader stream) throws XMLStreamException,
			ParseException {
		// Parse the id of the arc
		String id = stream.getAttributeValue(null, "id");

		// Parse the source of the arc
		String source = stream.getAttributeValue(null, "source");

		// Parse the target of the arc
		String target = stream.getAttributeValue(null, "target");

		// Create a new arc
		TemporaryArc temp = new TemporaryArc(id, source, target);

		// Add the arc to the list
		tempArcs.add(temp);

		// Read the next symbol
		next(stream);

		while ("inscription".equalsIgnoreCase(stream.getLocalName())) {
			next(stream);
			temp.marking = parseInscription(stream);
		}
	}

	/**
	 * Parses the inscription.
	 * 
	 * @param stream
	 *            The XML stream.
	 * @return The parsed marking.
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 * @throws ParseException
	 *             If the PNML file contains an error.
	 */
	private int parseInscription(XMLStreamReader stream)
			throws XMLStreamException, ParseException {
		if ("text".equalsIgnoreCase(stream.getLocalName())) {
			String marking = stream.getElementText();

			// Read the next
			next(stream);

			return Integer.parseInt(marking);
		} else {
			throw new ParseException();
		}
	}

	/**
	 * Parses a transition.
	 * 
	 * @param stream
	 *            The XML stream.
	 * @return A transition.
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 * @throws ParseException
	 *             If the PNML file contains an error.
	 */
	private Transition parseTransition(XMLStreamReader stream)
			throws XMLStreamException, ParseException {
		// Parse the id of the transition
		String id = stream.getAttributeValue(null, "id");

		// Create a new transition
		Transition transition = new Transition(id);

		// Add the node to the list
		nodes.put(id, transition);

		// Read the next symbol
		next(stream);

		while ("name".equalsIgnoreCase(stream.getLocalName())) {
			next(stream);
			transition.setName(parseName(stream));
		}

		return transition;
	}

	/**
	 * Parses a place.
	 * 
	 * @param net
	 *            The petri net this place belongs to.
	 * @param stream
	 *            The XML stream.
	 * @return A place.
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 * @throws ParseException
	 *             If the PNML file contains an error.
	 */
	private Place parsePlace(PetriNet net, XMLStreamReader stream)
			throws XMLStreamException, ParseException {
		// Parse the id of the place
		String id = stream.getAttributeValue(null, "id");

		// Create a new place
		Place place = new Place(id);

		// Add the node to the list
		nodes.put(id, place);

		// Read the next symbol
		next(stream);

		while ("name".equalsIgnoreCase(stream.getLocalName())
				|| "initialMarking".equalsIgnoreCase(stream.getLocalName())) {

			if ("name".equalsIgnoreCase(stream.getLocalName())) {
				next(stream);
				place.setName(parseName(stream));
			} else if ("initialMarking".equalsIgnoreCase(stream.getLocalName())) {
				next(stream);
				place.setMarking(parseMarking(stream));

				net.addInitial(place);
			}
		}

		return place;
	}

	/**
	 * Parses the name of an PNML element.
	 * 
	 * @param stream
	 *            The XML stream.
	 * @return The name of the element.
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 * @throws ParseException
	 *             If the PNML file contains an error.
	 */
	private String parseName(XMLStreamReader stream) throws XMLStreamException,
			ParseException {
		if ("text".equalsIgnoreCase(stream.getLocalName())) {
			String name = stream.getElementText();

			// Read the next
			next(stream);

			return name;
		} else {
			throw new ParseException();
		}
	}

	/**
	 * Parses the marking of the place.
	 * 
	 * @param stream
	 *            The XML stream.
	 * @return The number of tokens on the place.
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 * @throws ParseException
	 *             If the PNML file contains an error.
	 */
	private int parseMarking(XMLStreamReader stream) throws XMLStreamException,
			ParseException {
		if ("text".equalsIgnoreCase(stream.getLocalName())) {
			String marking = stream.getElementText();

			// Read the next
			next(stream);

			return Integer.parseInt(marking);
		} else {
			throw new ParseException();
		}
	}

	/**
	 * Reads in the next XML element (tag).
	 * 
	 * @param stream
	 *            The XML stream
	 * @throws XMLStreamException
	 *             If the XML stream contains an error.
	 */
	private void next(XMLStreamReader stream) throws XMLStreamException {
		do {
			stream.next();
		} while (stream.isStartElement() == false);
	}

	/**
	 * A temporary arc.
	 * 
	 * @author Dipl.-Inf. Thomas M. Prinz
	 * 
	 */
	private class TemporaryArc {

		/**
		 * The id of the arc.
		 */
		public final String id;

		/**
		 * The source of the arc.
		 */
		public final String source;

		/**
		 * The target of the arc.
		 */
		public final String target;

		/**
		 * The marking on this arc.
		 */
		public int marking;

		/**
		 * The constructor of a simple temporary arc.
		 * 
		 * @param id
		 *            The id.
		 * @param source
		 *            The source.
		 * @param target
		 *            The target.
		 */
		public TemporaryArc(String id, String source, String target) {
			this.id = id;
			this.source = source;
			this.target = target;
		}
	}
}

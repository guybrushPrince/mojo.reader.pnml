/**
 * Copyright 2015 mojo Friedrich Schiller University Jena
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
package de.jena.uni.mojo.plugin.pnml.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import de.jena.uni.mojo.analysis.information.AnalysisInformation;
import de.jena.uni.mojo.error.Annotation;
import de.jena.uni.mojo.error.ParseAnnotation;
import de.jena.uni.mojo.model.WorkflowGraph;
import de.jena.uni.mojo.plugin.pnml.parser.PNMLParser;
import de.jena.uni.mojo.plugin.pnml.parser.pnml.PetriNetContext;
import de.jena.uni.mojo.plugin.pnml.petrinet.model.PetriNet;
import de.jena.uni.mojo.plugin.pnml.petrinet.transformation.PetriNetTransformation;
import de.jena.uni.mojo.plugin.pnml.petrinet.transformation.PetriNetWorkflowGraphTransformation;
import de.jena.uni.mojo.reader.Reader;
import de.jena.uni.mojo.util.store.ErrorAndWarningStore;

/**
 * This mojo own pnml parser reads in pnml files and transforms them in a first
 * step into a petri net. Afterwards, the petri net will be transformed into at
 * least one workflow graph.
 * 
 * @author Dipl.-Inf. Thomas Prinz
 * 
 */
public class PNMLReader extends Reader {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -4368518287089471820L;

	/**
	 * A list of annotations that are important information.
	 */
	private final List<Annotation> annotations = new ArrayList<Annotation>();

	/**
	 * The constructor.
	 * 
	 * @param file
	 *            The file containing pnml code.
	 * @param analysisInformation
	 *            The analysis information.
	 */
	public PNMLReader(File file, AnalysisInformation analysisInformation) {
		super(file, analysisInformation);
	}

	@Override
	public List<Annotation> analyze() {
		try {
			// Define a new petri net context.
			PetriNetContext context = new PetriNetContext();

			// Open a new file input stream
			FileInputStream fileStream = new FileInputStream(file);

			// Create a new xml reader
			XMLInputFactory xif = XMLInputFactory.newInstance();
			InputStreamReader in = new InputStreamReader(fileStream, "UTF-8");
			XMLStreamReader xtr = xif.createXMLStreamReader(in);

			// Create a new BPMN parser and parse the xml file
			PNMLParser parser = new PNMLParser();
			PetriNet net = parser.parse(xtr);

			// Transform the petri net
			PetriNetTransformation transformation = new PetriNetTransformation();
			transformation.transform(net, context);

			try {
				fileStream.close();
			} catch (IOException e) {

			}

			parser = null;
			xtr = null;
			xif = null;
			context = null;
			transformation = null;

			if (net.isClosed()) {

				// Transform the petri net to a workflow graph
				PetriNetWorkflowGraphTransformation pnWfTransformation = new PetriNetWorkflowGraphTransformation();
				WorkflowGraph workflow = pnWfTransformation.transform(net);

				net = null;
				pnWfTransformation = null;

				this.graphs = Collections.singletonList(workflow);

				return annotations;
			} else {
				return annotations;
			}

		} catch (Exception e) {
			// There is an exception. Annotate it.
			ParseAnnotation annotation = new ParseAnnotation(this);
			annotations.add(annotation);

			return annotations;
		}
	}

	@Override
	public ErrorAndWarningStore getStore() {
		return new ErrorAndWarningStore();
	}

}

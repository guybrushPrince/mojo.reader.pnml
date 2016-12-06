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
package de.jena.uni.mojo.plugin.pnml;

import java.io.File;
import java.util.List;

import de.jena.uni.mojo.analysis.information.AnalysisInformation;
import de.jena.uni.mojo.error.Annotation;
import de.jena.uni.mojo.interpreter.IdInterpreter;
import de.jena.uni.mojo.plugin.SourcePlugin;
import de.jena.uni.mojo.plugin.pnml.interpreter.PNMLIdInterpreter;
import de.jena.uni.mojo.plugin.pnml.reader.PNMLReader;
import de.jena.uni.mojo.reader.Reader;

/**
 * 
 * @author Dipl.-Inf. Thomas Prinz
 *
 */
public class PNMLSourcePlugin implements SourcePlugin {

	@Override
	public String getName() {
		return "Mojo Source Plugin PNML";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getFileExtension() {
		return "pnml";
	}

	@Override
	public Reader getReader(File file, AnalysisInformation information) {
		return new PNMLReader(file, information);
	}

	@Override
	public IdInterpreter getIdInterpreter() {
		return new PNMLIdInterpreter();
	}

	@Override
	public List<Annotation> verify(File file, AnalysisInformation info) {
		// TODO
		return null;
	}

	@Override
	public List<Annotation> verify(String stream, AnalysisInformation info) {
		// TODO
		return null;
	}

}

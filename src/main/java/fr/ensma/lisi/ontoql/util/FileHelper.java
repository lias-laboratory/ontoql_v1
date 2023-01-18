/*********************************************************************************
* This file is part of OntoQL Project.
* Copyright (C) 2006  LISI - ENSMA
*   Teleport 2 - 1 avenue Clement Ader
*   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
* 
* OntoQL is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* OntoQL is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with OntoQL.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package fr.ensma.lisi.ontoql.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * An helper class for file handling
 * 
 * @author Stéphane Jean
 */
public class FileHelper {

	static final String STRING_TO_SEARCH_1 = "public interface";

	static final String STRING_TO_SEARCH_2 = "public class";

	public static void appendMethodToFile(File file, String methodToAppend) throws Exception {
		appendTextToFile(file, methodToAppend, STRING_TO_SEARCH_1, STRING_TO_SEARCH_2, true);
	}

	public static void appendElementToFile(File file, String methodToAppend) throws Exception {
		appendTextToFile(file, methodToAppend, "</ontology_model>", null, false);
	}

	public static void removeXMLElementFromFile(File file, String xmlElementNode) throws Exception {
		try {

			// Construct the new file that will later be renamed to the original
			// filename.
			File tempFile = new File(file.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			// Read from the original file and write to the new
			// unless content matches element to be removed.
			boolean insideElement = false;
			while ((line = br.readLine()) != null) {

				if (!line.trim().equals(xmlElementNode) && !insideElement) {
					pw.println(line);
					pw.flush();
				} else {
					if (insideElement && line.trim().equals(xmlElementNode)) { // we
						// exit
						// from
						// the
						// XML
						// element
						insideElement = false;
						xmlElementNode = "????????????"; // unreachable line
					} else { // we enter in the XMLElement
						insideElement = true;
						xmlElementNode = "</entity>";
					}

				}
			}
			pw.close();
			br.close();

			if (file.delete())
				tempFile.renameTo(file);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void appendXMLAttributeToFile(File file, String XMLAttributeNode, String xmlElementNode)
			throws Exception {
		try {

			// Construct the new file that will later be renamed to the original
			// filename.
			File tempFile = new File(file.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			// Read from the original file and write to the new
			// unless content matches element to be removed.
			boolean insideElement = false;
			while ((line = br.readLine()) != null) {

				if (!line.trim().startsWith(xmlElementNode)) {
					pw.println(line);
					pw.flush();
				} else {
					if (insideElement && line.trim().equals(xmlElementNode)) {
						// we exit from the XML element
						insideElement = false;
						pw.print("\t\t" + XMLAttributeNode);
						pw.flush();
						pw.println(line);
						pw.flush();
						xmlElementNode = "????????????"; // unreachable line
					} else { // we enter in the XMLElement
						insideElement = true;
						pw.println(line);
						pw.flush();
						xmlElementNode = "</entity>";
					}

				}
			}
			pw.close();
			br.close();

			if (file.delete())
				tempFile.renameTo(file);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void removeXMLAttributeFromFile(File file, String XMLAttributeNode, String xmlElementNode)
			throws Exception {
		try {

			// Construct the new file that will later be renamed to the original
			// filename.
			File tempFile = new File(file.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			// Read from the original file and write to the new
			// unless content matches element to be removed.
			boolean insideElement = false;
			while ((line = br.readLine()) != null) {

				if (!line.trim().startsWith(xmlElementNode)) {
					pw.println(line);
					pw.flush();
				} else {
					if (insideElement && line.trim().equals(xmlElementNode)) {
						// we erase the attribute
						xmlElementNode = "????????????"; // unreachable line
					} else { // we enter in the XMLElement
						insideElement = true;
						pw.println(line);
						pw.flush();
						xmlElementNode = XMLAttributeNode.trim();
					}

				}
			}
			pw.close();
			br.close();

			if (file.delete())
				tempFile.renameTo(file);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Append some text after or before some strings
	 * 
	 * @param file         a given file
	 * @param textToAppend text to append
	 * @param searchText1  one given string to search
	 * @param searchText2  an other string to search
	 * @param after        True if the text must be append after the searched
	 *                     strings
	 * @throws Exception
	 */
	public static void appendTextToFile(File file, String textToAppend, String searchText1, String searchText2,
			boolean after) throws Exception {

		File fileTmp = File.createTempFile(file.getName(), "tmp");
		BufferedReader br = null;
		BufferedWriter bw = null;
		String line;

		try {
			br = new BufferedReader(new FileReader(file));
			bw = new BufferedWriter(new FileWriter(fileTmp));

			while ((line = br.readLine()) != null) {
				int pos = line.indexOf(searchText1);
				if (searchText2 != null) {
					pos = line.indexOf(searchText2);
				}
				if (pos != -1 && !after) {
					bw.write(textToAppend);
					bw.newLine();
				}
				bw.write(line);
				bw.newLine();
				if (pos != -1 && after) {
					bw.newLine();
					bw.write(textToAppend);
				}
			}
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
			try {
				bw.close();
			} catch (Exception e) {
			}
		}
		if (file.delete())
			fileTmp.renameTo(file);
	}
}

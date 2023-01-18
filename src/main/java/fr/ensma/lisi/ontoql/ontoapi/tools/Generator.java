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
package fr.ensma.lisi.ontoql.ontoapi.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.cfg.OntologyModel;
import fr.ensma.lisi.ontoql.cfg.OntologyModelImpl;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.core.EntityDatatypeInt;
import fr.ensma.lisi.ontoql.core.EntityDatatypeString;
import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;
import fr.ensma.lisi.ontoql.ontomodel.OntoMultilingualAttribute;
import fr.ensma.lisi.ontoql.util.FileHelper;
import fr.ensma.lisi.ontoql.util.StringHelper;

/**
 * @author Stéphane Jean
 */
public class Generator {

	private Log log = LogFactory.getLog(Generator.class);

	/** extension of the java file. * */
	private static final String JAVA_EXTENSION = "java";

	/** base directory of source file * */
	private static final String BASE_DIR_SOURCE = "src/main/java";

	/** prefix of each OntoAPI class * */
	private String PREFIX_ONTOAPI_CLASS = "Onto";

	/** package of JOBDBC. * */
	private static final String PACKAGE_JOBDBC = "fr.ensma.lisi.ontoql.jobdbc";

	/**
	 * Constructs a new Generator using the defaults.
	 */
	public Generator() {
	}

	/**
	 * Append a getter method the OntoQLResultSet interface.
	 * 
	 * @param entity        the entity for which a method is needed
	 * @param ontologyModel the entities of the datamodel
	 * @throws Exception if a database access problem occurs
	 */
	public void appendGetterInResultSetForEntity(OntoEntity entity, OntologyModel ontologyModel) throws Exception {
		log.info("Generating a getter method for the entity " + entity.getName() + " in the interface OntoQLResultSet");

		// 1- Append the method to the interface
		File dirResultSet = this.getDir(PACKAGE_JOBDBC);
		File fileResultSet = new File(dirResultSet, "OntoQLResultSet.java");
		log.debug("Editting " + fileResultSet);

		String nameEntity = entity.getName();
		String nameOntoAPIClass = getName(nameEntity);
		String getterMethod = "\t/**\n"
				+ "\t* Retrieves the value of the designated column in the current row of this\n"
				+ "\t* <code>OntoQLResultSet</code> object as an instance of the OntoAPI\n" + "\t* <code>"
				+ nameOntoAPIClass + "</code> class.\n"
				+ "\t* @param columnIndex the first column is 1, the second is 2, ...\n"
				+ "\t* @return an instance of <code>" + nameOntoAPIClass + "</code>\n"
				+ "\t* @exception JOBDBCException if a database access error occurs\n" + "\t*/\n";
		getterMethod += "\t" + nameOntoAPIClass + " get" + nameOntoAPIClass
				+ "(int columnIndex) throws JOBDBCException;\n";
		FileHelper.appendMethodToFile(fileResultSet, getterMethod);

		// 2- Append the method to the implementation
		File dirResultSetImpl = this.getDir(PACKAGE_JOBDBC + ".impl");
		File fileResultSetImpl = new File(dirResultSetImpl, "OntoQLResultSetImpl.java");
		log.debug("Editting " + dirResultSetImpl);

		getterMethod = "\t/**\n" + "\t* Retrieves the value of the designated column in the current row of this\n"
				+ "\t* <code>OntoQLResultSet</code> object as an instance of the OntoAPI\n" + "\t* <code>"
				+ nameOntoAPIClass + "</code> class.\n"
				+ "\t* @param columnIndex the first column is 1, the second is 2, ...\n"
				+ "\t* @return an instance of <code>" + nameOntoAPIClass + "</code>\n"
				+ "\t* @exception JOBDBCException if a database access error occurs\n" + "\t*/\n";
		getterMethod += "\tpublic " + nameOntoAPIClass + " get" + nameOntoAPIClass
				+ "(int columnIndex) throws JOBDBCException {\n" + "\t\ttry {\n" + "\t\t\t" + nameOntoAPIClass
				+ " res = null;\n\n" + "\t\t\tint oid = resultSetDelegate.getInt(getMapIndex(columnIndex));\n"
				+ "\t\t\tif (oid != 0)\n" + "\t\t\tres = new " + nameOntoAPIClass + "(oid, session);\n\n"
				+ "\t\t\treturn res;\n" + "\t\t} catch (SQLException oExc) {\n"
				+ "\t\t\tthrow new JOBDBCException(oExc.getMessage());\n" + "\t\t}\n" + "\t}\n";
		FileHelper.appendMethodToFile(fileResultSetImpl, getterMethod);
	}

	public void appendMethodsInSessionForEntity(OntoEntity entity) throws Exception {
		String nameEntity = entity.getName();
		String nameOntoAPIClass = getName(nameEntity);
		log.info("Generating methods for the entity " + nameEntity + " in the interface Session");

		// 1- Append the methods to the interface
		// open file
		File dirSession = this.getDir(PACKAGE_JOBDBC);
		File fileSession = new File(dirSession, "OntoQLSession.java");
		log.debug("Editing " + fileSession);
		// write file
		String newMethod = getCodeNewMethodInInterface(nameOntoAPIClass);
		FileHelper.appendMethodToFile(fileSession, newMethod);

		// 2- Append the methods to the implementation
		// open file
		File dirSessionImpl = this.getDir(PACKAGE_JOBDBC + ".impl");
		File fileSessionImpl = new File(dirSessionImpl, "OntoQLSessionImpl.java");
		log.debug("Editing " + dirSessionImpl);
		// writte file
		newMethod = getCodeNewMethodInImpl(nameOntoAPIClass);
		FileHelper.appendMethodToFile(fileSessionImpl, newMethod);
	}

	/**
	 * Get the code of the new method in the interface of OntoQLSession.
	 * 
	 * @param nameOntoAPIClass name of the OntoAPI java class
	 * @return the code of the new method in the interface of OntoQLSession.
	 */
	public String getCodeNewMethodInInterface(String nameOntoAPIClass) {
		return "\t/**\n" + "\t* instantiate a new instance of the class <code>" + nameOntoAPIClass + "</code>\n"
				+ "\t* with the oid taken in parameter. \n" + "\t* @param oid the oid of the instance to instantiate\n"
				+ "\t* @return an instance of <code>" + nameOntoAPIClass + "</code>\n"
				+ "\t* @exception JOBDBCException if a database access error occurs\n" + "\t*/\n" + "\t"
				+ nameOntoAPIClass + " new" + nameOntoAPIClass + "(int oid) throws JOBDBCException;\n";
	}

	/**
	 * Get the code of the new method in the implementation of OntoQLSession.
	 * 
	 * @param nameOntoAPIClass name of the OntoAPI java class
	 * @return the code of the new method in the implementation of OntoQLSession.
	 */
	public String getCodeNewMethodInImpl(String nameOntoAPIClass) {
		return "\t/**\n" + "\t* instantiate an instance of the class <code>" + nameOntoAPIClass + "</code>\n"
				+ "\t* with the oid taken in parameter. \n" + "\t* @param oid the oid of the instance to instantiate\n"
				+ "\t* @return an instance of <code>" + nameOntoAPIClass + "</code>\n"
				+ "\t* @exception JOBDBCException if a database access error occurs\n" + "\t*/\n" + "\tpublic "
				+ nameOntoAPIClass + " new" + nameOntoAPIClass + "(int oid) throws JOBDBCException {\n"
				+ "\t\t return new " + nameOntoAPIClass + "(oid,this);\n" + "\t}\n";
	}

	/**
	 * Append a method to get an instance of a new entity if the interface and class
	 * OntoQLSession.
	 * 
	 * @param entity a given entity
	 * @throws Exception if an error occurs
	 */
	public void appendNewMethodInSessionForEntity(OntoEntity entity) throws Exception {
		String nameEntity = entity.getName();

		log.info("Generating a new method for the entity " + nameEntity + " in the interface Session");

	}

	/**
	 * Generate the JAVA class of a given entity
	 * 
	 * @param entity        a given entity
	 * @param ontologyModel the other entities in the model
	 * @throws Exception if an errors occurs
	 */
	public void generateJavaClassForEntity(OntoEntity entity, OntologyModelImpl ontologyModel) throws Exception {
		log.info("Generating the java class for the entity " + entity.getName());

		String saveToPackage = ontologyModel.getDefaultPackage();
		String saveToClassName = entity.getName();

		File dir = this.getDir(saveToPackage);
		File file = new File(dir, this.getFileName(saveToClassName));
		log.debug("Writting " + file);
		PrintWriter writer = new PrintWriter(new FileOutputStream(file));
		render(saveToPackage, getName(saveToClassName), entity, writer);
		writer.close();
	}

	/**
	 * Writte the content of the JAVA file of an entity
	 * 
	 * @param namePackage name of the package
	 * @param nameClass   name of the class
	 * @param entity      the given entity
	 * @param mainwriter  the writter of the file
	 * @throws Exception if an errors occurs
	 */
	public void render(String namePackage, String nameClass, OntoEntity entity, PrintWriter mainwriter)
			throws Exception {

		mainwriter.println("/*");
		mainwriter.println(" *    Project: OntoQL");
		mainwriter.println(" *    LISI-ENSMA and University of Poitiers");
		mainwriter.println(" *    Author : Stephane Jean");
		mainwriter.println(" *    Email  : jean@ensma.fr");
		mainwriter.println(" */");

		mainwriter.println("package " + namePackage + ";");
		mainwriter.println();
		generateImport(mainwriter);
		mainwriter.println();
		mainwriter.println("/** @author OntoQL CodeGenerator */");
		mainwriter.println();
		mainwriter.print("public class " + nameClass);

		OntoEntity superEntity = entity.getSuperMapEntity();
		if (entity.getSuperMapEntity() != null) {
			mainwriter.print(" extends " + getName(superEntity.getName()));
		} else {
			mainwriter.print(" extends " + PREFIX_ONTOAPI_CLASS + "Root");
		}
		mainwriter.println(" {");
		mainwriter.println();
		generateConstructors(mainwriter, entity);
		mainwriter.println();
		generateAttributesWithGetterAndSetter(mainwriter, entity);
		// mainwriter.println();
		// generateEqualsAndHashCodeMethod(mainwriter, entity);
		mainwriter.println();
		generateLoadMethod(mainwriter, entity);
		mainwriter.println("}");
	}

	private void generateEqualsAndHashCodeMethod(PrintWriter writter, OntoEntity entity) {
		writter.println("\tpublic int hashCode() {");
		writter.println("\t\tfinal int PRIME = 31;");
		writter.println("\t\tint result = 1;");
		writter.println("\t\tresult = PRIME * result + oid;");
		writter.println("\t\treturn result;");
		writter.println("\t}");
		writter.println();
		writter.println("\tpublic boolean equals(Object obj) {");
		writter.println("\t\tif (this == obj)");
		writter.println("\t\t\treturn true;");
		writter.println("\t\tif (obj == null)");
		writter.println("\t\t\treturn false;");
		writter.println("\t\tif (getClass() != obj.getClass())");
		writter.println("\t\t\treturn false;");
		writter.println("\t\tfinal OntoRoot other = (OntoRoot) obj;");
		writter.println("\t\tif (oid != other.oid)");
		writter.println("\t\t\treturn false;");
		writter.println("\t\treturn true;");
		writter.println("\t}");
	}

	/**
	 * Writte the import for the class
	 * 
	 * @param writter the writter of the file
	 */
	private void generateImport(PrintWriter writter) {
		writter.println("import java.sql.SQLException;");
		writter.println("import fr.ensma.lisi.ontoql.exception.JOBDBCException;");
		writter.println("import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;");
		writter.println("import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;");
		writter.println("import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;");
		writter.println("import java.util.Set;");
		writter.println("import fr.ensma.lisi.ontoql.internalapi.ModelUtil;");
	}

	/**
	 * Generate a constructor for this ontology element
	 * 
	 * @param writter writter in the file
	 * @param entity  the ontology element instanciated
	 */
	private void generateConstructors(PrintWriter writter, OntoEntity entity) {
		writter.println("\tpublic " + getName(entity.getName()) + "() {}");
		writter.println();
		writter.println("\tpublic " + getName(entity.getName()) + "(int oid, OntoQLSession s) {");
		writter.println("\t\tsuper(oid, s);");
		writter.println("\t}");
	}

	/**
	 * Generate all the code required for the attributes of an entity
	 * 
	 * @param writter writter in the file
	 * @param entity  the given entity
	 */
	private void generateAttributesWithGetterAndSetter(PrintWriter writter, OntoEntity entity) {

		ArrayList attributes = entity.getDefinedAttributes();
		OntoAttribute currentAttribute = null;
		EntityDatatype currentDatatype = null;
		for (int i = 0; i < attributes.size(); i++) {
			currentAttribute = (OntoAttribute) attributes.get(i);
			currentDatatype = currentAttribute.getRange();
			if (!currentAttribute.getName().equals("oid") && !entity.isRedefinedAttribute(currentAttribute)) {
				// the code generated depends on the datatype
				if (!currentDatatype.isAssociationType() && !currentDatatype.isCollectionAssociationType()) {
					if (currentAttribute instanceof OntoMultilingualAttribute) {
						generatePrimitiveMultilingualAttributeWithGetterAndSetter(writter, currentAttribute,
								currentDatatype);
					} else {
						generatePrimitiveAttributeWithGetterAndSetter(writter, currentAttribute, currentDatatype);
					}
				} else {
					generateRefOrCollectionAttributeWithGetterAndSetter(writter, entity, currentAttribute,
							currentDatatype);
				}
				writter.println();
			}
		}
	}

	/**
	 * Generate all the code required for a multilingual attribute
	 * 
	 * @param writter   writter in the file
	 * @param attribute the multilingual attribute
	 * @param datatype  the given entity
	 */
	private void generatePrimitiveMultilingualAttributeWithGetterAndSetter(PrintWriter writter, OntoAttribute attribute,
			EntityDatatype datatype) {

		String nameAttribute = attribute.getName();

		writter.println("\tprotected " + getJavaType(datatype) + " " + attribute.getName() + "_en;");
		writter.println("\tprotected " + getJavaType(datatype) + " " + attribute.getName() + "_fr;");
		writter.println();
		writter.println("\tpublic " + getJavaType(datatype) + " " + getGetter(nameAttribute) + "(String lg) {");
		writter.println("\t\tif (!isLoaded) {");
		writter.println("\t\t\tload();");
		writter.println("\t\t}");
		writter.println("\t\tif (lg.equals(ModelUtil.ENGLISH)) { ");
		writter.println("\t\t\treturn " + nameAttribute + "_en;");
		writter.println("\t\t}");
		writter.println("\t\t\telse {");
		writter.println("\t\t\treturn " + nameAttribute + "_fr;");
		writter.println("\t\t}");
		writter.println("\t}");
		writter.println();
		writter.println("\tpublic " + getJavaType(datatype) + " " + getGetter(nameAttribute) + "() {");
		writter.println("\t\treturn " + getGetter(nameAttribute) + "(session.getReferenceLanguage());");
		writter.println("\t}");
		writter.println();
		writter.println("\tpublic void " + getSetter(nameAttribute) + "(" + getJavaType(datatype) + " " + nameAttribute
				+ ", String lg) {");
		writter.println("\t\tif (lg.equals(ModelUtil.ENGLISH)) { ");
		writter.println("\t\tthis." + nameAttribute + "_en=" + nameAttribute + ";");
		writter.println("\t\t}");
		writter.println("\t\t\telse {");
		writter.println("\t\tthis." + nameAttribute + "_fr=" + nameAttribute + ";");
		writter.println("\t\t}");
		writter.println("\t}");
		writter.println();
		writter.println("\tpublic void " + getSetter(nameAttribute) + "(" + getJavaType(datatype) + " " + nameAttribute
				+ ") {");
		writter.println("\t\t" + getSetter(nameAttribute) + "(" + nameAttribute + ", session.getReferenceLanguage());");
		writter.println("\t}");
		writter.println();
	}

	/**
	 * Generate all the code required for a primitive attribute
	 * 
	 * @param writter   writter in the file
	 * @param attribute the primitive attribute
	 * @param datatype  the given entity
	 */
	private void generatePrimitiveAttributeWithGetterAndSetter(PrintWriter writter, OntoAttribute attribute,
			EntityDatatype datatype) {

		String nameAttribute = attribute.getName();

		writter.println("\tprotected " + getJavaType(datatype) + " " + attribute.getName() + ";");
		writter.println();
		writter.println("\tpublic " + getJavaType(datatype) + " " + getGetter(nameAttribute) + "() {");
		writter.println("\t\tif (!isLoaded) {");
		writter.println("\t\t\tload();");
		writter.println("\t\t}");
		writter.println("\t\treturn " + nameAttribute + ";");
		writter.println("\t}");
		writter.println();
		writter.println("\tpublic void " + getSetter(nameAttribute) + "(" + getJavaType(datatype) + " " + nameAttribute
				+ ") {");
		writter.println("\t\tthis." + nameAttribute + "=" + nameAttribute + ";");
		writter.println("\t}");
		writter.println();

	}

	/**
	 * /** Generate all the code required for a multilingual attribute
	 * 
	 * @param writter   writter in the file
	 * @param entity    a given entity
	 * @param attribute the multilingual attribute
	 * @param datatype  the given entity
	 */

	private void generateRefOrCollectionAttributeWithGetterAndSetter(PrintWriter writter, OntoEntity entity,
			OntoAttribute attribute, EntityDatatype datatype) {

		String nameAttribute = attribute.getName();
		String nameAttributeWithFirstLetterInUpperCase = StringHelper.firstLetterInUpperCase(nameAttribute);

		writter.println("\tprotected " + getJavaType(datatype) + " " + nameAttribute + ";");
		writter.println("\tprotected boolean isLoaded" + nameAttributeWithFirstLetterInUpperCase + ";");
		writter.println();
		writter.println("\tpublic " + getJavaType(datatype) + " " + getGetter(nameAttribute) + "() {");
		writter.println("\t\tif (!isLoaded" + nameAttributeWithFirstLetterInUpperCase + ") {");
		writter.println("\t\t\tload" + nameAttributeWithFirstLetterInUpperCase + "();");
		writter.println("\t\t}");
		writter.println("\t\treturn " + nameAttribute + ";");
		writter.println("\t}");
		writter.println();
		writter.println("\tpublic void " + getSetter(nameAttribute) + "(" + getJavaType(datatype) + " " + nameAttribute
				+ ") {");
		writter.println("\t\tthis." + nameAttribute + "=" + nameAttribute + ";");
		writter.println("\t}");
		writter.println();

		generateLoadMethodForAssociationOrRef(writter, entity, attribute, datatype);

	}

	/**
	 * Generate a method to load an association or a collection
	 */
	private void generateLoadMethodForAssociationOrRef(PrintWriter writter, OntoEntity entity, OntoAttribute attribute,
			EntityDatatype datatype) {

		String nameAttribute = attribute.getName();
		String nameAttributeWithFirstLetterInUpperCase = StringHelper.firstLetterInUpperCase(nameAttribute);

		writter.println("\tpublic void load" + nameAttributeWithFirstLetterInUpperCase + "() {");
		writter.println("\t\ttry {");
		writter.println("\t\t\tOntoQLStatement stmt = session.createOntoQLStatement();");
		;

		writter.print("\t\t\tOntoQLResultSet resultSet = stmt.executeQuery(\"");
		writter.print("SELECT #" + nameAttribute);
		writter.print(" FROM #" + entity.getName());
		writter.print(" WHERE #oid = \" + oid + \"");
		writter.println("\");");
		writter.println("\t\t\tif (resultSet.next()) {");
		writter.println(
				"\t\t\t\t" + getSetter(nameAttribute) + "(resultSet." + getGetter(getJavaType(datatype)) + "(1));\n");
		writter.println("\t\t\t\tisLoaded" + nameAttributeWithFirstLetterInUpperCase + "= true;");
		writter.println("\t\t\t} else {");
		writter.println("\t\t\t\tthrow new JOBDBCException(\"the instance of " + entity.getName()
				+ " with oid \" + getOid() + \" doesn't exist\");");
		writter.println("\t\t\t}");
		writter.println("\t\t} catch (SQLException e) {");
		writter.println("\t\t\tthrow new JOBDBCException(e);");
		writter.println("\t\t}");
		writter.println("\t}");
	}

	/**
	 * Get the java type corresponding to a datatype
	 */
	private String getJavaType(EntityDatatype dt) {
		String res = null;
		if (dt instanceof EntityDatatypeString) {
			res = "String";
		} else if (dt instanceof EntityDatatypeInt) {
			res = "int";
		} else if (dt instanceof EntityDatatypeCategory) {
			res = getName(((EntityDatatypeCategory) dt).getCagetory().getName());
		} else { // collection
			res = "Set";
		}
		return res;
	}

	/**
	 * Generate a method to load an ontology element
	 * 
	 * @param writter writter in the file
	 * @param entity  the ontology element instanciated
	 */
	private void generateLoadMethod(PrintWriter writter, OntoEntity entity) {

		writter.println("\tpublic void load() {");
		writter.println("\t\ttry {");
		writter.println("\t\t\tOntoQLStatement stmt = session.createOntoQLStatement();");
		;

		String completeSelectClause = "";
		String completeResClause = "";
		ArrayList attributes = entity.getApplicableAttributes();
		OntoAttribute currentAttribute = null;
		EntityDatatype currentDatatype = null;
		int j = 1;
		for (int i = 0; i < attributes.size(); i++) {
			currentAttribute = (OntoAttribute) attributes.get(i);
			currentDatatype = currentAttribute.getRange();
			if (!currentDatatype.isAssociationType() && !currentDatatype.isCollectionAssociationType()) {
				if (j != 1) {
					completeSelectClause += ", ";
				}
				if (!(currentAttribute instanceof OntoMultilingualAttribute)) {
					completeSelectClause += "#" + currentAttribute.getName();
					completeResClause += "\t\t\t\t" + getSetter(currentAttribute.getName()) + "(resultSet."
							+ getGetter(getJavaType(currentDatatype)) + "(" + j + "));\n";
				} else {
					completeSelectClause += "#" + currentAttribute.getName() + "[en]";
					completeSelectClause += ", #" + currentAttribute.getName() + "[fr]";
					completeResClause += "\t\t\t\t" + getSetter(currentAttribute.getName()) + "(resultSet."
							+ getGetter(getJavaType(currentDatatype)) + "(" + j + "), ModelUtil.ENGLISH);\n";
					j++;
					completeResClause += "\t\t\t\t" + getSetter(currentAttribute.getName()) + "(resultSet."
							+ getGetter(getJavaType(currentDatatype)) + "(" + j + "), ModelUtil.FRENCH);\n";
				}
				j++;
			}
		}

		String nameEntity = entity.getName();
		writter.print("\t\t\tString whereClause = \"#oid = \" + oid;\n");
		if (nameEntity.equals("class") || nameEntity.equals("property")) {
			writter.println("\t\t\tif (oid==0) {");
			writter.println("\t\t\t\tif (code!=null) {");
			writter.println("\t\t\t\t\twhereClause = \"#code = '\" + code + \"' AND #version = '\" + version + \"'\";");
			writter.println("\t\t\t\t}");
			writter.println("\t\t\t\telse {");
			writter.println("\t\t\t\t\tif (name_en != null) {");
			writter.println("\t\t\t\t\t\twhereClause = \"#name[en] = '\" + name_en + \"'\";");
			writter.println("\t\t\t\t\t}");
			writter.println("\t\t\t\t\telse {");
			writter.println("\t\t\t\t\t\twhereClause = \"#name[fr] = '\" + name_fr + \"'\"; ");
			writter.println("\t\t\t\t\t}");
			writter.println("\t\t\t\t}");
			writter.println("\t\t\t}");
			writter.println();
		}

		writter.print("\t\t\tOntoQLResultSet resultSet = stmt.executeQuery(\"");
		writter.print("SELECT " + completeSelectClause);
		writter.print(" FROM #" + nameEntity);
		writter.print(" WHERE \" + whereClause");
		writter.println(");");
		writter.println("\t\t\tif (resultSet.next()) {");
		writter.println(completeResClause);
		writter.println("\t\t\t\tisLoaded = true;");
		writter.println("\t\t\t} else {");
		writter.println("\t\t\t\tthrow new JOBDBCException(\"the instance of " + entity.getName()
				+ " with oid \" + oid + \" doesn't exist\");");
		writter.println("\t\t\t}");
		writter.println("\t\t} catch (SQLException e) {");
		writter.println("\t\t\tthrow new JOBDBCException(e);");
		writter.println("\t\t}");
		writter.println("\t}");
	}

	/**
	 * Get the directory correponding to a package name
	 * 
	 * @param packageName a given package name
	 * @return the directory as a File
	 * @throws Exception
	 */
	private File getDir(String packageName) throws Exception {
		File baseDir = new File(BASE_DIR_SOURCE);
		File dir = null;

		dir = new File(baseDir, packageName.replace('.', File.separatorChar));

		// if the directory exists, make sure it is a directory
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new Exception("The path: " + dir.getCanonicalPath() + " exists, but is not a directory");
			}
		} // else make the directory and any non-existent parent directories
		else {
			throw new Exception("unable to create directory: " + dir.getCanonicalPath());
		}

		return dir;
	}

	/**
	 * Return the name of the java file
	 */
	private String getFileName(String className) {
		return this.getName(className) + "." + JAVA_EXTENSION;
	}

	/**
	 * Return the name of an OntoAPI class
	 */
	private String getName(String className) {
		String name = null;
		name = StringHelper.firstLetterInUpperCase(className);
		return this.PREFIX_ONTOAPI_CLASS + name;
	}

	/**
	 * Generate the getter or setter method name for a given name of attribute
	 */
	private String getGetterOrSetter(String name, boolean isGetter) {
		String res = isGetter ? "get" : "set";
		res = res + StringHelper.firstLetterInUpperCase(name);
		return res;
	}

	private String getGetter(String name) {
		return getGetterOrSetter(name, true);
	}

	private String getSetter(String name) {
		return getGetterOrSetter(name, false);
	}

}

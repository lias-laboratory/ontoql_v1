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
package fr.ensma.lisi.ontoql.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * The base class of all runtime exceptions which can contain other exceptions.
 *
 * @author Stéphane JEAN
 */
public class NestableRuntimeException extends RuntimeException implements Nestable {

	private static final long serialVersionUID = -5497824503146801651L;

	/**
	 * The helper instance which contains much of the code which we delegate to.
	 */
	protected NestableDelegate delegate = new NestableDelegate(this);

	/**
	 * Holds the reference to the exception or error that caused this exception to
	 * be thrown.
	 */
	private Throwable cause = null;

	/**
	 * Constructs a new <code>NestableRuntimeException</code> without specified
	 * detail message.
	 */
	public NestableRuntimeException() {
		super();
	}

	/**
	 * Constructs a new <code>NestableRuntimeException</code> with specified detail
	 * message.
	 * 
	 * @param msg the error message
	 */
	public NestableRuntimeException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a new <code>NestableRuntimeException</code> with specified nested
	 * <code>Throwable</code>.
	 * 
	 * @param cause the exception or error that caused this exception to be thrown
	 */
	public NestableRuntimeException(Throwable cause) {
		super();
		this.cause = cause;
	}

	/**
	 * Constructs a new <code>NestableRuntimeException</code> with specified detail
	 * message and nested <code>Throwable</code>.
	 * 
	 * @param msg   the error message
	 * @param cause the exception or error that caused this exception to be thrown
	 */
	public NestableRuntimeException(String msg, Throwable cause) {
		super(msg);
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

	/**
	 * Returns the detail message string of this throwable. If it was created with a
	 * null message, returns the following: ( cause==null ? null : cause.toString(
	 * ).
	 */
	public String getMessage() {
		if (super.getMessage() != null) {
			return super.getMessage();
		} else if (cause != null) {
			return cause.toString();
		} else {
			return null;
		}
	}

	public String getMessage(int index) {
		if (index == 0) {
			return super.getMessage();
		} else {
			return delegate.getMessage(index);
		}
	}

	public String[] getMessages() {
		return delegate.getMessages();
	}

	public Throwable getThrowable(int index) {
		return delegate.getThrowable(index);
	}

	public int getThrowableCount() {
		return delegate.getThrowableCount();
	}

	public Throwable[] getThrowables() {
		return delegate.getThrowables();
	}

	public int indexOfThrowable(Class type) {
		return delegate.indexOfThrowable(type, 0);
	}

	public int indexOfThrowable(Class type, int fromIndex) {
		return delegate.indexOfThrowable(type, fromIndex);
	}

	public void printStackTrace() {
		delegate.printStackTrace();
	}

	public void printStackTrace(PrintStream out) {
		delegate.printStackTrace(out);
	}

	public void printStackTrace(PrintWriter out) {
		delegate.printStackTrace(out);
	}

	public final void printPartialStackTrace(PrintWriter out) {
		super.printStackTrace(out);
	}
}

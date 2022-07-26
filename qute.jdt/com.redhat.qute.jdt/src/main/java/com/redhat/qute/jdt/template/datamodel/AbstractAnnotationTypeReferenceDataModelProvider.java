/*******************************************************************************
* Copyright (c) 2022 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.redhat.qute.jdt.template.datamodel;

import static com.redhat.qute.jdt.utils.AnnotationUtils.isMatchAnnotation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeReferenceMatch;

/**
 * Abstract class for data model provider based on annotation search.
 *
 * @author Angelo ZERR
 *
 */
public abstract class AbstractAnnotationTypeReferenceDataModelProvider extends AbstractDataModelProvider {

	private static final Logger LOGGER = Logger
			.getLogger(AbstractAnnotationTypeReferenceDataModelProvider.class.getName());

	@Override
	protected String[] getPatterns() {
		return getAnnotationNames();
	}

	/**
	 * Returns the annotation names to search.
	 *
	 * @return the annotation names to search.
	 */
	protected abstract String[] getAnnotationNames();

	@Override
	protected SearchPattern createSearchPattern(String annotationName) {
		return createAnnotationTypeReferenceSearchPattern(annotationName);
	}

	@Override
	public void collectDataModel(SearchMatch match, SearchContext context, IProgressMonitor monitor) {
		IJavaElement javaElement = null;
		try {
			Object element = getMatchedElement(match);
			if (element instanceof IAnnotation) {
				// ex : for Local variable
				IAnnotation annotation = ((IAnnotation) element);
				javaElement = annotation.getParent();
				processAnnotation(javaElement, context, monitor, annotation);
			} else if (element instanceof IAnnotatable && element instanceof IJavaElement) {
				javaElement = (IJavaElement) element;
				processAnnotation(javaElement, context, monitor);
			}
		} catch (Exception e) {
			if (LOGGER.isLoggable(Level.SEVERE)) {
				LOGGER.log(Level.SEVERE,
						"Cannot collect Qute data model for the Java element '" + javaElement != null
								? javaElement.getElementName()
								: match.getElement() + "'.",
						e);
			}
		}
	}

	/**
	 * Return the element associated with the given <code>match</code> and null
	 * otherwise
	 *
	 * @param match the match
	 * @return
	 */
	private static Object getMatchedElement(SearchMatch match) {
		if (match instanceof TypeReferenceMatch) {

			// localElement exists if matched element is a
			// local variable (constructor/method parameter)
			Object localElement = ((TypeReferenceMatch) match).getLocalElement();
			return localElement != null ? localElement : match.getElement();
		}
		return match.getElement();
	}

	/**
	 * Processes the annotations bound to the current <code>javaElement</code> and
	 * adds item metadata if needed
	 *
	 * @param javaElement the Java element
	 * @param context     the context
	 * @param monitor     the monitor
	 * @throws JavaModelException
	 */
	protected void processAnnotation(IJavaElement javaElement, SearchContext context, IProgressMonitor monitor)
			throws JavaModelException {
		IAnnotation[] annotations = ((IAnnotatable) javaElement).getAnnotations();
		for (IAnnotation annotation : annotations) {
			processAnnotation(javaElement, context, monitor, annotation);
		}
	}

	/**
	 * Processes the current <code>annotation</code> bound to current
	 * <code>javaElement</code> and adds item metadata if needed
	 *
	 * @param javaElement the Java element
	 * @param context     the context
	 * @param monitor     the monitor
	 * @param annotation  the annotation
	 * @throws JavaModelException
	 */
	private void processAnnotation(IJavaElement javaElement, SearchContext context, IProgressMonitor monitor,
			IAnnotation annotation) throws JavaModelException {
		String[] names = getAnnotationNames();
		for (String annotationName : names) {
			if (isMatchAnnotation(annotation, annotationName)) {
				processAnnotation(javaElement, annotation, annotationName, context, monitor);
				break;
			}
		}
	}

	protected abstract void processAnnotation(IJavaElement javaElement, IAnnotation annotation, String annotationName,
			SearchContext context, IProgressMonitor monitor) throws JavaModelException;

}

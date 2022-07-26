/*******************************************************************************
* Copyright (c) 2021 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.redhat.qute.jdt.internal.template;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Range;

import com.redhat.qute.commons.datamodel.DataModelParameter;
import com.redhat.qute.commons.datamodel.DataModelTemplate;
import com.redhat.qute.jdt.utils.IJDTUtils;

/**
 * AST visitor used to collect {@link DataModelParameter} parameter for a given
 * {@link DataModelTemplate} template.
 * 
 * This visitor track the invocation of method
 * io.quarkus.qute.Template#data(String key, Object data) to collect parameters.
 * 
 * For instance, with this following code:
 * 
 * <code>
 * private final Template page;
 * ...
 * page.data("age", 13);
   page.data("name", "John");
 * </code>
 * 
 * the AST visitor will collect the following parameters:
 * 
 * <ul>
 * <li>parameter key='age', sourceType='int'</li>
 * <li>parameter key='name', sourceType='java.lang.String'</li>
 * </ul>
 * 
 * @author Angelo ZERR
 *
 */
public class TemplateDataLocation extends TemplateDataVisitor {

	private final String parameterName;

	private final IJDTUtils utils;

	private Location location;

	public TemplateDataLocation(String parameterName, IJDTUtils utils) {
		this.parameterName = parameterName;
		this.utils = utils;
	}

	@Override
	protected boolean visitParameter(Object paramName, Object paramType) {
		if (paramName instanceof StringLiteral) {
			StringLiteral literal = ((StringLiteral) paramName);
			String paramNameString = literal.getLiteralValue();
			if (parameterName.equals(paramNameString)) {
				try {
					Range range = utils.toRange(getMethod().getOpenable(), literal.getStartPosition(),
							literal.getLength());
					String uri = utils.toUri(getMethod().getTypeRoot());
					this.location = new Location(uri, range);
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		}
		return true;
	}

	public Location getLocation() {
		return location;
	}
}

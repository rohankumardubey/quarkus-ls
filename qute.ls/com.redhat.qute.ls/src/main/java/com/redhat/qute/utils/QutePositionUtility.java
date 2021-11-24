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
package com.redhat.qute.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Range;

import com.redhat.qute.ls.commons.BadLocationException;
import com.redhat.qute.parser.template.Expression;
import com.redhat.qute.parser.template.Node;
import com.redhat.qute.parser.template.Parameter;
import com.redhat.qute.parser.template.ParameterDeclaration;
import com.redhat.qute.parser.template.RangeOffset;
import com.redhat.qute.parser.template.Section;
import com.redhat.qute.parser.template.Template;

public class QutePositionUtility {

	private static final Logger LOGGER = Logger.getLogger(QutePositionUtility.class.getName());

	public static Location toLocation(LocationLink locationLink) {
		return new Location(locationLink.getTargetUri(), locationLink.getTargetRange());
	}

	public static Range selectStartTagName(Section section) {
		Template template = section.getOwnerTemplate();
		int startOffset = section.getStartTagOpenOffset(); // {#
		int endOffset = section.getAfterStartTagOpenOffset(); // {#each
		return createRange(startOffset, endOffset, template);
	}

	public static Range selectEndTagName(Section sectionTag) {
		Template template = sectionTag.getOwnerTemplate();
		int startOffset = sectionTag.getEndTagOpenOffset() + 2; // {\
		int endOffset = startOffset + sectionTag.getTag().length();
		return createRange(startOffset, endOffset, template);
	}

	public static Range selectClassName(ParameterDeclaration parameter) {
		Template template = parameter.getOwnerTemplate();
		int startOffset = parameter.getClassNameStart();
		int endOffset = parameter.getClassNameEnd();
		return createRange(startOffset, endOffset, template);
	}

	public static Range selectAlias(ParameterDeclaration parameter) {
		Template template = parameter.getOwnerTemplate();
		int startOffset = parameter.getAliasStart();
		int endOffset = parameter.getAliasEnd();
		return createRange(startOffset, endOffset, template);
	}

	public static Range selectParameterName(Parameter parameter) {
		Template template = parameter.getOwnerTemplate();
		int startOffset = parameter.getStartName();
		int endOffset = parameter.getEndName();
		return createRange(startOffset, endOffset, template);
	}

	public static Range createRange(Node node) {
		Template template = node.getOwnerTemplate();
		return createRange(node.getStart(), node.getEnd(), template);
	}

	public static Range createRange(int startOffset, int endOffset, Template template) {
		try {
			return new Range(template.positionAt(startOffset), template.positionAt(endOffset));
		} catch (BadLocationException e) {
			LOGGER.log(Level.SEVERE, "While creating Range the Offset was a BadLocation", e);
			return null;
		}
	}

	public static Range createRange(RangeOffset range, Template template) {
		return createRange(range.getStart(), range.getEnd(), template);
	}

	public static Node findBestNode(int offset, Node node) {
		switch (node.getKind()) {
		case Section: {
			Section section = (Section) node;
			if (section.isInParameters(offset)) {
				Expression expression = null;
				Parameter parameter = section.getParameterAtOffset(offset);
				if (parameter != null) {
					if (parameter.hasValueAssigned() && !parameter.isInValue(offset)) {
						// ex : {#let nam|e=value }
						return parameter;
					}
					if (!parameter.hasValueAssigned() && !parameter.isCanHaveExpression()) {
						// ex : {#for it|em in items }
						return parameter;
					}
					// ex : {#let name=va|lue }
					// ex : {#for item in ite|ms }
					expression = parameter.getJavaTypeExpression();
				}
				if (expression == null) {
					expression = section.getExpressionParameter();
				}
				if (expression != null) {
					Node expressionNode = expression.findNodeExpressionAt(offset);
					if (expressionNode != null) {
						return expressionNode;
					}
					return expression;
				}
			}
		}
			break;
		case Expression: {
			Expression expression = (Expression) node;
			Node expressionNode = expression.findNodeExpressionAt(offset);
			if (expressionNode != null) {
				return expressionNode;
			}
		}
			break;
		default:
			return node;
		}
		return node;
	}

	/**
	 * Returns the location for the given <code>target</code> node.
	 *
	 * @param target the target node.
	 * @return the location for the given <code>target</code> node.
	 */
	public static Location createLocation(Node target) {
		Template targetDocument = target.getOwnerTemplate();
		Range targetRange = createRange(target.getStart(), target.getEnd(), targetDocument);
		return new Location(targetDocument.getUri(), targetRange);
	}

}
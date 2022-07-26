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
package com.redhat.qute.parser.template.sections;

import com.redhat.qute.parser.template.ASTVisitor;
import com.redhat.qute.parser.template.SectionKind;

/**
 * Case section AST node.
 * 
 * <code>
 	{#switch person.name}
  		{#case 'John'} 
    		Hey John!
  		{#case 'Mary'}
    		Hey Mary!
	{/switch}
 * </code>
 * 
 * @author Angelo ZERR
 * 
 * @see https://quarkus.io/guides/qute-reference#when_section
 *
 */
public class CaseSection extends AssignSection {

	public static final String TAG = "case";

	public CaseSection(int start, int end) {
		super(TAG, start, end);
	}

	@Override
	public SectionKind getSectionKind() {
		return SectionKind.CASE;
	}

	@Override
	protected void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
}

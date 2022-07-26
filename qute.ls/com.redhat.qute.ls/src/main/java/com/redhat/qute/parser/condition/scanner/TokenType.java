/*******************************************************************************
* Copyright (c) 2022 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.redhat.qute.parser.condition.scanner;

/**
 * Token type for condition expression.
 * 
 * @author Angelo ZERR
 *
 */
public enum TokenType {

	StartBracketCondition, //
	EndBracketCondition, //
	// Other token types
	Unknown, //
	EOS;
}

/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
* 
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.redhat.qute.ls.commons.snippets;

/**
 * Loader used to load snippets in a given registry for a language id.
 * 
 * @author Angelo ZERR
 *
 */
public interface ISnippetRegistryLoader<T extends Snippet> {

	/**
	 * Register snippets in the given snippet registry.
	 * 
	 * @param registry
	 * @throws Exception
	 */
	void load(SnippetRegistry<T> registry) throws Exception;

	/**
	 * Returns the language id and null otherwise.
	 * 
	 * @return the language id and null otherwise.
	 */
	default String getLanguageId() {
		return null;
	}
}

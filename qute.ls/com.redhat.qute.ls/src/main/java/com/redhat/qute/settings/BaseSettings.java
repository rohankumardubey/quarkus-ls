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
package com.redhat.qute.settings;

/**
 * Base class for settings.
 *
 * @author Angelo ZERR
 *
 */
public class BaseSettings {

	private final QuteCodeLensSettings codeLensSettings;

	private final QuteInlayHintSettings inlayHintSettings;

	private final QuteValidationSettings validationSettings;

	private final QuteNativeSettings nativeImagesSettings;

	public BaseSettings() {
		this.validationSettings = new QuteValidationSettings();
		this.codeLensSettings = new QuteCodeLensSettings();
		this.inlayHintSettings = new QuteInlayHintSettings();
		this.nativeImagesSettings = new QuteNativeSettings();
	}

	/**
	 * Returns the Qute CodeLens settings.
	 *
	 * @return the Qute CodeLens settings.
	 */
	public QuteCodeLensSettings getCodeLensSettings() {
		return codeLensSettings;
	}

	/**
	 * Returns the inlay hint settings.
	 * 
	 * @return the inlay hint settings.
	 */
	public QuteInlayHintSettings getInlayHintSettings() {
		return inlayHintSettings;
	}

	/**
	 * Returns the Qute validation settings.
	 *
	 * @return the Qute validation settings.
	 */
	public QuteValidationSettings getValidationSettings() {
		return validationSettings;
	}

	/**
	 * Returns the Qute native images settings.
	 * 
	 * @return the Qute native images settings.
	 */
	public QuteNativeSettings getNativeSettings() {
		return nativeImagesSettings;
	}
}

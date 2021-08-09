/*******************************************************************************
* Copyright (c) 2021 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.redhat.microprofile.jdt.internal.quarkus.providers;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4mp.jdt.core.project.AbstractConfigSource;
import org.eclipse.lsp4mp.jdt.core.project.MicroProfileConfigPropertyInformation;
import org.yaml.snakeyaml.Yaml;

import com.redhat.microprofile.jdt.internal.quarkus.utils.YamlUtils;

/**
 * Yaml config source implementation
 *
 * @author dakwon
 *
 */
public class YamlConfigSource extends AbstractConfigSource<Map<String, Object>> {

	public YamlConfigSource(String configFileName, IJavaProject javaProject) {
		super(configFileName, javaProject);
	}

	@Override
	protected Map<String, Object> loadConfig(InputStream input) throws IOException {
		Yaml yaml = new Yaml();
		return (Map<String, Object>) yaml.load(input);
	}

	@Override
	protected String getProperty(String key, Map<String, Object> config) {
		String[] keyArray = key.split("\\.");
		Map<String, Object> curr = config;

		Object value;
		for (int i = 0; i < keyArray.length - 1; i++) {
			value = curr.get(keyArray[i]);
			if (value == null || value instanceof String) {
				return null;
			}

			curr = (Map<String, Object>) value;
		}

		value = curr.get(keyArray[keyArray.length - 1]);
		if (value instanceof Map) {
			// In this case:
			//
			// cors:
			// ~: true
			//
			// map.get(null) returns the value of ~
			value = ((Map<String, Object>) value).get(null);
		}

		if (value == null) {
			return null;
		}

		return String.valueOf(value);
	}

	@Override
	public Map<String, MicroProfileConfigPropertyInformation> getPropertyInformations(String propertyKey,
			Map<String, Object> config) {

		Map<String, MicroProfileConfigPropertyInformation> result = new HashMap<>();

		if (config == null) {
			return result;
		}

		final List<String> segments = MicroProfileConfigPropertyInformation.getSegments(propertyKey);
		if (segments.size() < 1) {
			return result;
		}

		for (String key : config.keySet()) {
			if (key.equals(segments.get(0))) {
				String value = YamlUtils.getValueRecursively(segments, config);
				if (value != null) {
					result.put(propertyKey, new MicroProfileConfigPropertyInformation(propertyKey, value,
							getSourceConfigFileURI(), getConfigFileName()));
				}
			} else if (key.charAt(0) == '%') {
				if (config.get(key) instanceof Map) {
					String value = YamlUtils.getValueRecursively(segments, config.get(key));
					if (value != null) {
						String propertyAndProfile = key + "." + propertyKey;
						result.put(propertyAndProfile, new MicroProfileConfigPropertyInformation(propertyAndProfile,
								value, getSourceConfigFileURI(), getConfigFileName()));
					}
				}
			}
		}

		return result;
	}

	@Override
	public int getOrdinal() {
		// See https://github.com/quarkusio/quarkus/blob/main/extensions/config-yaml/runtime/src/main/java/io/quarkus/config/yaml/runtime/ApplicationYamlConfigSourceLoader.java#L29
		// (or Quarkus --> yaml config extension --> ApplicationYamlConfigSourceLoader if the link is dead)
		return 255;
	}

}
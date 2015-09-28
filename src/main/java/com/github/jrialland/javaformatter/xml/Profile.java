/* (c) 2015 Julien Rialland
 */
package com.github.jrialland.javaformatter.xml;

import java.util.HashMap;
import java.util.Map;

public class Profile {

	private String kind;
	private Map<String, String> settings = new HashMap<>();

	public Profile() {
	}

	public void addSetting(Setting setting) {
		settings.put(setting.getId(), setting.getValue());
	}

	public Map<String, String> getSettings() {
		return settings;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}

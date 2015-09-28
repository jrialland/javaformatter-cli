package com.github.jrialland.javaformatter.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Profiles {

	public static final String PROFILE_KIND = "CodeFormatterProfile";

	private List<Map<String, String>>               profiles     = new ArrayList<>();

	public Profiles() {
	}

	public void addProfile(Profile profile) {
		if (PROFILE_KIND.equals(profile.getKind())) {
			profiles.add(profile.getSettings());
		}
	}

	public List<Map<String, String>> getProfiles() {
		return profiles;
	}
}
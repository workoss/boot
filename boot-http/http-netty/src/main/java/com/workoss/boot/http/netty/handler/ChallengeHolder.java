package com.workoss.boot.http.netty.handler;

import java.util.List;
import java.util.Map;

public class ChallengeHolder {

	private final boolean hasBasicChallenge;

	private final List<Map<String,String>> digestChallenges;

	public ChallengeHolder(boolean hasBasicChallenge, List<Map<String, String>> digestChallenges) {
		this.hasBasicChallenge = hasBasicChallenge;
		this.digestChallenges = digestChallenges;
	}

	public boolean hasBasicChallenge() {
		return hasBasicChallenge;
	}

	public List<Map<String, String>> getDigestChallenges() {
		return digestChallenges;
	}
}

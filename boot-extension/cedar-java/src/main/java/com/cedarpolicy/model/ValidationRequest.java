/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cedarpolicy.model;

import com.cedarpolicy.model.schema.Schema;
import com.cedarpolicy.model.slice.Policy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Information passed to Cedar for validation. */
public final class ValidationRequest {

	private final Schema schema;

	private final Map<String, String> policySet;

	/**
	 * Construct a validation request.
	 * @param schema Schema for the request
	 * @param policySet Map of Policy ID to policy.
	 */
	public ValidationRequest(Schema schema, Map<String, String> policySet) {
		if (schema == null) {
			throw new NullPointerException("schema");
		}

		if (policySet == null) {
			throw new NullPointerException("policySet");
		}

		this.schema = schema;
		this.policySet = policySet;
	}

	public ValidationRequest(Schema schema, Set<Policy> policySet) {
		if (schema == null) {
			throw new NullPointerException("schema");
		}

		if (policySet == null) {
			throw new NullPointerException("policySet");
		}

		this.schema = schema;
		this.policySet = new HashMap<>();
		for (Policy p : policySet) {
			this.policySet.put(p.policyID, p.policySrc);
		}
	}

	/**
	 * Get the schema.
	 * @return The schema.
	 */
	public Schema getSchema() {
		return this.schema;
	}

	/**
	 * Get the policy set.
	 * @return Map of policy ID to policy.
	 */
	public Map<String, String> getPolicySet() {
		return this.policySet;
	}

	/** Test equality. */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof ValidationRequest)) {
			return false;
		}

		final ValidationRequest other = (ValidationRequest) o;
		return schema.equals(other.schema) && policySet.equals(other.policySet);
	}

	/** Hash. */
	@Override
	public int hashCode() {
		return Objects.hash(schema, policySet);
	}

	/** Get readable string representation. */
	public String toString() {
		return "ValidationRequest(schema=" + schema + ", policySet=" + policySet + ")";
	}

}

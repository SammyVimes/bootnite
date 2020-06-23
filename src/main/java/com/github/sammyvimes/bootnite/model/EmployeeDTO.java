package com.github.sammyvimes.bootnite.model;

import java.io.Serializable;
import java.util.UUID;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class EmployeeDTO implements Serializable {

	@QuerySqlField(index = true)
	private UUID id;

	@QuerySqlField(index = true)
	private String name;

	@QuerySqlField(index = true)
	private boolean isEmployed;

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean isEmployed() {
		return isEmployed;
	}

	public void setEmployed(final boolean employed) {
		isEmployed = employed;
	}
}

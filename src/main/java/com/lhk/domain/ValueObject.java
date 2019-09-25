package com.lhk.domain;

import com.google.gson.Gson;

import java.io.Serializable;


public abstract class ValueObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Gson gson = new Gson();

	public ValueObject() {
		super();
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}

}

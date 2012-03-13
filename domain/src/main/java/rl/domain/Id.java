package rl.domain;

import java.io.Serializable;

public class Id implements Serializable {

	private int id;

	public Id() {
	}

	public Id(final int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

}

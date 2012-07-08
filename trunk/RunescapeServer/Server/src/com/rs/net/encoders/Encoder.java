package com.rs.net.encoders;

import com.rs.net.Session;

public abstract class Encoder {

	public Session session;

	public Encoder(Session session) {
		this.session = session;
	}

}

package de.spiritaner.maz.model;

import java.beans.Transient;

public abstract class PartialVolatileEntity {

	private boolean alreadyInitialized = false;

	@Transient
	public synchronized boolean initVolatiles() {
		if(alreadyInitialized) {
			return false;
		} else {
			initialize();
			alreadyInitialized = true;
			return true;
		}
	}

	protected abstract void initialize();
}

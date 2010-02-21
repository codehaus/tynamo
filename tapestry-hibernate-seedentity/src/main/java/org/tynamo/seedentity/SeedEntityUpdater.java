package org.tynamo.seedentity;

public final class SeedEntityUpdater {
	private Object originalEntity;
	private Object updatedEntity;

	public SeedEntityUpdater(Object originalEntity, Object updatedEntity) {
		this.originalEntity = originalEntity;
		this.updatedEntity = updatedEntity;
	}

	public Object getOriginalEntity() {
		return originalEntity;
	}

	public Object getUpdatedEntity() {
		return updatedEntity;
	}

}

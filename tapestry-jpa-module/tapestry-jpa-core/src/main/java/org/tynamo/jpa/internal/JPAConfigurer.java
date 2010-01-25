package org.tynamo.jpa.internal;

public final class JPAConfigurer {

    private final String persistenceUnit;

    public JPAConfigurer(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }
}

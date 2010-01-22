package org.tynamo.jpa2.internal;

public final class JPAConfigurer {

    private final String persistenceUnit;

    public JPAConfigurer(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }
}

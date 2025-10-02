package com.metalix.metalixbackend.shared.infrastructure.persistence.jpa.configuration.strategy;

import io.github.encryptorcode.pluralize.Pluralize;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class SnakeCaseWithPluralizedTablePhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        if (identifier == null) {
            return null;
        }
        String tableName = toSnakeCase(identifier.getText());
        String pluralizedTableName = Pluralize.pluralize(tableName);
        return Identifier.toIdentifier(pluralizedTableName);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        if (identifier == null) {
            return null;
        }
        return Identifier.toIdentifier(toSnakeCase(identifier.getText()));
    }

    private String toSnakeCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(text.charAt(0)));
        
        for (int i = 1; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                result.append('_');
                result.append(Character.toLowerCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }
        
        return result.toString();
    }
}


-- liquibase formatted sql
-- changeset DimaCat:1
CREATE TABLE volunteers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    working BOOLEAN NOT NULL
);
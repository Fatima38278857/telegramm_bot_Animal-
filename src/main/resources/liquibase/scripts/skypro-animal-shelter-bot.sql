-- liquibase formatted sql
-- changeset DimaCat:1
-- Создание таблицы pets
CREATE TABLE pets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    species VARCHAR(50) NOT NULL,
    age INT NOT NULL
);

-- Создание таблицы volunteers
CREATE TABLE volunteers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Not Working',
    pet_id INT,
    trial_period_start DATE,
    trial_period_end DATE,
    report_approved BOOLEAN DEFAULT false,
    comments VARCHAR(255),
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE SET NULL
);
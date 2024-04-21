-- liquibase formatted sql

-- changeset DimaCat:1
CREATE TABLE volunteers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Not Working', -- Статус работы: "Working" или "Not Working"
    pet_id INT, -- ID животного, которое назначено усыновителю
    trial_period_start DATE, -- Дата начала испытательного срока
    trial_period_end DATE, -- Дата окончания испытательного срока
    report_approved BOOLEAN DEFAULT false, -- Проверен ли отчет усыновителя
    comments VARCHAR(255), -- Замечания усыновителю
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE SET NULL -- Ссылка на таблицу с животными
);

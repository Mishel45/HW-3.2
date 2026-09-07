CREATE TABLE car (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    price NUMERIC(12, 2) NOT NULL
);

CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    age INT CHECK (age >= 0),
    has_driver_license BOOLEAN NOT NULL DEFAULT FALSE,
    car_id BIGINT,
    CONSTRAINT fk_person_car FOREIGN KEY (car_id) REFERENCES car(id) ON DELETE SET NULL
);
CREATE TABLE sensor_data_weights (
    id integer PRIMARY KEY,
    weight numeric NOT NULL,
    FOREIGN KEY (id) REFERENCES sensor_data(id) ON DELETE CASCADE
);
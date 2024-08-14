CREATE TABLE sensor_data_weights (
    id integer PRIMARY KEY,
    weight numeric NOT NULL,
    FOREIGN KEY (id) REFERENCES sensor_data(id) ON DELETE CASCADE
);
CREATE INDEX sensor_data_weights_weight_idx ON sensor_data_weights(weight);
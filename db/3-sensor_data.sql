CREATE TABLE sensor_data (
    id SERIAL PRIMARY KEY,
    device_id INT NOT NULL,
    sensor_name VARCHAR(32) NOT NULL,
    sensor_value numeric NOT NULL,
    received_at timestamp with time zone NOT NULL,
    FOREIGN KEY (device_id) REFERENCES devices(device_id) ON DELETE CASCADE
);
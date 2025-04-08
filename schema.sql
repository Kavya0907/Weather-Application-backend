--database: weather_db
CREATE TABLE weather_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255),
    start_date DATE,
    end_date DATE,
    temperature DOUBLE,
    weather_description VARCHAR(255),
    humidity DOUBLE,
    wind_speed DOUBLE,
    request_date DATETIME,
    weather_icon VARCHAR(255)
);
INSERT INTO users (`first_name`, `last_name`, `email`) VALUES
    ('Tom', 'Holland', 'tom.holand@yopmail.com'),
    ('Robert', 'Downey', 'iron.man@yopmail.com');

INSERT INTO vehicles (name, price_per_day, owner_id) VALUES
    ('Twingo', 15.50, 1);

INSERT INTO reservations (start_date, end_date, customer_id, vehicle_id) VALUES
    ('2024-06-03', '2024-08-02', 2, 1);

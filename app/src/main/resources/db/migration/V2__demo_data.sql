INSERT INTO station (id, city) VALUES
    (1, 'Москва'),
    (2, 'Калининград'),
    (3, 'Санкт-Петербург'),
    (4, 'Псков');

INSERT INTO customer (customer_number, first_name, last_name) VALUES
    (111111, 'Василий', 'Пупкин'),
    (222222, 'Владислав', 'Иванов'),
    (333333, 'Вячеслав', 'Иванов'),
    (444444, 'Илон', 'Макс');

INSERT INTO house (house_nr, price, street, model, station_id) VALUES
    ('Помещение 1', 20000, 'Ленинградский пр-т', '1', 1),
    ('Помещение 2', 25000, 'Краснопрудная 49', '2', 2),
    ('Помещение 3', 20000, 'Московский пр-т', '1', 3),
    ('Помещение 4', 30000, 'Московский пр-т', '3', 1),
    ('Помещение 5', 20000, 'Левитана', '1', 3),
    ('Помещение 6', 20000, 'Грига', '1', 2),
    ('Дом 1', 40000, 'Клубничная 39', '4', 2),
    ('Дом 2', 40000, 'Исаковская', '3', 3),
    ('Дом 3', 50000, 'Римская', '7', 2),
    ('Дом 4', 35000, 'Рижская', '3', 1);

INSERT INTO rental (rental_date, house_nr, driver_customer_number, rental_station_id, return_date, return_station_id, km) VALUES
    (CURRENT_DATE, 'Дом 1', 111111, 1, CURRENT_DATE, 2, 500),
    (CURRENT_DATE, 'Дом 2', 222222, 3, CURRENT_DATE, 2, 10000),
    (CURRENT_DATE, 'Дом 3', 333333, 3, CURRENT_DATE, 2, 500),
    (CURRENT_DATE, 'Дом 4', 111111, 1, CURRENT_DATE, 2, 500),
    (CURRENT_DATE, 'Помещение 1', 111111, 1, NULL, NULL, NULL);

ALTER TABLE station ALTER COLUMN id RESTART WITH 5;

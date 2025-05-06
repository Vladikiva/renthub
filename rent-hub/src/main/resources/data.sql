insert into station (city)
values ('Москва');
insert into station (city)
values ('Калининград');
insert into station (city)
values ('Санкт-Петербург');
insert into station (city)
values ('Псков');
insert into house (house_nr, price, street, model, station_id)
values ('Помещение 1', 20000, 'Ленинградский пр-т', '1', 1);
insert into house (house_nr, price, street, model, station_id)
values ('Помещение 2', 25000, 'Краснопрудная 49', '2', 2);
insert into house (house_nr, price, street, model, station_id)
values ('Помещение 3', 20000, 'Московский пр-т', '1', 3);
insert into house (house_nr, price, street, model, station_id)
values ('Помещение 4', 30000, 'Московский пр-т', '3', 1);
insert into house (house_nr, price, street, model, station_id)
values ('Помещение 5', 20000, 'Левитана', '1', 3);
insert into house (house_nr, price, street, model, station_id)
values ('Помещение 6', 20000, 'Грига', '1', 2);
insert into house (house_nr, price, street, model, station_id)
values ('Дом 1', 40000, 'Клубничная 39', '4', 2);
insert into house (house_nr, price, street, model, station_id)
values ('Дом 2', 40000, 'Исаковская', '3', 3);
insert into house (house_nr, price, street, model, station_id)
values ('Дом 3', 50000, 'Римская', '7', 2);
insert into house (house_nr, price, street, model, station_id)
values ('Дом 4', 35000, 'Рижская', '3', 1);
insert into customer (customer_number, first_name, last_name)
values (111111, 'Василий', 'Пупкин');
insert into customer (customer_number, first_name, last_name)
values (222222, 'Владислав', 'Иванов');
insert into customer (customer_number, first_name, last_name)
values (333333, 'Вячеслав', 'Иванов');
insert into customer (customer_number, first_name, last_name)
values (444444, 'Илон', 'Макс');
insert into rental (
        rental_date,
        house_house_nr,
        driver_customer_number,
        rental_station_id,
        return_date,
        return_station_id,
        km
    )
values (
        CURRENT_DATE(),
        'Дом 1',
        111111,
        1,
        CURRENT_DATE(),
        2,
        500
    );
insert into rental (
        rental_date,
        house_house_nr,
        driver_customer_number,
        rental_station_id,
        return_date,
        return_station_id,
        km
    )
values (
        CURRENT_DATE(),
        'Дом 2',
        222222,
        3,
        CURRENT_DATE(),
        2,
        10000
    );
insert into rental (
        rental_date,
        house_house_nr,
        driver_customer_number,
        rental_station_id,
        return_date,
        return_station_id,
        km
    )
values (
        CURRENT_DATE(),
        'Дом 3',
        333333,
        3,
        CURRENT_DATE(),
        2,
        500
    );
insert into rental (
        rental_date,
        house_house_nr,
        driver_customer_number,
        rental_station_id,
        return_date,
        return_station_id,
        km
    )
values (
        CURRENT_DATE(),
        'Дом 4',
        111111,
        1,
        CURRENT_DATE(),
        2,
        500
    );
insert into rental (
        rental_date,
        house_house_nr,
        driver_customer_number,
        rental_station_id,
        return_date,
        return_station_id,
        km
    )
values (
        CURRENT_DATE(),
        'Помещение 1',
        111111,
        1,
        null,
        null,
        null
    );
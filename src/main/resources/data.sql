INSERT INTO PLATAFORMAS (nombre, fabricante, tipo, fecha_de_lanzamiento, uuid)
VALUES ('Epic Games', 'Epic Games', 'Online', '2024-12-24', UUID()),
       ('Steam', 'Valve', 'Online', '2024-12-24', UUID());

INSERT INTO VIDEOJUEGOS (nombre, genero, almacenamiento, fecha_de_creacion, costo, plataforma_id, uuid)
VALUES ('Pepsiman', 'Estrategia', '10 TB', '1994-12-23', 100.0, 1, UUID()),
       ('Adan y Eva', 'Battle Royale', '9 MB', '2024-12-24', 200.0, 2, UUID()),
       ('The Witcher 3: Wild Hunt', 'RPG', '100 GB', '2024-12-24', 300.0, 2, UUID()),
       ('The Last of Us Part II', 'RPG', '100 GB', '2024-12-24', 400.0, 1, UUID());
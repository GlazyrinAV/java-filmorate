DELETE FROM DIRECTORS;
ALTER TABLE DIRECTORS ALTER COLUMN DIRECTOR_ID RESTART WITH 1;
insert into DIRECTORS VALUES ( 1, 'Director' );
insert into DIRECTORS VALUES ( 2, 'Other Director' );
DELETE FROM FILMS;
ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1;
insert into FILMS values (1, 'film', 'description', '1999-04-30', 100, 1);
insert into FILMS values (2, 'other film', 'other description', '2000-04-30', 100, 2);
insert into FILMS values (3, 'last film', 'last description', '2022-04-30', 99, 3);
DELETE FROM FILM_DIRECTOR;
INSERT INTO FILM_DIRECTOR VALUES ( 1, 1 );
INSERT INTO FILM_DIRECTOR VALUES ( 3, 2 )
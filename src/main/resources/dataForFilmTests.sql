DELETE FROM FILMS;
ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1;
INSERT INTO FILMS VALUES ( 1, 'new film', 'new description', '2000-04-22', 100, 1 );
INSERT INTO FILMS VALUES ( 2, 'second film', 'second description', '2000-04-22', 100, 1 );
INSERT INTO FILMS VALUES ( 3, 'third film', 'third description', '1976-04-22', 100, 3 );
INSERT INTO FILMS VALUES ( 4, 'final film', 'final description', '1987-04-22', 100, 2 );
DELETE FROM USERS;
ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1;
INSERT INTO USERS VALUES ( 1, 'Name', 'Login', 'abc@bca.ru', '1986-04-13' );
INSERT INTO USERS VALUES ( 2, 'Other_Name', 'Other_Login', 'zxy@yxz.ru', '1985-04-13' );
INSERT INTO USERS VALUES ( 3, 'AnotherOne_Name', 'AnotherOne_Login', 'ret@yxz.ru', '1987-04-13' );
DELETE FROM FILM_SCORE;
INSERT INTO FILM_SCORE VALUES ( 2, 2, 5 );
INSERT INTO FILM_SCORE VALUES ( 2, 1, 10 );
INSERT INTO FILM_SCORE VALUES ( 4, 2, 4 );
INSERT INTO FILM_SCORE VALUES ( 4, 1, 2 );
DELETE FROM FILM_GENRES;
INSERT INTO FILM_GENRES VALUES ( 1, 1 );
INSERT INTO FILM_GENRES VALUES ( 2, 1 );
DELETE FROM DIRECTORS;
ALTER TABLE DIRECTORS ALTER COLUMN DIRECTOR_ID RESTART WITH 1;
insert into DIRECTORS VALUES ( 1, 'Director' );
insert into DIRECTORS VALUES ( 2, 'Other Director' );
insert into DIRECTORS VALUES ( 3, 'Other Man' );
DELETE FROM FILM_DIRECTOR;
INSERT INTO FILM_DIRECTOR VALUES ( 1, 1 );
INSERT INTO FILM_DIRECTOR VALUES ( 2, 2 );
INSERT INTO FILM_DIRECTOR VALUES ( 3, 3 );
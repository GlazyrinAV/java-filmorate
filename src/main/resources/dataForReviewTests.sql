DELETE FROM FILMS;
ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1;
insert into FILMS values (1, 'film', 'description', '1999-04-30', 100, 1);
insert into FILMS values (2, 'other film', 'other description', '2000-04-30', 100, 2);
insert into FILMS values (3, 'last film', 'last description', '2022-04-30', 99, 3);
insert into FILMS values (4, 'not a film', 'not a description', '2022-04-30', 99, 3);
DELETE FROM USERS;
ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1;
INSERT INTO USERS VALUES ( 1, 'Name', 'Login', 'abc@bca.ru', '1986-04-13' );
INSERT INTO USERS VALUES ( 2, 'Other_Name', 'Other_Login', 'zxy@yxz.ru', '1985-04-13' );
DELETE FROM REVIEWS;
insert into REVIEWS values (2, 'other content', 2, 2, 'true');
insert into REVIEWS values (3, 'last content', 1, 2, 'false');
insert into REVIEWS values (4, 'with like content', 2, 1, 'true');
insert into REVIEWS values (5, 'with like content', 1, 3, 'false');
INSERT INTO REVIEWS_LIKES values (1, 4, 1);
INSERT INTO REVIEWS_LIKES values (2, 5, -1)

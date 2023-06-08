create table IF NOT EXISTS GENRES
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME VARCHAR_IGNORECASE not null,
    constraint GENRES_PK
        primary key (GENRE_ID)
);

create table IF NOT EXISTS RATINGS
(
    RATING_ID   INTEGER auto_increment,
    RATING_NAME VARCHAR_IGNORECASE not null,
    constraint RATINGS_PK
        primary key (RATING_ID)
);

create table IF NOT EXISTS FRIENDSHIP_STATUS
(
    FRIENDSHIP_STATUS_ID     INTEGER auto_increment,
    "Friendship_status_name" CHARACTER(20) not null,
    constraint FRIENDSHIP_STATUS_PK
        primary key (FRIENDSHIP_STATUS_ID)
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment,
    NAME         VARCHAR_IGNORECASE(256) not null,
    DESCRIPTION  VARCHAR_IGNORECASE(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER                 not null,
    RATING_ID    INTEGER,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_FK
        foreign key (RATING_ID) references RATINGS,
    constraint CHECK_FILM UNIQUE (NAME, RELEASE_DATE, DESCRIPTION)
);

create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER auto_increment
        unique,
    NAME     VARCHAR_IGNORECASE(256),
    LOGIN    VARCHAR_IGNORECASE(256) not null,
    EMAIL    VARCHAR_IGNORECASE(256) not null
        unique,
    BIRTHDAY DATE                    not null,
    constraint USERS_PK
        primary key (USER_ID)
);

create table IF NOT EXISTS FILM_GENRES
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRES_PK
        primary key (FILM_ID, GENRE_ID),
    constraint FILM_GENRES_FK
        foreign key (GENRE_ID) references GENRES(GENRE_ID) on delete cascade
);

create table if not exists FILM_LIKES
(
    FILM_ID integer not null,
    USER_ID integer not null,
    constraint FILM_LIKES_PK primary key (FILM_ID, USER_ID),
    constraint FILM_LIKES_FK foreign key (USER_ID) references USERS(USER_ID) on delete cascade,
    constraint FILM_LIKES_FK_1 foreign key (FILM_ID) references FILMS(FILM_ID) on delete cascade
);

create table if not exists LIST_OF_FRIENDS
(
    FRIEND_ID            INTEGER not null,
    USER_ID              INTEGER not null,
    FRIENDSHIP_STATUS_ID INTEGER not null,
    constraint LIST_OF_FRIENDS_PK
        primary key (USER_ID, FRIEND_ID),
    constraint LIST_OF_FRIENDS_FK
        foreign key (USER_ID) references USERS on delete cascade,
    constraint LIST_OF_FRIENDS_FK_2
        foreign key (FRIENDSHIP_STATUS_ID) references FRIENDSHIP_STATUS on delete cascade,
    constraint LIST_OF_FRIENDS_USERS_USER_ID_FK
        foreign key (FRIEND_ID) references USERS on delete cascade,
    constraint CHECK_NAME
        check ("USER_ID" <> "FRIEND_ID")
);
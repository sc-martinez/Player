create database player;

create schema public;


create table artist
(
    name           varchar not null
        constraint artist_pk
            primary key,
    members        character varying[],
    website        varchar,
    youtubewebsite varchar,
    image          varchar,
    description    text
);

alter table artist
    owner to postgres;

create table album
(
    name        varchar not null
        constraint album_pk
            primary key,
    image       varchar,
    year        integer,
    label       varchar,
    artist      varchar
        constraint album_artist_name_fk
            references artist,
    description varchar
);

alter table album
    owner to postgres;

create table playlist
(
    name            varchar not null
        constraint playlist_pk
            primary key,
    path_to_image   integer,
    description     integer,
    averagerate     integer,
    number_of_songs integer
);

alter table playlist
    owner to postgres;

create table song
(
    id       integer not null
        constraint song_pk
            primary key,
    title    varchar,
    artist   varchar
        constraint song_artist_name_fk
            references artist,
    album    varchar
        constraint song_album_name_fk
            references album,
    genre    character varying[],
    playlist character varying[],
    path     varchar,
    text     varchar,
    rate     smallint,
    moods    character varying[],
    image    varchar,
    year     integer
);

alter table song
    owner to postgres;


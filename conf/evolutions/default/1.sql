# Network schema

# --- !Ups

CREATE SEQUENCE network_id_seq;
CREATE TABLE network (
    id integer NOT NULL DEFAULT nextval('network_id_seq'),
    networkName varchar(255)
);

INSERT INTO network (networkName) VALUES ('default');

CREATE SEQUENCE node_id_seq;
CREATE TABLE node (
    id integer NOT NULL DEFAULT nextval('node_id_seq'),
    nodeName varchar(255),
    networkId integer NOT NULL,
    FOREIGN KEY (networkId) REFERENCES network(id)
);

CREATE SEQUENCE initTime_id_seq;
CREATE TABLE initTime (
    id integer NOT NULL DEFAULT nextval('initTime_id_seq'),
    nodeId integer NOT NULL,
    initTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (nodeId) REFERENCES node(id)
);

CREATE SEQUENCE coordinates_id_seq;
CREATE TABLE coordinate (
    id integer NOT NULL DEFAULT nextval('coordinates_id_seq'),
    nodeId integer NOT NULL,
    coordinateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    x double,
    y double,
    FOREIGN KEY (nodeId) REFERENCES node(id)
);

# --- !Downs

DROP TABLE coordinate;
DROP SEQUENCE coordinates_id_seq;

DROP TABLE initTime;
DROP SEQUENCE initTime_id_seq;

DROP TABLE node;
DROP SEQUENCE node_id_seq;

DROP TABLE network;
DROP SEQUENCE network_id_seq;
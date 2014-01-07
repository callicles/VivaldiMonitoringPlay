# --- !Ups

CREATE TABLE network (
    id bigserial PRIMARY KEY NOT NULL,
    networkName varchar(255)
);

INSERT INTO network (networkName) VALUES ('default');

CREATE TABLE node (
    id bigserial PRIMARY KEY NOT NULL,
    nodeName varchar(255),
    networkId integer NOT NULL,
    FOREIGN KEY (networkId) REFERENCES network(id)
);

CREATE TABLE initTime (
    id bigserial PRIMARY KEY NOT NULL,
    nodeId integer NOT NULL,
    initTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (nodeId) REFERENCES node(id)
);

CREATE TABLE coordinate (
    id bigserial PRIMARY KEY NOT NULL,
    nodeId integer NOT NULL,
    coordinateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    x decimal,
    y decimal,
    FOREIGN KEY (nodeId) REFERENCES node(id)
);

# --- !Downs

DROP TABLE coordinate;

DROP TABLE initTime;

DROP TABLE node;

DROP TABLE network;
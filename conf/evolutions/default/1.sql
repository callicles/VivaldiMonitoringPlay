# --- !Ups

CREATE TABLE network (
    id bigserial PRIMARY KEY NOT NULL,
    networkName varchar(255) UNIQUE
);

INSERT INTO network (networkName) VALUES ('default');

CREATE TABLE node (
    id bigserial PRIMARY KEY NOT NULL,
    nodeName varchar(255),
    networkId integer NOT NULL,
    UNIQUE (nodeName, networkId),
    FOREIGN KEY (networkId) REFERENCES network(id) ON DELETE CASCADE
);

CREATE TABLE initTime (
    id bigserial PRIMARY KEY NOT NULL,
    nodeId integer NOT NULL,
    initTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (nodeId) REFERENCES node(id) ON DELETE CASCADE
);

CREATE TABLE coordinate (
    id bigserial PRIMARY KEY NOT NULL,
    nodeId integer NOT NULL,
    coordinateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    x decimal,
    y decimal,
    FOREIGN KEY (nodeId) REFERENCES node(id) ON DELETE CASCADE
);

CREATE TABLE closeNode (
    id bigserial PRIMARY KEY NOT NULL,
    localNodeId integer NOT NULL,
    distantNodeId integer NOT NULL,
    logTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    distance decimal,
    UNIQUE (distantNodeId,localNodeId, logTime),
    FOREIGN KEY (distantNodeId) REFERENCES node(id) ON DELETE CASCADE,
    FOREIGN KEY (localNodeId) REFERENCES node(id) ON DELETE CASCADE
);

# --- !Downs

DROP TABLE closeNode;

DROP TABLE coordinate;

DROP TABLE initTime;

DROP TABLE node;

DROP TABLE network;
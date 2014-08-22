CREATE TABLE projects (
	id SERIAL PRIMARY KEY,
	name VARCHAR(64) NOT NULL,
	active BOOLEAN,
	modified_time BIGINT NOT NULL,
	create_time BIGINT NOT NULL,
	version INT,
	last_modified_by VARCHAR(64) NOT NULL,
	description VARCHAR(255),
	enc_type SMALLINT,
	settings_blob BYTEA
);
CREATE UNIQUE INDEX project_id ON projects (id);
CREATE INDEX project_name ON projects(name);

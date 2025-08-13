CREATE DATABASE stockflow_demo
    ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

ALTER DATABASE stockflow_demo SET timezone TO 'UTC';

\c stockflow_demo;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS stockflow AUTHORIZATION stock_user;

ALTER DATABASE stockflow_demo SET search_path TO stockflow, public;

GRANT ALL PRIVILEGES ON SCHEMA stockflow TO stock_user;

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM stock_user;

DROP DATABASE IF EXISTS stock_user;
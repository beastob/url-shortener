CREATE TABLE IF NOT EXISTS urls (
  id            VARCHAR(60)     DEFAULT gen_random_uuid() PRIMARY KEY,
  shorten_url    VARCHAR(9)      NOT NULL    UNIQUE,
  url           VARCHAR(2048)   NOT NULL    UNIQUE
);
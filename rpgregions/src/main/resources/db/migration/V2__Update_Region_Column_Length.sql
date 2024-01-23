-- Ok, so sqlite does not support altering columns, so we have to do this hacky stuff.
-- I make a new temporary table with the new region varchar(64), copy all the data from the old table into the temp one,
-- then drop the old table and rename the new one to the old one.
CREATE TABLE IF NOT EXISTS migrate_rpgregions_discoveries (uuid varchar(32) NOT NULL, region varchar(64) NOT NULL, time varchar(64) NOT NULL, PRIMARY KEY(uuid, region));
INSERT INTO migrate_rpgregions_discoveries SELECT * FROM rpgregions_discoveries;
DROP TABLE rpgregions_discoveries;
ALTER TABLE migrate_rpgregions_discoveries RENAME TO rpgregions_discoveries;

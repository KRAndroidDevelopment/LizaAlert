CREATE TABLE `lost_humans` (
`id` INT NOT NULL ,
`src_url` VARCHAR( 255 ) NOT NULL ,
`photo_url` VARCHAR( 255 ) NOT NULL ,
`description` VARCHAR( 10000 ) NOT NULL ,
`date` INT NOT NULL ,
`city` VARCHAR( 50 ) NOT NULL ,
`state` INT DEFAULT '0' NOT NULL ,
PRIMARY KEY ( `id` ) ,
INDEX ( `src_url` )
);

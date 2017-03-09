CREATE TABLE `lost_humans` (
`id` INT NOT NULL ,
`src_url` VARCHAR( 255 ) NOT NULL ,
`photo_url` VARCHAR( 255 ) NOT NULL ,
`description` VARCHAR( 10000 ) NOT NULL ,
`date` VARCHAR( 50 ) NOT NULL ,
`city` VARCHAR( 50 ) NOT NULL ,
`state` INT DEFAULT '0' NOT NULL ,
`title` VARCHAR( 250 ) NOT NULL ,
PRIMARY KEY ( `id` ) ,
INDEX ( `src_url` )
);

-- Delete the tables if they exist. To be able to drop
-- them in arbitrary order, disable foreign key checks.

-- Turn of key checks
set foreign_key_checks = 0;

-- Drop tables in arbitrary order
drop table if exists Cookies;
drop table if exists Ingredients;
drop table if exists Recipes;
drop table if exists IngredientDelivery;
drop table if exists Customers;
drop table if exists Orders;
drop table if exists Pallets;
drop table if exists NbrPallets;
drop table if exists LoadingOrders;
drop table if exists LoadingBills;
drop table if exists Tests;
drop table if exists LabQueue;

-- Turn on key checks
set foreign_key_checks = 1;

create TABLE Cookies(
	cookieName varchar(30),
	PRIMARY KEY(cookieName)
	);	
	
create TABLE Ingredients(
	ingredient varchar(40),
	amount decimal(6,2) NOT NULL,
	PRIMARY KEY(ingredient)
	);

create TABLE Recipes(
	cookieName varchar(30),
	ingredient varchar(40),
	ingredientAmount decimal(6,2) NOT NULL,
	PRIMARY KEY(cookieName, ingredient),
	FOREIGN KEY(cookieName) REFERENCES Cookies(cookieName),
	FOREIGN KEY(ingredient) REFERENCES Ingredients(ingredient)
	);

create TABLE IngredientDelivery(
	ingredient varchar(40),
	deliveryTimeStamp datetime NOT NULL,
	deliveryAmount decimal(6,2) NOT NULL,
	PRIMARY KEY(ingredient, deliveryTimeStamp),
	FOREIGN KEY(ingredient) REFERENCES Ingredients(ingredient)
	);
	
create TABLE Customers(
	customerName varchar(20),
	customerAdress varchar(50) NOT NULL,
	PRIMARY KEY(customerName)
	);

-- We should delete the deliveryTimeStamp because we really don't need it in two places...
create TABLE Orders(
	orderNbr int auto_increment, 
	expectedDate DATE, 
	nbrPalletsTotal int, 
	customerName varchar(20), 
	PRIMARY KEY(orderNbr),
	FOREIGN KEY(customerName) REFERENCES Customers(customerName)
	);

create TABLE Pallets(
	palletNbr int auto_increment,
	productionTimeStamp datetime NOT NULL,
	cookieName varchar(30) NOT NULL,
	orderNbr int,
	loaded 	boolean,
	blocked	boolean,
	PRIMARY KEY(palletNbr),
	FOREIGN KEY(cookieName) REFERENCES Cookies(cookieName),
	FOREIGN KEY(orderNbr) REFERENCES Orders(orderNbr)
	);

create TABLE NbrPallets(
	orderNbr int NOT NULL,
	cookieName varchar(30) NOT NULL, 
	nbrPallets int NOT NULL,
	FOREIGN KEY(orderNbr) REFERENCES Orders(orderNbr),
	FOREIGN KEY(cookieName) REFERENCES Cookies(cookieName)
	);

create TABLE LoadingOrders(
	loadNbr int auto_increment, 
	orderNbr int,
	PRIMARY KEY(loadNbr, orderNbr),
	FOREIGN KEY(orderNbr) REFERENCES Orders(orderNbr)
	);

create TABLE LoadingBills(
	loadNbr int, 
	orderNbr int, 
	deliveryTimeStamp datetime,
	PRIMARY KEY(loadNbr, orderNbr),
	FOREIGN KEY(orderNbr) REFERENCES Orders(orderNbr)
	);
	
	
-- Initial Data

INSERT into Cookies values('Nut cookie');	
INSERT into Cookies values('Amneris');	
INSERT into Cookies values('Tango');	
INSERT into Cookies values('Almond delight');	
INSERT into Cookies values('Berliner');	
INSERT into Cookies values('Nut ring');


INSERT into Ingredients values('Flour', 0);
INSERT into Ingredients values('Butter', 0);
INSERT into Ingredients values('Icing sugar', 0);
INSERT into Ingredients values('Roasted, chopped nuts', 0);
INSERT into Ingredients values('Fine-ground nuts', 0);
INSERT into Ingredients values('Ground, roasted nuts', 0);
INSERT into Ingredients values('Sugar', 0);
INSERT into Ingredients values('Eggs', 0);
INSERT into Ingredients values('Egg whites', 0);
INSERT into Ingredients values('Bread crumbs', 0);
INSERT into Ingredients values('Chocolate', 0);
INSERT into Ingredients values('Marzipan', 0);
INSERT into Ingredients values('Potato Starch', 0);
INSERT into Ingredients values('Wheat flour', 0);
INSERT into Ingredients values('Sodium bicarbonate', 0);
INSERT into Ingredients values('Vanilla', 0);
INSERT into Ingredients values('Chopped almonds', 0);
INSERT into Ingredients values('Cinnamon', 0);
INSERT into Ingredients values('Vanilla sugar', 0);

INSERT into Recipes values('Nut Ring','Flour', 450);
INSERT into Recipes values('Nut Ring','Butter', 450);
INSERT into Recipes values('Nut Ring','Icing Sugar', 190);
INSERT into Recipes values('Nut Ring','Roasted, chopped nuts', 225);

INSERT into Recipes values('Nut Cookie','Fine-ground nuts', 750);
INSERT into Recipes values('Nut Cookie','Ground, roasted nuts', 625);
INSERT into Recipes values('Nut Cookie','Bread crumbs', 125);
INSERT into Recipes values('Nut Cookie','Sugar', 375);
INSERT into Recipes values('Nut Cookie','Egg whites', 3.5);
INSERT into Recipes values('Nut Cookie','Chocolate', 50);

INSERT into Recipes values('Amneris','Marzipan', 750);
INSERT into Recipes values('Amneris','Butter', 250);
INSERT into Recipes values('Amneris','Eggs', 250);
INSERT into Recipes values('Amneris','Potato Starch', 25);
INSERT into Recipes values('Amneris','Wheat flour', 25);

INSERT into Recipes values('Tango','Butter', 200);
INSERT into Recipes values('Tango','Sugar', 250);
INSERT into Recipes values('Tango','Flour', 300);
INSERT into Recipes values('Tango','Sodium bicarbonate', 4);
INSERT into Recipes values('Tango','Vanilla', 2);

INSERT into Recipes values('Almond delight','Butter', 400);
INSERT into Recipes values('Almond delight','Sugar', 270);
INSERT into Recipes values('Almond delight','Chopped almonds', 279);
INSERT into Recipes values('Almond delight','Flour', 400);
INSERT into Recipes values('Almond delight','Cinnamon', 10);

INSERT into Recipes values('Berliner','Flour', 350);
INSERT into Recipes values('Berliner','Butter', 250);
INSERT into Recipes values('Berliner','Icing Sugar', 100);
INSERT into Recipes values('Berliner','Eggs', 50);
INSERT into Recipes values('Berliner','Vanilla sugar', 5);
INSERT into Recipes values('Berliner','Chocolate', 50);


INSERT into Customers values('Kaffebrod AB', 'Landskrona');
INSERT into Customers values('Bjudkakor AB', 'Ystad');
INSERT into Customers values('Kalaskakor AB', 'Kristianstad');
INSERT into Customers values('Gastkakor AB', 'Hassleholm');
INSERT into Customers values('Skanekakor AB', 'Perstorp');
INSERT into Customers values('Finkakor AB', 'Helsingborg');
INSERT into Customers values('Smabrod AB', 'Malmo');

-- PROCEDURES

DROP PROCEDURE IF EXISTS delivery;
DROP PROCEDURE IF EXISTS producePallet;
DROP PROCEDURE IF EXISTS placeOrder;
DROP PROCEDURE IF EXISTS orderDelivered;
DROP PROCEDURE IF EXISTS palletIntoAnOrder;
DROP PROCEDURE IF EXISTS palletOutOfAnOrder;
DROP PROCEDURE IF EXISTS orderIntoALoad;

-- Put ingredient amounts into ingredient stock
DELIMITER //
CREATE PROCEDURE delivery(
	IN ing varchar(40), 
	IN dt datetime,
	IN am decimal(6,2)
)
BEGIN
	insert into ingredientdelivery values(ing, dt, am);
	update ingredients set amount = amount + am where ingredient = ing;
END//
DELIMITER ;

-- Make a Pallet
DELIMITER //
CREATE PROCEDURE producePallet(
	IN cName varchar(30), 
	IN dt datetime
)
BEGIN
	update Ingredients set amount = amount - (
		select ingredientAmount 
		from Recipes 
		where Ingredients.ingredient = Recipes.ingredient AND cookieName = cName
	)
	where ingredient in (
		select ingredient 
		from Recipes 
		where cookieName = cName
	);
	insert into pallets values(null, dt, cName, null, 0, 0);
END//
DELIMITER ;

-- Make an Order
DELIMITER //
CREATE PROCEDURE placeOrder(
	IN custName varchar(20), 
	IN expDate date,
	IN almondDelight int(11),
	IN amneris int(11),
	IN berliner int(11),
	IN nutCookie int(11),
	IN nutRing int(11),
	IN tango int(11)
)
BEGIN
	insert into orders values(null, expDate, null, custName);
	set @ordNbr = (
		select max(orderNbr) 
		from orders
	);
	insert into nbrPallets values(@ordNbr, 'Almond delight', almondDelight);
	insert into nbrPallets values(@ordNbr, 'Amneris', amneris);
	insert into nbrPallets values(@ordNbr, 'Berliner', berliner);
	insert into nbrPallets values(@ordNbr, 'Nut cookie', nutCookie);
	insert into nbrPallets values(@ordNbr, 'Nut ring', nutRing);
	insert into nbrPallets values(@ordNbr, 'Tango', tango);
	update orders set nbrPalletsTotal = ( 
		select sum(nbrPallets) 
		from nbrPallets 
		where orderNbr = @ordNbr 
	) 
	where orderNbr = @ordNbr; 
END//
DELIMITER ;

-- Mark an Order as Delivered
DELIMITER //
CREATE PROCEDURE orderDelivered(
	IN oNbr int,
	IN dTimeStamp DateTime
)
BEGIN
	insert into loadingBills (loadNbr, orderNbr, deliveryTimeStamp) select loadNbr, orderNbr, dTimeStamp from loadingOrders where orderNbr = oNbr;
	delete from loadingOrders where orderNbr = oNbr;
END//
DELIMITER ;

-- Assign a pallet to an Order
DELIMITER //
CREATE PROCEDURE palletIntoAnOrder(
	IN pNbr int, 
	IN oNbr int
)
BEGIN
	update pallets set orderNbr = oNbr where palletNbr = pNbr;
	-- update orders set nbrPalletsTotal = nbrPalletsTotal-1 where orderNbr = oNbr;
END//
DELIMITER ;

-- Unassign a Pallet from an Order
-- Bug: could screw up things if pallet unassigned
DELIMITER //
CREATE PROCEDURE palletOutOfAnOrder(
	IN pNbr int
)
BEGIN
	-- set @oNbr = (select orderNbr from pallets where palletNbr = pNbr);
	update pallets set orderNbr = null where palletNbr = pNbr;
	-- update orders set nbrPalletsTotal = nbrPalletsTotal+1 where orderNbr = @oNbr;
END//
DELIMITER ;

-- Assign an Order into a Load
DELIMITER //
CREATE PROCEDURE orderIntoALoad(
	IN lNbr int, 
	IN oNbr int
)
BEGIN
	insert into loadingorders values(lNbr , oNbr);
	update pallets set loaded = 1 where orderNbr = oNbr;
END//
DELIMITER ;


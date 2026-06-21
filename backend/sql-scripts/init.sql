CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS inventory_db;
CREATE DATABASE IF NOT EXISTS request_db;

GRANT ALL PRIVILEGES ON auth_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON inventory_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON request_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

USE inventory_db;
CREATE TABLE IF NOT EXISTS stationery_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    unit VARCHAR(50),
    available_quantity INT,
    minimum_quantity INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity) VALUES 
('A4 Paper Rim', 'Paper', 'Rim', 50, 10),
('Blue Pen', 'Writing', 'Box', 100, 20),
('Notebook', 'Paper', 'Piece', 200, 50);


//init.sql is made to initialize the databases and tables required for the application. It creates three databases: auth_db, inventory_db, and request_db. It also grants all privileges on these databases to the root user. Then it creates a stationery_items table in the inventory_db database and inserts some sample data into it.
//if init.sql is removed, the databases and tables will not be created automatically, and the application will fail to start due to missing database schema. The application relies on these databases and tables to store and manage data related to authentication, inventory, and requests.

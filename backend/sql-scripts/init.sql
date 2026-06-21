CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS inventory_db;
CREATE DATABASE IF NOT EXISTS request_db;

GRANT ALL PRIVILEGES ON auth_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON inventory_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON request_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- ---------------------------------------------------------
-- INVENTORY DATABASE
-- ---------------------------------------------------------
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

INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity) 
SELECT * FROM (SELECT 'A4 Paper Rim', 'Paper', 'Rim', 50, 10) AS tmp
WHERE NOT EXISTS (
    SELECT name FROM stationery_items WHERE name = 'A4 Paper Rim'
) LIMIT 1;

INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity) 
SELECT * FROM (SELECT 'Blue Pen', 'Writing', 'Box', 100, 20) AS tmp
WHERE NOT EXISTS (
    SELECT name FROM stationery_items WHERE name = 'Blue Pen'
) LIMIT 1;

INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity) 
SELECT * FROM (SELECT 'Notebook', 'Paper', 'Piece', 200, 50) AS tmp
WHERE NOT EXISTS (
    SELECT name FROM stationery_items WHERE name = 'Notebook'
) LIMIT 1;

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(255),
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- AUTH DATABASE
-- ---------------------------------------------------------
USE auth_db;
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- REQUEST DATABASE
-- ---------------------------------------------------------
USE request_db;
CREATE TABLE IF NOT EXISTS request_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_email VARCHAR(255),
    item_id BIGINT,
    quantity INT,
    status VARCHAR(50),
    rejection_reason VARCHAR(255),
    request_group_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- init.sql is made to initialize the databases and tables required for the application. It creates three databases: auth_db, inventory_db, and request_db. It also grants all privileges on these databases to the root user. Then it creates tables and inserts some sample data.
-- if init.sql is removed, the databases and tables will not be created automatically, and the application will fail to start due to missing database schema. The application relies on these databases and tables to store and manage data related to authentication, inventory, and requests.

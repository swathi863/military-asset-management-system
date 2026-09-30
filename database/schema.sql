-- ====================================================================
-- MILITARY ASSET MANAGEMENT SYSTEM (MAMS) - DATABASE SCHEMA (MySQL 8.0)
-- ====================================================================

CREATE DATABASE IF NOT EXISTS military_asset_db;
USE military_asset_db;

-- 1. Bases Table
CREATE TABLE IF NOT EXISTS bases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    commander_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Equipment Types Table
CREATE TABLE IF NOT EXISTS equipment_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(50) NOT NULL, -- WEAPONS, VEHICLES, AMMUNITION, COMMUNICATIONS, MEDICAL, HEAVY_EQUIPMENT
    name VARCHAR(100) NOT NULL,
    unit_of_measure VARCHAR(20) NOT NULL, -- UNITS, ROUNDS, VEHICLES, SETS
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Users Table (RBAC)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL, -- ADMIN, BASE_COMMANDER, LOGISTICS_OFFICER
    assigned_base_id BIGINT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_base_id) REFERENCES bases(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Assets Table (Individual Tracked Units)
CREATE TABLE IF NOT EXISTS assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    equipment_type_id BIGINT NOT NULL,
    current_base_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, ASSIGNED, IN_TRANSIT, EXPENDED, MAINTENANCE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id),
    FOREIGN KEY (current_base_id) REFERENCES bases(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Base Inventories Table (Aggregated Balances per Base and Equipment Type)
CREATE TABLE IF NOT EXISTS base_inventories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    base_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    opening_balance INT NOT NULL DEFAULT 0,
    current_stock INT NOT NULL DEFAULT 0,
    assigned_count INT NOT NULL DEFAULT 0,
    expended_count INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_base_equipment (base_id, equipment_type_id),
    FOREIGN KEY (base_id) REFERENCES bases(id),
    FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Purchases Table
CREATE TABLE IF NOT EXISTS purchases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_number VARCHAR(50) NOT NULL UNIQUE,
    base_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_cost DECIMAL(12, 2) NOT NULL,
    total_cost DECIMAL(12, 2) NOT NULL,
    vendor_name VARCHAR(100) NOT NULL,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    recorded_by_user_id BIGINT,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (base_id) REFERENCES bases(id),
    FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id),
    FOREIGN KEY (recorded_by_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Transfers Table
CREATE TABLE IF NOT EXISTS transfers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transfer_code VARCHAR(50) NOT NULL UNIQUE,
    source_base_id BIGINT NOT NULL,
    target_base_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, IN_TRANSIT, COMPLETED, CANCELLED
    transfer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completion_date TIMESTAMP NULL,
    initiated_by_user_id BIGINT,
    approved_by_user_id BIGINT,
    remarks TEXT,
    FOREIGN KEY (source_base_id) REFERENCES bases(id),
    FOREIGN KEY (target_base_id) REFERENCES bases(id),
    FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id),
    FOREIGN KEY (initiated_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Assignments Table
CREATE TABLE IF NOT EXISTS assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assignment_code VARCHAR(50) NOT NULL UNIQUE,
    base_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    assigned_to_personnel VARCHAR(100) NOT NULL,
    service_id VARCHAR(50) NOT NULL,
    rank_title VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    assignment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expected_return_date DATE NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, RETURNED, OVERDUE
    assigned_by_user_id BIGINT,
    notes TEXT,
    FOREIGN KEY (base_id) REFERENCES bases(id),
    FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id),
    FOREIGN KEY (assigned_by_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Expenditures Table
CREATE TABLE IF NOT EXISTS expenditures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expenditure_code VARCHAR(50) NOT NULL UNIQUE,
    base_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    expenditure_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(100) NOT NULL, -- TRAINING, OPERATIONAL_USAGE, DAMAGED_DISPOSAL, EXPIRED
    operation_name VARCHAR(100),
    recorded_by_user_id BIGINT,
    remarks TEXT,
    FOREIGN KEY (base_id) REFERENCES bases(id),
    FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id),
    FOREIGN KEY (recorded_by_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Audit Logs Table (API Logging requirement)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(50) NOT NULL,
    user_role VARCHAR(30) NOT NULL,
    base_id BIGINT,
    action VARCHAR(50) NOT NULL,
    resource VARCHAR(150) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    status_code INT NOT NULL,
    FOREIGN KEY (base_id) REFERENCES bases(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- INDEXES FOR PERFORMANCE
CREATE INDEX idx_purchases_date_base ON purchases(purchase_date, base_id);
CREATE INDEX idx_transfers_date ON transfers(transfer_date);
CREATE INDEX idx_assignments_base ON assignments(base_id, status);
CREATE INDEX idx_expenditures_base ON expenditures(base_id, expenditure_date);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);

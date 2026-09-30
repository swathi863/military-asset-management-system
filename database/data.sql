-- ====================================================================
-- INITIAL SEED DATA FOR MILITARY ASSET MANAGEMENT SYSTEM (MAMS)
-- ====================================================================

USE military_asset_db;

-- Insert Bases
INSERT INTO bases (id, code, name, location, commander_name) VALUES
(1, 'BASE-001', 'Fort Liberty (Bragg)', 'North Carolina, USA', 'General Marcus Vance'),
(2, 'BASE-002', 'Camp Pendleton', 'California, USA', 'Col. Sarah Jenkins'),
(3, 'BASE-003', 'Ramstein Air Base', 'Kaiserslautern, Germany', 'Brig. Gen. David Sterling'),
(4, 'BASE-004', 'Camp Humphreys', 'Pyeongtaek, South Korea', 'Col. Robert Chen');

-- Insert Equipment Types
INSERT INTO equipment_types (id, category, name, unit_of_measure, description) VALUES
(1, 'WEAPONS', 'M4A1 Carbine 5.56mm', 'UNITS', 'Standard issue automatic assault rifle'),
(2, 'WEAPONS', 'M249 SAW Light Machine Gun', 'UNITS', '5.56mm Squad Automatic Weapon'),
(3, 'VEHICLES', 'JLTV Armored Vehicle', 'VEHICLES', 'Joint Light Tactical Vehicle with armor plating'),
(4, 'VEHICLES', 'M1A2 Abrams Main Battle Tank', 'VEHICLES', 'Armored heavy tracked battle tank'),
(5, 'AMMUNITION', '5.56x45mm NATO Ammo Box (1000 rds)', 'ROUNDS', 'Standard rifle ammunition crate'),
(6, 'AMMUNITION', '120mm Tank Shell (High Explosive)', 'ROUNDS', 'Main gun heavy ammunition'),
(7, 'COMMUNICATIONS', 'AN/PRC-152A Tactical Radio', 'SETS', 'Handheld multiband satellite radio'),
(8, 'MEDICAL', 'Tactical Field Medical Kit', 'SETS', 'Complete combat casualty care kit');

-- Insert Users (Passwords: password123 hashed via BCrypt $2a$10$e.g.)
-- For seed convenience, standard bcrypt for "password123": $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD00DMxs.AQ8476a
INSERT INTO users (id, username, password_hash, full_name, email, role, assigned_base_id, active) VALUES
(1, 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD00DMxs.AQ8476a', 'Command Admin Central', 'admin@military.gov', 'ADMIN', NULL, TRUE),
(2, 'commander_bragg', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD00DMxs.AQ8476a', 'General Marcus Vance', 'commander.bragg@military.gov', 'BASE_COMMANDER', 1, TRUE),
(3, 'commander_pendleton', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD00DMxs.AQ8476a', 'Col. Sarah Jenkins', 'commander.pendleton@military.gov', 'BASE_COMMANDER', 2, TRUE),
(4, 'logistics_officer', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD00DMxs.AQ8476a', 'Captain Alex Mercer', 'logistics@military.gov', 'LOGISTICS_OFFICER', 1, TRUE);

-- Insert Base Inventories
INSERT INTO base_inventories (id, base_id, equipment_type_id, opening_balance, current_stock, assigned_count, expended_count) VALUES
(1, 1, 1, 500, 420, 50, 30),
(2, 1, 3, 40, 32, 5, 3),
(3, 1, 5, 10000, 7500, 500, 2000),
(4, 2, 1, 350, 280, 40, 30),
(5, 2, 2, 60, 48, 8, 4),
(6, 2, 7, 120, 100, 15, 5),
(7, 3, 3, 25, 20, 3, 2),
(8, 3, 4, 12, 10, 2, 0),
(9, 4, 1, 200, 160, 25, 15);

-- Insert Purchases
INSERT INTO purchases (id, purchase_order_number, base_id, equipment_type_id, quantity, unit_cost, total_cost, vendor_name, purchase_date, recorded_by_user_id, remarks) VALUES
(1, 'PO-2026-001', 1, 1, 100, 1200.00, 120000.00, 'Colt Defense Systems', '2026-01-15 10:00:00', 4, 'Annual rifle procurement'),
(2, 'PO-2026-002', 1, 3, 5, 220000.00, 1100000.00, 'Oshkosh Defense', '2026-02-10 14:30:00', 4, 'Tactical vehicle batch'),
(3, 'PO-2026-003', 2, 5, 5000, 45.00, 225000.00, 'Lake City Ammunition Plant', '2026-03-01 09:15:00', 4, 'Live fire exercise stock'),
(4, 'PO-2026-004', 3, 7, 30, 4500.00, 135000.00, 'L3Harris Technologies', '2026-03-20 11:45:00', 4, 'Comm upgrade for European Theater');

-- Insert Transfers
INSERT INTO transfers (id, transfer_code, source_base_id, target_base_id, equipment_type_id, quantity, status, transfer_date, completion_date, initiated_by_user_id, approved_by_user_id, remarks) VALUES
(1, 'TRF-2026-101', 1, 2, 1, 30, 'COMPLETED', '2026-02-01 08:00:00', '2026-02-03 16:00:00', 4, 2, 'Pacific deployment redistribution'),
(2, 'TRF-2026-102', 2, 4, 2, 10, 'IN_TRANSIT', '2026-03-15 13:00:00', NULL, 4, 3, 'Far East squad reinforcement'),
(3, 'TRF-2026-103', 1, 3, 3, 4, 'PENDING', '2026-03-25 09:30:00', NULL, 4, NULL, 'NATO exercise support');

-- Insert Assignments
INSERT INTO assignments (id, assignment_code, base_id, equipment_type_id, assigned_to_personnel, service_id, rank_title, quantity, assignment_date, expected_return_date, status, assigned_by_user_id, notes) VALUES
(1, 'ASN-2026-01', 1, 1, 'Sgt. John Miller', 'MIL-98421', 'Sergeant', 1, '2026-02-15 08:30:00', '2026-10-15', 'ACTIVE', 2, 'Issued for perimeter defense detail'),
(2, 'ASN-2026-02', 1, 3, 'Lt. Commander Eric Vance', 'MIL-44210', 'Lieutenant', 1, '2026-03-01 10:00:00', '2026-09-30', 'ACTIVE', 2, 'Command convoy lead vehicle'),
(3, 'ASN-2026-03', 2, 7, 'Cpl. Samantha Reed', 'MIL-77123', 'Corporal', 2, '2026-03-10 14:00:00', '2026-04-10', 'ACTIVE', 3, 'Comms check patrol');

-- Insert Expenditures
INSERT INTO expenditures (id, expenditure_code, base_id, equipment_type_id, quantity, expenditure_date, reason, operation_name, recorded_by_user_id, remarks) VALUES
(1, 'EXP-2026-01', 1, 5, 2000, '2026-02-20 16:00:00', 'TRAINING', 'Exercise Valor Shield 2026', 4, 'Spent during 82nd Airborne qualification drill'),
(2, 'EXP-2026-02', 1, 1, 5, '2026-03-05 11:20:00', 'DAMAGED_DISPOSAL', 'N/A', 2, 'Irreparably damaged in heavy vehicle accident'),
(3, 'EXP-2026-03', 2, 5, 1500, '2026-03-18 15:45:00', 'OPERATIONAL_USAGE', 'Coastal Readiness Alpha', 4, 'Expenditure during live marine coastal drill');

-- Insert Sample Audit Logs
INSERT INTO audit_logs (id, timestamp, username, user_role, base_id, action, resource, details, ip_address, status_code) VALUES
(1, '2026-03-25 09:30:00', 'logistics_officer', 'LOGISTICS_OFFICER', 1, 'INITIATE_TRANSFER', '/api/transfers', 'Initiated transfer TRF-2026-103: 4 JLTVs to Ramstein', '192.168.1.105', 201),
(2, '2026-03-25 10:15:00', 'commander_bragg', 'BASE_COMMANDER', 1, 'CREATE_PURCHASE', '/api/purchases', 'Recorded purchase PO-2026-001', '192.168.1.100', 201);

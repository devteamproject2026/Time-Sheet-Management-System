-- ============================================================
-- P26 - TIME SHEET MANAGEMENT SYSTEM
-- File: P26-Createdb.sql
-- Purpose: Database creation, table structure, keys & constraints
-- ============================================================

DROP DATABASE IF EXISTS TimeSheetDB;
CREATE DATABASE TimeSheetDB;
USE TimeSheetDB;


-- ------------------------------------------------------------
-- 1. USERS TABLE
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    contact VARCHAR(15),
    role ENUM('ADMIN', 'HR_HEAD', 'MANAGER', 'EMPLOYEE') NOT NULL,
    approval_status ENUM('PENDING', 'APPROVED', 'REJECTED')
        DEFAULT 'PENDING',
    account_status ENUM('ACTIVE', 'INACTIVE')
        DEFAULT 'INACTIVE',
    joining_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 2. CLIENTS TABLE
-- ------------------------------------------------------------
CREATE TABLE clients (
    client_id INT PRIMARY KEY AUTO_INCREMENT,
    client_name VARCHAR(100) NOT NULL,
    company_name VARCHAR(100),
    email VARCHAR(100),
    contact VARCHAR(15),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 3. PROJECTS TABLE
-- ------------------------------------------------------------
CREATE TABLE projects (
    project_id INT PRIMARY KEY AUTO_INCREMENT,
    project_name VARCHAR(100) NOT NULL,
    description TEXT,
    client_id INT NOT NULL,
    manager_id INT NOT NULL,
    hr_head_id INT NOT NULL,
    start_date DATE,
    end_date DATE,
    status ENUM('ACTIVE', 'COMPLETED', 'ON_HOLD') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(client_id),
    FOREIGN KEY (manager_id) REFERENCES users(user_id),
    FOREIGN KEY (hr_head_id) REFERENCES users(user_id)
);

-- ------------------------------------------------------------
-- 4. EMPLOYEE_PROJECTS TABLE
-- ------------------------------------------------------------
CREATE TABLE employee_projects (
    employee_project_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    project_id INT NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(employee_id, project_id),
    FOREIGN KEY (employee_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 5. TASKS TABLE
-- ------------------------------------------------------------
CREATE TABLE tasks (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    manager_id INT NOT NULL,
    employee_id INT NOT NULL,
    task_name VARCHAR(100) NOT NULL,
    task_description TEXT,
    start_date DATE,
    end_date DATE,
    status ENUM('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'ASSIGNED',
    progress_percent INT DEFAULT 0,
    remarks TEXT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    FOREIGN KEY (manager_id) REFERENCES users(user_id),
    FOREIGN KEY (employee_id) REFERENCES users(user_id)
);

-- ------------------------------------------------------------
-- 6. ATTENDANCE TABLE
-- ------------------------------------------------------------
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in TIME,
    check_out TIME,
    status ENUM('PRESENT', 'ABSENT', 'HALF_DAY') DEFAULT 'PRESENT',
    UNIQUE(employee_id, attendance_date), -- Prevents duplicate daily logs
    FOREIGN KEY (employee_id) REFERENCES users(user_id)
);

-- ------------------------------------------------------------
-- 7. TIMESHEETS TABLE
-- ------------------------------------------------------------
CREATE TABLE timesheets (
    timesheet_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    task_id INT NOT NULL, -- Project ID removed to avoid redundant data paths
    work_date DATE NOT NULL,
    hours_worked DECIMAL(4,2),
    work_description TEXT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES users(user_id),
    FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);

-- ------------------------------------------------------------
-- 8. TIMESHEET_APPROVALS TABLE
-- ------------------------------------------------------------
CREATE TABLE timesheet_approvals (
    approval_id INT PRIMARY KEY AUTO_INCREMENT,
    timesheet_id INT NOT NULL,
    manager_id INT NOT NULL,
    approval_status ENUM('APPROVED', 'REJECTED') NOT NULL,
    comments VARCHAR(500),
    approval_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (timesheet_id) REFERENCES timesheets(timesheet_id) ON DELETE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES users(user_id)
);

-- ------------------------------------------------------------
-- 9. COMPLAINTS TABLE
-- ------------------------------------------------------------
CREATE TABLE complaints (
    complaint_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    manager_id INT,
    subject VARCHAR(100) NOT NULL,
    description TEXT,
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED') DEFAULT 'OPEN',
    resolution TEXT,
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES users(user_id),
    FOREIGN KEY (manager_id) REFERENCES users(user_id)
);

-- ============================================================
-- END OF FILE
-- ============================================================

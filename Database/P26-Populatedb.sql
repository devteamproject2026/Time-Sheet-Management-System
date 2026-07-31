-- ============================================================
-- P26 - TIME SHEET MANAGEMENT SYSTEM
-- File: P26-Populatedb.sql
-- Purpose: Dummy/sample data for testing
-- ============================================================

USE TimeSheetDB;

-- ------------------------------------------------------------
-- 1. USERS
-- ------------------------------------------------------------
INSERT INTO users (username, password, first_name, last_name, email, contact, role, approval_status, account_status, joining_date) 
VALUES
('admin1',    '$2a$12$.qZSioXNjYx8MieqmIPuieOz.bsWhvdVATZuZUOPKXPFI1tDsIrI6',  'Rohit',   'Sharma',   'admin1@company.com',    '9800000001', 'ADMIN',     'APPROVED', 'ACTIVE',   '2022-01-10'),
('hrhead1',   'Hr@123',     'Anita',   'Deshmukh', 'anita.hr@company.com',  '9800000002', 'HR_HEAD',   'APPROVED', 'ACTIVE',   '2022-02-15'),
('hrhead2',   'Hr@123',     'Suresh',  'Patil',    'suresh.hr@company.com', '9800000003', 'HR_HEAD',   'PENDING',  'INACTIVE', '2023-05-01'),
('manager1',  'Mgr@123',    'Priya',   'Kulkarni', 'priya.mgr@company.com', '9800000004', 'MANAGER',   'APPROVED', 'ACTIVE',   '2022-03-01'),
('manager2',  'Mgr@123',    'Vikram',  'Singh',    'vikram.mgr@company.com','9800000005', 'MANAGER',   'APPROVED', 'ACTIVE',   '2022-06-20'),
('emp1',      'Emp@123',    'Sneha',   'Joshi',    'sneha.emp@company.com', '9800000006', 'EMPLOYEE',  'APPROVED', 'ACTIVE',   '2023-01-05'),
('emp2',      'Emp@123',    'Rahul',   'Verma',    'rahul.emp@company.com', '9800000007', 'EMPLOYEE',  'APPROVED', 'ACTIVE',   '2023-02-10'),
('emp3',      'Emp@123',    'Pooja',   'Nair',     'pooja.emp@company.com', '9800000008', 'EMPLOYEE',  'APPROVED', 'ACTIVE',   '2023-03-15'),
('emp4',      'Emp@123',    'Aditya',  'Rao',      'aditya.emp@company.com','9800000009', 'EMPLOYEE',  'APPROVED', 'INACTIVE', '2023-04-20');
-- ------------------------------------------------------------
-- 2. CLIENTS
-- ------------------------------------------------------------
INSERT INTO clients (client_name, company_name, email, contact, address) VALUES
('Mahesh Agarwal', 'Agarwal Textiles Pvt Ltd', 'contact@agarwaltextiles.com', '9911100001', 'Pune, Maharashtra'),
('Kavita Rane',     'Rane Logistics',           'info@ranelogistics.com',      '9911100002', 'Mumbai, Maharashtra'),
('John D''Souza',   'DSouza FinTech Solutions', 'john@dsouzafintech.com',      '9911100003', 'Bengaluru, Karnataka');

-- ------------------------------------------------------------
-- 3. PROJECTS
-- ------------------------------------------------------------
INSERT INTO projects (project_name, description, client_id, manager_id, hr_head_id, start_date, end_date, status) VALUES
('Inventory Management System', 'Web app to manage textile stock and orders', 1, 4, 2, '2024-01-01', '2024-06-30', 'ACTIVE'),
('Fleet Tracking Portal',       'Real-time vehicle tracking dashboard',       2, 5, 2, '2024-02-15', '2024-08-15', 'ACTIVE'),
('Loan Approval Automation',    'Automated loan approval workflow system',    3, 4, 2, '2023-09-01', '2024-01-31', 'COMPLETED');

-- ------------------------------------------------------------
-- 4. EMPLOYEE_PROJECTS
-- ------------------------------------------------------------
INSERT INTO employee_projects (employee_id, project_id) VALUES
(6, 1),  -- Sneha  -> Inventory Management System
(7, 1),  -- Rahul  -> Inventory Management System
(8, 2),  -- Pooja  -> Fleet Tracking Portal
(6, 3),  -- Sneha  -> Loan Approval Automation
(9, 2);  -- Aditya -> Fleet Tracking Portal

-- ------------------------------------------------------------
-- 5. TASKS
-- ------------------------------------------------------------
INSERT INTO tasks (project_id, manager_id, employee_id, task_name, task_description, start_date, end_date, status, progress_percent, remarks) VALUES
(1, 4, 6, 'Design DB Schema',        'Create ER diagram and schema for inventory module', '2024-01-02', '2024-01-10', 'COMPLETED',   100, 'Approved by client'),
(1, 4, 7, 'Build Login Module',      'Implement authentication and role-based access',     '2024-01-11', '2024-01-20', 'IN_PROGRESS', 60,  'On track'),
(2, 5, 8, 'Integrate GPS API',       'Integrate third-party GPS tracking API',              '2024-02-16', '2024-03-01', 'IN_PROGRESS', 40,  'Waiting on API keys'),
(2, 5, 9, 'Dashboard UI',            'Build fleet tracking dashboard UI',                   '2024-03-02', '2024-03-20', 'ASSIGNED',    0,   NULL),
(3, 4, 6, 'Loan Rules Engine',       'Implement business rules for loan approval',          '2023-09-05', '2023-11-30', 'COMPLETED',   100, 'Delivered on time');

-- ------------------------------------------------------------
-- 6. ATTENDANCE
-- ------------------------------------------------------------
INSERT INTO attendance (employee_id, attendance_date, check_in, check_out, status) VALUES
(6, '2024-01-02', '09:05:00', '18:02:00', 'PRESENT'),
(7, '2024-01-11', '09:15:00', '17:50:00', 'PRESENT'),
(8, '2024-02-16', '09:00:00', '13:00:00', 'HALF_DAY'),
(9, '2024-03-02', NULL,        NULL,       'ABSENT'),
(6, '2024-01-03', '09:10:00', '18:05:00', 'PRESENT');

-- ------------------------------------------------------------
-- 7. TIMESHEETS
-- ------------------------------------------------------------
INSERT INTO timesheets (employee_id, task_id, work_date, hours_worked, work_description, status) VALUES
(6, 1, '2024-01-02', 8.00, 'Worked on ER diagram and normalization', 'APPROVED'),
(7, 2, '2024-01-11', 7.50, 'Set up login controller and JWT auth',   'APPROVED'),
(8, 3, '2024-02-16', 6.00, 'Explored GPS API documentation',         'PENDING'),
(6, 5, '2023-09-05', 8.00, 'Drafted loan approval rule set',         'APPROVED'),
(9, 4, '2024-03-02', 4.00, 'Initial dashboard wireframe',            'REJECTED');

-- ------------------------------------------------------------
-- 8. TIMESHEET_APPROVALS
-- ------------------------------------------------------------
INSERT INTO timesheet_approvals (timesheet_id, manager_id, approval_status, comments) VALUES
(1, 4, 'APPROVED', 'Good progress, matches task plan'),
(2, 4, 'APPROVED', 'Login module on schedule'),
(4, 4, 'APPROVED', 'Rules documented well'),
(5, 5, 'REJECTED', 'Wireframe incomplete, please redo');

-- ------------------------------------------------------------
-- 9. COMPLAINTS
-- ------------------------------------------------------------
INSERT INTO complaints (employee_id, manager_id, subject, description, status, resolution, resolved_at) VALUES
(7, 4, 'Laptop performance issue',  'Laptop hangs frequently during builds', 'RESOLVED',    'IT replaced RAM module',           '2024-01-15 11:00:00'),
(8, 5, 'API access delay',         'GPS API credentials not yet received',  'IN_PROGRESS', NULL,                                NULL),
(9, 5, 'Unclear task requirements','Dashboard UI task lacks clear specs',   'OPEN',         NULL,                                NULL);

-- ============================================================
-- END OF FILE
-- ============================================================

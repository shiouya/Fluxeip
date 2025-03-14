INSERT INTO status (status_name, status_type) 
VALUES 
('在職', '員工狀態'),
('離職', '員工狀態'),
('休假', '員工狀態');

-- 插入職位表 (position) 的假資料
INSERT INTO position (position_name) 
VALUES 
('老闆'),
('總經理'),
('經理'),
('組長'),
('員工');


-- 插入部門表 (department) 的假資料
INSERT INTO department (department_name) 
VALUES 
('總經理部'),
('行政部'),
('人資部'),
('業務部'),
('技術部');

-- 插入員工表 (employee) 的假資料
INSERT INTO employee (employee_name, password, position_id, department_id, hire_date, status_id) 
VALUES 
('張三', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 1, 1, '2020-01-01', 1),    -- 老闆，總經理部，在職
('李四', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 2, 1, '2021-02-15', 1),    -- 總經理，總經理部，在職
('小明', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 3, 2, '2019-03-20', 1),    -- 經理，行政部，在職
('王五', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 4, 2, '2019-03-20', 2),    -- 組長，行政部，離職
('名五', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 4, 2, '2019-03-20', 1),    -- 組長，行政部，在職
('趙六', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 2, '2022-05-10', 1),    -- 員工，行政部，在職
('李六', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 2, '2022-05-10', 1),    -- 員工，行政部，在職
('大六', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 2, '2022-05-10', 1),    -- 員工，行政部，在職
('孫七', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 3, 3, '2018-07-30', 1),    -- 經理，人資部，在職
('名八', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 4, 3, '2019-03-20', 1),    -- 組長，人資部，在職
('趙一', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 3, '2022-05-10', 1),    -- 員工，人資部，在職
('李逆', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 3, '2022-05-10', 2),    -- 員工，人資部，離職
('大拍', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 3, '2022-05-10', 1),    -- 員工，人資部，在職
('發明', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 3, 4, '2019-03-20', 1),    -- 經理，業務部，在職
('王幾', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 4, 4, '2019-03-20', 2),    -- 組長，業務部，離職
('名滔', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 4, 4, '2019-03-20', 1),    -- 組長，業務部，在職
('趙郎', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 4, '2022-05-10', 1),    -- 員工，業務部，在職
('每六', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 4, '2022-05-10', 1),    -- 員工，業務部，在職
('趴六', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 4, '2022-05-10', 1),    -- 員工，業務部，在職
('孫能', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 3, 5, '2018-07-30', 1),    -- 經理，技術部，在職
('名尬', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 4, 5, '2019-03-20', 1),    -- 組長，技術部，在職
('趙差', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 5, '2022-05-10', 1),    -- 員工，技術部，在職
('李繳', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 5, '2022-05-10', 2),    -- 員工，技術部，離職
('大剛', '$2a$10$tTEgd7DBHpZ.q4W6tjAbiubDeoDW0bIgHzkqLcrPHFaoeOvtng/2C', 5, 5, '2022-05-10', 1);    -- 員工，技術部，在職

INSERT INTO roles (role_name)
VALUES 
('最高管理員'),
('次等管理員'),
('行政主管'),
('人資主管'),
('業務主管'),
('技術主管'),
('員工');

INSERT INTO employee_detail (employee_id, gender, birthday, identity_card, email, phone, employee_photo, address, emergency_contact, emergency_phone)
VALUES 
(1001, '男', '1990-05-14', 'A123456789', 'john.doe@example.com', '123-456-7890', NULL, '123 Main St, City', 'Sarah Doe', '098-765-4321'),
(1002, '女', '1985-08-22', 'B987654321', 'jane.smith@example.com', '234-567-8901', NULL, '456 Oak St, City', 'Robert Smith', '098-765-4322'),
(1003, '男', '1988-02-10', 'C192837465', 'mark.johnson@example.com', '345-678-9012', NULL, '789 Pine St, City', 'Laura Johnson', '098-765-4323'),
(1004, '女', '1993-11-05', 'D564738291', 'emily.davis@example.com', '456-789-0123', NULL, '101 Maple St, City', 'Thomas Davis', '098-765-4324'),
(1005, '男', '1995-04-15', 'E837264510', 'michael.brown@example.com', '567-890-1234', NULL, '202 Birch St, City', 'Jessica Brown', '098-765-4325'),
(1006, '男', '1990-05-14', 'A123456787', 'john.1doe@example.com', '123-456-7891', NULL, '123 Main St, City', 'Sarah Doe', '098-765-4321'),
(1007, '女', '1985-08-22', 'B987654325', 'jane.s2mith@example.com', '234-567-8201', NULL, '456 Oak St, City', 'Robert Smith', '098-765-4322'),
(1008, '男', '1988-02-10', 'C192837466', 'mark.j0ohnson@example.com', '345-678-3012', NULL, '789 Pine St, City', 'Laura Johnson', '098-765-4323'),
(1009, '女', '1993-11-05', 'D564738298', 'emily.4davis@example.com', '456-789-0223', NULL, '101 Maple St, City', 'Thomas Davis', '098-765-4324'),
(1010, '男', '1995-04-15', 'E837264519', 'michael0.brown@example.com', '567-890-2234', NULL, '202 Birch St, City', 'Jessica Brown', '098-765-4325'),
(1011, '男', '1990-05-14', 'A123456781', 'john.doe2@example.com', '123-456-1890', NULL, '123 Main St, City', 'Sarah Doe', '098-765-4321'),
(1012, '女', '1985-08-22', 'B987654322', 'jane.smit5h@example.com', '234-561-8901', NULL, '456 Oak St, City', 'Robert Smith', '098-765-4322'),
(1013, '男', '1988-02-10', 'C192837464', 'mark.joh1nson@example.com', '345-178-9012', NULL, '789 Pine St, City', 'Laura Johnson', '098-765-4323'),
(1014, '女', '1993-11-05', 'D564738297', 'emily.da7vis@example.com', '456-719-0123', NULL, '101 Maple St, City', 'Thomas Davis', '098-765-4324'),
(1015, '男', '1995-04-15', 'E837264516', 'michael.b1rown@example.com', '567-290-1234', NULL, '202 Birch St, City', 'Jessica Brown', '098-765-4325'),
(1016, '男', '1990-05-14', 'A123456788', 'john.do2e@example.com', '123-456-7390', NULL, '123 Main St, City', 'Sarah Doe', '098-765-4321'),
(1017, '女', '1985-08-22', 'B987654320', 'jane.smi5th@example.com', '234-567-2901', NULL, '456 Oak St, City', 'Robert Smith', '098-765-4322'),
(1018, '男', '1988-02-10', 'C192837461', 'mark.joh9nson@example.com', '345-671-9012', NULL, '789 Pine St, City', 'Laura Johnson', '098-765-4323'),
(1019, '女', '1993-11-05', 'D564738293', 'emily.dav0is@example.com', '456-789-3123', NULL, '101 Maple St, City', 'Thomas Davis', '098-765-4324'),
(1020, '男', '1995-04-15', 'E837264520', 'michael.b1rown@example.com', '567-895-1234', NULL, '202 Birch St, City', 'Jessica Brown', '098-765-4325'),
(1021, '男', '1990-05-14', 'A123456786', 'john.do2e@example.com', '123-456-7891', NULL, '123 Main St, City', 'Sarah Doe', '098-765-4321'),
(1022, '女', '1985-08-22', 'B987654921', 'jane.smith3@example.com', '234-567-8501', NULL, '456 Oak St, City', 'Robert Smith', '098-765-4322'),
(1023, '男', '1988-02-10', 'C192837865', 'mark.johnson1@example.com', '345-678-3012', NULL, '789 Pine St, City', 'Laura Johnson', '098-765-4323'),
(1024, '女', '1993-11-05', 'D564738791', 'emily.davis0@example.com', '456-789-0223', NULL, '101 Maple St, City', 'Thomas Davis', '098-765-4324');

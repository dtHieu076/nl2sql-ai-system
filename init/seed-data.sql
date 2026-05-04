-- 1. Insert Data Source (Kết nối tới HR Database)
INSERT INTO data_sources (name, db_type, jdbc_url, driver_class_name, username, password, status, assigned_role)
VALUES (
    'HR Database', 
    'postgresql', 
    'jdbc:postgresql://localhost:5432/hr_db', 
    'org.postgresql.Driver', 
    'admin', 
    'password123', 
    'ACTIVE', 
    'HR'
);

DO $$
DECLARE
    v_ds_id BIGINT;
    v_att_id BIGINT;
    v_ct_id BIGINT;
    v_emp_id BIGINT;
    v_ea_id BIGINT;
    v_ec_id BIGINT;
    v_es_id BIGINT;
    v_jt_id BIGINT;
    v_shift_id BIGINT;
    v_sr_id BIGINT;
    v_ss_id BIGINT;
    v_skill_id BIGINT;
    v_sc_id BIGINT;
    v_sg_id BIGINT;
BEGIN
    -- Lấy ID của data source vừa tạo
    SELECT id INTO v_ds_id FROM data_sources WHERE name = 'HR Database' ORDER BY id DESC LIMIT 1;

    -- ==========================================
    -- 2. INSERT TABLES METADATA
    -- ==========================================
    
    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'employee', 'Thông tin hồ sơ nhân viên cơ bản: mã NV, tên, email, ngày sinh, trạng thái') RETURNING id INTO v_emp_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'attendance', 'Dữ liệu chấm công hàng ngày: giờ vào/ra, số giờ làm, đi muộn, về sớm') RETURNING id INTO v_att_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'contract_template', 'Mẫu hợp đồng lao động: loại hợp đồng, thời hạn mặc định') RETURNING id INTO v_ct_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'employee_contract', 'Hợp đồng lao động thực tế của từng nhân viên: lương cơ bản, ngày ký, ngày hết hạn') RETURNING id INTO v_ec_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'employee_assignment', 'Quá trình công tác, phân công phòng ban, chức danh của nhân viên') RETURNING id INTO v_ea_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'job_title', 'Danh mục chức danh công việc, cấp bậc (grade level)') RETURNING id INTO v_jt_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'shift', 'Danh mục ca làm việc: giờ bắt đầu/kết thúc, thời gian nghỉ, quy định đi muộn') RETURNING id INTO v_shift_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'shift_rule', 'Luật tính công, làm thêm giờ (OT), làm cuối tuần cho từng ca') RETURNING id INTO v_sr_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'shift_scope', 'Phạm vi áp dụng ca làm việc cho phòng ban hoặc team nào') RETURNING id INTO v_ss_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'skill_group', 'Nhóm kỹ năng (VD: Soft skill, Hard skill, Management)') RETURNING id INTO v_sg_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'skill', 'Danh mục kỹ năng chi tiết, level tối đa') RETURNING id INTO v_skill_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'skill_criteria', 'Tiêu chí đánh giá cho từng kỹ năng') RETURNING id INTO v_sc_id;

    INSERT INTO tables_metadata (data_source_id, schema_name, table_name, table_description)
    VALUES (v_ds_id, 'public', 'employee_skill', 'Đánh giá kỹ năng hiện tại của nhân viên, cấp độ hiện tại, chứng chỉ') RETURNING id INTO v_es_id;

    -- ==========================================
    -- 3. INSERT COLUMNS METADATA (Các cột quan trọng cho NL2SQL)
    -- ==========================================

    -- Bảng Employee
    INSERT INTO columns_metadata (table_id, column_name, data_type, is_primary_key) VALUES 
    (v_emp_id, 'id', 'uuid', TRUE),
    (v_emp_id, 'employee_code', 'varchar', FALSE),
    (v_emp_id, 'full_name', 'varchar', FALSE),
    (v_emp_id, 'gender', 'varchar', FALSE),
    (v_emp_id, 'dob', 'date', FALSE),
    (v_emp_id, 'work_email', 'varchar', FALSE),
    (v_emp_id, 'status', 'varchar', FALSE),
    (v_emp_id, 'employment_type', 'varchar', FALSE);

    -- Bảng Attendance
    INSERT INTO columns_metadata (table_id, column_name, data_type, is_primary_key, is_foreign_key) VALUES 
    (v_att_id, 'id', 'uuid', TRUE, FALSE),
    (v_att_id, 'employee_id', 'uuid', FALSE, TRUE),
    (v_att_id, 'shift_id', 'uuid', FALSE, TRUE),
    (v_att_id, 'work_date', 'date', FALSE, FALSE),
    (v_att_id, 'check_in', 'timestamp', FALSE, FALSE),
    (v_att_id, 'check_out', 'timestamp', FALSE, FALSE),
    (v_att_id, 'worked_hours', 'numeric', FALSE, FALSE),
    (v_att_id, 'late_minutes', 'integer', FALSE, FALSE),
    (v_att_id, 'status', 'varchar', FALSE, FALSE);

    -- Bảng Employee Contract
    INSERT INTO columns_metadata (table_id, column_name, data_type, is_primary_key, is_foreign_key) VALUES 
    (v_ec_id, 'id', 'uuid', TRUE, FALSE),
    (v_ec_id, 'employee_id', 'uuid', FALSE, TRUE),
    (v_ec_id, 'contract_template_id', 'uuid', FALSE, TRUE),
    (v_ec_id, 'base_salary', 'numeric', FALSE, FALSE),
    (v_ec_id, 'start_date', 'date', FALSE, FALSE),
    (v_ec_id, 'end_date', 'date', FALSE, FALSE),
    (v_ec_id, 'status', 'varchar', FALSE, FALSE);

    -- Bảng Job Title
    INSERT INTO columns_metadata (table_id, column_name, data_type, is_primary_key) VALUES 
    (v_jt_id, 'id', 'uuid', TRUE),
    (v_jt_id, 'name', 'varchar', FALSE),
    (v_jt_id, 'grade_level', 'varchar', FALSE);

    -- Bảng Employee Assignment
    INSERT INTO columns_metadata (table_id, column_name, data_type, is_primary_key, is_foreign_key) VALUES 
    (v_ea_id, 'id', 'uuid', TRUE, FALSE),
    (v_ea_id, 'employee_id', 'uuid', FALSE, TRUE),
    (v_ea_id, 'job_title_id', 'uuid', FALSE, TRUE),
    (v_ea_id, 'department_name', 'varchar', FALSE, FALSE),
    (v_ea_id, 'from_date', 'date', FALSE, FALSE);

    -- Bảng Shift
    INSERT INTO columns_metadata (table_id, column_name, data_type, is_primary_key) VALUES 
    (v_shift_id, 'id', 'uuid', TRUE),
    (v_shift_id, 'code', 'varchar', FALSE),
    (v_shift_id, 'name', 'varchar', FALSE),
    (v_shift_id, 'start_time', 'time', FALSE),
    (v_shift_id, 'end_time', 'time', FALSE);

    -- Bảng Skill
    INSERT INTO columns_metadata (table_id, column_name, data_type, is_primary_key, is_foreign_key) VALUES 
    (v_skill_id, 'id', 'uuid', TRUE, FALSE),
    (v_skill_id, 'group_id', 'uuid', FALSE, TRUE),
    (v_skill_id, 'name', 'varchar', FALSE, FALSE),
    (v_skill_id, 'max_level', 'integer', FALSE, FALSE);

    -- Bảng Employee Skill
    INSERT INTO columns_metadata (table_id, column_name, data_type, is_primary_key, is_foreign_key) VALUES 
    (v_es_id, 'id', 'uuid', TRUE, FALSE),
    (v_es_id, 'employee_id', 'uuid', FALSE, TRUE),
    (v_es_id, 'skill_id', 'uuid', FALSE, TRUE),
    (v_es_id, 'current_level', 'integer', FALSE, FALSE),
    (v_es_id, 'certified', 'boolean', FALSE, FALSE);

    -- ==========================================
    -- 4. INSERT RELATIONSHIPS (Khóa ngoại)
    -- ==========================================
    
    INSERT INTO table_relationships (data_source_id, source_table_id, target_table_id, relationship_type) VALUES 
    -- Từ Attendance
    (v_ds_id, v_att_id, v_emp_id, 'MANY_TO_ONE'),
    (v_ds_id, v_att_id, v_shift_id, 'MANY_TO_ONE'),
    
    -- Từ Employee Contract
    (v_ds_id, v_ec_id, v_emp_id, 'MANY_TO_ONE'),
    (v_ds_id, v_ec_id, v_ct_id, 'MANY_TO_ONE'),

    -- Từ Employee Assignment
    (v_ds_id, v_ea_id, v_emp_id, 'MANY_TO_ONE'),
    (v_ds_id, v_ea_id, v_jt_id, 'MANY_TO_ONE'),

    -- Từ Shift (Rule & Scope)
    (v_ds_id, v_sr_id, v_shift_id, 'ONE_TO_ONE'),
    (v_ds_id, v_ss_id, v_shift_id, 'MANY_TO_ONE'),

    -- Từ Kỹ năng (Skill & Employee Skill)
    (v_ds_id, v_skill_id, v_sg_id, 'MANY_TO_ONE'),
    (v_ds_id, v_sc_id, v_skill_id, 'MANY_TO_ONE'),
    (v_ds_id, v_es_id, v_emp_id, 'MANY_TO_ONE'),
    (v_ds_id, v_es_id, v_skill_id, 'MANY_TO_ONE');

    -- ==========================================
    -- 5. INSERT PROMPT TEMPLATE MẶC ĐỊNH
    -- ==========================================
    INSERT INTO prompt_templates (name, template, is_active)
    VALUES ('Advanced HR NL2SQL', 'Bạn là trợ lý AI chuyên về SQL PostgreSQL cho hệ thống Nhân sự (HR). Hãy viết câu lệnh SQL tối ưu nhất để trả lời câu hỏi của người dùng dựa trên Schema sau: {{schema_info}}. Lưu ý JOIN các bảng dựa vào các khóa ngoại đã định nghĩa.', TRUE);

END $$;
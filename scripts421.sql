ALTER TABLE student
    ADD CONSTRAINT check_student_age CHECK (age >= 16);


ALTER TABLE student
    ADD CONSTRAINT student_name_unique UNIQUE (name),
    ALTER COLUMN name SET NOT NULL;


ALTER TABLE faculty
    ADD CONSTRAINT faculty_name_color_unique UNIQUE (name, color);


ALTER TABLE student
    ALTER COLUMN age SET DEFAULT 20;
USE english_learning_system;

-- 更新homework_submissions表的字段类型
ALTER TABLE homework_submissions MODIFY COLUMN content TEXT;
ALTER TABLE homework_submissions MODIFY COLUMN image TEXT;
ALTER TABLE homework_submissions MODIFY COLUMN audio TEXT;

-- 验证更新结果
DESCRIBE homework_submissions;
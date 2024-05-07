ALTER TABLE judge_calendar_rules ADD CONSTRAINT unique_judge_date_constraint UNIQUE (judge_id, date);

CREATE TABLE judge_calendar_rules (


    judge_id                varchar(64),
    judge_calendar_id       varchar(64),
    rule_type               varchar(64),
    date                    varchar(64),
    notes                   varchar(64),
    CONSTRAINT pk_judge_calendar_rules_id PRIMARY KEY (judge_calendar_id)


);
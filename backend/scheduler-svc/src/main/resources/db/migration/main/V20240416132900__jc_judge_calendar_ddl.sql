CREATE TABLE judge_calendar_rules (

    judgeId              varchar(64),
    id                   varchar(64),
    ruleType             varchar(64),
    date                 varchar(64),
    notes                varchar(2000),
    createdBy            character varying(64),
    createdTime          bigint,
    lastModifiedBy       character varying(64),
    lastModifiedTime     bigint,
    rowVersion           bigint,
    tenantId             character varying(1000),
    CONSTRAINT pk_judge_calendar_rules_id PRIMARY KEY (id)


);
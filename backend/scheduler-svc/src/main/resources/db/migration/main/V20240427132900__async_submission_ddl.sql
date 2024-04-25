CREATE TABLE async_submission (
    court_id            VARCHAR(64),
    case_id             VARCHAR(64),
    async_submission_id VARCHAR(64) PRIMARY KEY,
    submission_type     VARCHAR(64),
    title               VARCHAR(500),
    description         text,
    status              VARCHAR(64),
    submission_date     date,
    response_date       date,
    created_by          VARCHAR(64),
    created_time        bigint,
    last_modified_by    VARCHAR(64),
    last_modified_time  bigint,
    row_version         int4,
    tenant_id           VARCHAR(12)
);
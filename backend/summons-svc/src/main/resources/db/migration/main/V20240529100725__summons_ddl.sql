CREATE TABLE summons (
    summons_id VARCHAR(255) PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    order_type VARCHAR(50) NOT NULL,
    channel_name VARCHAR(255) NOT NULL,
    is_accepted_by_channel BOOLEAN NOT NULL,
    channel_acknowledgement_id VARCHAR(255),
    request_date TIMESTAMP NOT NULL,
    status_of_delivery VARCHAR(255) NOT NULL,
    additional_fields jsonb NULL,
    created_by varchar(64) NULL,
    last_modified_by varchar(64) NULL,
    created_time int8 NULL,
    last_modified_time int8 NULL,
    row_version int4 NULL
);

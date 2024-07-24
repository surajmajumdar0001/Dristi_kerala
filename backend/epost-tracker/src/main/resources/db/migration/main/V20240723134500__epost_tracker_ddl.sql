CREATE TABLE dristi_epost_tracker (
    process_number varchar(64) NOT NULL PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    file_store_id varchar(64) NOT NULL,
    task_number varchar(64) NOT NULL,
    tracking_number varchar(64) NOT NULL,
    pincode varchar(64) NOT NULL,
    address varchar(1000) NOT NULL,
    delivery_status varchar(64) NULL,
    remarks varchar(64) NOT NULL,
    additionalDetails jsonb NULL,
    row_version int4 NULL,
    booking_date varchar(64) NOT NULL,
    received_date varchar(64) NOT NULL,
    createdBy varchar(64) NULL,
    lastModifiedBy varchar(64) NULL,
    createdTime int8 NULL,
    lastModifiedTime int8 NULL
);
CREATE TABLE hearing_booking
(

    court_id                character varying(64),
    judge_id                character varying(64),
    case_id                 character varying(64),
    hearing_booking_id      character varying(64),
    date                    date,
    event_type              character varying(64),
    title                   character varying(512),
    description             text,
    status                  character varying(64),
    start_time              timestamp,
    end_time                timestamp,
    created_by              character varying(64),
    created_time            bigint,
    last_modified_by        character varying(64),
    last_modified_time      bigint,
    row_version             bigint,
    tenant_id               character varying(64),

    CONSTRAINT pk_hearing_booking_id PRIMARY KEY (hearing_booking_id)

);
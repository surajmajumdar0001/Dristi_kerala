CREATE TABLE hearing_booking
(

    court_id                character varying(64),
    judge_id                character varying(64),
    case_id                 character varying(64),
    hearing_booking_id      character varying(64),
    date                    character varying(64),
    event_type              character varying(64),
    title                   character varying(64),
    description             character varying(64),
    status                  character varying(64),
    start_time              character varying(64),
    end_time                character varying(64),

    CONSTRAINT pk_hearing_booking_id PRIMARY KEY (hearing_booking_id)

);
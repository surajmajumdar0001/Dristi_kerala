CREATE TABLE hearing_booking_reschedule_request (

    hearing_booking_id              character varying(64),
    reschedule_request_id           character varying(64),
    requester_id                    character varying(64),
    reason                          character varying(64),
    status                          character varying(64),
    action_comment                  character varying(64),

    CONSTRAINT pk_hearing_booking_reschedule_request_id PRIMARY KEY (reschedule_request_id)

);
CREATE TABLE hearing_booking_reschedule_request (

    hearingBookingId        character varying(64),
    rescheduleRequestId     character varying(64),
    requesterId             character varying(64),
    reason                  character varying(2000),
    status                  character varying(64),
    actionComment           character varying(2000),
    createdBy               character varying(64),
    createdTime             bigint,
    lastModifiedBy          character varying(64),
    lastModifiedTime        bigint,
    rowVersion              bigint,
    tenantId                character varying(1000),
    courtId                 character varying(64),
    judgeId                 character varying(64),
    documents               jsonb,

    CONSTRAINT pk_hearing_booking_reschedule_request_id PRIMARY KEY (rescheduleRequestId)

);
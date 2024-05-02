CREATE TABLE hearing_booking
(

    courtId              character varying(64),
    judgeId              character varying(64),
    caseId               character varying(64),
    hearingBookingId     character varying(64),
    date                 date,
    eventType            character varying(64),
    title                character varying(2000),
    description          character varying(2000),
    status               character varying(64),
    startTime            timestamp,
    endTime              timestamp,
    createdBy            character varying(64),
    createdTime          bigint,
    lastModifiedBy       character varying(64),
    lastModifiedTime     bigint,
    rowVersion           bigint,
    tenantId             character varying(1000),

    CONSTRAINT pk_hearing_booking_id PRIMARY KEY (hearingBookingId)

);
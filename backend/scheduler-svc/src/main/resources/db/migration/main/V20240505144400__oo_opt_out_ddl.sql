CREATE TABLE opt_out
(

    id                              character varying(64),
    individualId                    character varying(64),
    judgeId                         character varying(64),
    caseId                          character varying(64),
    reschedulerequestid             character varying(64),
    optoutDates                     jsonb
    createdBy                       character varying(64),
    createdTime                     bigint,
    lastModifiedBy                  character varying(64),
    lastModifiedTime                bigint,
    rowVersion                      bigint,
    tenantId                        character varying(1000),

    CONSTRAINT pk_hearing_booking_id PRIMARY KEY (hearingBookingId)

);
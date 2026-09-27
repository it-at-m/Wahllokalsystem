CREATE OR REPLACE FUNCTION uuid_v4_formatted_sys_guid RETURN VARCHAR2 IS
    g RAW(16) := SYS_GUID();
    v VARCHAR2(36);
BEGIN
    SELECT LOWER(
                   SUBSTR(hex, 1, 8)  || '-' || SUBSTR(hex, 9, 4)   || '-' ||
                   SUBSTR(hex, 13, 4) || '-' || SUBSTR(hex, 17, 4)  || '-' ||
                   SUBSTR(hex, 21, 12)
           ) AS display_uuid
    INTO v
    FROM (SELECT RAWTOHEX(SYS_GUID()) AS hex FROM DUAL);

    RETURN v;
END;
/
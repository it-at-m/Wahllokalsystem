INSERT INTO Authority
VALUES ('68d798ca-ea64-49ce-a433-becd7ebb4c98', 'WLS_WAHLVORSTAND');

INSERT INTO Permission
VALUES ('68d798ca-ea64-49ce-a433-becd7ebb4c98', 'permission1');
INSERT INTO Permission
VALUES ('68d798ca-ea64-49ce-a433-becd7ebb4c99', 'permission2');

-- link authority with permissions
INSERT INTO Secauthorities_Secpermissions
VALUES ('68d798ca-ea64-49ce-a433-becd7ebb4c98', '68d798ca-ea64-49ce-a433-becd7ebb4c98');
INSERT INTO Secauthorities_Secpermissions
VALUES ('68d798ca-ea64-49ce-a433-becd7ebb4c98', '68d798ca-ea64-49ce-a433-becd7ebb4c99');
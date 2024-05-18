CREATE DATABASE integrated1 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE status (
statusId INTEGER AUTO_INCREMENT PRIMARY KEY,
statusName VARCHAR(50) NOT NULL UNIQUE DEFAULT 'No Status',
statusDescription VARCHAR(200),
statusColor VARCHAR(20) DEFAULT '#CCCCCC',
createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE task (
taskId INTEGER AUTO_INCREMENT PRIMARY KEY,
taskTitle VARCHAR(100) NOT NULL,
taskDescription VARCHAR(500),
taskAssignees VARCHAR(30),
statusId INTEGER NOT NULL,
createdOn DATETIME DEFAULT CURRENT_TIMESTAMP,
updatedOn DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (statusId) REFERENCES status (statusId) ON DELETE RESTRICT
);

INSERT INTO status(statusName,statusDescription,statusColor)
VALUES ('No Status','The default status','#CCCCCC');

INSERT INTO status(statusName,statusDescription,statusColor)
VALUES ('To Do',null,'#FFA500');

INSERT INTO status(statusName,statusDescription,statusColor)
VALUES ('Doing','Being worked on','#4169E1');

INSERT INTO status(statusName,statusDescription,statusColor)
VALUES ('Done','Finished','#008000');

select * from status;

INSERT INTO task (taskTitle, taskDescription, taskAssignees, statusId, createdOn, updatedOn)
VALUES (
'TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0',
'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5',
'Assignees1Assignees2Assignees3',
1,
STR_TO_DATE('22/4/2024 09:00:00', '%d/%m/%Y %H:%i:%s'),
STR_TO_DATE('22/4/2024 09:00:00', '%d/%m/%Y %H:%i:%s')
);

INSERT INTO task (taskTitle, taskDescription, taskAssignees, statusId, createdOn, updatedOn)
VALUES (
'Repository',
null,
null,
2,
STR_TO_DATE('22/4/2024 09:05:00', '%d/%m/%Y %H:%i:%s'),
STR_TO_DATE('22/4/2024 14:00:00', '%d/%m/%Y %H:%i:%s')
);

INSERT INTO task (taskTitle, taskDescription, taskAssignees, statusId, createdOn, updatedOn)
VALUES (
'ดาต้าเบส',
'ສ້າງຖານຂໍ້ມູນ',
'あなた、彼、彼女 (私ではありません)',
3,
STR_TO_DATE('22/4/2024 09:10:00', '%d/%m/%Y %H:%i:%s'),
STR_TO_DATE('25/4/2024 00:00:00', '%d/%m/%Y %H:%i:%s')
);

INSERT INTO task (taskTitle, taskDescription, taskAssignees, statusId, createdOn, updatedOn)
VALUES (
'Infrastructure',
'Setup containers',
'ไก่งวง กับ เพนกวิน',
4,
STR_TO_DATE('22/4/2024 09:15:00', '%d/%m/%Y %H:%i:%s'),
STR_TO_DATE('22/4/2024 10:00:00', '%d/%m/%Y %H:%i:%s')
);

commit;

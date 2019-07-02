--This script is used for unit test cases, DO NOT CHANGE!
DROP TABLE IF EXISTS User;

CREATE TABLE User (UserId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
 UserName VARCHAR(30) NOT NULL,
 EmailAddress VARCHAR(30) NOT NULL,
 Status VARCHAR(8),
 CreatedOn TIMESTAMP WITHOUT TIME ZONE,
 ModifiedOn TIMESTAMP WITHOUT TIME ZONE);

CREATE UNIQUE INDEX idx_un on User(UserName);

CREATE UNIQUE INDEX idx_ea on User(EmailAddress);

INSERT INTO User (UserName, EmailAddress, Status, CreatedOn) VALUES ('shashank','shashank@gmail.com', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO User (UserName, EmailAddress, Status, CreatedOn) VALUES ('arun','arun@gmail.com', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO User (UserName, EmailAddress, Status, CreatedOn) VALUES ('ravi','ravi@gmail.com', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO User (UserName, EmailAddress, Status, CreatedOn) VALUES ('ali','ali@gmail.com', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO User (UserName, EmailAddress, Status, CreatedOn) VALUES ('sachin','sachin@gmail.com', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO User (UserName, EmailAddress, Status, CreatedOn) VALUES ('virat','virat@gmail.com', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO User (UserName, EmailAddress, Status, CreatedOn) VALUES ('non_account_user','non_account_user@gmail.com', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO User (UserName, EmailAddress, Status, CreatedOn, ModifiedOn) VALUES ('non_active','non_active@gmail.com', 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

DROP TABLE IF EXISTS Account;

CREATE TABLE Account (AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
UserId LONG,
Balance DECIMAL(19,4),
CurrencyCode VARCHAR(30),
Status VARCHAR(8),
CreatedOn TIMESTAMP WITHOUT TIME ZONE,
ModifiedOn TIMESTAMP WITHOUT TIME ZONE,
FOREIGN KEY (UserId) REFERENCES User(UserId)
);

CREATE UNIQUE INDEX idx_acc on Account(UserId);

INSERT INTO Account (UserId,Balance,CurrencyCode, Status, CreatedOn) VALUES (1,100.00,'USD', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO Account (UserId,Balance,CurrencyCode, Status, CreatedOn) VALUES (2,200.00,'USD', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO Account (UserId,Balance,CurrencyCode, Status, CreatedOn) VALUES (3,500.00,'INR', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO Account (UserId,Balance,CurrencyCode, Status, CreatedOn) VALUES (4,500.00,'INR', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO Account (UserId,Balance,CurrencyCode, Status, CreatedOn) VALUES (5,500.00,'GBP', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO Account (UserId,Balance,CurrencyCode, Status, CreatedOn) VALUES (6,500.00,'GBP', 'ACTIVE', CURRENT_TIMESTAMP);
INSERT INTO Account (UserId,Balance,CurrencyCode, Status, CreatedOn, ModifiedOn) VALUES (8,500.00,'GBP', 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


DROP TABLE IF EXISTS Transactions;

CREATE TABLE Transactions (
TransactionId VARCHAR(36) PRIMARY KEY NOT NULL,
SenderAccountId LONG,
ReceiverAccountId LONG,
CreatedOn TIMESTAMP WITHOUT TIME ZONE,
TransactionDate TIMESTAMP WITHOUT TIME ZONE,
Amount DECIMAL(19,4),
serviceType VARCHAR(20)
);

DROP TABLE IF EXISTS TransactionDetails;

CREATE TABLE TransactionDetails (
TransactionDetailsId VARCHAR(36) PRIMARY KEY NOT NULL,
TransactionId VARCHAR(36),
AccountId LONG,
CreatedOn TIMESTAMP WITHOUT TIME ZONE,
SequenceNo INT,
EntryType VARCHAR(2)
);
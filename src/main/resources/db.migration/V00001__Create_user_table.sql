-- TBotBase.Users definition

CREATE TABLE `Users` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `firstName` varchar(100) DEFAULT NULL,
                         `isBot` tinyint(1) DEFAULT NULL,
                         `lastName` varchar(100) DEFAULT NULL,
                         `userName` varchar(100) DEFAULT NULL,
                         `languageCode` varchar(3) DEFAULT NULL,
                         `canJoinGroups` tinyint(1) DEFAULT NULL,
                         `canReadAllGroupMessages` tinyint(1) DEFAULT NULL,
                         `supportInlineQueries` tinyint(1) DEFAULT NULL,
                         `isPremium` tinyint(1) DEFAULT NULL,
                         `addedToAttachmentMenu` tinyint(1) DEFAULT NULL,
                         `tgId` int NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
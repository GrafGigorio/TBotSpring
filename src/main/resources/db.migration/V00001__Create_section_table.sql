-- TBotBase.`section` definition

CREATE TABLE `section` (
                           `id` int NOT NULL,
                           `fatherId` int DEFAULT NULL,
                           `title` int DEFAULT NULL,
                           `shopId` int DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `section_pk` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
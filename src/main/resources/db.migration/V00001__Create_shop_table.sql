-- TBotBase.shop definition

CREATE TABLE `shop` (
                        `id` int NOT NULL,
                        `mastertgid` int DEFAULT NULL,
                        `title` varchar(1000) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `shops_pk` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
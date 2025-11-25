-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Generation Time: Nov 25, 2025 at 04:29 PM
-- Server version: 8.0.43
-- PHP Version: 8.3.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `KinoKatalog`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_review` (IN `p_user_id` INT, IN `p_movie_id` INT, IN `p_rating` INT, IN `p_text` TEXT)   BEGIN
  INSERT INTO reviews (user_id, movie_id, rating, review_text, created_at)
  VALUES (p_user_id, p_movie_id, p_rating, p_text, NOW());

  CALL update_movie_avg_rating(p_movie_id);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `get_movie_details` (IN `p_movie_id` INT)   BEGIN
  SELECT
    m.id,
    m.title,
    m.release_date,
    m.runtime,
    get_avg_rating(m.id) AS average_rating,
    GROUP_CONCAT(g.name SEPARATOR ', ') AS genres
  FROM movies m
  LEFT JOIN movie_genres mg ON mg.movie_id = m.id
  LEFT JOIN genres g ON g.id = mg.genre_id
  WHERE m.id = p_movie_id
  GROUP BY m.id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `update_movie_avg_rating` (IN `p_movie_id` INT)   BEGIN
  DECLARE new_avg DECIMAL(3,2);
  SELECT get_avg_rating(p_movie_id) INTO new_avg;
  UPDATE movies SET average_rating = new_avg WHERE id = p_movie_id;
END$$

--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `get_avg_rating` (`p_movie_id` INT) RETURNS DECIMAL(3,2) DETERMINISTIC BEGIN
  DECLARE avg_rating DECIMAL(3,2);
  SELECT ROUND(AVG(rating), 2)
  INTO avg_rating
  FROM reviews
  WHERE movie_id = p_movie_id;
  RETURN IFNULL(avg_rating, 0.0);
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `user_review_count` (`p_user_id` INT) RETURNS INT DETERMINISTIC BEGIN
  DECLARE total INT;
  SELECT COUNT(*) INTO total FROM reviews WHERE user_id = p_user_id;
  RETURN total;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `collections`
--

CREATE TABLE `collections` (
  `id` int NOT NULL,
  `user_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(5000) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `collection_movies`
--

CREATE TABLE `collection_movies` (
  `id` int NOT NULL,
  `collection_id` int NOT NULL,
  `movie_id` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

CREATE TABLE `comments` (
  `id` int NOT NULL,
  `review_id` int NOT NULL,
  `user_id` int NOT NULL,
  `comment_text` text NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Triggers `comments`
--
DELIMITER $$
CREATE TRIGGER `trg_before_insert_comment` BEFORE INSERT ON `comments` FOR EACH ROW BEGIN
  IF NEW.comment_text IS NULL OR CHAR_LENGTH(TRIM(NEW.comment_text)) = 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Comment text cannot be empty';
  END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Stand-in structure for view `comment_thread_view`
-- (See below for the actual view)
--
CREATE TABLE `comment_thread_view` (
`comment_id` int
,`comment_text` text
,`commenter` varchar(50)
,`created_at` datetime
,`movie_title` varchar(512)
,`review_id` int
,`review_text` varchar(5000)
);

-- --------------------------------------------------------

--
-- Table structure for table `companies`
--

CREATE TABLE `companies` (
  `id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `origin_country` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `companies`
--

INSERT INTO `companies` (`id`, `name`, `origin_country`) VALUES
(2, 'Walt Disney Pictures', 'US'),
(3, 'Pixar', 'US'),
(4, 'Paramount Pictures', 'US'),
(5, 'Columbia Pictures', 'US'),
(12, 'New Line Cinema', 'US'),
(33, 'Universal Pictures', 'US'),
(56, 'Amblin Entertainment', 'US'),
(81, 'Plan B Entertainment', 'US'),
(130, 'Jerry Bruckheimer Films', 'US'),
(174, 'Warner Bros. Pictures', 'US'),
(178, 'Ghoulardi Film Company', 'US'),
(327, 'Nordisk Film Sweden', 'SE'),
(333, 'Original Film', 'US'),
(420, 'Marvel Studios', 'US'),
(429, 'DC', 'US'),
(487, 'ÄŒeskÃ¡ televize', 'CZ'),
(497, 'Revolution Studios', 'US'),
(521, 'DreamWorks Animation', 'US'),
(551, 'Mandalay Pictures', 'US'),
(738, 'Myriad Pictures', 'US'),
(829, 'Vertigo Entertainment', 'US'),
(923, 'Legendary Pictures', 'US'),
(955, 'Nikkatsu Corporation', 'JP'),
(1020, 'Millennium Media', 'US'),
(1063, 'Brightlight Pictures', 'CA'),
(1632, 'Lionsgate', 'US'),
(1761, 'Elsani Film', 'DE'),
(2251, 'Sony Pictures Animation', 'US'),
(2527, 'Marc Platt Productions', 'US'),
(2531, 'MRC', 'US'),
(2785, 'Warner Bros. Animation', 'US'),
(2844, 'Prospero Pictures', 'CA'),
(2883, 'Aniplex', 'JP'),
(2918, 'Shueisha', 'JP'),
(3096, 'Tyler Perry Studios', 'US'),
(3287, 'Screen Gems', 'US'),
(3393, 'Huayi Brothers Pictures', 'CN'),
(3528, 'Thunder Road', 'US'),
(3835, 'Odyssey Motion Pictures', 'US'),
(5887, 'ufotable', 'JP'),
(6458, 'INCAA', 'AR'),
(8288, 'Zentropa International Sweden', 'SE'),
(8789, 'Fuzzy Door Productions', 'US'),
(9965, 'Ãnima Estudios', 'MX'),
(9993, 'DC Entertainment', 'US'),
(9996, 'Syncopy', 'GB'),
(11221, 'Victory Man', 'CA'),
(11407, 'Baltimore Pictures', 'US'),
(11565, 'The Safran Company', 'US'),
(11658, 'Brainstorm Media', 'US'),
(11761, 'Mark Canton Productions', 'US'),
(12006, 'Goldmann Pictures', 'US'),
(13692, 'Zazi Films', 'FR'),
(13769, 'Lynda Obst Productions', 'US'),
(14589, 'Code Entertainment', 'US'),
(17094, 'Princess Pictures', 'AU'),
(17818, 'Beijing Enlight Pictures', 'CN'),
(18609, 'BoulderLight Pictures', 'US'),
(19456, 'Envision Media Arts', 'US'),
(19647, 'Monolith Pictures', 'US'),
(21444, 'MAPPA', 'JP'),
(21777, 'TC Productions', 'US'),
(21979, 'Mer Film', 'NO'),
(22213, 'TSG Entertainment', 'US'),
(23008, '87Eleven', 'US'),
(24701, 'Pink Productions', 'CZ'),
(24870, 'Endorfilm', 'CZ'),
(25702, 'Cliffbrook Films', 'US'),
(33603, 'Shivhans Pictures', 'US'),
(33768, 'The Solution', 'US'),
(44973, 'Badhouse Studios Mexico', 'MX'),
(48460, 'Youku', 'CN'),
(48738, 'Campbell Grobman Films', 'US'),
(48788, 'Practical Pictures', 'US'),
(51112, 'Lava Films', 'PL'),
(59827, 'Patrick Aiello Productions', 'US'),
(73162, 'Greendale Productions', 'CA'),
(76907, 'Atomic Monster', 'US'),
(82552, 'Elevation Pictures', 'CA'),
(82819, 'Skydance Media', 'US'),
(86647, 'OPE Partners', 'US'),
(92394, 'Well Go USA Entertainment', 'US'),
(94107, 'MMC Movies', 'DE'),
(94218, 'Troll Court Entertainment', 'US'),
(100071, 'Shine Screens', 'IN'),
(101034, 'Jaigantic Studios', 'US'),
(104363, '30WEST', 'US'),
(109501, 'Bazelevs', 'US'),
(114336, 'Reese Wernick Productions', 'US'),
(118657, 'Korokoro', 'FR'),
(118854, 'Rideback', 'US'),
(119056, 'Particular Crowd', 'US'),
(121470, '87North Productions', 'US'),
(123230, 'Eighty Two Films', 'US'),
(123244, 'Picturestart', 'US'),
(126602, 'See Ãt Film', 'KR'),
(127928, '20th Century Studios', 'US'),
(133093, 'Monarch Media', 'US'),
(136126, 'Phase 4 Productions', 'FR'),
(141986, 'Perspectiva Audiovisual', 'AR'),
(149142, 'Vivamax', 'PH'),
(158529, 'Ethea Entertainment', 'US'),
(161357, 'about:blank', 'US'),
(161769, 'Beijing Enlight Media', 'CN'),
(162093, 'MOTOR', 'DK'),
(167789, 'Chengdu Coco Cartoon', 'CN'),
(168181, 'Tango Entertainment', 'US'),
(171066, 'Scanbox Production', 'DK'),
(172841, 'Ryder Picture Company', 'US'),
(173343, 'Nippon Top Art', 'JP'),
(176762, 'Kevin Feige Productions', 'US'),
(177082, 'Chatrone', 'US'),
(178086, 'One Way Ticket Films', 'CZ'),
(184898, 'DC Studios', 'US'),
(186769, 'Viaplay Studios', 'SE'),
(194232, 'Apple Studios', 'US'),
(198278, 'Federation Studios', 'FR'),
(199610, 'Pelikula Indiopendent', NULL),
(199632, 'Dawn Apollo Films', 'US'),
(210819, 'QWGmire', 'US'),
(212004, 'Dukkah Producciones', 'AR'),
(216687, 'Domain Entertainment', 'US'),
(218924, 'Mediefondet Zefyr', 'NO'),
(221429, 'Media Capital Technologies', 'US'),
(221878, '1.21 Entertainment', 'US'),
(237412, 'AmorFortuna', 'US'),
(238345, 'Brand in Motion', NULL),
(238346, 'Myriad Entertainment Corp.', NULL),
(238347, 'Reality RR Studios', NULL),
(240675, 'Subconscious', 'US'),
(243953, 'Play by Play Entertainment', 'CA'),
(243955, 'Peacemaker Filmworks', 'CA'),
(247983, 'Watermark Media', 'US'),
(251954, 'Valencia Producciones FX', NULL),
(252366, 'Freshman Year', 'US'),
(252367, 'Fireside Films', 'US'),
(261808, 'TMeng Pictures', NULL),
(264766, 'Bespoke Production Capital', 'US'),
(264767, 'No Comp Films', NULL),
(266787, 'Republic Pictures', 'US'),
(269742, 'Elsani & Neary Media', 'DE'),
(270079, 'Courtney Solomon Productions', NULL),
(270104, 'Project Foxtrot', 'US'),
(273292, 'Chengdu Zizai Jingjie Culture Media', 'CN'),
(273293, 'Beijing Coloroom Technology', 'CN');

-- --------------------------------------------------------

--
-- Table structure for table `genres`
--

CREATE TABLE `genres` (
  `id` int NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `genres`
--

INSERT INTO `genres` (`id`, `name`) VALUES
(28, 'Action'),
(12, 'Adventure'),
(16, 'Animation'),
(35, 'Comedy'),
(80, 'Crime'),
(99, 'Documentary'),
(18, 'Drama'),
(10751, 'Family'),
(14, 'Fantasy'),
(36, 'History'),
(27, 'Horror'),
(10402, 'Music'),
(9648, 'Mystery'),
(10749, 'Romance'),
(878, 'Science Fiction'),
(53, 'Thriller'),
(10770, 'TV Movie'),
(10752, 'War'),
(37, 'Western');

-- --------------------------------------------------------

--
-- Table structure for table `movies`
--

CREATE TABLE `movies` (
  `id` int NOT NULL,
  `tmdb_id` int DEFAULT NULL,
  `title` varchar(512) DEFAULT NULL,
  `overview` varchar(5000) DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `runtime` int DEFAULT NULL,
  `average_rating` decimal(3,2) DEFAULT '0.00',
  `review_count` int DEFAULT '0',
  `poster_url` varchar(1024) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `movies`
--

INSERT INTO `movies` (`id`, `tmdb_id`, `title`, `overview`, `release_date`, `runtime`, `average_rating`, `review_count`, `poster_url`, `created_at`) VALUES
(155, 155, 'The Dark Knight', NULL, '2008-07-16', 152, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(7451, 7451, 'xXx', NULL, '2002-08-09', 124, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(13494, 13494, 'Red Sonja', NULL, '2025-07-31', 110, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(157336, 157336, 'Interstellar', NULL, '2014-11-05', 169, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(506763, 506763, 'Detective Dee: The Four Heavenly Kings', NULL, '2018-07-27', 132, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(541671, 541671, 'Ballerina', NULL, '2025-06-04', 125, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(552524, 552524, 'Lilo & Stitch', NULL, '2025-05-17', 108, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(574475, 574475, 'Final Destination Bloodlines', NULL, '2025-05-14', 110, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(575265, 575265, 'Mission: Impossible - The Final Reckoning', NULL, '2025-05-17', 170, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(604079, 604079, 'The Long Walk', NULL, '2025-09-10', 108, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(617126, 617126, 'The Fantastic 4: First Steps', NULL, '2025-07-22', 115, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(691363, 691363, 'The Thing Behind The Door', NULL, '2023-01-16', 81, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(715287, 715287, 'Stepmom\'s Desire', NULL, '2020-05-29', 78, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(755898, 755898, 'War of the Worlds', NULL, '2025-07-29', 91, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(803796, 803796, 'KPop Demon Hunters', NULL, '2025-06-20', 96, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(828769, 828769, 'Undryable Younger Cousin', NULL, '2021-02-01', 70, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(911430, 911430, 'F1', NULL, '2025-06-25', 156, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(914215, 914215, 'Humane', NULL, '2024-04-26', 93, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(938086, 938086, 'Dangerous Younger Cousin', NULL, '2021-11-23', 71, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(980477, 980477, 'Ne Zha 2', NULL, '2025-01-29', 144, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(987400, 987400, 'Aztec Batman: Clash of Empires', NULL, '2025-09-18', 89, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(993234, 993234, 'Borders of Love', NULL, '2022-11-03', 95, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(994682, 994682, 'Sexy Oral: Uwakina Kuchibiru', NULL, '1984-02-17', 51, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1007734, 1007734, 'Nobody 2', NULL, '2025-08-13', 89, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1009640, 1009640, 'Valiant One', NULL, '2025-01-30', 86, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1022787, 1022787, 'Elio', NULL, '2025-06-18', 97, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1028248, 1028248, 'As Good as Dead', NULL, '2022-12-16', 88, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1035259, 1035259, 'The Naked Gun', NULL, '2025-07-30', 85, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1038392, 1038392, 'The Conjuring: Last Rites', NULL, '2025-09-03', 135, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1051486, 1051486, 'Stockholm Bloodbath', NULL, '2024-01-19', 145, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1054867, 1054867, 'One Battle After Another', NULL, '2025-09-23', 162, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1061474, 1061474, 'Superman', NULL, '2025-07-09', 130, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1078605, 1078605, 'Weapons', NULL, '2025-08-04', 129, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1083433, 1083433, 'I Know What You Did Last Summer', NULL, '2025-07-16', 111, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1087192, 1087192, 'How to Train Your Dragon', NULL, '2025-06-06', 125, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1119878, 1119878, 'Ice Road: Vengeance', NULL, '2025-06-27', 113, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1151334, 1151334, 'Eenie Meanie', NULL, '2025-08-21', 106, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1175942, 1175942, 'The Bad Guys 2', NULL, '2025-07-24', 104, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1188808, 1188808, 'Tuhog', NULL, '2023-11-03', 92, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1215020, 1215020, 'American Sweatshop', NULL, '2025-09-19', 93, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1218925, 1218925, 'Chainsaw Man - The Movie: Reze Arc', NULL, '2025-09-19', 100, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1234821, 1234821, 'Jurassic World Rebirth', NULL, '2025-07-01', 134, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1242011, 1242011, 'Together', NULL, '2025-07-28', 101, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1265344, 1265344, 'Swiped', NULL, '2025-09-09', 111, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1267319, 1267319, 'Mantis', NULL, '2025-09-26', 113, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1284120, 1284120, 'The Ugly Stepsister', NULL, '2025-03-07', 109, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1289888, 1289888, 'French Lover', NULL, '2025-09-25', 122, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1311031, 1311031, 'Demon Slayer: Kimetsu no Yaiba Infinity Castle', NULL, '2025-07-18', 156, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1319965, 1319965, 'All of You', NULL, '2025-09-26', 98, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1328803, 1328803, 'Prisoner of War', NULL, '2025-09-19', 113, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1337395, 1337395, 'Kiskisan', NULL, '2024-09-27', 60, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1339658, 1339658, 'Oh, Hi!', NULL, '2025-07-25', 95, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1369679, 1369679, 'Get Fast', NULL, '2024-12-12', 88, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1371189, 1371189, 'Ruth & Boaz', NULL, '2025-09-25', NULL, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1382406, 1382406, 'Striking Rescue', NULL, '2024-12-05', 106, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1403735, 1403735, 'Laila', NULL, '2025-02-14', 134, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1447287, 1447287, 'Donde tÃº quieras', NULL, '2025-03-20', NULL, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1450529, 1450529, 'Gunman', NULL, '2025-06-12', 80, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1498658, 1498658, 'Mantis', NULL, '2025-09-19', 10, 0.00, 0, NULL, '2025-11-25 15:48:53'),
(1506456, 1506456, 'Maalikaya', NULL, '2025-07-25', 70, 0.00, 0, NULL, '2025-11-25 15:48:53');

-- --------------------------------------------------------

--
-- Table structure for table `movie_cast`
--

CREATE TABLE `movie_cast` (
  `id` int NOT NULL,
  `movie_id` int DEFAULT NULL,
  `person_id` int DEFAULT NULL,
  `movie_character` varchar(255) DEFAULT NULL,
  `billing_order` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `movie_cast`
--

INSERT INTO `movie_cast` (`id`, `movie_id`, `person_id`, `movie_character`, `billing_order`) VALUES
(1, 617126, 1253360, 'Reed Richards / Mister Fantastic', 0),
(2, 617126, 556356, 'Sue Storm / Invisible Woman', 1),
(3, 617126, 21042, 'Ben Grimm / The Thing', 2),
(4, 617126, 1597365, 'Johnny Storm / Human Torch', 3),
(5, 617126, 202032, 'Galactus', 4),
(6, 617126, 936970, 'Shalla-Bal / Silver Surfer', 5),
(7, 617126, 1294982, 'Harvey Elder / Mole Man', 6),
(8, 617126, 10871, 'Rachel Rozman', 7),
(9, 617126, 65447, 'Lynne Nichols / F4 Chief of Staff', 8),
(10, 617126, 34546, 'Ted Gilbert', 9),
(11, 1267319, 1296713, 'Lee Han-wool / Mantis', 0),
(12, 1267319, 1983073, 'Shin Jae-yi', 1),
(13, 1267319, 1566012, 'Dok-go', 2),
(14, 1267319, 85008, 'Cha Min-gyu', 3),
(15, 1267319, 20737, 'Gil Bok-soon', 4),
(16, 1267319, 1572347, 'Nam Bae-soo', 5),
(17, 1267319, 3792104, 'Soo-min', 6),
(18, 1267319, 3794191, 'Puma Lee', 7),
(19, 1267319, 2149762, 'Dong-yeong', 8),
(20, 1267319, 3126573, 'Benjamin Jo', 9),
(21, 1311031, 1256603, 'Tanjiro Kamado (voice)', 0),
(22, 1311031, 119145, 'Zenitsu Agatsuma (voice)', 1),
(23, 1311031, 9705, 'Giyu Tomioka (voice)', 2),
(24, 1311031, 81244, 'Akaza (voice)', 3),
(25, 1311031, 233590, 'Inosuke Hashibira (voice)', 4),
(26, 1311031, 90571, 'Muzan Kibutsuji (voice)', 5),
(27, 1311031, 1452028, 'Kanao Tsuyuri (voice)', 6),
(28, 1311031, 1245094, 'Genya Shinazugawa (voice)', 7),
(29, 1311031, 24647, 'Tengen Uzui (voice)', 8),
(30, 1311031, 1563442, 'Nezuko Kamado (voice)', 9),
(31, 1328803, 78110, 'James Wright', 0),
(32, 1328803, 172994, NULL, 1),
(33, 1328803, 78036, NULL, 2),
(34, 1328803, 105271, NULL, 3),
(35, 1328803, 1309195, NULL, 4),
(36, 1328803, 1323453, NULL, 5),
(37, 1328803, 1045069, NULL, 6),
(38, 1328803, 227618, NULL, 7),
(39, 1328803, 1949603, NULL, 8),
(40, 1328803, 2682053, 'Japanese Soldier', 9),
(41, 755898, 9778, 'William Radford', 0),
(42, 755898, 52605, 'NASA Scientist Sandra Salas', 1),
(43, 755898, 9048, 'NSA Director Donald Briggs', 2),
(44, 755898, 1632530, 'Faith Radford', 3),
(45, 755898, 60482, 'David Radford', 4),
(46, 755898, 90498, 'Mark Goodman', 5),
(47, 755898, 71402, 'FBI Field Agent Sheila Jeffries', 6),
(48, 755898, 98811, 'News Reporter', 7),
(49, 755898, 21710, 'Secretary of Defense Walter Crystal', 8),
(50, 755898, 30697, 'POTUS', 9),
(51, 1009640, 2077864, 'Captain Edward Brockman', 0),
(52, 1009640, 1452046, 'Specialist Selby', 1),
(53, 1009640, 972079, 'Josh Weaver', 2),
(54, 1009640, 208296, 'Chris Lebold', 3),
(55, 1009640, 3115351, 'Binna', 4),
(56, 1009640, 1872281, 'Wilson Lee', 5),
(57, 1009640, 1518194, 'Jonah Ross', 6),
(58, 1009640, 2651106, 'Cooper', 7),
(59, 1009640, 97614, 'Milner', 8),
(60, 1009640, 1252319, 'Medic Crew Chief', 9),
(61, 1038392, 21657, 'Lorraine Warren', 0),
(62, 1038392, 17178, 'Ed Warren', 1),
(63, 1038392, 3011652, 'Judy Warren', 2),
(64, 1038392, 1452045, 'Tony Spera', 3),
(65, 1038392, 1355139, 'Janet Smurl', 4),
(66, 1038392, 3839198, 'Carin Smurl', 5),
(67, 1038392, 51120, 'Jack Smurl', 6),
(68, 1038392, 979807, 'Drew Thomas', 7),
(69, 1038392, 80619, 'Father Gordon', 8),
(70, 1038392, 3042998, 'Heather Smurl', 9),
(71, 1450529, 1084216, 'Pablo, \"El Galgo\"', 0),
(72, 1450529, 140542, 'La Madrina', 1),
(73, 1450529, 1637068, 'Nelson', 2),
(74, 1450529, 1061040, 'Isa', 3),
(75, 1450529, 1110361, 'Noni', 4),
(76, 1450529, 143604, 'Lalo', 5),
(77, 1450529, 5345557, 'Pitu', 6),
(78, 1450529, 5345558, 'Vir', 7),
(79, 1450529, 1152801, 'Nilda', 8),
(80, 1450529, 3678291, 'Amiga de Isa', 9),
(81, 1054867, 6193, 'Bob', 0),
(82, 1054867, 2228, 'Col. Steven J. Lockjaw', 1),
(83, 1054867, 3914706, 'Willa', 2),
(84, 1054867, 1121, 'Sensei Sergio St. Carlos', 3),
(85, 1054867, 35705, 'Deandra', 4),
(86, 1054867, 964679, 'Perfidia', 5),
(87, 1054867, 65829, 'Laredo', 6),
(88, 1054867, 3417, 'Virgil Throckmorton', 7),
(89, 1054867, 87956, 'Bill Desmond', 8),
(90, 1054867, 239580, 'Sommerville', 9),
(91, 987400, 52746, 'Yohualli Coatl / Batman (voice)', 0),
(92, 987400, 1340020, 'HernÃ¡n CortÃ©s / Two-Face (voice)', 1),
(93, 987400, 1134885, 'Yoka / Joker (voice)', 2),
(94, 987400, 266, 'Moctezuma (voice)', 3),
(95, 987400, 1265056, 'Jefe Toltecatzin (voice)', 4),
(96, 987400, 2027555, 'Joven Yohualli (voice)', 5),
(97, 987400, 4102576, 'Pedro de Alvarado (voice)', 6),
(98, 987400, 36638, 'Hiedra del Bosque / Poison Ivy (voice)', 7),
(99, 987400, 589722, 'Mujer Jaguar / Catwoman (voice)', 8),
(100, 1078605, 936970, 'Justine', 0),
(101, 1078605, 16851, 'Archer', 1),
(102, 1078605, 71375, 'Paul', 2),
(103, 1078605, 148992, 'James', 3),
(104, 1078605, 30082, 'Marcus', 4),
(105, 1078605, 23882, 'Gladys', 5),
(106, 1078605, 2407997, 'Alex', 6),
(107, 1078605, 18271, 'Captain Ed', 7),
(108, 1078605, 1105322, 'Alex\'s Dad', 8),
(109, 1078605, 2438324, 'Alex\'s Mom', 9),
(110, 1061474, 1785590, 'Superman', 0),
(111, 1061474, 993774, 'Lois Lane', 1),
(112, 1061474, 3292, 'Lex Luthor', 2),
(113, 1061474, 39391, 'Mr. Terrific', 3),
(114, 1061474, 51797, 'Guy Gardner', 4),
(115, 1061474, 1428070, 'Hawkgirl', 5),
(116, 1061474, 1601451, 'The Engineer', 6),
(117, 1061474, 61263, 'Jimmy Olsen', 7),
(118, 1061474, 21088, 'Gary', 8),
(119, 1061474, 5441666, 'Superman Robot #12 (voice)', 9),
(120, 1447287, 2851847, 'Julia', 0),
(121, 1447287, 1241666, 'Ramiro', 1),
(122, 1447287, 2521623, 'Francy', 2),
(123, 1447287, 1251536, 'AgustÃ­n', 3),
(124, 1447287, 839684, 'Oscar', 4),
(125, 1447287, 1524495, 'Paulina', 5),
(126, 803796, 144279, 'Rumi (voice)', 0),
(127, 803796, 1948606, 'Mira (voice)', 1),
(128, 803796, 2983147, 'Zoey (voice)', 2),
(129, 803796, 1571598, 'Jinu (voice)', 3),
(130, 803796, 28662, 'Celine (voice)', 4),
(131, 803796, 83586, 'Bobby (voice)', 5),
(132, 803796, 25002, 'Gwi-Ma (voice)', 6),
(133, 803796, 18307, 'Healer Han / Additional Voices (voice)', 7),
(134, 803796, 2371545, 'Variety Show Host 1 / Idol Host / Romance Saja (voice)', 8),
(135, 803796, 1700631, 'Host (voice)', 9),
(136, 1007734, 59410, 'Hutch Mansell', 0),
(137, 1007734, 935, 'Becca Mansell', 1),
(138, 1007734, 4430, 'Lendina', 2),
(139, 1007734, 40543, 'Wyatt Martin', 3),
(140, 1007734, 3492, 'Sheriff Abel', 4),
(141, 1007734, 150, 'Harry Mansell', 5),
(142, 1007734, 1062, 'David Mansell', 6),
(143, 1007734, 119251, 'Brady Mansell', 7),
(144, 1007734, 2593447, 'Sammy Mansell', 8),
(145, 1007734, 5414, 'The Barber', 9),
(146, 575265, 500, 'Ethan Hunt', 0),
(147, 575265, 39459, 'Grace', 1),
(148, 575265, 10182, 'Luther Stickell', 2),
(149, 575265, 11108, 'Benji Dunn', 3),
(150, 575265, 65344, 'Gabriel', 4),
(151, 575265, 139820, 'Paris', 5),
(152, 575265, 15319, 'Kittridge', 6),
(153, 575265, 7497, 'Serling', 7),
(154, 575265, 47627, 'Walters', 8),
(155, 575265, 17039, 'General Sidney', 9),
(156, 1035259, 3896, 'Frank Drebin Jr.', 0),
(157, 1035259, 6736, 'Beth Davenport', 1),
(158, 1035259, 1294982, 'Ed Hocken Jr.', 2),
(159, 1035259, 6413, 'Richard Cane', 3),
(160, 1035259, 30485, 'Chief Davis', 4),
(161, 1035259, 79072, 'Sig Gustafson', 5),
(162, 1035259, 1700631, 'Detective Barnes', 6),
(163, 1035259, 2568575, 'Detective Park', 7),
(164, 1035259, 143261, 'Detective Taylor', 8),
(165, 1035259, 2669816, 'Not Nordberg Jr.', 9),
(166, 1087192, 2803710, 'Hiccup', 0),
(167, 1087192, 2064124, 'Astrid', 1),
(168, 1087192, 17276, 'Stoick', 2),
(169, 1087192, 11109, 'Gobber', 3),
(170, 1087192, 3792786, 'Snoutlout', 4),
(171, 1087192, 1139349, 'Fishlegs', 5),
(172, 1087192, 1587577, 'Ruffnut', 6),
(173, 1087192, 2104260, 'Tuffnut', 7),
(174, 1087192, 1216581, 'Hoark', 8),
(175, 1087192, 11115, 'Spitelout', 9),
(176, 13494, 1464589, 'Red Sonja', 0),
(177, 13494, 118616, 'Emperor Dragan the Magnificent', 1),
(178, 13494, 1715541, 'Annisia', 2),
(179, 13494, 220232, 'Osin The Untouched', 3),
(180, 13494, 78909, 'Hawk', 4),
(181, 13494, 1510237, 'General Karlak', 5),
(182, 13494, 1416789, 'Amarak', 6),
(183, 13494, 3103059, 'Teresia', 7),
(184, 13494, 2845000, 'Varla', 8),
(185, 13494, 212236, 'Ayala', 9),
(186, 911430, 287, 'Sonny Hayes', 0),
(187, 911430, 1837297, 'Joshua Pearce', 1),
(188, 911430, 3810, 'Ruben Cervantes', 2),
(189, 911430, 62105, 'Kate McKenna', 3),
(190, 911430, 10920, 'Peter Banning', 4),
(191, 911430, 3398, 'Kaspar Smolinski', 5),
(192, 911430, 65447, 'Bernadette', 6),
(193, 911430, 1221073, 'Nickleby', 7),
(194, 911430, 1477143, 'Fazio', 8),
(195, 911430, 58431, 'Dodge', 9),
(196, 914215, 449, 'Jared York', 0),
(197, 914215, 4570, 'Rachel York', 1),
(198, 914215, 2181157, 'Noah York', 2),
(199, 914215, 1689143, 'Ashley York', 3),
(200, 914215, 1833002, 'Mia York', 4),
(201, 914215, 115986, 'Dawn Kim', 5),
(202, 914215, 5921, 'Tony', 6),
(203, 914215, 2780098, 'Grace', 7),
(204, 914215, 15029, 'Bob', 8),
(205, 914215, 8212, 'Charles York', 9),
(206, 1234821, 1245, 'Zora Bennett', 0),
(207, 1234821, 932967, 'Duncan Kincaid', 1),
(208, 1234821, 80860, 'Dr. Henry Loomis', 2),
(209, 1234821, 36669, 'Martin Krebs', 3),
(210, 1234821, 1168097, 'Reuben Delgado', 4),
(211, 1234821, 1362844, 'Teresa Delgado', 5),
(212, 1234821, 2525701, 'Xavier Dobbs', 6),
(213, 1234821, 3673493, 'Isabella Delgado', 7),
(214, 1234821, 2496534, 'Nina', 8),
(215, 1234821, 1517537, 'LeClerc', 9),
(216, 1218925, 3651176, 'Denji (voice)', 0),
(217, 1218925, 1452028, 'Reze (voice)', 1),
(218, 1218925, 1647448, 'Pochita (voice)', 2),
(219, 1218925, 2004086, 'Makima (voice)', 3),
(220, 1218925, 2812912, 'Aki (voice)', 4),
(221, 1218925, 2359492, 'Power (voice)', 5),
(222, 1218925, 1783522, 'Kobeni (voice)', 6),
(223, 1218925, 1256603, 'Beam (voice)', 7),
(224, 1218925, 1252998, 'The Violence Fiend (voice)', 8),
(225, 1218925, 1296667, 'Angel Devil (voice)', 9),
(226, 938086, 3164807, NULL, 0),
(227, 938086, 3424435, NULL, 1),
(228, 938086, 1622390, NULL, 2),
(229, 938086, 1907997, NULL, 3),
(230, 938086, 3424436, NULL, 4),
(231, 938086, 2576502, NULL, 5),
(232, 938086, 3674367, NULL, 6),
(233, 1242011, 54697, 'Tim', 0),
(234, 1242011, 88029, 'Millie', 1),
(235, 1242011, 62752, 'Jamie', 2),
(236, 1242011, 1871887, 'Cath', 3),
(237, 1242011, 2746473, 'Jordy', 4),
(238, 1242011, 2926221, 'Luke', 5),
(239, 1242011, 2300917, 'Carol', 6),
(240, 1242011, 1094511, 'Doctor Mendoza', 7),
(241, 1242011, 1976084, 'Keri', 8),
(242, 1242011, 5210260, 'Chaplin', 9),
(243, 1028248, 13022, 'Sonny Kilbane', 0),
(244, 1028248, 64856, 'Bryant', 1),
(245, 1028248, 54649, 'Piro', 2),
(246, 1028248, 1602205, 'Heather', 3),
(247, 1028248, 78036, 'Eric', 4),
(248, 1028248, 2092053, 'Marisol', 5),
(249, 1028248, 1148219, 'Oscar', 6),
(250, 1028248, 928812, 'Ivan / Vlad', 7),
(251, 1028248, 1314891, 'Rueben', 8),
(252, 1028248, 1020699, 'Hector', 9),
(253, 1175942, 6807, 'Wolf (voice)', 0),
(254, 1175942, 1231717, 'Snake (voice)', 1),
(255, 1175942, 1625558, 'Tarantula (voice)', 2),
(256, 1175942, 64342, 'Shark (voice)', 3),
(257, 1175942, 1560244, 'Piranha (voice)', 4),
(258, 1175942, 1545693, 'Diane Foxington (voice)', 5),
(259, 1175942, 1075037, 'Kitty Kat (voice)', 6),
(260, 1175942, 10871, 'Doom aka Susan (voice)', 7),
(261, 1175942, 2408703, 'Pigtail Petrova (voice)', 8),
(262, 1175942, 24357, 'Commissioner Misty Luggins (voice)', 9),
(263, 1319965, 17606, 'Laura', 0),
(264, 1319965, 21422, 'Simon', 1),
(265, 1319965, 118034, 'Andrea', 2),
(266, 1319965, 1102427, 'Lukas', 3),
(267, 1319965, 1080542, 'Dee', 4),
(268, 1319965, 1658940, 'Jay Gorin', 5),
(269, 1319965, 4431189, 'Sascha', 6),
(270, 1319965, 239109, 'Ekhard', 7),
(271, 1319965, 1706937, 'Jennie', 8),
(272, 1319965, 1766423, 'Imma', 9),
(273, 1119878, 3896, 'Mike McCann', 0),
(274, 1119878, 64439, 'Dhani Yangchen', 1),
(275, 1119878, 55174, 'Gurty', 2),
(276, 1119878, 2459258, 'Starr Myers', 3),
(277, 1119878, 5538791, 'Vijay Rai', 4),
(278, 1119878, 42394, 'Professor Myers', 5),
(279, 1119878, 93119, 'Spike', 6),
(280, 1119878, 1386458, 'Rudra Yash', 7),
(281, 1119878, 4841107, 'Jeet', 8),
(282, 1119878, 1502688, 'Ganesh Rai', 9),
(283, 691363, 28228, 'AdÃ¨le', 0),
(284, 691363, 1168774, 'Jean', 1),
(285, 691363, 1347698, 'La paysanne', 2),
(286, 691363, 1175154, 'Le moine', 3),
(287, 691363, 1404182, 'Le dÃ©serteur allemand', 4),
(288, 691363, 2595136, 'Le soldat Boileau', 5),
(289, 691363, 2595331, 'Le soldat Verdier', 6),
(290, 1289888, 78423, 'Abel Camara', 0),
(291, 1289888, 237881, 'Marion', 1),
(292, 1289888, 83966, 'Camille', 2),
(293, 1289888, 544681, 'Sami', 3),
(294, 1289888, 1701031, 'LÃ©na', 4),
(295, 1289888, 5107231, 'IngÃ©nieur du son', 5),
(296, 1289888, 1826078, 'Estelle', 6),
(297, 1289888, 1967163, 'CÃ©dric', 7),
(298, 1289888, 1734406, 'Antoine', 8),
(299, 1289888, 59032, 'Nathalie', 9),
(300, 1188808, 1234526, 'Michael', 0),
(301, 1188808, 4012042, 'Abie', 1),
(302, 1188808, 1353052, 'Roldan', 2),
(303, 1188808, 1185131, 'Commander Fidel', 3),
(304, 1188808, 2157538, 'Lando', 4),
(305, 1188808, 4095744, 'Therese', 5),
(306, 1188808, 223188, 'Military Doctor', 6),
(307, 1188808, 3353535, 'Rheng', 7),
(308, 1188808, 2959345, 'Darwin', 8),
(309, 1188808, 1345103, 'Terrorist', 9),
(310, 7451, 12835, 'Xander Cage', 0),
(311, 7451, 18514, 'Yelena', 1),
(312, 7451, 20982, 'Yorgi', 2),
(313, 7451, 2231, 'Agent Augustus Gibbons', 3),
(314, 7451, 53347, 'Agent Toby Lee Shavers', 4),
(315, 7451, 2341, 'Milan Sova', 5),
(316, 7451, 39849, 'Kirill', 6),
(317, 7451, 53348, 'Kolya', 7),
(318, 7451, 137332, 'Viktor', 8),
(319, 7451, 140250, 'Senator Dick Hotchkiss', 9),
(320, 980477, 2367355, 'Young Nezha (voice)', 0),
(321, 980477, 2367356, 'Youth Nezha / Jie Jie Shou Left (voice)', 1),
(322, 980477, 2368846, 'Ao Bing (voice)', 2),
(323, 980477, 2368848, 'Li Jing (voice)', 3),
(324, 980477, 2368849, 'Lady Yin (voice)', 4),
(325, 980477, 2368857, 'Master Taiyi (voice)', 5),
(326, 980477, 1574893, 'Shen Gongbao (voice)', 6),
(327, 980477, 1129530, 'Wuliang Xianweng (voice)', 7),
(328, 980477, 4927413, 'Ao Guang, Donghai Longwang / Seller (voice)', 8),
(329, 980477, 5224789, 'Ao Run, Xihai Longwang (voice)', 9),
(330, 552524, 3988423, 'Lilo', 0),
(331, 552524, 3025125, 'Nani', 1),
(332, 552524, 66193, 'Stitch (voice)', 2),
(333, 552524, 58225, 'Jumba', 3),
(334, 552524, 141034, 'Pleakley', 4),
(335, 552524, 24047, 'Cobra Bubbles', 5),
(336, 552524, 59401, 'TÅ«tÅ«', 6),
(337, 552524, 13445, 'Mrs. Kekoa', 7),
(338, 552524, 4022587, 'David', 8),
(339, 552524, 1278487, 'Grand Councilwoman (voice)', 9),
(340, 1051486, 1282054, 'Anne Eriksson', 0),
(341, 1051486, 150802, 'King Kristian', 1),
(342, 1051486, 1164111, 'Freja Eriksson', 2),
(343, 1051486, 1013156, 'Didrik Slagheck', 3),
(344, 1051486, 76555, 'Gustave Trolle', 4),
(345, 1051486, 4455, 'Hemming Gadh', 5),
(346, 1051486, 130414, 'Kristina Gyllenstierna', 6),
(347, 1051486, 40423, 'Sylvestre', 7),
(348, 1051486, 11110, 'Birgitta', 8),
(349, 1051486, 1137377, 'Sten Sture', 9),
(350, 1369679, 1808592, 'The Thief', 0),
(351, 1369679, 38560, 'The Cowboy', 1),
(352, 1369679, 1997524, 'Nushi', 2),
(353, 1369679, 208677, 'Sly', 3),
(354, 1369679, 2509658, 'Ravi', 4),
(355, 1369679, 64670, 'Vic', 5),
(356, 1369679, 65800, 'Don', 6),
(357, 1369679, 1808625, 'Mrs. Murphy', 7),
(358, 1369679, 5035291, 'Tom', 8),
(359, 1369679, 2353260, 'Justin', 9),
(360, 1265344, 1016168, 'Whitney', 0),
(361, 1265344, 224167, 'Sean', 1),
(362, 1265344, 2673750, 'Tisha', 2),
(363, 1265344, 1898600, 'Justin', 3),
(364, 1265344, 221018, 'Andrey', 4),
(365, 1265344, 1211535, 'JB', 5),
(366, 1265344, 1262629, 'Beth', 6),
(367, 1265344, 3495653, 'Stephanie', 7),
(368, 1265344, 3535989, 'Adam', 8),
(369, 1265344, 1970630, 'Diego', 9),
(370, 993234, 1283520, 'Hana, Petr\'s Wife', 0),
(371, 993234, 2153405, 'Petr, Hana\'s Husband', 1),
(372, 993234, 1078302, 'Vanda, VÃ­t\'s Wife', 2),
(373, 993234, 1244677, 'VÃ­t, Vanda\'s Husband', 3),
(374, 993234, 2053430, 'Antonie, Barmaid', 4),
(375, 993234, 1161739, 'Marek, Alzbeta\'s Husband', 5),
(376, 506763, 993943, 'Detective Dee', 0),
(377, 506763, 936431, 'Yuchi Zhenjin', 1),
(378, 506763, 12672, 'Wu Zetian', 2),
(379, 506763, 1254211, 'Shatuo Zhong', 3),
(380, 506763, 1476994, 'Shui Yue', 4),
(381, 506763, 1063128, 'Master Yuan Ce', 5),
(382, 506763, 1175572, 'Yi An', 6),
(383, 506763, 1622942, 'Phantom Blade', 7),
(384, 506763, 3357415, 'Hong Yuanyu', 8),
(385, 506763, 1573820, 'Huan Tian Zhenren', 9),
(386, 1284120, 2890913, 'Elvira', 0),
(387, 1284120, 63769, 'Rebekka', 1),
(388, 1284120, 1384976, 'Agnes', 2),
(389, 1284120, 3126191, 'Alma', 3),
(390, 1284120, 3676023, 'Prince Julian', 4),
(391, 1284120, 2984635, 'Isak', 5),
(392, 1284120, 114580, 'Otto', 6),
(393, 1284120, 213487, 'Sophie von Kronenberg', 7),
(394, 1284120, 968176, 'Madam Vanja', 8),
(395, 1284120, 526011, 'Dr. EsthÃ©tique', 9),
(396, 1022787, 2986863, 'Elio / Other Elio (voice)', 0),
(397, 1022787, 3455058, 'Glordon (voice)', 1),
(398, 1022787, 8691, 'Olga SolÃ­s (voice)', 2),
(399, 1022787, 18, 'Lord Grigon (voice)', 3),
(400, 1022787, 5077775, 'Ambassador Helix (voice)', 4),
(401, 1022787, 1624816, 'Ambassador Questa (voice)', 5),
(402, 1022787, 2443561, 'Bryce (voice)', 6),
(403, 1022787, 1600724, 'Caleb (voice)', 7),
(404, 1022787, 16808, 'Ambassador Tegmen (voice)', 8),
(405, 1022787, 57409, 'Ambassador Turais (voice)', 9),
(406, 541671, 224513, 'Eve', 0),
(407, 541671, 6384, 'John Wick', 1),
(408, 541671, 6972, 'Winston', 2),
(409, 541671, 5657, 'The Director', 3),
(410, 541671, 5168, 'The Chancellor', 4),
(411, 541671, 5887, 'Lena', 5),
(412, 541671, 4872540, 'Ella', 6),
(413, 541671, 2052200, 'Tatiana', 7),
(414, 541671, 4886, 'Daniel Pine', 8),
(415, 541671, 129101, 'Charon', 9),
(416, 1382406, 57207, 'Bai An', 0),
(417, 1382406, 2749667, 'Wu Zheng', 1),
(418, 1382406, 78878, 'Long Tai', 2),
(419, 1382406, 140478, 'He Yinghao', 3),
(420, 1382406, 4250352, 'He Ting', 4),
(421, 1382406, 2467558, 'Lu Ping', 5),
(422, 1382406, 3613013, 'Sang Kang', 6),
(423, 1382406, 4191243, 'Clay', 7),
(424, 1382406, 2644834, 'Jiu Shu', 8),
(425, 1382406, 2586775, 'Veterinary', 9),
(426, 1151334, 1372369, 'Edie', 0),
(427, 1151334, 1327613, 'John', 1),
(428, 1151334, 1271, 'Nico', 2),
(429, 1151334, 18324, 'Dad Meaney', 3),
(430, 1151334, 1488960, 'The Chaperone', 4),
(431, 1151334, 1420679, 'Perm Walters', 5),
(432, 1151334, 79082, 'Leo', 6),
(433, 1151334, 87192, 'George', 7),
(434, 1151334, 1511922, 'Young Edie', 8),
(435, 1151334, 1560246, 'Baby Girl', 9),
(436, 157336, 10297, 'Cooper', 0),
(437, 157336, 1813, 'Brand', 1),
(438, 157336, 3895, 'Professor Brand', 2),
(439, 157336, 83002, 'Murph', 3),
(440, 157336, 1893, 'Tom', 4),
(441, 157336, 8210, 'Doyle', 5),
(442, 157336, 17052, 'Getty', 6),
(443, 157336, 851784, 'Murph (10 Yrs.)', 7),
(444, 157336, 9560, 'Murph (older)', 8),
(445, 157336, 12074, 'Donald', 9),
(446, 715287, 1814297, NULL, 0),
(447, 715287, 2553497, NULL, 1),
(448, 715287, 2619408, NULL, 2),
(449, 715287, 2486773, NULL, 3),
(450, 1371189, 1437842, 'Ruth', 0),
(451, 1371189, 1562322, 'Boaz', 1),
(452, 1371189, 119598, 'Naomi', 2),
(453, 1371189, 2125455, 'Lena', 3),
(454, 1371189, 2752446, 'Breana', 4),
(455, 1371189, 4758270, 'Syrus', 5),
(456, 1371189, 224253, 'Self', 6),
(457, 1371189, 88132, 'Self', 7),
(458, 1371189, 1367903, 'Sauce', 8),
(459, 1371189, 37937, 'Eli', 9),
(460, 1498658, 4267452, NULL, 0),
(461, 1498658, 5506436, NULL, 1),
(462, 1498658, 2354094, NULL, 2),
(463, 1498658, 4716215, NULL, 3),
(464, 1498658, 5506439, NULL, 4),
(465, 1498658, 5506440, NULL, 5),
(466, 1498658, 4267446, NULL, 6),
(467, 994682, 3398439, 'Natsumi', 0),
(468, 994682, 1701223, 'Yukari', 1),
(469, 994682, 1118802, 'Yachiyo', 2),
(470, 994682, 1168489, 'Masao', 3),
(471, 994682, 3568019, 'Visitor', 4),
(472, 994682, 3496624, 'Visitor', 5),
(473, 994682, 551812, 'Estate Agent', 6),
(474, 1215020, 1136940, 'Daisy Moriarty', 0),
(475, 1215020, 1784612, 'Ava Alvim', 1),
(476, 1215020, 1859, 'Joy', 2),
(477, 1215020, 54811, 'Bob', 3),
(478, 1215020, 3304725, 'Paul', 4),
(479, 1215020, 1711290, 'Isaac', 5),
(480, 1215020, 62972, 'Counselor', 6),
(481, 1215020, 4299620, 'Mark Smith', 7),
(482, 1215020, 4526195, 'Violet Smith', 8),
(483, 1215020, 529302, 'Sergeant Nemeth', 9),
(484, 604079, 2764542, 'Raymond Garraty / #47', 0),
(485, 604079, 2761308, 'Peter McVries / #23', 1),
(486, 604079, 1446466, 'Stebbins / #38', 2),
(487, 604079, 2362672, 'Arthur Baker / #6', 3),
(488, 604079, 1440574, 'Gary Barkovitch / #5', 4),
(489, 604079, 3033265, 'Hank Olson / #46', 5),
(490, 604079, 3395594, 'Richard Harkness / #49', 6),
(491, 604079, 3238101, 'Collie Parker / #48', 7),
(492, 604079, 2, 'The Major', 8),
(493, 604079, 2054851, 'Curley / #7', 9),
(494, 574475, 3480304, 'Stefani Reyes', 0),
(495, 574475, 1547148, 'Charlie Reyes', 1),
(496, 574475, 58724, 'Darlene Campbell', 2),
(497, 574475, 144852, 'Erik', 3),
(498, 574475, 1482198, 'Bobby', 4),
(499, 574475, 1441215, 'Julia', 5),
(500, 574475, 62919, 'Uncle Howard', 6),
(501, 574475, 45428, 'Aunt Brenda', 7),
(502, 574475, 1376128, 'Marty Reyes', 8),
(503, 574475, 19384, 'William John Bludworth', 9),
(504, 828769, 1908004, NULL, 0),
(505, 828769, 3165040, NULL, 1),
(506, 828769, 2467484, NULL, 2),
(507, 828769, 2094677, NULL, 3),
(508, 155, 3894, 'Bruce Wayne', 0),
(509, 155, 1810, 'Joker', 1),
(510, 155, 6383, 'Harvey Dent', 2),
(511, 155, 3895, 'Alfred', 3),
(512, 155, 1579, 'Rachel', 4),
(513, 155, 64, 'Gordon', 5),
(514, 155, 192, 'Lucius Fox', 6),
(515, 155, 53651, 'Ramirez', 7),
(516, 155, 57597, 'Wuertz', 8),
(517, 155, 2037, 'Scarecrow', 9),
(518, 1339658, 1782117, 'Iris', 0),
(519, 1339658, 33235, 'Isaac', 1),
(520, 1339658, 1803287, 'Max', 2),
(521, 1339658, 1674830, 'Kenny', 3),
(522, 1339658, 212, 'Steve', 4),
(523, 1339658, 114470, 'Mom', 5),
(524, 1339658, 972079, 'Joe', 6),
(525, 1339658, 1702807, 'Syd', 7),
(526, 1339658, 964138, 'Deputy', 8),
(527, 1339658, 3343058, 'Medic', 9),
(528, 1403735, 1893251, 'Sonu Model / Laila', 0),
(529, 1403735, 3154351, 'Jenny', 1),
(530, 1403735, 584878, 'Shankar \"Shanku\"', 2),
(531, 1403735, 109743, 'Rusthom', 3),
(532, 1403735, 312870, 'Khaidi', 4),
(533, 1403735, 3237637, 'Sundari', 5),
(534, 1403735, 3747367, 'Subhaleka', 6),
(535, 1403735, 223146, 'Waseem', 7),
(536, 1403735, 1429160, NULL, 8),
(537, 1403735, 4477653, 'Sunisith', 9),
(538, 1083433, 2420169, 'Ava Brucks', 0),
(539, 1083433, 1642789, 'Danica Richards', 1),
(540, 1083433, 2286541, 'Stevie Ward', 2),
(541, 1083433, 3052321, 'Teddy Spencer', 3),
(542, 1083433, 1599391, 'Milo Griffin', 4),
(543, 1083433, 33259, 'Julie James', 5),
(544, 1083433, 33260, 'Ray Bronson', 6),
(545, 1083433, 20215, 'Grant Spencer', 7),
(546, 1083433, 4408050, 'Tyler Trevino', 8),
(547, 1083433, 11863, 'Helen Shivers', 9),
(548, 1337395, 4012042, 'Bea', 0),
(549, 1337395, 4866792, 'Cheska', 1),
(550, 1337395, 3603443, 'Rener', 2),
(551, 1337395, 3371806, 'Adelle', 3),
(552, 1337395, 1588844, 'Tita Luring', 4),
(553, 1506456, 4103293, 'Lila', 0),
(554, 1506456, 5165492, 'Kara', 1),
(555, 1506456, 1348735, 'Nanay Eka', 2),
(556, 1506456, 4669945, 'Bugs', 3),
(557, 1506456, 5326859, 'Rose', 4),
(558, 1506456, 1401573, 'Miles', 5),
(559, 1506456, 4680507, 'Rose', 6),
(560, 1506456, 2120295, 'Mom Wilms', 7),
(561, 1506456, 3970304, 'Mary', 8),
(562, 1506456, 5574619, 'Jun', 9);

-- --------------------------------------------------------

--
-- Table structure for table `movie_companies`
--

CREATE TABLE `movie_companies` (
  `id` int NOT NULL,
  `movie_id` int DEFAULT NULL,
  `company_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `movie_companies`
--

INSERT INTO `movie_companies` (`id`, `movie_id`, `company_id`) VALUES
(1, 617126, 420),
(2, 617126, 176762),
(3, 1267319, 126602),
(4, 1311031, 5887),
(5, 1311031, 2883),
(6, 1311031, 2918),
(7, 1328803, 92394),
(8, 1328803, 238345),
(9, 1328803, 238346),
(10, 1328803, 3835),
(11, 1328803, 238347),
(12, 755898, 33),
(13, 755898, 109501),
(14, 755898, 59827),
(15, 1009640, 133093),
(16, 1009640, 12006),
(17, 1009640, 1063),
(18, 1038392, 12),
(19, 1038392, 76907),
(20, 1038392, 11565),
(21, 1038392, 216687),
(22, 1450529, 212004),
(23, 1450529, 141986),
(24, 1450529, 6458),
(25, 1054867, 174),
(26, 1054867, 178),
(27, 1054867, 216687),
(28, 987400, 2785),
(29, 987400, 9965),
(30, 987400, 177082),
(31, 987400, 119056),
(32, 987400, 9993),
(33, 1078605, 12),
(34, 1078605, 240675),
(35, 1078605, 829),
(36, 1078605, 18609),
(37, 1078605, 216687),
(38, 1061474, 184898),
(39, 1061474, 94218),
(40, 1061474, 11565),
(41, 1061474, 216687),
(42, 1447287, 251954),
(43, 803796, 2251),
(44, 1007734, 33),
(45, 1007734, 121470),
(46, 1007734, 86647),
(47, 1007734, 123230),
(48, 575265, 4),
(49, 575265, 82819),
(50, 575265, 21777),
(51, 1035259, 8789),
(52, 1035259, 4),
(53, 1035259, 216687),
(54, 1087192, 521),
(55, 1087192, 2527),
(56, 13494, 1020),
(57, 13494, 11761),
(58, 13494, 270079),
(59, 13494, 48738),
(60, 911430, 81),
(61, 911430, 130),
(62, 911430, 199632),
(63, 911430, 194232),
(64, 911430, 19647),
(65, 914215, 11221),
(66, 914215, 82552),
(67, 914215, 2844),
(68, 1234821, 33),
(69, 1234821, 56),
(70, 1218925, 21444),
(71, 1242011, 123244),
(72, 1242011, 168181),
(73, 1242011, 104363),
(74, 1242011, 17094),
(75, 1242011, 221878),
(76, 1242011, 270104),
(77, 1028248, 3835),
(78, 1028248, 44973),
(79, 1028248, 101034),
(80, 1175942, 521),
(81, 1319965, 2531),
(82, 1319965, 172841),
(83, 1319965, 266787),
(84, 1119878, 14589),
(85, 1119878, 33603),
(86, 1119878, 19456),
(87, 1119878, 33768),
(88, 691363, 136126),
(89, 1289888, 13692),
(90, 1289888, 198278),
(91, 1289888, 118657),
(92, 1188808, 149142),
(93, 7451, 497),
(94, 980477, 167789),
(95, 980477, 17818),
(96, 980477, 161769),
(97, 980477, 273292),
(98, 980477, 273293),
(99, 552524, 2),
(100, 552524, 118854),
(101, 1051486, 186769),
(102, 1051486, 327),
(103, 1369679, 243953),
(104, 1369679, 73162),
(105, 1369679, 243955),
(106, 1265344, 127928),
(107, 1265344, 158529),
(108, 993234, 24870),
(109, 993234, 178086),
(110, 993234, 24701),
(111, 993234, 487),
(112, 993234, 51112),
(113, 506763, 3393),
(114, 1284120, 21979),
(115, 1284120, 51112),
(116, 1284120, 162093),
(117, 1284120, 171066),
(118, 1284120, 8288),
(119, 1284120, 218924),
(120, 1022787, 3),
(121, 541671, 3528),
(122, 541671, 23008),
(123, 541671, 1632),
(124, 1382406, 48460),
(125, 1382406, 261808),
(126, 1151334, 127928),
(127, 1151334, 114336),
(128, 157336, 923),
(129, 157336, 9996),
(130, 157336, 13769),
(131, 1371189, 3096),
(132, 994682, 955),
(133, 994682, 173343),
(134, 1215020, 1761),
(135, 1215020, 11407),
(136, 1215020, 269742),
(137, 1215020, 738),
(138, 1215020, 11658),
(139, 1215020, 94107),
(140, 604079, 1632),
(141, 604079, 161357),
(142, 604079, 829),
(143, 604079, 221429),
(144, 574475, 12),
(145, 574475, 48788),
(146, 574475, 252366),
(147, 574475, 252367),
(148, 574475, 216687),
(149, 155, 174),
(150, 155, 923),
(151, 155, 9996),
(152, 155, 429),
(153, 1339658, 210819),
(154, 1339658, 247983),
(155, 1339658, 25702),
(156, 1339658, 237412),
(157, 1339658, 264766),
(158, 1339658, 264767),
(159, 1403735, 100071),
(160, 1083433, 5),
(161, 1083433, 551),
(162, 1083433, 333),
(163, 1083433, 3287),
(164, 1083433, 22213),
(165, 1337395, 149142),
(166, 1506456, 149142),
(167, 1506456, 199610);

-- --------------------------------------------------------

--
-- Table structure for table `movie_crew`
--

CREATE TABLE `movie_crew` (
  `id` int NOT NULL,
  `movie_id` int NOT NULL,
  `person_id` int NOT NULL,
  `job` varchar(100) NOT NULL,
  `department` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `movie_crew`
--

INSERT INTO `movie_crew` (`id`, `movie_id`, `person_id`, `job`, `department`) VALUES
(1, 617126, 1212522, 'Director', NULL),
(2, 617126, 507, 'Screenplay', NULL),
(3, 617126, 579281, 'Screenplay', NULL),
(4, 617126, 1184281, 'Screenplay', NULL),
(5, 617126, 1184283, 'Screenplay', NULL),
(6, 1267319, 3987900, 'Director', NULL),
(7, 1267319, 3987900, 'Writer', NULL),
(8, 1267319, 1293686, 'Writer', NULL),
(9, 1267319, 5684693, 'Writer', NULL),
(10, 1311031, 1283010, 'Director', NULL),
(11, 1311031, 931823, 'Screenplay', NULL),
(12, 1311031, 2282830, 'Screenplay', NULL),
(13, 1328803, 54649, 'Director', NULL),
(14, 1328803, 86521, 'Writer', NULL),
(15, 755898, 1634181, 'Director', NULL),
(16, 755898, 10387, 'Screenplay', NULL),
(17, 755898, 116227, 'Screenplay', NULL),
(18, 1009640, 3767680, 'Director', NULL),
(19, 1009640, 3767680, 'Screenplay', NULL),
(20, 1009640, 1842294, 'Screenplay', NULL),
(21, 1038392, 1607672, 'Director', NULL),
(22, 1038392, 1247264, 'Screenplay', NULL),
(23, 1038392, 1661846, 'Screenplay', NULL),
(24, 1038392, 142686, 'Screenplay', NULL),
(25, 1450529, 2405377, 'Director', NULL),
(26, 1450529, 2405377, 'Writer', NULL),
(27, 1450529, 5312570, 'Writer', NULL),
(28, 1054867, 4762, 'Director', NULL),
(29, 1054867, 4762, 'Writer', NULL),
(30, 987400, 1345328, 'Director', NULL),
(31, 987400, 1220905, 'Screenplay', NULL),
(32, 1078605, 84833, 'Director', NULL),
(33, 1078605, 84833, 'Writer', NULL),
(34, 1061474, 15218, 'Writer', NULL),
(35, 1061474, 15218, 'Director', NULL),
(36, 1447287, 4479608, 'Director', NULL),
(37, 1447287, 2477076, 'Director', NULL),
(38, 1447287, 5300499, 'Screenplay', NULL),
(39, 803796, 3003169, 'Director', NULL),
(40, 803796, 1453550, 'Director', NULL),
(41, 803796, 3003169, 'Screenplay', NULL),
(42, 803796, 2512183, 'Screenplay', NULL),
(43, 803796, 2512184, 'Screenplay', NULL),
(44, 803796, 1453550, 'Screenplay', NULL),
(45, 1007734, 235430, 'Director', NULL),
(46, 1007734, 4123026, 'Screenplay', NULL),
(47, 1007734, 1076800, 'Screenplay', NULL),
(48, 575265, 9033, 'Writer', NULL),
(49, 575265, 33312, 'Writer', NULL),
(50, 575265, 9033, 'Director', NULL),
(51, 1035259, 62854, 'Director', NULL),
(52, 1035259, 1579776, 'Writer', NULL),
(53, 1035259, 1579777, 'Writer', NULL),
(54, 1035259, 62854, 'Writer', NULL),
(55, 1087192, 69797, 'Director', NULL),
(56, 1087192, 69797, 'Writer', NULL),
(57, 13494, 69593, 'Director', NULL),
(58, 13494, 1480624, 'Writer', NULL),
(59, 911430, 15244, 'Screenplay', NULL),
(60, 911430, 86270, 'Director', NULL),
(61, 914215, 549983, 'Writer', NULL),
(62, 914215, 1177335, 'Director', NULL),
(63, 1234821, 508, 'Writer', NULL),
(64, 1234821, 129894, 'Director', NULL),
(65, 1218925, 1286723, 'Director', NULL),
(66, 1218925, 1491863, 'Screenplay', NULL),
(67, 938086, 3424437, 'Director', NULL),
(68, 1242011, 1393127, 'Director', NULL),
(69, 1242011, 1393127, 'Writer', NULL),
(70, 1028248, 115091, 'Director', NULL),
(71, 1028248, 64856, 'Writer', NULL),
(72, 1175942, 1261455, 'Director', NULL),
(73, 1175942, 967321, 'Screenplay', NULL),
(74, 1175942, 52803, 'Screenplay', NULL),
(75, 1319965, 1161919, 'Director', NULL),
(76, 1319965, 1161919, 'Writer', NULL),
(77, 1319965, 21422, 'Writer', NULL),
(78, 1119878, 876, 'Director', NULL),
(79, 1119878, 876, 'Writer', NULL),
(80, 691363, 1334056, 'Screenplay', NULL),
(81, 691363, 1334056, 'Director', NULL),
(82, 1289888, 1592793, 'Screenplay', NULL),
(83, 1289888, 1119418, 'Screenplay', NULL),
(84, 1289888, 1592793, 'Director', NULL),
(85, 1188808, 586012, 'Director', NULL),
(86, 1188808, 4201517, 'Writer', NULL),
(87, 1188808, 586012, 'Writer', NULL),
(88, 7451, 53345, 'Screenplay', NULL),
(89, 7451, 18878, 'Director', NULL),
(90, 980477, 2367353, 'Director', NULL),
(91, 980477, 2367353, 'Writer', NULL),
(92, 552524, 1540069, 'Director', NULL),
(93, 552524, 3624081, 'Screenplay', NULL),
(94, 552524, 2032020, 'Screenplay', NULL),
(95, 1051486, 29605, 'Director', NULL),
(96, 1051486, 81399, 'Writer', NULL),
(97, 1051486, 3886342, 'Writer', NULL),
(98, 1369679, 3656771, 'Writer', NULL),
(99, 1369679, 1808592, 'Director', NULL),
(100, 1265344, 1552301, 'Writer', NULL),
(101, 1265344, 110876, 'Director', NULL),
(102, 1265344, 110876, 'Writer', NULL),
(103, 1265344, 2673791, 'Writer', NULL),
(104, 993234, 2383553, 'Director', NULL),
(105, 993234, 2383553, 'Screenplay', NULL),
(106, 993234, 3608024, 'Screenplay', NULL),
(107, 993234, 1071356, 'Screenplay', NULL),
(108, 993234, 1283520, 'Screenplay', NULL),
(109, 506763, 26760, 'Director', NULL),
(110, 506763, 564820, 'Writer', NULL),
(111, 1284120, 1480411, 'Writer', NULL),
(112, 1284120, 1480411, 'Director', NULL),
(113, 1022787, 1171950, 'Director', NULL),
(114, 1022787, 2009739, 'Director', NULL),
(115, 1022787, 1226604, 'Screenplay', NULL),
(116, 1022787, 1368145, 'Screenplay', NULL),
(117, 1022787, 2808625, 'Screenplay', NULL),
(118, 1022787, 1684652, 'Director', NULL),
(119, 541671, 2104243, 'Writer', NULL),
(120, 541671, 3950, 'Director', NULL),
(121, 1382406, 3534623, 'Director', NULL),
(122, 1382406, 5043998, 'Screenplay', NULL),
(123, 1151334, 1521958, 'Writer', NULL),
(124, 1151334, 1521958, 'Director', NULL),
(125, 157336, 527, 'Writer', NULL),
(126, 157336, 525, 'Writer', NULL),
(127, 157336, 525, 'Director', NULL),
(128, 715287, 2513312, 'Director', NULL),
(129, 1371189, 166479, 'Writer', NULL),
(130, 1371189, 88129, 'Writer', NULL),
(131, 1371189, 1025028, 'Director', NULL),
(132, 1498658, 4267452, 'Director', NULL),
(133, 1498658, 4267452, 'Writer', NULL),
(134, 994682, 548478, 'Director', NULL),
(135, 994682, 1190906, 'Screenplay', NULL),
(136, 1215020, 3712964, 'Writer', NULL),
(137, 1215020, 51853, 'Director', NULL),
(138, 604079, 10943, 'Director', NULL),
(139, 604079, 1283814, 'Screenplay', NULL),
(140, 574475, 1938317, 'Director', NULL),
(141, 574475, 1181022, 'Director', NULL),
(142, 574475, 1661850, 'Screenplay', NULL),
(143, 574475, 2198345, 'Screenplay', NULL),
(144, 828769, 2484593, 'Director', NULL),
(145, 155, 527, 'Screenplay', NULL),
(146, 155, 525, 'Screenplay', NULL),
(147, 155, 525, 'Director', NULL),
(148, 1339658, 1702810, 'Screenplay', NULL),
(149, 1339658, 1702810, 'Director', NULL),
(150, 1403735, 5240969, 'Director', NULL),
(151, 1403735, 4333289, 'Writer', NULL),
(152, 1083433, 1721377, 'Director', NULL),
(153, 1083433, 1721377, 'Screenplay', NULL),
(154, 1083433, 4745902, 'Screenplay', NULL),
(155, 1337395, 2144356, 'Director', NULL),
(156, 1506456, 1495470, 'Director', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `movie_genres`
--

CREATE TABLE `movie_genres` (
  `movie_id` int NOT NULL,
  `genre_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `movie_genres`
--

INSERT INTO `movie_genres` (`movie_id`, `genre_id`) VALUES
(7451, 12),
(13494, 12),
(157336, 12),
(506763, 12),
(552524, 12),
(575265, 12),
(617126, 12),
(803796, 12),
(980477, 12),
(987400, 12),
(1022787, 12),
(1051486, 12),
(1061474, 12),
(1087192, 12),
(1175942, 12),
(1234821, 12),
(13494, 14),
(506763, 14),
(803796, 14),
(980477, 14),
(987400, 14),
(1087192, 14),
(1218925, 14),
(1284120, 14),
(1311031, 14),
(803796, 16),
(980477, 16),
(987400, 16),
(1022787, 16),
(1175942, 16),
(1218925, 16),
(1311031, 16),
(155, 18),
(157336, 18),
(715287, 18),
(828769, 18),
(911430, 18),
(938086, 18),
(993234, 18),
(994682, 18),
(1051486, 18),
(1119878, 18),
(1188808, 18),
(1265344, 18),
(1284120, 18),
(1319965, 18),
(1337395, 18),
(1371189, 18),
(1506456, 18),
(574475, 27),
(604079, 27),
(691363, 27),
(803796, 27),
(914215, 27),
(1038392, 27),
(1078605, 27),
(1083433, 27),
(1242011, 27),
(1284120, 27),
(155, 28),
(7451, 28),
(13494, 28),
(506763, 28),
(541671, 28),
(575265, 28),
(911430, 28),
(980477, 28),
(987400, 28),
(1007734, 28),
(1009640, 28),
(1028248, 28),
(1035259, 28),
(1051486, 28),
(1054867, 28),
(1061474, 28),
(1087192, 28),
(1119878, 28),
(1151334, 28),
(1218925, 28),
(1234821, 28),
(1267319, 28),
(1311031, 28),
(1328803, 28),
(1369679, 28),
(1382406, 28),
(1447287, 28),
(1450529, 28),
(552524, 35),
(803796, 35),
(1022787, 35),
(1035259, 35),
(1151334, 35),
(1175942, 35),
(1284120, 35),
(1289888, 35),
(1339658, 35),
(1403735, 35),
(1051486, 36),
(1265344, 36),
(1328803, 36),
(155, 53),
(7451, 53),
(541671, 53),
(575265, 53),
(604079, 53),
(755898, 53),
(914215, 53),
(1007734, 53),
(1054867, 53),
(1083433, 53),
(1119878, 53),
(1151334, 53),
(1215020, 53),
(1267319, 53),
(1311031, 53),
(1328803, 53),
(1369679, 53),
(1382406, 53),
(1450529, 53),
(155, 80),
(7451, 80),
(541671, 80),
(1028248, 80),
(1035259, 80),
(1054867, 80),
(1175942, 80),
(1267319, 80),
(1369679, 80),
(1382406, 80),
(157336, 878),
(552524, 878),
(617126, 878),
(755898, 878),
(914215, 878),
(1022787, 878),
(1061474, 878),
(1234821, 878),
(506763, 9648),
(574475, 9648),
(1078605, 9648),
(1083433, 9648),
(1215020, 9648),
(803796, 10402),
(1371189, 10402),
(715287, 10749),
(993234, 10749),
(1188808, 10749),
(1289888, 10749),
(1319965, 10749),
(1339658, 10749),
(1371189, 10749),
(1403735, 10749),
(1506456, 10749),
(552524, 10751),
(803796, 10751),
(938086, 10751),
(1022787, 10751),
(1087192, 10751),
(1175942, 10751),
(1009640, 10752),
(1051486, 10752),
(1328803, 10752);

-- --------------------------------------------------------

--
-- Stand-in structure for view `movie_summary_view`
-- (See below for the actual view)
--
CREATE TABLE `movie_summary_view` (
`avg_rating` decimal(6,2)
,`movie_id` int
,`review_count` bigint
,`title` varchar(512)
);

-- --------------------------------------------------------

--
-- Table structure for table `movie_tags`
--

CREATE TABLE `movie_tags` (
  `movie_id` int NOT NULL,
  `tag_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `movie_tags`
--

INSERT INTO `movie_tags` (`movie_id`, `tag_id`) VALUES
(617126, 1);

-- --------------------------------------------------------

--
-- Table structure for table `people`
--

CREATE TABLE `people` (
  `id` int NOT NULL,
  `tmdb_id` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `bio` varchar(5000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `people`
--

INSERT INTO `people` (`id`, `tmdb_id`, `name`, `birth_date`, `bio`) VALUES
(2, NULL, 'Mark Hamill', NULL, NULL),
(18, NULL, 'Brad Garrett', NULL, NULL),
(64, NULL, 'Gary Oldman', NULL, NULL),
(150, NULL, 'RZA', NULL, NULL),
(192, NULL, 'Morgan Freeman', NULL, NULL),
(212, NULL, 'David Cross', NULL, NULL),
(266, NULL, 'Humberto Busto', NULL, NULL),
(287, NULL, 'Brad Pitt', NULL, NULL),
(449, NULL, 'Jay Baruchel', NULL, NULL),
(500, NULL, 'Tom Cruise', NULL, NULL),
(507, NULL, 'Josh Friedman', NULL, NULL),
(508, NULL, 'David Koepp', NULL, NULL),
(525, NULL, 'Christopher Nolan', NULL, NULL),
(527, NULL, 'Jonathan Nolan', NULL, NULL),
(876, NULL, 'Jonathan Hensleigh', NULL, NULL),
(935, NULL, 'Connie Nielsen', NULL, NULL),
(1062, NULL, 'Christopher Lloyd', NULL, NULL),
(1121, NULL, 'Benicio del Toro', NULL, NULL),
(1245, NULL, 'Scarlett Johansson', NULL, NULL),
(1271, NULL, 'Andy GarcÃ­a', NULL, NULL),
(1579, NULL, 'Maggie Gyllenhaal', NULL, NULL),
(1810, NULL, 'Heath Ledger', NULL, NULL),
(1813, NULL, 'Anne Hathaway', NULL, NULL),
(1859, NULL, 'Christiane Paul', NULL, NULL),
(1893, NULL, 'Casey Affleck', NULL, NULL),
(2037, NULL, 'Cillian Murphy', NULL, NULL),
(2228, NULL, 'Sean Penn', NULL, NULL),
(2231, NULL, 'Samuel L. Jackson', NULL, NULL),
(2341, NULL, 'Richy MÃ¼ller', NULL, NULL),
(3292, NULL, 'Nicholas Hoult', NULL, NULL),
(3398, NULL, 'Kim Bodnia', NULL, NULL),
(3417, NULL, 'Tony Goldwyn', NULL, NULL),
(3492, NULL, 'Colin Hanks', NULL, NULL),
(3810, NULL, 'Javier Bardem', NULL, NULL),
(3894, NULL, 'Christian Bale', NULL, NULL),
(3895, NULL, 'Michael Caine', NULL, NULL),
(3896, NULL, 'Liam Neeson', NULL, NULL),
(3950, NULL, 'Len Wiseman', NULL, NULL),
(4430, NULL, 'Sharon Stone', NULL, NULL),
(4455, NULL, 'Ulrich Thomsen', NULL, NULL),
(4570, NULL, 'Emily Hampshire', NULL, NULL),
(4762, NULL, 'Paul Thomas Anderson', NULL, NULL),
(4886, NULL, 'Norman Reedus', NULL, NULL),
(5168, NULL, 'Gabriel Byrne', NULL, NULL),
(5414, NULL, 'Colin Salmon', NULL, NULL),
(5657, NULL, 'Anjelica Huston', NULL, NULL),
(5887, NULL, 'Catalina Sandino Moreno', NULL, NULL),
(5921, NULL, 'Martin Roach', NULL, NULL),
(6193, NULL, 'Leonardo DiCaprio', NULL, NULL),
(6383, NULL, 'Aaron Eckhart', NULL, NULL),
(6384, NULL, 'Keanu Reeves', NULL, NULL),
(6413, NULL, 'Danny Huston', NULL, NULL),
(6736, NULL, 'Pamela Anderson', NULL, NULL),
(6807, NULL, 'Sam Rockwell', NULL, NULL),
(6972, NULL, 'Ian McShane', NULL, NULL),
(7497, NULL, 'Holt McCallany', NULL, NULL),
(8210, NULL, 'Wes Bentley', NULL, NULL),
(8212, NULL, 'Peter Gallagher', NULL, NULL),
(8691, NULL, 'Zoe SaldaÃ±a', NULL, NULL),
(9033, NULL, 'Christopher McQuarrie', NULL, NULL),
(9048, NULL, 'Clark Gregg', NULL, NULL),
(9560, NULL, 'Ellen Burstyn', NULL, NULL),
(9705, NULL, 'Takahiro Sakurai', NULL, NULL),
(9778, NULL, 'Ice Cube', NULL, NULL),
(10182, NULL, 'Ving Rhames', NULL, NULL),
(10297, NULL, 'Matthew McConaughey', NULL, NULL),
(10387, NULL, 'Marc Hyman', NULL, NULL),
(10871, NULL, 'Natasha Lyonne', NULL, NULL),
(10920, NULL, 'Tobias Menzies', NULL, NULL),
(10943, NULL, 'Francis Lawrence', NULL, NULL),
(11108, NULL, 'Simon Pegg', NULL, NULL),
(11109, NULL, 'Nick Frost', NULL, NULL),
(11110, NULL, 'Kate Ashfield', NULL, NULL),
(11115, NULL, 'Peter Serafinowicz', NULL, NULL),
(11863, NULL, 'Sarah Michelle Gellar', NULL, NULL),
(12074, NULL, 'John Lithgow', NULL, NULL),
(12672, NULL, 'Carina Lau', NULL, NULL),
(12835, NULL, 'Vin Diesel', NULL, NULL),
(13022, NULL, 'Tom Berenger', NULL, NULL),
(13445, NULL, 'Tia Carrere', NULL, NULL),
(15029, NULL, 'Enrico Colantoni', NULL, NULL),
(15218, NULL, 'James Gunn', NULL, NULL),
(15244, NULL, 'Ehren Kruger', NULL, NULL),
(15319, NULL, 'Henry Czerny', NULL, NULL),
(16808, NULL, 'Matthias SchweighÃ¶fer', NULL, NULL),
(16851, NULL, 'Josh Brolin', NULL, NULL),
(17039, NULL, 'Nick Offerman', NULL, NULL),
(17052, NULL, 'Topher Grace', NULL, NULL),
(17178, NULL, 'Patrick Wilson', NULL, NULL),
(17276, NULL, 'Gerard Butler', NULL, NULL),
(17606, NULL, 'Imogen Poots', NULL, NULL),
(18271, NULL, 'Toby Huss', NULL, NULL),
(18307, NULL, 'Daniel Dae Kim', NULL, NULL),
(18324, NULL, 'Steve Zahn', NULL, NULL),
(18514, NULL, 'Asia Argento', NULL, NULL),
(18878, NULL, 'Rob Cohen', NULL, NULL),
(19384, NULL, 'Tony Todd', NULL, NULL),
(20215, NULL, 'Billy Campbell', NULL, NULL),
(20737, NULL, 'Jeon Do-yeon', NULL, NULL),
(20982, NULL, 'Marton Csokas', NULL, NULL),
(21042, NULL, 'Ebon Moss-Bachrach', NULL, NULL),
(21088, NULL, 'Alan Tudyk', NULL, NULL),
(21422, NULL, 'Brett Goldstein', NULL, NULL),
(21657, NULL, 'Vera Farmiga', NULL, NULL),
(21710, NULL, 'Michael O\'Neill', NULL, NULL),
(23882, NULL, 'Amy Madigan', NULL, NULL),
(24047, NULL, 'Courtney B. Vance', NULL, NULL),
(24357, NULL, 'Alex Borstein', NULL, NULL),
(24647, NULL, 'Katsuyuki Konishi', NULL, NULL),
(25002, NULL, 'Lee Byung-hun', NULL, NULL),
(26760, NULL, 'Tsui Hark', NULL, NULL),
(28228, NULL, 'SÃ©verine Ferrer', NULL, NULL),
(28662, NULL, 'Yunjin Kim', NULL, NULL),
(29605, NULL, 'Mikael HÃ¥fstrÃ¶m', NULL, NULL),
(30082, NULL, 'Benedict Wong', NULL, NULL),
(30485, NULL, 'CCH Pounder', NULL, NULL),
(30697, NULL, 'Jim Meskimen', NULL, NULL),
(33235, NULL, 'Logan Lerman', NULL, NULL),
(33259, NULL, 'Jennifer Love Hewitt', NULL, NULL),
(33260, NULL, 'Freddie Prinze Jr.', NULL, NULL),
(33312, NULL, 'Erik Jendresen', NULL, NULL),
(34546, NULL, 'Mark Gatiss', NULL, NULL),
(35705, NULL, 'Regina Hall', NULL, NULL),
(36638, NULL, 'Maya Zapata', NULL, NULL),
(36669, NULL, 'Rupert Friend', NULL, NULL),
(37937, NULL, 'Gregory Alan Williams', NULL, NULL),
(38560, NULL, 'Lou Diamond Phillips', NULL, NULL),
(39391, NULL, 'Edi Gathegi', NULL, NULL),
(39459, NULL, 'Hayley Atwell', NULL, NULL),
(39849, NULL, 'Werner Daehn', NULL, NULL),
(40423, NULL, 'Thomas Chaanhing', NULL, NULL),
(40543, NULL, 'John Ortiz', NULL, NULL),
(42394, NULL, 'Bernard Curry', NULL, NULL),
(45428, NULL, 'April Telek', NULL, NULL),
(47627, NULL, 'Janet McTeer', NULL, NULL),
(51120, NULL, 'Elliot Cowan', NULL, NULL),
(51797, NULL, 'Nathan Fillion', NULL, NULL),
(51853, NULL, 'Uta Briesewitz', NULL, NULL),
(52605, NULL, 'Eva Longoria', NULL, NULL),
(52746, NULL, 'Horacio GarcÃ­a Rojas', NULL, NULL),
(52803, NULL, 'Etan Cohen', NULL, NULL),
(53345, NULL, 'Rich Wilkes', NULL, NULL),
(53347, NULL, 'Michael Roof', NULL, NULL),
(53348, NULL, 'Petr JÃ¡kl', NULL, NULL),
(53651, NULL, 'Monique Gabriela Curnen', NULL, NULL),
(54649, NULL, 'Louis Mandylor', NULL, NULL),
(54697, NULL, 'Dave Franco', NULL, NULL),
(54811, NULL, 'Joel Fry', NULL, NULL),
(55174, NULL, 'Marcus Thomas', NULL, NULL),
(57207, NULL, 'Tony Jaa', NULL, NULL),
(57409, NULL, 'Ana de la Reguera', NULL, NULL),
(57597, NULL, 'Ron Dean', NULL, NULL),
(58225, NULL, 'Zach Galifianakis', NULL, NULL),
(58431, NULL, 'Abdul Salis', NULL, NULL),
(58724, NULL, 'Rya Kihlstedt', NULL, NULL),
(59032, NULL, 'Isabelle Candelier', NULL, NULL),
(59401, NULL, 'Amy Hill', NULL, NULL),
(59410, NULL, 'Bob Odenkirk', NULL, NULL),
(60482, NULL, 'Henry Hunter Hall', NULL, NULL),
(61263, NULL, 'Skyler Gisondo', NULL, NULL),
(62105, NULL, 'Kerry Condon', NULL, NULL),
(62752, NULL, 'Damon Herriman', NULL, NULL),
(62854, NULL, 'Akiva Schaffer', NULL, NULL),
(62919, NULL, 'Alex Zahara', NULL, NULL),
(62972, NULL, 'Tim Plester', NULL, NULL),
(63769, NULL, 'Ane Dahl Torp', NULL, NULL),
(64342, NULL, 'Craig Robinson', NULL, NULL),
(64439, NULL, 'Fan Bingbing', NULL, NULL),
(64670, NULL, 'Philip Granger', NULL, NULL),
(64856, NULL, 'Michael Jai White', NULL, NULL),
(65344, NULL, 'Esai Morales', NULL, NULL),
(65447, NULL, 'Sarah Niles', NULL, NULL),
(65800, NULL, 'James Hutson', NULL, NULL),
(65829, NULL, 'Wood Harris', NULL, NULL),
(66193, NULL, 'Chris Sanders', NULL, NULL),
(69593, NULL, 'MJ Bassett', NULL, NULL),
(69797, NULL, 'Dean DeBlois', NULL, NULL),
(71375, NULL, 'Alden Ehrenreich', NULL, NULL),
(71402, NULL, 'Andrea Savage', NULL, NULL),
(76555, NULL, 'Jakob Oftebro', NULL, NULL),
(78036, NULL, 'Michael Copon', NULL, NULL),
(78110, NULL, 'Scott Adkins', NULL, NULL),
(78423, NULL, 'Omar Sy', NULL, NULL),
(78878, NULL, 'Xing Yu', NULL, NULL),
(78909, NULL, 'Michael Bisping', NULL, NULL),
(79072, NULL, 'Kevin Durand', NULL, NULL),
(79082, NULL, 'Randall Park', NULL, NULL),
(80619, NULL, 'Steve Coulter', NULL, NULL),
(80860, NULL, 'Jonathan Bailey', NULL, NULL),
(81244, NULL, 'Akira Ishida', NULL, NULL),
(81399, NULL, 'Erlend Loe', NULL, NULL),
(83002, NULL, 'Jessica Chastain', NULL, NULL),
(83586, NULL, 'Ken Jeong', NULL, NULL),
(83966, NULL, 'Pascale Arbillot', NULL, NULL),
(84833, NULL, 'Zach Cregger', NULL, NULL),
(85008, NULL, 'Sul Kyung-gu', NULL, NULL),
(86270, NULL, 'Joseph Kosinski', NULL, NULL),
(86521, NULL, 'Marc Clebanoff', NULL, NULL),
(87192, NULL, 'Mike O\'Malley', NULL, NULL),
(87956, NULL, 'D.W. Moffett', NULL, NULL),
(88029, NULL, 'Alison Brie', NULL, NULL),
(88129, NULL, 'Michael Elliot', NULL, NULL),
(88132, NULL, 'Jermaine Dupri', NULL, NULL),
(90498, NULL, 'Devon Bostick', NULL, NULL),
(90571, NULL, 'Toshihiko Seki', NULL, NULL),
(93119, NULL, 'Geoff Morrell', NULL, NULL),
(97614, NULL, 'Ronald Patrick Thompson', NULL, NULL),
(98811, NULL, 'Nicole Pulliam', NULL, NULL),
(105271, NULL, 'Donald Cerrone', NULL, NULL),
(109743, NULL, 'Abhimanyu Singh', NULL, NULL),
(110876, NULL, 'Rachel Lee Goldenberg', NULL, NULL),
(114470, NULL, 'Polly Draper', NULL, NULL),
(114580, NULL, 'Ralph Carlsson', NULL, NULL),
(115091, NULL, 'R. Ellis Frazier', NULL, NULL),
(115986, NULL, 'Uni Park', NULL, NULL),
(116227, NULL, 'Kenny Golde', NULL, NULL),
(118034, NULL, 'Zawe Ashton', NULL, NULL),
(118616, NULL, 'Robert Sheehan', NULL, NULL),
(119145, NULL, 'Hiro Shimono', NULL, NULL),
(119251, NULL, 'Gage Munroe', NULL, NULL),
(119598, NULL, 'Phylicia RashÄd', NULL, NULL),
(129101, NULL, 'Lance Reddick', NULL, NULL),
(129894, NULL, 'Gareth Edwards', NULL, NULL),
(130414, NULL, 'Emily Beecham', NULL, NULL),
(137332, NULL, 'Jan FilipenskÃ½', NULL, NULL),
(139820, NULL, 'Pom Klementieff', NULL, NULL),
(140250, NULL, 'Tom Everett', NULL, NULL),
(140478, NULL, 'Philip Keung Ho-Man', NULL, NULL),
(140542, NULL, 'Julieta DÃ­az', NULL, NULL),
(141034, NULL, 'Billy Magnussen', NULL, NULL),
(142686, NULL, 'David Leslie Johnson-McGoldrick', NULL, NULL),
(143261, NULL, 'Michael Beasley', NULL, NULL),
(143604, NULL, 'MatÃ­as Desiderio', NULL, NULL),
(144279, NULL, 'Arden Cho', NULL, NULL),
(144852, NULL, 'Richard Harmon', NULL, NULL),
(148992, NULL, 'Austin Abrams', NULL, NULL),
(150802, NULL, 'Claes Bang', NULL, NULL),
(166479, NULL, 'Cory Tynan', NULL, NULL),
(172994, NULL, 'Peter Shinkoda', NULL, NULL),
(202032, NULL, 'Ralph Ineson', NULL, NULL),
(208296, NULL, 'Callan Mulvey', NULL, NULL),
(208677, NULL, 'Lee Majdoub', NULL, NULL),
(212236, NULL, 'Manal El-Feitury', NULL, NULL),
(213487, NULL, 'Cecilia Forss', NULL, NULL),
(220232, NULL, 'Luke Pasqualino', NULL, NULL),
(221018, NULL, 'Dan Stevens', NULL, NULL),
(223146, NULL, 'Ravi Mariya', NULL, NULL),
(223188, NULL, 'Carlene Aguilar', NULL, NULL),
(224167, NULL, 'Ben Schnetzer', NULL, NULL),
(224253, NULL, 'Babyface', NULL, NULL),
(224513, NULL, 'Ana de Armas', NULL, NULL),
(227618, NULL, 'Masanori Mimoto', NULL, NULL),
(233590, NULL, 'Yoshitsugu Matsuoka', NULL, NULL),
(235430, NULL, 'Timo Tjahjanto', NULL, NULL),
(237881, NULL, 'Sara Giraudeau', NULL, NULL),
(239109, NULL, 'Tariq Rasheed', NULL, NULL),
(239580, NULL, 'Paul Grimstad', NULL, NULL),
(312870, NULL, 'Vineet Kumar', NULL, NULL),
(526011, NULL, 'Adam Lundgren', NULL, NULL),
(529302, NULL, 'Joplin Sibtain', NULL, NULL),
(544681, NULL, 'Alban Ivanov', NULL, NULL),
(548478, NULL, 'Isao Hayashi', NULL, NULL),
(549983, NULL, 'Michael Sparaga', NULL, NULL),
(551812, NULL, 'Kensuke Tamai', NULL, NULL),
(556356, NULL, 'Vanessa Kirby', NULL, NULL),
(564820, NULL, 'Zhang Jialu', NULL, NULL),
(579281, NULL, 'Eric Pearson', NULL, NULL),
(584878, NULL, 'Babloo Prithiveeraj', NULL, NULL),
(586012, NULL, 'G.B. Sampedro', NULL, NULL),
(589722, NULL, 'Teresa Ruiz', NULL, NULL),
(839684, NULL, 'AndrÃ©s CastaÃ±eda', NULL, NULL),
(851784, NULL, 'Mackenzie Foy', NULL, NULL),
(928812, NULL, 'Roberto \'Sanz\' Sanchez', NULL, NULL),
(931823, NULL, 'Hikaru KondÃ´', NULL, NULL),
(932967, NULL, 'Mahershala Ali', NULL, NULL),
(936431, NULL, 'William Feng', NULL, NULL),
(936970, NULL, 'Julia Garner', NULL, NULL),
(964138, NULL, 'Jimmy Gary Jr.', NULL, NULL),
(964679, NULL, 'Teyana Taylor', NULL, NULL),
(967321, NULL, 'Yoni Brenner', NULL, NULL),
(968176, NULL, 'Katarzyna Herman', NULL, NULL),
(972079, NULL, 'Desmin Borges', NULL, NULL),
(979807, NULL, 'Shannon Kook', NULL, NULL),
(993774, NULL, 'Rachel Brosnahan', NULL, NULL),
(993943, NULL, 'Mark Chao', NULL, NULL),
(1013156, NULL, 'Mikkel Boe FÃ¸lsgaard', NULL, NULL),
(1016168, NULL, 'Lily James', NULL, NULL),
(1020699, NULL, 'Guillermo IvÃ¡n', NULL, NULL),
(1025028, NULL, 'Alanna Brown', NULL, NULL),
(1045069, NULL, 'Shane Kosugi', NULL, NULL),
(1061040, NULL, 'Maite Lanata', NULL, NULL),
(1063128, NULL, 'Ethan Juan', NULL, NULL),
(1071356, NULL, 'KateÅ™ina JandÃ¡ÄkovÃ¡', NULL, NULL),
(1075037, NULL, 'Danielle Brooks', NULL, NULL),
(1076800, NULL, 'Derek Kolstad', NULL, NULL),
(1078302, NULL, 'EliÅ¡ka KÅ™enkovÃ¡', NULL, NULL),
(1080542, NULL, 'Jenna Coleman', NULL, NULL),
(1084216, NULL, 'Sergio Podeley', NULL, NULL),
(1094511, NULL, 'Aljin Abella', NULL, NULL),
(1102427, NULL, 'Steven Cree', NULL, NULL),
(1105322, NULL, 'Whitmer Thomas', NULL, NULL),
(1110361, NULL, 'Mariano Torre', NULL, NULL),
(1118802, NULL, 'Kazuyo Ezaki', NULL, NULL),
(1119418, NULL, 'Hugo GÃ©lin', NULL, NULL),
(1129530, NULL, 'Wang Deshun', NULL, NULL),
(1134885, NULL, 'Omar Chaparro', NULL, NULL),
(1136940, NULL, 'Lili Reinhart', NULL, NULL),
(1137377, NULL, 'Adam PÃ¥lsson', NULL, NULL),
(1139349, NULL, 'Julian Dennison', NULL, NULL),
(1148219, NULL, 'Luca Oriel', NULL, NULL),
(1152801, NULL, 'Susana Varela', NULL, NULL),
(1161739, NULL, 'Hynek ÄŒermÃ¡k', NULL, NULL),
(1161919, NULL, 'William Bridges', NULL, NULL),
(1164111, NULL, 'Alba August', NULL, NULL),
(1168097, NULL, 'Manuel Garcia-Rulfo', NULL, NULL),
(1168489, NULL, 'Masayoshi Takigawa', NULL, NULL),
(1168774, NULL, 'David Doukhan', NULL, NULL),
(1171950, NULL, 'Madeline Sharafian', NULL, NULL),
(1175154, NULL, 'Philippe Lamendin', NULL, NULL),
(1175572, NULL, 'Tiger Xu', NULL, NULL),
(1177335, NULL, 'Caitlin Cronenberg', NULL, NULL),
(1181022, NULL, 'Zach Lipovsky', NULL, NULL),
(1184281, NULL, 'Jeff Kaplan', NULL, NULL),
(1184283, NULL, 'Ian Springer', NULL, NULL),
(1185131, NULL, 'Kier Legaspi', NULL, NULL),
(1190906, NULL, 'ShÅichi Ikeda', NULL, NULL),
(1211535, NULL, 'Ian Colletti', NULL, NULL),
(1212522, NULL, 'Matt Shakman', NULL, NULL),
(1216581, NULL, 'Murray McArthur', NULL, NULL),
(1220905, NULL, 'Ernie Altbacker', NULL, NULL),
(1221073, NULL, 'Will Merrick', NULL, NULL),
(1226604, NULL, 'Julia Cho', NULL, NULL),
(1231717, NULL, 'Marc Maron', NULL, NULL),
(1234526, NULL, 'Arron Villaflor', NULL, NULL),
(1241666, NULL, 'Juan A. Baptista', NULL, NULL),
(1244677, NULL, 'Martin Hofmann', NULL, NULL),
(1245094, NULL, 'Nobuhiko Okamoto', NULL, NULL),
(1247264, NULL, 'Ian B. Goldberg', NULL, NULL),
(1251536, NULL, 'Carlos-Manuel Vesga', NULL, NULL),
(1252319, NULL, 'Leo Chiang', NULL, NULL),
(1252998, NULL, 'Yuuya Uchida', NULL, NULL),
(1253360, NULL, 'Pedro Pascal', NULL, NULL),
(1254211, NULL, 'Lin Gengxin', NULL, NULL),
(1256603, NULL, 'Natsuki Hanae', NULL, NULL),
(1261455, NULL, 'Pierre Perifel', NULL, NULL),
(1262629, NULL, 'Mary Neely', NULL, NULL),
(1265056, NULL, 'Jorge R. Gutierrez', NULL, NULL),
(1278487, NULL, 'Hannah Waddingham', NULL, NULL),
(1282054, NULL, 'Sophie Cookson', NULL, NULL),
(1283010, NULL, 'Haruo Sotozaki', NULL, NULL),
(1283520, NULL, 'Hana VagnerovÃ¡', NULL, NULL),
(1283814, NULL, 'JT Mollner', NULL, NULL),
(1286723, NULL, 'Tatsuya Yoshihara', NULL, NULL),
(1293686, NULL, 'Byun Sung-hyun', NULL, NULL),
(1294982, NULL, 'Paul Walter Hauser', NULL, NULL),
(1296667, NULL, 'Maaya Uchida', NULL, NULL),
(1296713, NULL, 'Yim Si-wan', NULL, NULL),
(1309195, NULL, 'Michael Rene Walton', NULL, NULL),
(1314891, NULL, 'Mauricio Mendoza', NULL, NULL),
(1323453, NULL, 'Gary Cairns', NULL, NULL),
(1327613, NULL, 'Karl Glusman', NULL, NULL),
(1334056, NULL, 'Fabrice Blin', NULL, NULL),
(1340020, NULL, 'Ãlvaro Morte', NULL, NULL),
(1345103, NULL, 'Manu Respall', NULL, NULL),
(1345328, NULL, 'Juan Jose Meza-Leon', NULL, NULL),
(1347698, NULL, 'ClÃ©mence Verniau', NULL, NULL),
(1348735, NULL, 'Ruby Ruiz', NULL, NULL),
(1353052, NULL, 'Joko Diaz', NULL, NULL),
(1355139, NULL, 'Rebecca Calder', NULL, NULL),
(1362844, NULL, 'Luna Blaise', NULL, NULL),
(1367903, NULL, 'Lecrae', NULL, NULL),
(1368145, NULL, 'Mark Hammer', NULL, NULL),
(1372369, NULL, 'Samara Weaving', NULL, NULL),
(1376128, NULL, 'Andrew Tinpo Lee', NULL, NULL),
(1384976, NULL, 'Thea Sofie Loch NÃ¦ss', NULL, NULL),
(1386458, NULL, 'Mahesh Jadu', NULL, NULL),
(1393127, NULL, 'Michael Shanks', NULL, NULL),
(1401573, NULL, 'Mark Dionisio', NULL, NULL),
(1404182, NULL, 'Fabien Jegoudez', NULL, NULL),
(1416789, NULL, 'Eliza Matengu', NULL, NULL),
(1420679, NULL, 'Marshawn Lynch', NULL, NULL),
(1428070, NULL, 'Isabela Merced', NULL, NULL),
(1429160, NULL, 'Prudhviraj', NULL, NULL),
(1437842, NULL, 'Serayah', NULL, NULL),
(1440574, NULL, 'Charlie Plummer', NULL, NULL),
(1441215, NULL, 'Anna Lore', NULL, NULL),
(1446466, NULL, 'Garrett Wareing', NULL, NULL),
(1452028, NULL, 'Reina Ueda', NULL, NULL),
(1452045, NULL, 'Ben Hardy', NULL, NULL),
(1452046, NULL, 'Lana Condor', NULL, NULL),
(1453550, NULL, 'Chris Appelhans', NULL, NULL),
(1464589, NULL, 'Matilda Lutz', NULL, NULL),
(1476994, NULL, 'Ma Sichun', NULL, NULL),
(1477143, NULL, 'Joseph Balderrama', NULL, NULL),
(1480411, NULL, 'Emilie Blichfeldt', NULL, NULL),
(1480624, NULL, 'Tasha Huo', NULL, NULL),
(1482198, NULL, 'Owen Patrick Joyner', NULL, NULL),
(1488960, NULL, 'Jermaine Fowler', NULL, NULL),
(1491863, NULL, 'Hiroshi Seko', NULL, NULL),
(1495470, NULL, 'Roman Perez Jr.', NULL, NULL),
(1502688, NULL, 'Shapoor Batliwalla', NULL, NULL),
(1510237, NULL, 'Martyn Ford', NULL, NULL),
(1511922, NULL, 'Elle Graham', NULL, NULL),
(1517537, NULL, 'Bechir Sylvain', NULL, NULL),
(1518194, NULL, 'Jonathan Whitesell', NULL, NULL),
(1521958, NULL, 'Shawn Simmons', NULL, NULL),
(1524495, NULL, 'Marilyn PatiÃ±o', NULL, NULL),
(1540069, NULL, 'Dean Fleischer Camp', NULL, NULL),
(1545693, NULL, 'Zazie Beetz', NULL, NULL),
(1547148, NULL, 'Teo Briones', NULL, NULL),
(1552301, NULL, 'Kim Caramele', NULL, NULL),
(1560244, NULL, 'Anthony Ramos', NULL, NULL),
(1560246, NULL, 'Kyanna Simone Simpson', NULL, NULL),
(1562322, NULL, 'Tyler Lepley', NULL, NULL),
(1563442, NULL, 'Akari Kito', NULL, NULL),
(1566012, NULL, 'Jo Woo-jin', NULL, NULL),
(1571598, NULL, 'Ahn Hyo-seop', NULL, NULL),
(1572347, NULL, 'Jeon Bae-soo', NULL, NULL),
(1573820, NULL, 'Zhang Aoyue', NULL, NULL),
(1574893, NULL, 'Yang Wei', NULL, NULL),
(1579776, NULL, 'Dan Gregor', NULL, NULL),
(1579777, NULL, 'Doug Mand', NULL, NULL),
(1587577, NULL, 'Bronwyn James', NULL, NULL),
(1588844, NULL, 'Tabs Sumulong', NULL, NULL),
(1592793, NULL, 'Lisa-Nina Rives', NULL, NULL),
(1597365, NULL, 'Joseph Quinn', NULL, NULL),
(1599391, NULL, 'Jonah Hauer-King', NULL, NULL),
(1600724, NULL, 'Jake T. Getman', NULL, NULL),
(1601451, NULL, 'MarÃ­a Gabriela de FarÃ­a', NULL, NULL),
(1602205, NULL, 'Gillian White', NULL, NULL),
(1607672, NULL, 'Michael Chaves', NULL, NULL),
(1622390, NULL, 'Lee Chae-dam', NULL, NULL),
(1622942, NULL, 'Sun Jiaolong', NULL, NULL),
(1624816, NULL, 'Jameela Jamil', NULL, NULL),
(1625558, NULL, 'Awkwafina', NULL, NULL),
(1632530, NULL, 'Iman Benson', NULL, NULL),
(1634181, NULL, 'Rich Lee', NULL, NULL),
(1637068, NULL, 'Ramiro Blas', NULL, NULL),
(1642789, NULL, 'Madelyn Cline', NULL, NULL),
(1647448, NULL, 'Shiori Izawa', NULL, NULL),
(1658940, NULL, 'Ã‰va Magyar', NULL, NULL),
(1661846, NULL, 'Richard Naing', NULL, NULL),
(1661850, NULL, 'Guy Busick', NULL, NULL),
(1674830, NULL, 'John Reynolds', NULL, NULL),
(1684652, NULL, 'Adrian Molina', NULL, NULL),
(1689143, NULL, 'Alanna Bale', NULL, NULL),
(1700631, NULL, 'Liza Koshy', NULL, NULL),
(1701031, NULL, 'Cindy Bruna', NULL, NULL),
(1701223, NULL, 'Chiaki Kitahara', NULL, NULL),
(1702807, NULL, 'Diana Irvine', NULL, NULL),
(1702810, NULL, 'Sophie Brooks', NULL, NULL),
(1706937, NULL, 'Nadia Albina', NULL, NULL),
(1711290, NULL, 'Josh Whitehouse', NULL, NULL),
(1715541, NULL, 'Wallis Day', NULL, NULL),
(1721377, NULL, 'Jennifer Kaytin Robinson', NULL, NULL),
(1734406, NULL, 'Amaury de Crayencour', NULL, NULL),
(1766423, NULL, 'Ieva AndrejevaitÄ—', NULL, NULL),
(1782117, NULL, 'Molly Gordon', NULL, NULL),
(1783522, NULL, 'Karin Takahashi', NULL, NULL),
(1784612, NULL, 'Daniela Melchior', NULL, NULL),
(1785590, NULL, 'David Corenswet', NULL, NULL),
(1803287, NULL, 'Geraldine Viswanathan', NULL, NULL),
(1808592, NULL, 'James Clayton', NULL, NULL),
(1808625, NULL, 'Elan Ross Gibson', NULL, NULL),
(1814297, NULL, 'Lee Soo', NULL, NULL),
(1826078, NULL, 'AgnÃ¨s Hurstel', NULL, NULL),
(1833002, NULL, 'Sirena Gulamgaus', NULL, NULL),
(1837297, NULL, 'Damson Idris', NULL, NULL),
(1842294, NULL, 'Eric Tipton', NULL, NULL),
(1871887, NULL, 'Mia Morrissey', NULL, NULL),
(1872281, NULL, 'Daniel Jun', NULL, NULL),
(1893251, NULL, 'Vishwak Sen', NULL, NULL),
(1898600, NULL, 'Jackson White', NULL, NULL),
(1907997, NULL, 'Min Do-yoon', NULL, NULL),
(1908004, NULL, 'Sang Woo', NULL, NULL),
(1938317, NULL, 'Adam B. Stein', NULL, NULL),
(1948606, NULL, 'May Hong', NULL, NULL),
(1949603, NULL, 'Gabbi Garcia', NULL, NULL),
(1967163, NULL, 'Xavier Lacaille', NULL, NULL),
(1970630, NULL, 'Pedro Correa', NULL, NULL),
(1976084, NULL, 'Sarah Lang', NULL, NULL),
(1983073, NULL, 'Park Gyu-young', NULL, NULL),
(1997524, NULL, 'Fei Ren', NULL, NULL),
(2004086, NULL, 'Tomori Kusunoki', NULL, NULL),
(2009739, NULL, 'Domee Shi', NULL, NULL),
(2027555, NULL, 'JesÃºs GuzmÃ¡n', NULL, NULL),
(2032020, NULL, 'Mike Van Waes', NULL, NULL),
(2052200, NULL, 'Juliet Doherty', NULL, NULL),
(2053430, NULL, 'Antonie FormanovÃ¡', NULL, NULL),
(2054851, NULL, 'Roman Griffin Davis', NULL, NULL),
(2064124, NULL, 'Nico Parker', NULL, NULL),
(2077864, NULL, 'Chase Stokes', NULL, NULL),
(2092053, NULL, 'Gabriella Quezada', NULL, NULL),
(2094677, NULL, 'Park Hyun-jung', NULL, NULL),
(2104243, NULL, 'Shay Hatten', NULL, NULL),
(2104260, NULL, 'Harry Trevaldwyn', NULL, NULL),
(2120295, NULL, 'Wilma Hollis', NULL, NULL),
(2125455, NULL, 'Walnette Marie Santiago', NULL, NULL),
(2144356, NULL, 'Bobby Bonifacio', NULL, NULL),
(2149762, NULL, 'Yoo Su-bin', NULL, NULL),
(2153405, NULL, 'MatyÃ¡Å¡ Å˜eznÃ­Äek', NULL, NULL),
(2157538, NULL, 'Billy Villeta', NULL, NULL),
(2181157, NULL, 'Sebastian Chacon', NULL, NULL),
(2198345, NULL, 'Lori Evans Taylor', NULL, NULL),
(2282830, NULL, 'Koyoharu Gotouge', NULL, NULL),
(2286541, NULL, 'Sarah Pidgeon', NULL, NULL),
(2300917, NULL, 'Francesca Waters', NULL, NULL),
(2353260, NULL, 'Michael Matic', NULL, NULL),
(2354094, NULL, 'Alessandro CalÃ¬ Ventura', NULL, NULL),
(2359492, NULL, 'Fairouz Ai', NULL, NULL),
(2362672, NULL, 'Tut Nyuot', NULL, NULL),
(2367353, NULL, 'Yang Yu', NULL, NULL),
(2367355, NULL, 'Lu Yanting', NULL, NULL),
(2367356, NULL, 'Joseph', NULL, NULL),
(2368846, NULL, 'Han Mo', NULL, NULL),
(2368848, NULL, 'Chen Hao', NULL, NULL),
(2368849, NULL, 'Lu Qi', NULL, NULL),
(2368857, NULL, 'Zhang Jiaming', NULL, NULL),
(2371545, NULL, 'Joel Kim Booster', NULL, NULL),
(2383553, NULL, 'Tomasz WiÅ„ski', NULL, NULL),
(2405377, NULL, 'Cristian Tapia Marchiori', NULL, NULL),
(2407997, NULL, 'Cary Christopher', NULL, NULL),
(2408703, NULL, 'Maria Bakalova', NULL, NULL),
(2420169, NULL, 'Chase Sui Wonders', NULL, NULL),
(2438324, NULL, 'Callie Schuttera', NULL, NULL),
(2443561, NULL, 'Young Dylan', NULL, NULL),
(2459258, NULL, 'Grace O\'Sullivan', NULL, NULL),
(2467484, NULL, 'Lee Min-woo', NULL, NULL),
(2467558, NULL, 'Peng Bo', NULL, NULL),
(2477076, NULL, 'Guillermo BarragÃ¡n', NULL, NULL),
(2484593, NULL, 'Choi Sung-eun', NULL, NULL),
(2486773, NULL, 'Jung In', NULL, NULL),
(2496534, NULL, 'Philippine Velge', NULL, NULL),
(2509658, NULL, 'Alisha Ahamed', NULL, NULL),
(2512183, NULL, 'Danya Jimenez', NULL, NULL),
(2512184, NULL, 'Hannah McMechan', NULL, NULL),
(2513312, NULL, 'Lee Dong-Joon', NULL, NULL),
(2521623, NULL, 'Juliana Galvis', NULL, NULL),
(2525701, NULL, 'David Iacono', NULL, NULL),
(2553497, NULL, 'Tae Hee', NULL, NULL),
(2568575, NULL, 'Eddie Yu', NULL, NULL),
(2576502, NULL, 'Gil Dong', NULL, NULL),
(2586775, NULL, 'Zhao Leiqi', NULL, NULL),
(2593447, NULL, 'Paisley Cadorath', NULL, NULL),
(2595136, NULL, 'Yves Lecat', NULL, NULL),
(2595331, NULL, 'Quentin Surtel', NULL, NULL),
(2619408, NULL, 'James', NULL, NULL),
(2644834, NULL, 'Ge Shuai', NULL, NULL),
(2651106, NULL, 'Stephen Adekolu', NULL, NULL),
(2669816, NULL, 'Moses Jones', NULL, NULL),
(2673750, NULL, 'Myha\'la', NULL, NULL),
(2673791, NULL, 'Bill Parker', NULL, NULL),
(2682053, NULL, 'Sol Eugenio', NULL, NULL),
(2746473, NULL, 'Karl Richmond', NULL, NULL),
(2749667, NULL, 'Eason Hung', NULL, NULL),
(2752446, NULL, 'Nijah Brenea', NULL, NULL),
(2761308, NULL, 'David Jonsson', NULL, NULL),
(2764542, NULL, 'Cooper Hoffman', NULL, NULL),
(2780098, NULL, 'Blessing Adedijo', NULL, NULL),
(2803710, NULL, 'Mason Thames', NULL, NULL),
(2808625, NULL, 'Mike Jones', NULL, NULL),
(2812912, NULL, 'Shogo Sakata', NULL, NULL),
(2845000, NULL, 'Joana Nwamerue', NULL, NULL),
(2851847, NULL, 'Diana Hoyos', NULL, NULL),
(2890913, NULL, 'Lea Myren', NULL, NULL),
(2926221, NULL, 'Jack Kenny', NULL, NULL),
(2959345, NULL, 'Billy Jake Cortez', NULL, NULL),
(2983147, NULL, 'Ji-young Yoo', NULL, NULL),
(2984635, NULL, 'Malte GÃ¥rdinger', NULL, NULL),
(2986863, NULL, 'Yonas Kibreab', NULL, NULL),
(3003169, NULL, 'Maggie Kang', NULL, NULL),
(3011652, NULL, 'Mia Tomlinson', NULL, NULL),
(3025125, NULL, 'Sydney Agudong', NULL, NULL),
(3033265, NULL, 'Ben Wang', NULL, NULL),
(3042998, NULL, 'KÃ­la Lord Cassidy', NULL, NULL),
(3052321, NULL, 'Tyriq Withers', NULL, NULL),
(3103059, NULL, 'Danica Davis', NULL, NULL),
(3115351, NULL, 'Diana Tsoy', NULL, NULL),
(3126191, NULL, 'Flo Fagerli', NULL, NULL),
(3126573, NULL, 'Choi Hyun-wook', NULL, NULL),
(3154351, NULL, 'Akanksha Sharma', NULL, NULL),
(3164807, NULL, 'Yoon Yool', NULL, NULL),
(3165040, NULL, 'Rika', NULL, NULL),
(3237637, NULL, 'Kamakshi Bhaskarla', NULL, NULL),
(3238101, NULL, 'Joshua Odjick', NULL, NULL),
(3304725, NULL, 'Jeremy Ang Jones', NULL, NULL),
(3343058, NULL, 'Ruben Ortiz', NULL, NULL),
(3353535, NULL, 'Andrea Babierra', NULL, NULL),
(3357415, NULL, 'Chen Weiran', NULL, NULL),
(3371806, NULL, 'Robb Guinto', NULL, NULL),
(3395594, NULL, 'Jordan Gonzalez', NULL, NULL),
(3398439, NULL, 'Seiko Mihara', NULL, NULL),
(3424435, NULL, 'Yoo Ji-hyun', NULL, NULL),
(3424436, NULL, 'Ji Min-iI', NULL, NULL),
(3424437, NULL, 'Kim Tae-hoon', NULL, NULL),
(3455058, NULL, 'Remy Edgerly', NULL, NULL),
(3480304, NULL, 'Kaitlyn Santa Juana', NULL, NULL),
(3495653, NULL, 'Ana Yi Puig', NULL, NULL),
(3496624, NULL, 'Takashi Kimura', NULL, NULL),
(3534623, NULL, 'Cheng Siyi', NULL, NULL),
(3535989, NULL, 'Aidan Laprete', NULL, NULL),
(3568019, NULL, 'Tadayoshi Ishizuka', NULL, NULL),
(3603443, NULL, 'Juan Calma', NULL, NULL),
(3608024, NULL, 'Petra HÅ¯lovÃ¡', NULL, NULL),
(3613013, NULL, 'Yu Bolin', NULL, NULL),
(3624081, NULL, 'Chris Kekaniokalani Bright', NULL, NULL),
(3651176, NULL, 'Kikunosuke Toya', NULL, NULL),
(3656771, NULL, 'Darrell Smith', NULL, NULL),
(3673493, NULL, 'Audrina Miranda', NULL, NULL),
(3674367, NULL, 'Kenny', NULL, NULL),
(3676023, NULL, 'Isac Calmroth', NULL, NULL),
(3678291, NULL, 'Carolina Saade', NULL, NULL),
(3712964, NULL, 'Matthew Nemeth', NULL, NULL),
(3747367, NULL, 'Surabhi Prabhavathi', NULL, NULL),
(3767680, NULL, 'Steve Barnett', NULL, NULL),
(3792104, NULL, 'Bae Gang-hee', NULL, NULL),
(3792786, NULL, 'Gabriel Howell', NULL, NULL),
(3794191, NULL, 'Hwang Sung-bin', NULL, NULL),
(3839198, NULL, 'Tilly Walker', NULL, NULL),
(3886342, NULL, 'Nora LandsrÃ¸d', NULL, NULL),
(3914706, NULL, 'Chase Infiniti', NULL, NULL),
(3970304, NULL, 'Jalyn Perez', NULL, NULL),
(3987900, NULL, 'Lee Tae-sung', NULL, NULL),
(3988423, NULL, 'Maia Kealoha', NULL, NULL),
(4012042, NULL, 'Apple Dy', NULL, NULL),
(4022587, NULL, 'Kaipo Dudoit', NULL, NULL),
(4095744, NULL, 'Dyessa Garcia', NULL, NULL),
(4102576, NULL, 'JosÃ© Carlos Illanes', NULL, NULL),
(4103293, NULL, 'Jenn Rosa', NULL, NULL),
(4123026, NULL, 'Aaron Rabin', NULL, NULL),
(4191243, NULL, 'Mao Fan', NULL, NULL),
(4201517, NULL, 'Marvic Kevin Reyes', NULL, NULL),
(4250352, NULL, 'Chen Duo-Yi', NULL, NULL),
(4267446, NULL, 'Alessandro Ambrosini', NULL, NULL),
(4267452, NULL, 'Paolo Gallina', NULL, NULL),
(4299620, NULL, 'Alex Lee', NULL, NULL),
(4333289, NULL, 'Vasudeva Murthy', NULL, NULL),
(4408050, NULL, 'Gabbriette', NULL, NULL),
(4431189, NULL, 'Alara-Star Khan', NULL, NULL),
(4477653, NULL, 'Sunisith', NULL, NULL),
(4479608, NULL, 'AndrÃ©s Valencia', NULL, NULL),
(4526195, NULL, 'Faith Delaney', NULL, NULL),
(4669945, NULL, 'Sheena Cole', NULL, NULL),
(4680507, NULL, 'Izza Watanabe', NULL, NULL),
(4716215, NULL, 'Marco Antonini', NULL, NULL),
(4745902, NULL, 'Sam Lansky', NULL, NULL),
(4758270, NULL, 'James Lee Thomas', NULL, NULL),
(4841107, NULL, 'Amelia Bishop', NULL, NULL),
(4866792, NULL, 'Skye Gonzaga', NULL, NULL),
(4872540, NULL, 'Ava McCarthy', NULL, NULL),
(4927413, NULL, 'Yu Chen', NULL, NULL),
(5035291, NULL, 'Suleiman Abutu', NULL, NULL),
(5043998, NULL, 'Guo Haiwen', NULL, NULL),
(5077775, NULL, 'Brandon Moon', NULL, NULL),
(5107231, NULL, 'BenoÃ®t Crou', NULL, NULL),
(5165492, NULL, 'Aliya Raymundo', NULL, NULL),
(5210260, NULL, 'Rob Brown', NULL, NULL),
(5224789, NULL, 'Zhou Yongxi', NULL, NULL),
(5240969, NULL, 'Ram Narayan', NULL, NULL),
(5300499, NULL, 'Mario Zamora S.', NULL, NULL),
(5312570, NULL, 'Clara Ambrosoni', NULL, NULL),
(5326859, NULL, 'Karen Lopez', NULL, NULL),
(5345557, NULL, 'Gonzalo Gravano', NULL, NULL),
(5345558, NULL, 'Bianca Di Pasquale', NULL, NULL),
(5441666, NULL, 'Grace Chan', NULL, NULL),
(5506436, NULL, 'Matteo Radaelli', NULL, NULL),
(5506439, NULL, 'Salvatore Torrisi', NULL, NULL),
(5506440, NULL, 'Angelo Thomann', NULL, NULL),
(5538791, NULL, 'Saksham Sharma', NULL, NULL),
(5574619, NULL, 'Dante Aragon', NULL, NULL),
(5684693, NULL, 'Lee Jin-seong', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `id` int NOT NULL,
  `user_id` int NOT NULL,
  `movie_id` int NOT NULL,
  `rating` tinyint NOT NULL,
  `review_text` varchar(5000) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ;

--
-- Dumping data for table `reviews`
--

INSERT INTO `reviews` (`id`, `user_id`, `movie_id`, `rating`, `review_text`, `created_at`, `updated_at`) VALUES
(1, 1, 1447287, 7, 'Review 1 by user1', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(2, 1, 575265, 8, 'Review 2 by user1', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(3, 1, 1083433, 2, 'Review 3 by user1', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(4, 2, 1022787, 2, 'Review 4 by user2', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(5, 2, 1038392, 2, 'Review 5 by user2', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(6, 2, 755898, 6, 'Review 6 by user2', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(7, 3, 13494, 1, 'Review 7 by user3', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(8, 3, 604079, 2, 'Review 8 by user3', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(9, 3, 1337395, 1, 'Review 9 by user3', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(10, 4, 617126, 5, 'Review 10 by user4', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(11, 4, 574475, 10, 'Review 11 by user4', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(12, 4, 13494, 3, 'Review 12 by user4', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(13, 5, 506763, 6, 'Review 13 by user5', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(14, 5, 13494, 9, 'Review 14 by user5', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(15, 5, 1382406, 5, 'Review 15 by user5', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(16, 6, 541671, 9, 'Review 16 by user6', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(17, 6, 1061474, 9, 'Review 17 by user6', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(18, 6, 1369679, 9, 'Review 18 by user6', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(19, 7, 1447287, 3, 'Review 19 by user7', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(20, 7, 506763, 10, 'Review 20 by user7', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(21, 7, 1087192, 3, 'Review 21 by user7', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(22, 8, 1311031, 1, 'Review 22 by user8', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(23, 8, 7451, 9, 'Review 23 by user8', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(24, 8, 691363, 1, 'Review 24 by user8', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(25, 9, 1009640, 3, 'Review 25 by user9', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(26, 9, 828769, 5, 'Review 26 by user9', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(27, 9, 1403735, 3, 'Review 27 by user9', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(28, 10, 914215, 6, 'Review 28 by user10', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(29, 10, 1337395, 5, 'Review 29 by user10', '2025-11-25 15:48:54', '2025-11-25 15:48:54'),
(30, 10, 1007734, 2, 'Review 30 by user10', '2025-11-25 15:48:54', '2025-11-25 15:48:54');

--
-- Triggers `reviews`
--
DELIMITER $$
CREATE TRIGGER `trg_after_delete_review` AFTER DELETE ON `reviews` FOR EACH ROW BEGIN
  CALL update_movie_avg_rating(OLD.movie_id);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_after_insert_review` AFTER INSERT ON `reviews` FOR EACH ROW BEGIN
  CALL update_movie_avg_rating(NEW.movie_id);
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `tags`
--

CREATE TABLE `tags` (
  `id` int NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tags`
--

INSERT INTO `tags` (`id`, `name`) VALUES
(1, 'Marxist');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `is_verified` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_login` datetime DEFAULT NULL,
  `role` enum('USER','ADMIN') DEFAULT 'USER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `password_hash`, `full_name`, `is_verified`, `created_at`, `last_login`, `role`) VALUES
(1, 'user1', 'user1@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER'),
(2, 'user2', 'user2@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER'),
(3, 'user3', 'user3@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER'),
(4, 'user4', 'user4@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER'),
(5, 'user5', 'user5@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER'),
(6, 'user6', 'user6@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER'),
(7, 'user7', 'user7@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER'),
(8, 'user8', 'user8@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER'),
(9, 'user9', 'user9@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER'),
(10, 'user10', 'user10@example.com', '$2a$10$FakeHashedPassword', NULL, 1, '2025-11-25 15:48:53', NULL, 'USER');

-- --------------------------------------------------------

--
-- Stand-in structure for view `user_profile_view`
-- (See below for the actual view)
--
CREATE TABLE `user_profile_view` (
`avg_rating_given` decimal(6,2)
,`total_reviews` bigint
,`user_id` int
,`username` varchar(50)
);

-- --------------------------------------------------------

--
-- Table structure for table `watchlists`
--

CREATE TABLE `watchlists` (
  `id` int NOT NULL,
  `user_id` int NOT NULL,
  `movie_id` int NOT NULL,
  `added_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `watchlists`
--

INSERT INTO `watchlists` (`id`, `user_id`, `movie_id`, `added_at`) VALUES
(1, 1, 911430, '2025-11-25 15:48:54'),
(2, 1, 980477, '2025-11-25 15:48:54'),
(3, 2, 1289888, '2025-11-25 15:48:54'),
(4, 2, 155, '2025-11-25 15:48:54'),
(5, 3, 1447287, '2025-11-25 15:48:54'),
(6, 3, 1061474, '2025-11-25 15:48:54'),
(7, 4, 1382406, '2025-11-25 15:48:54'),
(8, 4, 1054867, '2025-11-25 15:48:54'),
(9, 5, 1087192, '2025-11-25 15:48:54'),
(10, 5, 803796, '2025-11-25 15:48:54'),
(11, 6, 1038392, '2025-11-25 15:48:54'),
(12, 6, 1022787, '2025-11-25 15:48:54'),
(13, 7, 1265344, '2025-11-25 15:48:54'),
(14, 7, 993234, '2025-11-25 15:48:54'),
(15, 8, 1007734, '2025-11-25 15:48:54'),
(16, 8, 1083433, '2025-11-25 15:48:54'),
(17, 9, 715287, '2025-11-25 15:48:54'),
(18, 9, 1337395, '2025-11-25 15:48:54'),
(19, 10, 1447287, '2025-11-25 15:48:54'),
(20, 10, 1078605, '2025-11-25 15:48:54');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `collections`
--
ALTER TABLE `collections`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `collection_movies`
--
ALTER TABLE `collection_movies`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ux_collection_movie` (`collection_id`,`movie_id`),
  ADD KEY `movie_id` (`movie_id`);

--
-- Indexes for table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `review_id` (`review_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `companies`
--
ALTER TABLE `companies`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `genres`
--
ALTER TABLE `genres`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `movies`
--
ALTER TABLE `movies`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `tmdb_id` (`tmdb_id`);

--
-- Indexes for table `movie_cast`
--
ALTER TABLE `movie_cast`
  ADD PRIMARY KEY (`id`),
  ADD KEY `movie_id` (`movie_id`),
  ADD KEY `person_id` (`person_id`);

--
-- Indexes for table `movie_companies`
--
ALTER TABLE `movie_companies`
  ADD PRIMARY KEY (`id`),
  ADD KEY `movie_id` (`movie_id`),
  ADD KEY `company_id` (`company_id`);

--
-- Indexes for table `movie_crew`
--
ALTER TABLE `movie_crew`
  ADD PRIMARY KEY (`id`),
  ADD KEY `movie_id` (`movie_id`),
  ADD KEY `person_id` (`person_id`);

--
-- Indexes for table `movie_genres`
--
ALTER TABLE `movie_genres`
  ADD PRIMARY KEY (`movie_id`,`genre_id`),
  ADD KEY `genre_id` (`genre_id`);

--
-- Indexes for table `movie_tags`
--
ALTER TABLE `movie_tags`
  ADD PRIMARY KEY (`movie_id`,`tag_id`),
  ADD KEY `tag_id` (`tag_id`);

--
-- Indexes for table `people`
--
ALTER TABLE `people`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `tmdb_id` (`tmdb_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `movie_id` (`movie_id`);

--
-- Indexes for table `tags`
--
ALTER TABLE `tags`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `watchlists`
--
ALTER TABLE `watchlists`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ux_watchlist_user_movie` (`user_id`,`movie_id`),
  ADD KEY `movie_id` (`movie_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `collections`
--
ALTER TABLE `collections`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `collection_movies`
--
ALTER TABLE `collection_movies`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `companies`
--
ALTER TABLE `companies`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=273294;

--
-- AUTO_INCREMENT for table `genres`
--
ALTER TABLE `genres`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10771;

--
-- AUTO_INCREMENT for table `movies`
--
ALTER TABLE `movies`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1506457;

--
-- AUTO_INCREMENT for table `movie_cast`
--
ALTER TABLE `movie_cast`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=563;

--
-- AUTO_INCREMENT for table `movie_companies`
--
ALTER TABLE `movie_companies`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=168;

--
-- AUTO_INCREMENT for table `movie_crew`
--
ALTER TABLE `movie_crew`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=157;

--
-- AUTO_INCREMENT for table `people`
--
ALTER TABLE `people`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5684694;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tags`
--
ALTER TABLE `tags`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `watchlists`
--
ALTER TABLE `watchlists`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

-- --------------------------------------------------------

--
-- Structure for view `comment_thread_view`
--
DROP TABLE IF EXISTS `comment_thread_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `comment_thread_view`  AS SELECT `c`.`id` AS `comment_id`, `c`.`comment_text` AS `comment_text`, `c`.`created_at` AS `created_at`, `u`.`username` AS `commenter`, `r`.`id` AS `review_id`, `r`.`review_text` AS `review_text`, `m`.`title` AS `movie_title` FROM (((`comments` `c` join `users` `u` on((`u`.`id` = `c`.`user_id`))) join `reviews` `r` on((`r`.`id` = `c`.`review_id`))) join `movies` `m` on((`m`.`id` = `r`.`movie_id`))) ;

-- --------------------------------------------------------

--
-- Structure for view `movie_summary_view`
--
DROP TABLE IF EXISTS `movie_summary_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `movie_summary_view`  AS SELECT `m`.`id` AS `movie_id`, `m`.`title` AS `title`, count(`r`.`id`) AS `review_count`, round(avg(`r`.`rating`),2) AS `avg_rating` FROM (`movies` `m` left join `reviews` `r` on((`r`.`movie_id` = `m`.`id`))) GROUP BY `m`.`id`, `m`.`title` ;

-- --------------------------------------------------------

--
-- Structure for view `user_profile_view`
--
DROP TABLE IF EXISTS `user_profile_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `user_profile_view`  AS SELECT `u`.`id` AS `user_id`, `u`.`username` AS `username`, count(`r`.`id`) AS `total_reviews`, round(avg(`r`.`rating`),2) AS `avg_rating_given` FROM (`users` `u` left join `reviews` `r` on((`r`.`user_id` = `u`.`id`))) GROUP BY `u`.`id`, `u`.`username` ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `collections`
--
ALTER TABLE `collections`
  ADD CONSTRAINT `collections_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `collection_movies`
--
ALTER TABLE `collection_movies`
  ADD CONSTRAINT `collection_movies_ibfk_1` FOREIGN KEY (`collection_id`) REFERENCES `collections` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `collection_movies_ibfk_2` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `comments`
--
ALTER TABLE `comments`
  ADD CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `movie_cast`
--
ALTER TABLE `movie_cast`
  ADD CONSTRAINT `movie_cast_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
  ADD CONSTRAINT `movie_cast_ibfk_2` FOREIGN KEY (`person_id`) REFERENCES `people` (`id`);

--
-- Constraints for table `movie_companies`
--
ALTER TABLE `movie_companies`
  ADD CONSTRAINT `movie_companies_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
  ADD CONSTRAINT `movie_companies_ibfk_2` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`);

--
-- Constraints for table `movie_crew`
--
ALTER TABLE `movie_crew`
  ADD CONSTRAINT `movie_crew_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `movie_crew_ibfk_2` FOREIGN KEY (`person_id`) REFERENCES `people` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `movie_genres`
--
ALTER TABLE `movie_genres`
  ADD CONSTRAINT `movie_genres_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
  ADD CONSTRAINT `movie_genres_ibfk_2` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`);

--
-- Constraints for table `movie_tags`
--
ALTER TABLE `movie_tags`
  ADD CONSTRAINT `movie_tags_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
  ADD CONSTRAINT `movie_tags_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`);

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `watchlists`
--
ALTER TABLE `watchlists`
  ADD CONSTRAINT `watchlists_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `watchlists_ibfk_2` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE;

DELIMITER $$
--
-- Events
--
CREATE DEFINER=`root`@`localhost` EVENT `evt_cleanup_unverified_users` ON SCHEDULE EVERY 1 DAY STARTS '2025-11-25 15:48:54' ON COMPLETION NOT PRESERVE ENABLE DO DELETE FROM users
  WHERE is_verified = FALSE
  AND created_at < (NOW() - INTERVAL 7 DAY)$$

DELIMITER ;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

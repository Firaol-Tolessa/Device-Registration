-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 21, 2023 at 09:50 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `managementserverdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `futronicdevice`
--

CREATE TABLE `futronicdevice` (
  `deviceid` varchar(20) NOT NULL,
  `device_public_key` varchar(500) NOT NULL,
  `capDate` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `futronicdevice`
--

INSERT INTO `futronicdevice` (`deviceid`, `device_public_key`, `capDate`) VALUES
('00000185', 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlxrYTEUdLBgZ7ZJi536KLW474W4pFy1//sYYhWPln/6osZ3lkYtVeMFne5ek/Mk8p/tVbBYidJktKEEbIm8t8koIZr2j4NlUVQZAN7eI5i9wrvlYlWbjQN4+SWMcVGNlUToWhWOaRdU/mjnhjqd3sA0UWFLkyBeffqFRmy/6FCMWQC1nAAAqoDnDdaxeWTwQQJmhKwNVBCxM1+StiRvwUJI+Y49DraAa4oVDTLr9xveKgw7Sy7EyLL9aHa3ep5OZ+f/F685DbDvYxhC4pq+wZilP6Cqdw0js1ueLbO/8QNJcCkdaLbBc6H3lotPa9B7gh6dB9qit09kswgvPL4xOqQIDAQAB', '2023-09-19 14:19:50');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

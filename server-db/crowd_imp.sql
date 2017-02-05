-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Nov 02, 2016 at 09:52 AM
-- Server version: 5.6.17
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `crowd_imp`
--

-- --------------------------------------------------------

--
-- Table structure for table `post`
--

CREATE TABLE IF NOT EXISTS `post` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `post_text` text NOT NULL,
  `img` text NOT NULL,
  `location` text NOT NULL,
  `date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `upload_user` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=73 ;

--
-- Dumping data for table `post`
--

INSERT INTO `post` (`id`, `post_text`, `img`, `location`, `date`, `modified_date`, `user_id`, `upload_user`) VALUES
(2, 'no light. .fix it!', '', 'MS lab', '2016-11-02 10:47:06', '2016-11-02 10:47:06', 4, 3),
(3, 'say no to wallchalking. . . ', '3.jpg', 'agriculture university', '2016-11-02 10:57:14', '2016-11-02 10:57:14', 4, 4),
(18, 'the BS teachers need to work hard on what ', '', 'DCS', '2016-11-02 11:16:43', '2016-11-02 11:16:43', 10, 4),
(19, 'the grass in the department ground is looking very ', '', 'DCS lawn', '2016-11-02 11:16:46', '2016-11-02 11:16:46', 4, 5),
(20, 'the teachers of the DCS are really helpf', '', 'DCS', '2016-11-02 11:16:59', '2016-11-02 11:16:59', 5, 4);

-- --------------------------------------------------------

--
-- Table structure for table `post_impression`
--

CREATE TABLE IF NOT EXISTS `post_impression` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `impression` int(4) NOT NULL,
  `user_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=77 ;

--
-- Dumping data for table `post_impression`
--

INSERT INTO `post_impression` (`id`, `impression`, `user_id`, `post_id`) VALUES
(1, 1, 3, 1),
(2, -1, 1, 2),
(3, 1, 5, 2),
(4, 1, 5, 1),
(5, -1, 4, 3),
(6, 1, 4, 3),
(7, 1, 5, 7),
(8, 1, 5, 6),
(9, 1, 5, 5),
(10, 1, 5, 8),
(11, 1, 4, 7),
(12, -1, 4, 8),
(13, 1, 5, 9),
(14, 1, 11, 2),
(15, -1, 5, 10),
(16, 1, 5, 11),
(17, 1, 10, 9),
(18, -1, 4, 12),
(19, -1, 4, 11),
(20, 1, 5, 16),
(21, -1, 4, 13),
(22, -1, 8, 15),
(23, 1, 10, 15),
(24, 1, 4, 10),
(25, 1, 5, 17),
(26, 1, 4, 22),
(27, 1, 4, 20),
(28, 1, 4, 19),
(29, 1, 5, 24),
(30, 1, 4, 17),
(31, 1, 5, 25),
(32, 1, 5, 27),
(33, -1, 5, 27),
(34, 1, 4, 23),
(35, 1, 5, 29),
(36, 1, 4, 34),
(37, 1, 4, 35),
(38, 1, 4, 31),
(39, 1, 4, 30),
(40, 1, 1, 36),
(41, -1, 4, 28),
(42, 1, 4, 25),
(43, 1, 4, 24),
(44, 1, 4, 38),
(45, 1, 4, 37),
(46, 1, 4, 26),
(47, 1, 4, 40),
(48, 1, 4, 39),
(49, 1, 4, 41),
(50, 1, 4, 44),
(51, 1, 4, 43),
(52, 1, 4, 42),
(53, 1, 4, 45),
(54, 1, 4, 47),
(55, 1, 1, 48),
(56, 1, 1, 47),
(57, 1, 1, 50),
(58, 1, 1, 49),
(59, 1, 4, 51),
(60, 1, 4, 56),
(61, 1, 4, 55),
(62, 1, 4, 54),
(63, 1, 4, 53),
(64, 1, 4, 52),
(65, 1, 4, 48),
(66, 1, 4, 50),
(67, 1, 4, 49),
(68, 1, 4, 15),
(69, 1, 4, 57),
(70, 1, 4, 59),
(71, 1, 4, 60),
(72, 1, 4, 63),
(73, 1, 4, 66),
(74, 1, 4, 65),
(75, 1, 4, 68),
(76, 1, 4, 67);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` text NOT NULL,
  `email_id` text NOT NULL,
  `password` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=12 ;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`, `email_id`, `password`) VALUES
(1, 'samir', 'samir.k433@yahoo.com', '12345'),
(2, 'ali_khan', 'ali@gmail.com', 'ali'),
(3, 'tayyab', 'tayyabahmed@gmail.com', 'tayyab12345'),
(4, 'kami', 'kami.uop@gmail.com', '12345'),
(5, 'somi', 'somi@gmail.com', 'somi123'),
(6, 'khan_09', 'khan09@gmail.com', '12345'),
(7, 'shah433', 'shah433@yahoo.com', '12345'),
(8, 'usman', 'usman55@yahoo.com', 'usman'),
(9, 'javad', 'javad123@yahoo.com', 'javad'),
(10, 'sifat', 'sifat99@yahoo.com', 'javad'),
(11, 'dildar_khan', 'dil_khan@yahoo.com', 'javad');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

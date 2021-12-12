USE `database`;

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;

CREATE TABLE `line` (
  `ownLineID` int(11) NOT NULL,
  `stateless` tinytext CHARACTER SET utf8 COLLATE utf8_german2_ci NOT NULL,
  `name` tinytext CHARACTER SET utf8 COLLATE utf8_german2_ci NOT NULL,
  `number` tinytext CHARACTER SET utf8 COLLATE utf8_german2_ci NOT NULL,
  `operator` tinytext CHARACTER SET utf8 COLLATE utf8_german2_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `lineDelays` (
  `lineID` int(11) NOT NULL,
  `linekey` int(11) NOT NULL,
  `requestTime` datetime NOT NULL,
  `delay` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `linePaths` (
  `ownLineID` int(11) NOT NULL,
  `path` text NOT NULL,
  `created` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `lineStarts` (
  `lineID` int(11) NOT NULL,
  `linekey` int(11) NOT NULL,
  `startTime` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `lineStops` (
  `lineID` int(11) NOT NULL,
  `stopID` int(11) NOT NULL,
  `departureDelay` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `stations` (
  `ownStopID` int(11) NOT NULL,
  `stopID` int(11) NOT NULL,
  `gid` tinytext CHARACTER SET utf8 COLLATE utf8_german2_ci NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `place` tinytext CHARACTER SET utf8 COLLATE utf8_german2_ci NOT NULL,
  `name` tinytext CHARACTER SET utf8 COLLATE utf8_german2_ci NOT NULL,
  `nameWithPlace` tinytext CHARACTER SET utf8 COLLATE utf8_german2_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `stationsMANUELL` (
  `stopID` int(11) NOT NULL,
  `nameWithPlace` tinytext CHARACTER SET utf8 COLLATE utf8_german2_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `line`
  ADD PRIMARY KEY (`ownLineID`);

ALTER TABLE `lineDelays`
  ADD KEY `fk_lineDelays_OwnLineID` (`lineID`);

ALTER TABLE `lineStarts`
  ADD PRIMARY KEY (`lineID`,`linekey`);

ALTER TABLE `lineStops`
  ADD KEY `fk_lineStops_OwnStopID` (`stopID`),
  ADD KEY `fk_lineStops_OwnLineID` (`lineID`);

ALTER TABLE `stations`
  ADD PRIMARY KEY (`ownStopID`);

ALTER TABLE `line`
  MODIFY `ownLineID` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `stations`
  MODIFY `ownStopID` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `lineDelays`
  ADD CONSTRAINT `fk_lineDelays_OwnLineID` FOREIGN KEY (`lineID`) REFERENCES `line` (`ownLineID`) ON DELETE CASCADE;

ALTER TABLE `lineStarts`
  ADD CONSTRAINT `fk_lineStarts_OwnLineID` FOREIGN KEY (`lineID`) REFERENCES `line` (`ownLineID`) ON DELETE CASCADE;

ALTER TABLE `lineStops`
  ADD CONSTRAINT `fk_lineStops_OwnLineID` FOREIGN KEY (`lineID`) REFERENCES `line` (`ownLineID`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_lineStops_OwnStopID` FOREIGN KEY (`stopID`) REFERENCES `stations` (`ownStopID`) ON DELETE CASCADE;

INSERT INTO `stations` (ownStopID, stopID, gid, x, y, place, name, nameWithPlace) VALUES
(
 (1, 6002417, 'de:08222:2417', '8.4699363', '49.4797472', 'Mannheim', 'Hauptbahnhof', 'Mannheim, Hauptbahnhof')
);

COMMIT;

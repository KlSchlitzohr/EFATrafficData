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

INSERT INTO `stations` (ownStopID, stopID, gid, x, y, place, name, nameWithPlace)
VALUES (1, 6002417, 'de:08222:2417', 8.4699363, 49.4797472, 'Mannheim', 'Hauptbahnhof', 'Mannheim, Hauptbahnhof'),
       (2, 6002357, 'de:08222:2357', 8.4874086, 49.484708, 'Mannheim', 'Lanzvilla', 'Mannheim, Lanzvilla'),
       (3, 6003144, 'de:07316:3144', 8.1399672, 49.3500404, 'Neustadt (Weinstraße)', 'Hauptbahnhof', 'Neustadt, Hauptbahnhof'),
       (4, 6005553, 'de:08222:5553', 8.4742033, 49.5468268, 'Schönau (MA)', 'Endstelle', 'Schönau, Endstelle'),
       (5, 5006115, 'de:08111:6115', 9.1827944, 48.7848768, 'Stuttgart', 'Hauptbahnhof (oben)', 'Stuttgart Hauptbahnhof (oben)'),
       (6, 7000090, 'de:08212:90', 8.4010356, 48.9933494, 'Karlsruhe', 'Hauptbahnhof', 'Karlsruhe Hauptbahnhof'),
       (7, 6003505, 'de:08226:3505', 8.5782911, 49.3833735, 'Schwetzingen', 'Bahnhof', 'Schwetzingen, Bahnhof'),
       (8, 6002470, 'de:08222:2470', 8.4931488, 49.5074102, 'Mannheim', 'Ulmenweg', 'Mannheim, Ulmenweg'),
       (9, 6002525, 'de:08222:2525', 8.5350552, 49.424158, 'Rheinau (MA)', 'Bahnhof', 'MA-Rheinau, Bahnhof'),
       (10, 7032637, 'de:07334:32637', 8.365076, 49.225588, 'Germersheim', 'Germersheim', 'Germersheim'),
       (11, 28580953, 'de:02000:80953', 9.9343789, 53.5519845, 'Hamburg', 'Altona', 'Hamburg Altona'),
       (12, 6000116, 'de:07332:116', 8.170447, 49.4609792, 'Bad Dürkheim', 'Bahnhof', 'Bad Dürkheim, Bahnhof'),
       (13, 6002650, 'de:08222:2650', 8.4949005, 49.5358564, 'Waldhof (MA)', 'Waldfriedhof', 'Waldhof, Waldfriedhof'),
       (14, 6002567, 'de:08222:2567', 8.4496344, 49.5418724, 'Sandhofen', 'Endstelle', 'Sandhofen, Endstelle'),
       (15, 6008209, 'de:08225:8209', 9.143556, 49.3522641, 'Mosbach (Baden)', 'Bahnhof', 'Mosbach, Bahnhof'),
       (16, 7032585, 'de:08215:32585', 8.4906156, 49.1617754, 'Graben-Neudorf', 'Graben-Neudorf', 'Graben-Neudorf'),
       (17, 20005194, 'de:05911:5194', 7.2233352, 51.4794661, 'Bochum', 'Hauptbahnhof', 'Bochum Hauptbahnhof'),
       (18, 6005700, 'de:07314:5700', 8.3643573, 49.488577, 'Oggersheim', 'Endstelle', 'Oggersheim, Endstelle'),
       (19, 6003508, 'de:08226:3508', 8.5693619, 49.3791687, 'Schwetzingen', 'Krankenhaus', 'Schwetzingen, Krankenhaus'),
       (20, 6003894, 'de:08222:3894', 8.5168104, 49.4602377, 'Neuhermsheim', 'SAP Arena S-Bahnhof (RNV)', 'Neuhermsheim, SAP Arena S-Bf. (RNV)'),
       (21, 6002501, 'de:08222:2501', 8.4697477, 49.450007, 'Neckarau', 'Rheingoldhalle', 'Neckarau, Rheingoldhalle'),
       (22, 6009037, 'de:07315:9037', 8.258419, 50.0014502, 'Mainz', 'Hauptbahnhof', 'Mainz, Hauptbahnhof'),
       (23, 6002451, 'de:08222:2451', 8.4662353, 49.4877776, 'Mannheim', 'Paradeplatz', 'Mannheim, Paradeplatz'),
       (24, 6005563, 'de:08226:5563', 8.5946315, 49.5049427, 'Heddesheim (Baden)', 'Bahnhof (RNV)', 'Heddesheim, Bahnhof (RNV)'),
       (25, 10000010, 'de:06412:10', 8.6626609, 50.1068098, 'Frankfurt (Main)', 'Hauptbahnhof', 'Frankfurt (Main) Hauptbahnhof'),
       (26, 6002429, 'de:08222:2429', 8.4961941, 49.4787783, 'Mannheim', 'Luisenpark/Technoseum', 'Mannheim, Luisenp./Technoseum'),
       (27, 6006006, 'de:08222:6006', 8.4575665, 49.4967226, 'Mannheim', 'Popakademie', 'Mannheim, Popakademie'),
       (28, 6005560, 'de:08222:5560', 8.4698555, 49.4646692, 'Lindenhof (MA)', 'Pfalzplatz', 'Lindenhof, Pfalzplatz'),
       (29, 6001664, 'de:08226:1664', 8.5293599, 49.3501048, 'Ketsch', 'Gewerbegebiet Süd', 'Ketsch, Gewerbegebiet Süd'),
       (30, 40000153, 'de:11000:900120005', 13.4350866, 52.5103305, 'Berlin', 'S Ostbahnhof', 'S Ostbahnhof (Berlin)');

COMMIT;

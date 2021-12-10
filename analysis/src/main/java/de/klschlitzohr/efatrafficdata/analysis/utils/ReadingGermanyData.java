package de.klschlitzohr.efatrafficdata.analysis.utils;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

@Getter
@Log4j2
public class ReadingGermanyData {

    private ArrayList<Point> pointsGermany;
    private Grid grid;

    public ReadingGermanyData() {
        pointsGermany = new ArrayList<>();
        readFiles();
    }

    public void readFiles() {
        pointsGermany = readFile(new File("deutschland-points.txt"));
        double minPointX = pointsGermany.stream().mapToDouble(Point::getX).min().getAsDouble();
        double maxPointX = pointsGermany.stream().mapToDouble(Point::getX).max().getAsDouble();
        double minPointY = pointsGermany.stream().mapToDouble(Point::getY).min().getAsDouble();
        double maxPointY = pointsGermany.stream().mapToDouble(Point::getY).max().getAsDouble();
        log.info("Min X: " + minPointX);
        log.info("Max X: " + maxPointX);
        log.info("Min Y: " + minPointY);
        log.info("Max Y: " + maxPointY);
        log.info(maxPointX - minPointX);
        log.info(maxPointY - minPointY);

        double width = maxPointX - minPointX;
        double height = maxPointY - minPointY;

        ArrayList<ArrayList<Point>> pointsList = new ArrayList<>();
        pointsList.add(pointsGermany);
        pointsList.add(readFile(new File("baden-wuerttemberg-points.txt")));
        pointsList.add(readFile(new File("hessen-points.txt")));
        pointsList.add(readFile(new File("saarland-points.txt")));

        grid = new Grid(pointsList, width, height,minPointX,minPointY);
    }

    private ArrayList<Point> readFile(File file) {
        ArrayList<Point> points = new ArrayList<>();
        try {
            String[] strings = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))).split("\n");
            Stream<String> filestream = Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
            ArrayList<String> fileline = new ArrayList<>();
            filestream.forEach(fileline::add);
            double x = 0;
            double y = 0;
            for (String string : fileline) {
                if (string.trim().isEmpty()) {
                    if (x != 0 && y != 0)
                        points.add(new Point(x, y));
                    x = 0;
                    y = 0;
                } else {
                    if (string.contains(","))
                        x = Double.parseDouble(string.replace(",", ""));
                    else
                        y = Double.parseDouble(string);
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return points;
    }

}

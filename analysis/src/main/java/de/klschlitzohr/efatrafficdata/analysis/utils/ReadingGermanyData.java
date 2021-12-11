package de.klschlitzohr.efatrafficdata.analysis.utils;

import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.github.filosganga.geogson.model.FeatureCollection;
import com.github.filosganga.geogson.model.MultiPolygon;
import com.github.filosganga.geogson.model.Polygon;
import com.github.filosganga.geogson.model.positions.AbstractPositions;
import com.github.filosganga.geogson.model.positions.LinearPositions;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        var featureGermany = getFeatureCollection("https://raw.githubusercontent.com/isellsoap/deutschlandGeoJSON/main/1_deutschland/3_mittel.geo.json");
        var featureStates = getFeatureCollection("https://raw.githubusercontent.com/isellsoap/deutschlandGeoJSON/main/2_bundeslaender/2_hoch.geo.json");
        if (featureGermany == null || featureStates == null) {
            return;
        }
        pointsGermany = getGeoPoints(featureGermany, 0); // Deutschland
        double minPointX = pointsGermany.stream().mapToDouble(Point::getX).min().orElse(0);
        double maxPointX = pointsGermany.stream().mapToDouble(Point::getX).max().orElse(0);
        double minPointY = pointsGermany.stream().mapToDouble(Point::getY).min().orElse(0);
        double maxPointY = pointsGermany.stream().mapToDouble(Point::getY).max().orElse(0);
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
        pointsList.add(getGeoPoints(featureStates, 0));  // Baden-Württemberg
        pointsList.add(getGeoPoints(featureStates, 6));  // Hessen
        pointsList.add(getGeoPoints(featureStates, 11)); // Saarland

        log.info("Finished get points");

        grid = new Grid(pointsList, width, height,minPointX,minPointY);
    }

    private FeatureCollection getFeatureCollection(String url) {
        try {
            var gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new GeometryAdapterFactory())
                    .create();
            var reader = new InputStreamReader(new URL(url).openStream());
            return gson.fromJson(reader, FeatureCollection.class);
        } catch (Exception ex) {
            log.error(ex);
            return null;
        }
    }

    private ArrayList<Point> getGeoPoints(FeatureCollection featureCollection, int featureIndex) {
        var feature = featureCollection.features().get(featureIndex);
        if (feature.geometry() instanceof MultiPolygon multiPolygon) {
            return getGeoPoints(multiPolygon);
        }
        if (feature.geometry() instanceof Polygon polygon) {
            return getGeoPoints(polygon);
        }
        log.error("Type '" + feature.geometry().getClass().getSimpleName() + "' not supported!");
        return new ArrayList<>();
    }

    private ArrayList<Point> getGeoPoints(MultiPolygon multiPolygon) {
        var linearPositions = multiPolygon
                .positions().children().stream()
                .map(AbstractPositions::children)
                .flatMap(List::stream);

        return getGeoPoints(linearPositions);
    }

    private ArrayList<Point> getGeoPoints(Polygon polygon) {
        var linearPositions = polygon.positions().children().stream();

        return getGeoPoints(linearPositions);
    }

    private ArrayList<Point> getGeoPoints(Stream<LinearPositions> linearPositions) {
        var points = linearPositions
                .map(AbstractPositions::children)
                .flatMap(List::stream)
                .map(x -> new Point(x.lon(), x.lat()))
                .toList();

        return new ArrayList<>(points);
    }
}

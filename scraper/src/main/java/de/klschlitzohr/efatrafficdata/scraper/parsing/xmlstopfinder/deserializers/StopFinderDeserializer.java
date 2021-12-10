package de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.deserializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.Point;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.ItdOdvAssignedStop;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.StopFinder;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created on 30.10.2021
 *
 * @author KlSchlitzohr
 */
public final class StopFinderDeserializer implements JsonDeserializer<StopFinder> {

    @SneakyThrows
    @Override
    public StopFinder deserialize(JsonElement jsonElement, Type type,
                                  JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!(jsonElement instanceof JsonObject)) {
            throw new JsonParseException("Unexpected json element of type " + jsonElement.getClass().getName());
        }
        JsonObject object = jsonElement.getAsJsonObject();

        JsonElement pointsElement = object.get("points");
        List<Point> points;

        if (pointsElement.isJsonArray()) {
            points = jsonDeserializationContext.deserialize(pointsElement, new TypeToken<List<Point>>() {
            }.getType());
        } else if (pointsElement.isJsonObject()) {
            JsonObject pointsObj = pointsElement.getAsJsonObject();
            points = Collections.singletonList(jsonDeserializationContext.deserialize(pointsObj.get("point"), Point.class));
        } else {
            //throw new JsonParseException("Unexpected json element of type " + pointsElement.getClass().getName());
            points = Collections.emptyList();
        }


        JsonElement itdOdvAssignedStopsElement = object.get("itdOdvAssignedStops");
        List<ItdOdvAssignedStop> itdOdvAssignedStops;
        if (itdOdvAssignedStopsElement != null) {
            if (itdOdvAssignedStopsElement.isJsonObject()) {
                itdOdvAssignedStops = Collections.singletonList
                        (jsonDeserializationContext.deserialize(itdOdvAssignedStopsElement, ItdOdvAssignedStop.class));
            } else if (itdOdvAssignedStopsElement.isJsonArray()) {
                itdOdvAssignedStops = jsonDeserializationContext.deserialize
                        (itdOdvAssignedStopsElement, new TypeToken<List<ItdOdvAssignedStop>>(){}.getType());
            } else {
                throw new JsonParseException("Unexpected json element of type " + itdOdvAssignedStopsElement.getClass().getName());
            }
        } else {
            itdOdvAssignedStops = Collections.emptyList();
        }

        StopFinder out = new StopFinder();
        for (Field field : StopFinder.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals("points")) {
                field.set(out, points);
                continue;
            } else if (field.getName().equals("itdOdvAssignedStops")) {
                field.set(out, itdOdvAssignedStops);
                continue;
            }
            field.set(out, jsonDeserializationContext.deserialize(object.get(field.getName()), field.getType()));
        }
        return out;
    }

}

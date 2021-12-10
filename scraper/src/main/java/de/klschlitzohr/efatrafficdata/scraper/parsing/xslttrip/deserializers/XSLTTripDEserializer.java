package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.deserializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.XSLTTRIP;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.Trip;
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
public class XSLTTripDEserializer implements JsonDeserializer<XSLTTRIP> {

    @SneakyThrows
    @Override
    public XSLTTRIP deserialize(JsonElement jsonElement, Type type,
                                  JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!(jsonElement instanceof JsonObject)) {
            throw new JsonParseException("Unexpected json element of type " + jsonElement.getClass().getName());
        }
        JsonObject object = jsonElement.getAsJsonObject();

        JsonElement pointsElement = object.get("trips");
        List<Trip> trips;

        if (pointsElement.isJsonArray()) {
            trips = jsonDeserializationContext.deserialize(pointsElement, new TypeToken<List<Trip>>() {
            }.getType());
        } else if (pointsElement.isJsonObject()) {
            JsonObject pointsObj = pointsElement.getAsJsonObject();
            trips = Collections.singletonList(jsonDeserializationContext.deserialize(pointsObj.get("trip"), Trip.class));
        } else {
            //throw new JsonParseException("Unexpected json element of type " + pointsElement.getClass().getName());
            trips = Collections.emptyList();
        }


        XSLTTRIP out = new XSLTTRIP();
        for (Field field : XSLTTRIP.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals("trips")) {
                field.set(out, trips);
                continue;
            }
            field.set(out, jsonDeserializationContext.deserialize(object.get(field.getName()), field.getType()));
        }
        return out;
    }

}

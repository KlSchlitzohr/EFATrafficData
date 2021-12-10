package de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.deserializers;

import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.Departure;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.XSLTDM;
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
public class XSLTDMDeserializer implements JsonDeserializer<XSLTDM> {

    @SneakyThrows
    @Override
    public XSLTDM deserialize(JsonElement jsonElement, Type type,
                                  JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!(jsonElement instanceof JsonObject)) {
            throw new JsonParseException("Unexpected json element of type " + jsonElement.getClass().getName());
        }
        JsonObject object = jsonElement.getAsJsonObject();

        JsonElement departureListElement = object.get("departureList");
        List<Departure> departureList;
            if (departureListElement.isJsonArray()) {
                departureList = jsonDeserializationContext.deserialize(departureListElement, new TypeToken<List<Departure>>() {
                }.getType());
            } else if (departureListElement.isJsonObject()) {
                JsonObject pointsObj = departureListElement.getAsJsonObject();
                departureList = Collections.singletonList(
                        jsonDeserializationContext.deserialize(pointsObj.get("departure"), Departure.class));
            } else {
                departureList = Collections.emptyList();
                //throw new JsonParseException("Unexpected json element of type " + departureListElement.getClass().getName());
            }


        XSLTDM out = new XSLTDM();
        for (Field field : XSLTDM.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals("departureList")) {
                field.set(out, departureList);
                continue;
            }
            field.set(out, jsonDeserializationContext.deserialize(object.get(field.getName()), field.getType()));
        }
        return out;
    }

}

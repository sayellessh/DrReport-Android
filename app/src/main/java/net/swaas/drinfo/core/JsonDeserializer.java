package net.swaas.drinfo.core;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.swaas.drinfo.logger.LogTracer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SwaaS on 6/9/2015.
 */
public class JsonDeserializer implements com.google.gson.JsonDeserializer<Date> {
    private static final LogTracer LOG_TRACER = LogTracer.instance(JsonDeserializer.class);
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String s = json.getAsJsonPrimitive().getAsString();
        try {
            Date d = parseDate(s, "yyyy-MM-dd'T'HH:mm:ss");
            return d;
        } catch (ParseException e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e.toString(), e);
        }
        return null;
    }

    public static Date parseDate(String sDate, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(sDate);
        return date;
    }
}

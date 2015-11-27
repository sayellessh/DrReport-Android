package net.swaas.drinfo.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SwaaS on 6/4/2015.
 */
public class ReflectionHelper {

    public static <T> T parseJson(String jsonObject, Class<T> tClass) {
        GsonSuperClassExclusionStrategy gsonSuperClassExclusionStrategy = new GsonSuperClassExclusionStrategy();
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(gsonSuperClassExclusionStrategy)
                .addSerializationExclusionStrategy(gsonSuperClassExclusionStrategy)
                .registerTypeAdapter(Date.class, new JsonDeserializer())
                .create();
        T out = gson.fromJson(jsonObject, tClass);
        return out;
    }

    public static <T> List<T> parseJsonArray(String sJsonArray, Class<T> tClass) throws JSONException {
        List<T> out = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(sJsonArray);
        if(jsonArray != null && jsonArray.length() > 0) {
            for(int i=0;i<=jsonArray.length()-1;i++) {
                String sJsonObject = jsonArray.getString(i);
                out.add(parseJson(sJsonObject, tClass));
            }
        }
        return out;
    }
}

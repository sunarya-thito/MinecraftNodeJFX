package thito.nodeflow.minecraft.chat;

import com.google.common.reflect.*;
import com.google.gson.*;
import thito.nodeflow.minecraft.*;

import java.io.*;
import java.util.*;

public class MinecraftLanguage {
    private static Gson gson = new Gson();

    private static Map<String, String> map;

    static {
        try (InputStreamReader reader = new InputStreamReader(FXUtil.class.getResourceAsStream("en_us.json"))) {
            map = gson.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static Map<String, String> getMap() {
        return map;
    }
}

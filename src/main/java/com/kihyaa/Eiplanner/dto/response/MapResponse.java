package com.kihyaa.Eiplanner.dto.response;

import java.util.HashMap;
import java.util.Map;

public class MapResponse {
    private final Map<String, String> data;

    public MapResponse(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }

    public static MapResponse of(String key, String value) {
        Map<String, String> data = new HashMap<>();
        data.put(key, value);
        return new MapResponse(data);
    }
}


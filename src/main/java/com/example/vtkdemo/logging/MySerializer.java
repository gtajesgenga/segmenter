package com.example.vtkdemo.logging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.splunk.logging.EventBodySerializer;
import com.splunk.logging.HttpEventCollectorEventInfo;
import com.splunk.logging.serialization.EventInfoTypeAdapter;

import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySerializer implements EventBodySerializer {
    private final EventInfoTypeAdapter typeAdapter = new EventInfoTypeAdapter();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(HttpEventCollectorEventInfo.class, typeAdapter)
            .disableHtmlEscaping()
            .create();

    @Override public String serializeEventBody(HttpEventCollectorEventInfo eventInfo, Object formattedMessage) {
        Pattern pattern = Pattern.compile("(?<K>(?i)[a-z]+)=(?<V>[\\w\\-]+|'[\\w \\-]+')");
        Matcher matcher = pattern.matcher((String) formattedMessage);
        HttpEventCollectorEventInfo newEvent = new HttpEventCollectorEventInfo(eventInfo.getSeverity(), eventInfo.getMessage(), eventInfo.getLoggerName(), eventInfo.getThreadName(), Optional
                .ofNullable(eventInfo.getProperties()).orElse(new HashMap<>()), eventInfo.getExceptionMessage(), eventInfo.getMarker());
        while (matcher.find()) {
            newEvent.getProperties().put(matcher.group("K"), matcher.group("V"));
        }

        return gson.toJson(newEvent);
    }
}

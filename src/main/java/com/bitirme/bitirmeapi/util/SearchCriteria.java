package com.bitirme.bitirmeapi.util;

import lombok.Value;

import java.util.regex.Pattern;

@Value
public class SearchCriteria {
    String key;
    String operation;
    Object value;

    public boolean isDate() {
        Pattern pattern = Pattern
                .compile("\\d{2}-\\d{2}-\\d{4}");
        return this.key.endsWith("Date") && pattern.matcher(value.toString()).matches();
    }

    public boolean isWaypoint() {
        Pattern pattern = Pattern
                .compile("[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)");
        return key.endsWith("Waypoint") && pattern.matcher(value.toString()).matches();
    }

    public boolean isNumeric() {
        try{
            double driverId = Double.parseDouble(value.toString());
        }catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
}

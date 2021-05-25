package com.bitirme.bitirmeapi.util.converter;

import com.bitirme.bitirmeapi.trip.request.Status;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, Status> {
    @Override
    public Status convert(String source) {
        try {
            return Status.valueOf(source.toUpperCase());
        } catch(IllegalArgumentException e) {
            return null;
        }

    }
}

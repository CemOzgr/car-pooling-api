package com.bitirme.bitirmeapi.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleGeocodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleGeocodeService.class);

    public String getAddress(double lat, double lng) {
        GoogleGeocode geocode = getGeocode(lat, lng);
        if(geocode.getResults().isEmpty()) {
            throw new IllegalStateException("No address returned");
        }
        return geocode.getResults().get(0).getFormattedAddress();
    }

    public GoogleGeocode getGeocode(double lat, double lng) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(getUrl(lat, lng), GoogleGeocode.class);
    }

    public String getUrl(double lat, double lng) {
        return String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                "%f,%f&key=AIzaSyDWr21z6L50L_meZliKW1FpkrG7qPaJwoU",lat,lng);
    }

}

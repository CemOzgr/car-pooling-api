package com.bitirme.bitirmeapi.trip.waypoint;

import com.bitirme.bitirmeapi.google.GoogleGeocodeService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WaypointService {
    private final WaypointRepository waypointRepository;
    private final GoogleGeocodeService geocodeService;
    private final GeometryFactory geometryFactory;

    @Autowired
    public WaypointService(WaypointRepository waypointRepository,
                           GoogleGeocodeService geocodeService, GeometryFactory geometryFactory) {
        this.waypointRepository = waypointRepository;
        this.geocodeService = geocodeService;
        this.geometryFactory = geometryFactory;
    }

    @Transactional
    public void saveWaypoint(Waypoint waypoint) {
        waypointRepository.save(waypoint);
    }

    public Waypoint getWaypointFromCoordinates(double lat, double lng) {
        String address = geocodeService.getAddress(lat, lng);
        String[] addressSections = address.split("/");
        String city = addressSections[addressSections.length-1].split(",")[0];

        Coordinate coordinate = new Coordinate(lng, lat);

        Point point = geometryFactory.createPoint(coordinate);

        return new Waypoint(point, city, address);
    }

    public Waypoint loadByAddress(String address) {
        return waypointRepository.findByAddress(address)
                .orElse(null);
    }

    public boolean doesAddressExists(String address) {

        return waypointRepository.doesAddressExists(address);
    }

}

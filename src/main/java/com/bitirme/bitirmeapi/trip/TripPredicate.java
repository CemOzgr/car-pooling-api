package com.bitirme.bitirmeapi.trip;

import com.bitirme.bitirmeapi.trip.waypoint.Waypoint;
import com.bitirme.bitirmeapi.util.SearchCriteria;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.spatial.jts.JTSGeometryPath;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TripPredicate {
    private final SearchCriteria searchCriteria;

    public TripPredicate(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public BooleanExpression getPredicate() {
        PathBuilder<Trip> entityPath = new PathBuilder<>(Trip.class, "trip");
        if(searchCriteria.isDate()) {
            DateTimePath<LocalDateTime> path =
                    entityPath.getDateTime(searchCriteria.getKey(), LocalDateTime.class);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(searchCriteria.getValue().toString(), formatter);
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.parse("00:00:00"));

            switch (searchCriteria.getOperation()) {
                case ":":
                    return path.between(dateTime, dateTime.plusHours(23).plusMinutes(59));
                case ">":
                    return path.after(dateTime);
                case "<":
                    return path.before(dateTime);
            }
        }else if(searchCriteria.isNumeric()) {
            NumberPath<Integer> numberPath = entityPath.getNumber(searchCriteria.getKey(), Integer.class);

            int numberValue = Integer.parseInt(searchCriteria.getValue().toString());
            return numberPath.eq(numberValue);

        }else if(searchCriteria.isWaypoint()) {
            if(!searchCriteria.getOperation().equals(":")) {
                throw new IllegalArgumentException("invalid operation");
            }

            String[] coordinateString = searchCriteria.getValue().toString().split(",");
            double latitude = Double.parseDouble(coordinateString[0]);
            double longitude = Double.parseDouble(coordinateString[1]);

            Coordinate coordinate = new Coordinate(longitude,latitude);

            GeometryFactory factory = new GeometryFactory();
            Geometry point = factory.createPoint(coordinate);

            EntityPath<Waypoint> wayPointPath = entityPath.get(searchCriteria.getKey(), Waypoint.class);
            JTSGeometryPath<Geometry> pointPath = new JTSGeometryPath<>(wayPointPath, "coordinates");

            return pointPath.distance(point).loe(0.8);

        }
        return null;
    }

}

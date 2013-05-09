/*
 * Copyright (c) 2012 Romain Gallet
 *
 * This file is part of Geocalc.
 *
 * Geocalc is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Geocalc is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Geocalc. If not, see http://www.gnu.org/licenses/.
 */

package com.grum.geocalc;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Earth related calculations.
 * 
 * @author rgallet
 */
public class EarthCalc
{
    public static final double EARTH_DIAMETER = 6371.01 * 1000; // meters

    /**
     * Returns the distance between two points
     * 
     * @param standPoint The stand point
     * @param forePoint The fore point
     * @return The distance, in meters
     */
    public static double getDistance(final Point standPoint, final Point forePoint)
    {

        final double diffLongitudes = toRadians(forePoint.getLongitude() - standPoint.getLongitude());

        final double slat = toRadians(standPoint.getLatitude());
        final double flat = toRadians(forePoint.getLatitude());

        // Vincenty formula
        double c = sqrt(pow(cos(flat) * sin(diffLongitudes), 2d)
                        + pow(cos(slat) * sin(flat) - sin(slat) * cos(flat) * cos(diffLongitudes), 2d));
        c = c / (sin(slat) * sin(flat) + cos(slat) * cos(flat) * cos(diffLongitudes));
        c = atan(c);

        return EARTH_DIAMETER * c;
    }

    /**
     * Returns the coordinates of a point which is "distance" away from standPoint in the direction
     * of "bearing"
     * <p/>
     * Note: North is equal to 0 for bearing value
     * 
     * @param standPoint Origin
     * @param bearing Direction in degrees
     * @param distance distance in meters
     * @return forepoint coordinates
     * @see http
     *      ://stackoverflow.com/questions/877524/calculating-coordinates-given-a-bearing-and-a-
     *      distance
     */
    public static Point pointRadialDistance(final Point standPoint,
                                            final double bearing,
                                            final double distance)
    {

        final double rlat1 = toRadians(standPoint.getLatitude());
        final double rlon1 = toRadians(standPoint.getLongitude());
        final double rbearing = toRadians(bearing);
        final double rdistance = distance / EARTH_DIAMETER; // normalize linear distance to radian
                                                            // angle

        final double rlat = asin(sin(rlat1) * cos(rdistance) + cos(rlat1) * sin(rdistance) * cos(rbearing));

        final double epsilon = 0.000001;

        double rlon;

        if (cos(rlat) == 0.0 || abs(cos(rlat)) < epsilon)
        { // Endpoint a pole
            rlon = rlon1;

        }
        else
        {
            rlon = ((rlon1 - asin(sin(rbearing) * sin(rdistance) / cos(rlat)) + PI) % (2 * PI)) - PI;
        }

        return new Point(new RadianCoordinate(rlat), new RadianCoordinate(rlon));
    }

    /**
     * Returns the bearing, in decimal degrees, from standPoint to forePoint
     * 
     * @param standPoint
     * @param forePoint
     * @return bearing, in decimal degrees
     */
    public static double getBearing(final Point standPoint, final Point forePoint)
    {
        final double latitude1 = toRadians(standPoint.getLatitude());
        final double longitude1 = standPoint.getLongitude();

        final double latitude2 = toRadians(forePoint.getLatitude());
        final double longitude2 = forePoint.getLongitude();

        final double longDiff = toRadians(longitude2 - longitude1);

        // invertedBearing because it represents the angle, in radians, of standPoint from
        // forePoint's point of view
        // we want the opposite
        final double invertedBearing = ((atan2(sin(longDiff) * cos(latitude2), cos(latitude1)
                                                                               * sin(latitude2)
                                                                               - sin(latitude1)
                                                                               * cos(latitude2)
                                                                               * cos(longDiff))));

        final double rbearing = (-invertedBearing + 2 * PI) % (2 * PI);

        return toDegrees(rbearing);
    }

    /**
     * Returns an area around standPoint
     * 
     * @param standPoint The centre of the area
     * @param distance Distance around standPoint, im meters
     * @return The area
     * @see http://www.movable-type.co.uk/scripts/latlong.html
     */
    public static BoundingArea getBoundingArea(final Point standPoint, final double distance)
    {

        // 45 degrees is going north-west
        final Point northWest = pointRadialDistance(standPoint, 45, distance);

        // 225 degrees is going south-east
        final Point southEast = pointRadialDistance(standPoint, 225, distance);

        return new BoundingArea(northWest, southEast);
    }
}

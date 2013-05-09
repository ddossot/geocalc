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

import static org.junit.Assert.assertEquals;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * @author rgallet
 */
public class DistanceTest
{
    private static final Log LOGGER = LogFactory.getLog(DistanceTest.class);

    @Test
    public void testDistance()
    {
        // Kew
        Coordinate lat = new DegreeCoordinate(51.4843774);
        Coordinate lng = new DegreeCoordinate(-0.2912044);
        final Point kew = new Point(lat, lng);

        // Richmond
        lat = new DegreeCoordinate(51.4613418);
        lng = new DegreeCoordinate(-0.3035466);
        final Point richmond = new Point(lat, lng);

        assertEquals(2700.3261395525724, EarthCalc.getDistance(richmond, kew), 10E-3);
    }

    @Test
    public void testSymetricDistance()
    {
        // Kew
        Coordinate lat = new DegreeCoordinate(51.4843774);
        Coordinate lng = new DegreeCoordinate(-0.2912044);
        final Point kew = new Point(lat, lng);

        // Richmond
        lat = new DegreeCoordinate(51.4613418);
        lng = new DegreeCoordinate(-0.3035466);
        final Point richmond = new Point(lat, lng);

        assertEquals(EarthCalc.getDistance(richmond, kew), EarthCalc.getDistance(kew, richmond), 10E-10);
    }

    @Test
    public void testZeroDistance()
    {
        // Kew
        final Coordinate lat = new DegreeCoordinate(51.4843774);
        final Coordinate lng = new DegreeCoordinate(-0.2912044);
        final Point kew = new Point(lat, lng);

        assertEquals(EarthCalc.getDistance(kew, kew), 0, 0);
    }

    @Test
    public void testBoundingAreaDistance()
    {
        // Kew
        final Coordinate lat = new DegreeCoordinate(51.4843774);
        final Coordinate lng = new DegreeCoordinate(-0.2912044);
        final Point kew = new Point(lat, lng);

        final BoundingArea area = EarthCalc.getBoundingArea(kew, 3000);

        final double northEastDistance = EarthCalc.getDistance(kew, area.getNorthEast());
        LOGGER.info("North East => " + northEastDistance);
        assertEquals(3000d, northEastDistance, 1E-3);

        final double southWestDistance = EarthCalc.getDistance(kew, area.getSouthWest());
        LOGGER.info("South West => " + southWestDistance);
        assertEquals(3000d, southWestDistance, 1E-3);

        final Point northWest = area.getNorthWest();
        final double northWestDistance = EarthCalc.getDistance(kew, northWest);
        LOGGER.info("North West => " + northWestDistance);
        assertEquals(3000d, northWestDistance, 2);

        final Point southEast = area.getSouthEast();
        final double southEastDistance = EarthCalc.getDistance(kew, southEast);
        LOGGER.info("South East => " + southEastDistance);
        assertEquals(3000d, southEastDistance, 2);

        final Point middleNorth = new Point(new DegreeCoordinate(area.getNorthEast().getLatitude()),
            new DegreeCoordinate(
                (area.getSouthWest().getLongitude() + area.getNorthEast().getLongitude()) / 2));
        final double middleNorthDistance = EarthCalc.getDistance(kew, middleNorth);
        LOGGER.info("Middle North => " + middleNorthDistance);
        assertEquals(2120d, middleNorthDistance, 1);

        final Point middleSouth = new Point(new DegreeCoordinate(area.getSouthWest().getLatitude()),
            new DegreeCoordinate(
                (area.getSouthWest().getLongitude() + area.getNorthEast().getLongitude()) / 2));
        final double middleSouthDistance = EarthCalc.getDistance(kew, middleSouth);
        LOGGER.info("Middle South => " + middleSouthDistance);
        assertEquals(2120d, middleSouthDistance, 2);

        final Point middleWest = new Point(new DegreeCoordinate(
            (area.getNorthEast().getLatitude() + area.getSouthWest().getLatitude()) / 2),
            new DegreeCoordinate(area.getNorthEast().getLongitude()));
        final double middleWestDistance = EarthCalc.getDistance(kew, middleWest);
        LOGGER.info("Middle West => " + middleWestDistance);
        assertEquals(2120d, middleWestDistance, 3);

        final Point middleEast = new Point(new DegreeCoordinate(
            (area.getNorthEast().getLatitude() + area.getSouthWest().getLatitude()) / 2),
            new DegreeCoordinate(area.getSouthWest().getLongitude()));
        final double middleEastDistance = EarthCalc.getDistance(kew, middleEast);
        LOGGER.info("Middle East => " + middleEastDistance);
        assertEquals(2120d, middleEastDistance, 1);
    }

    @Test
    public void testBoundingAreaNorthPole()
    {
        // North Pole
        final Coordinate lat = new DegreeCoordinate(90d);
        final Coordinate lng = new DegreeCoordinate(0);
        final Point northPole = new Point(lat, lng);

        final BoundingArea area = EarthCalc.getBoundingArea(northPole, 10000);
        LOGGER.info("North East => " + area.getNorthEast());
        LOGGER.info("South West => " + area.getSouthWest());
    }

    @Test
    public void testPointRadialDistanceZero()
    {
        // Kew
        final Coordinate lat = new DegreeCoordinate(51.4843774);
        final Coordinate lng = new DegreeCoordinate(-0.2912044);
        final Point kew = new Point(lat, lng);

        Point sameKew = EarthCalc.pointRadialDistance(kew, 45, 0);
        assertEquals(lat.getValue(), sameKew.getLatitude(), 1E-10);
        assertEquals(lng.getValue(), sameKew.getLongitude(), 1E-10);

        sameKew = EarthCalc.pointRadialDistance(kew, 90, 0);
        assertEquals(lat.getValue(), sameKew.getLatitude(), 1E-10);
        assertEquals(lng.getValue(), sameKew.getLongitude(), 1E-10);

        sameKew = EarthCalc.pointRadialDistance(kew, 180, 0);
        assertEquals(lat.getValue(), sameKew.getLatitude(), 1E-10);
        assertEquals(lng.getValue(), sameKew.getLongitude(), 1E-10);
    }

    @Test
    public void testBearing()
    {
        // Kew
        final Coordinate lat = new DegreeCoordinate(51.4843774);
        final Coordinate lng = new DegreeCoordinate(-0.2912044);
        final Point kew = new Point(lat, lng);

        for (int i = 0; i <= 360; i = i + 1)
        {
            final Point test = EarthCalc.pointRadialDistance(kew, i, 10000);
            final double bearing = EarthCalc.getBearing(kew, test);
            // modulo 360 to care for when i == 360, which rightly returns a bearing of 0
            assertEquals((i % 360), bearing, 1E-10);
        }
    }

    @Test
    public void testPointRadialDistance()
    {
        // Kew
        Coordinate lat = new DegreeCoordinate(51.4843774);
        Coordinate lng = new DegreeCoordinate(-0.2912044);
        final Point kew = new Point(lat, lng);

        // Richmond
        lat = new DegreeCoordinate(51.4613418);
        lng = new DegreeCoordinate(-0.3035466);
        final Point richmond = new Point(lat, lng);

        final double distance = EarthCalc.getDistance(kew, richmond);
        final double bearing = EarthCalc.getBearing(kew, richmond);

        final Point allegedRichmond = EarthCalc.pointRadialDistance(kew, bearing, distance);

        assertEquals(richmond.getLatitude(), allegedRichmond.getLatitude(), 10E-5);
        assertEquals(richmond.getLongitude(), allegedRichmond.getLongitude(), 10E-5);

    }
}

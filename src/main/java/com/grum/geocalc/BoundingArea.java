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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents an area, defined by its top left and bottom right coordinates
 * 
 * @author rgallet
 */
public class BoundingArea
{
    private static final Log LOGGER = LogFactory.getLog(BoundingArea.class);

    private final Point northEast, southWest;
    private final Point southEast, northWest;

    public BoundingArea(final Point northEast, final Point southWest)
    {
        this.northEast = northEast;
        this.southWest = southWest;

        southEast = new Point(new DegreeCoordinate(southWest.getLatitude()), new DegreeCoordinate(
            northEast.getLongitude()));
        northWest = new Point(new DegreeCoordinate(northEast.getLatitude()), new DegreeCoordinate(
            southWest.getLongitude()));
    }

    @Deprecated()
    public Point getBottomRight()
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("getBottomRight() is deprecated. Use getSouthWest() instead.");
        }

        return southWest;
    }

    @Deprecated
    public Point getTopLeft()
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("getTopLeft() is deprecated. Use getNorthEast() instead.");
        }

        return northEast;
    }

    public Point getNorthEast()
    {
        return northEast;
    }

    public Point getSouthWest()
    {
        return southWest;
    }

    public Point getSouthEast()
    {
        return southEast;
    }

    public Point getNorthWest()
    {
        return northWest;
    }

    @Override
    public String toString()
    {
        return "BoundingArea{" + "northEast=" + northEast + ", southWest=" + southWest + '}';
    }

    public boolean isContainedWithin(final Point point)
    {
        final boolean predicate1 = point.getLatitude() >= this.southWest.getLatitude()
                                   && point.getLatitude() <= this.northEast.getLatitude();

        if (!predicate1)
        {
            return false;
        }

        boolean predicate2;

        if (southWest.getLongitude() > northEast.getLongitude())
        { // area is going across the max/min longitude boundaries (ie. sort of back of the Earth)
          // we "split" the area in 2, longitude-wise, point only needs to be in one or the other.
            final boolean predicate3 = point.getLongitude() <= northEast.getLongitude()
                                       && point.getLongitude() >= -180;
            final boolean predicate4 = point.getLongitude() >= southWest.getLongitude()
                                       && point.getLongitude() <= 180;

            predicate2 = predicate3 || predicate4;
        }
        else
        {
            predicate2 = point.getLongitude() >= southWest.getLongitude()
                         && point.getLongitude() <= northEast.getLongitude();
        }

        return predicate1 && predicate2;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final BoundingArea other = (BoundingArea) obj;
        if (this.northEast != other.northEast
            && (this.northEast == null || !this.northEast.equals(other.northEast)))
        {
            return false;
        }
        if (this.southWest != other.southWest
            && (this.southWest == null || !this.southWest.equals(other.southWest)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 13 * hash + (this.northEast != null ? this.northEast.hashCode() : 0);
        hash = 13 * hash + (this.southWest != null ? this.southWest.hashCode() : 0);
        return hash;
    }
}

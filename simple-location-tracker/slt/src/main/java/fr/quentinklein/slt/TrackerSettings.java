package fr.quentinklein.slt;

/*
 * Copyright (C) 2013 Yasir Ali, Quentin Klein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Quentin Klein <klein.quentin@gmail.com>, Yasir.Ali <ali.yasir0@gmail.com>
 * <p>
 * Helps the LocationTracker to set the attributes:
 * <ul>
 *  <li>useGPS <ul><li>true if GPS usage is wanted</li><li>false otherwise</li></ul></li>
 *  <li>useNetwork <ul><li>true if Network usage is wanted</li><li>false otherwise</li></ul></li>
 *  <li>usePassive <ul><li>true if Passive usage is wanted</li><li>false otherwise</li></ul></li>
 *  <li>minTimeBetweenUpdates the minimum time interval between location updates in milliseconds</li>
 *  <li>minMetersBetweenUpdates the minimum distance between location updates, in meters</li>
 *  <li>timeout the minimum time delay before the tracker stops scanning for location in milliseconds</li>
 * </ul>
 * </p>
 */
public class TrackerSettings {
    /**
     * Basic tracker settings, with all the default parameters
     * <ul>
     *     <li>5min between updates</li>
     *     <li>100m between updates</li>
     *     <li>100m between updates</li>
     *     <li>1m timeout</li>
     *     <li>Uses Network</li>
     *     <li>Uses Passive</li>
     * </ul>
     */
    public static final TrackerSettings DEFAULT = new TrackerSettings();
    /**
     * The default time interval between location updates
     * Its value is 5 minutes
     */
    public static final long DEFAULT_MIN_TIME_BETWEEN_UPDATES = 5 * 60 * 1000;
    /**
     * The default distance between location updates
     * Its value is 100m
     */
    public static final float DEFAULT_MIN_METERS_BETWEEN_UPDATES = 100;
    /**
     * The default value of timeout that helps to stop the listener if the listener is taking too much time
     * Its value is 1 minutes
     */
    public static final int DEFAULT_TIMEOUT = 1 * 60 * 1000;

    /**
     * The minimum time interval between location updates, in milliseconds by default its value is {@link #DEFAULT_MIN_TIME_BETWEEN_UPDATES}
     */
    private long timeBetweenUpdates = -1;
    /**
     * The minimum distance between location updates in meters, by default its value is {@link #DEFAULT_MIN_METERS_BETWEEN_UPDATES}
     */
    private float metersBetweenUpdates = -1;
    /**
     * The value of timeout to stop the listener after a specified time in case the listener is unable to get the location for a specified time
     */
    private int timeout = -1;

    /**
     * Specifies if tracker should use the GPS (default is true)
     */
    private boolean useGPS = true;
    /**
     * Specifies if tracker should use the Network (default is true)
     */
    private boolean useNetwork = true;
    /**
     * Specifies if tracker should use the Passive provider (default is true)
     */
    private boolean usePassive = true;

    public void setTimeBetweenUpdates(long timeBetweenUpdates) {
        this.timeBetweenUpdates = timeBetweenUpdates;
    }

    public long getTimeBetweenUpdates() {
        return this.timeBetweenUpdates <= 0 ? DEFAULT_MIN_TIME_BETWEEN_UPDATES : this.timeBetweenUpdates;
    }

    public void setMetersBetweenUpdates(float metersBetweenUpdates) {
        this.metersBetweenUpdates = metersBetweenUpdates;
    }

    public float getMetersBetweenUpdates() {
        return this.metersBetweenUpdates <= 0 ? DEFAULT_MIN_METERS_BETWEEN_UPDATES : this.metersBetweenUpdates;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return this.timeout <= -1 ? DEFAULT_TIMEOUT : this.timeout;
    }

    public void setUseGPS(boolean useGPS) {
        this.useGPS = useGPS;
    }

    public boolean shouldUseGPS() {
        return this.useGPS;
    }

    public void setUseNetwork(boolean useNetwork) {
        this.useNetwork = useNetwork;
    }

    public boolean shouldUseNetwork() {
        return this.useNetwork;
    }

    public void setUsePassive(boolean usePassive) {
        this.usePassive = usePassive;
    }

    public boolean shouldUsePassive() {
        return this.usePassive;
    }
}

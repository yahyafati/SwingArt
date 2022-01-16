package com.yahya.stupid.things.utils;

import com.yahya.stupid.things.model.Planet;

public class PlanetInfo {


    public final static Planet[] PLANETS = {
            new Planet("Mercury", 10, 80, 1),
            new Planet("Venus", 12, 120, .75),
            new Planet("Earth", 16, 150, .6),
            new Planet("Mars", 14, 180, .5),
            new Planet("Jupiter", 25, 230, .4),
            new Planet("Saturn", 22, 270, .2),
            new Planet("Uranus", 18, 330, .15),
            new Planet("Neptune", 20, 370, .1),
    };

    public final static Planet[] REAL_PLANETS = {
            new Planet("Mercury", 10, 46e6, 1),
            new Planet("Venus", 12, 107.5e6, .75),
            new Planet("Earth", 16, 147e6, .6),
            new Planet("Mars", 14, 228e6, .5),
            new Planet("Jupiter", 25, 746.74e6, .6),
            new Planet("Saturn", 22, 1.5e9, .6),
            new Planet("Uranus", 18, 2.95e9, .6),
            new Planet("Neptune", 20, 5e9, .6),
    };
}

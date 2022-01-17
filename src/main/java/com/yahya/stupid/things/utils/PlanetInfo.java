package com.yahya.stupid.things.utils;

import com.yahya.stupid.things.model.Planet;

public class PlanetInfo {


    public final static Planet[] PLANETS = {
            new Planet("Mercury", 5, 80, 1), // 1),
            new Planet("Venus", 9, 120, 0.39), // .75),
            new Planet("Earth", 12, 150, 0.24), // .6),
            new Planet("Mars", 14, 180, 0.12), // .5),
            new Planet("Jupiter", 20, 230, 0.02), // .4),
            new Planet("Saturn", 18, 270, 8.3e-3), // .2),
            new Planet("Uranus", 16, 330, 2.87e-3), // .15),
            new Planet("Neptune", 18, 370, 1.46e-3), // .1),
    };

    public final static Planet[] REAL_PLANETS = {
            new Planet("Mercury", 10, 46e6, 1),
            new Planet("Venus", 12, 107.5e6, 0.39),
            new Planet("Earth", 16, 147e6, 0.24),
            new Planet("Mars", 14, 228e6, 0.12),
            new Planet("Jupiter", 25, 746.74e6, 0.02),
            new Planet("Saturn", 22, 1.5e9, 8.3e-3),
            new Planet("Uranus", 18, 2.95e9, 2.87e-3),
            new Planet("Neptune", 20, 5e9, 1.46e-3),
    };
}

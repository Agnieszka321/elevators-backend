package com.fortum.codechallenge.elevators.backend.api;

public enum Direction {
    UP,
    DOWN,
    NONE,
    BOTH;

    public static Direction opposite(Direction direction){
        return  (direction == UP) ? DOWN : UP;
    }

    public static Direction getDirection(CallingDirection callingDirection){
        return  (callingDirection == CallingDirection.UP) ? UP : DOWN;
    }
}

package edu.sustech.xiangqi;

import java.awt.*;

public class Move {
    Point start;
    Point end;
    int priorityScore;

    public Move(Point start, Point end, int priorityScore) {
        this.start = start;
        this.end = end;
        this.priorityScore = priorityScore;
    }
}
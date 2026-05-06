package com.mazegame.model;

import java.util.ArrayList;
import java.util.List;

public class Maze {
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int START = 2;
    public static final int EXIT = 3;
    public static final int COLLECTIBLE = 4;

    private int[][] grid;
    private final int width;
    private final int height;

    public Maze(int width, int height) {
        this.width = width % 2 == 0 ? width + 1 : width;
        this.height = height % 2 == 0 ? height + 1 : height;
        this.grid = new int[this.height][this.width];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCell(int x, int y) { return grid[y][x]; }
    public void setCell(int x, int y, int value) { grid[y][x] = value; }
    public int[][] getGrid() { return grid; }

    public static class Point {
        public int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Point) {
                Point p = (Point) obj;
                return p.x == x && p.y == y;
            }
            return false;
        }
        @Override
        public int hashCode() { return x * 31 + y; }
    }
}

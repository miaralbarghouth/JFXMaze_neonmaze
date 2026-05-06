package com.mazegame.logic;

import com.mazegame.model.Maze;
import com.mazegame.model.Maze.Point;
import java.util.*;

public class MazeManager {

    // Create new maze
    public static void generateMaze(Maze maze) {
        int width = maze.getWidth();
        int height = maze.getHeight();
        int[][] grid = maze.getGrid();
        Random rand = new Random();

        boolean solvable = false;
        while (!solvable) {
            // Initialize with walls
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    grid[y][x] = Maze.WALL;
                }
            }

            // Start DFS from (1, 1)
            dfs(1, 1, maze, rand);

            // Add more paths to create branches and loops
            addExtraPaths(maze, rand);

            // Set Start
            grid[1][1] = Maze.START;

            // Force the Exit at (height-2, width-2) to have ONLY ONE entrance
            int ex = width - 2;
            int ey = height - 2;
            grid[ey][ex] = Maze.EXIT;

            // Close 3 sides of the exit, leave one open (LEFT)
            grid[ey-1][ex] = Maze.WALL; // Top
            grid[ey+1][ex] = Maze.WALL; // Bottom
            grid[ey][ex+1] = Maze.WALL; // Right
            
            // Critical: Ensure the entrance cell is clear
            grid[ey][ex-1] = Maze.PATH;

            // Check solvability before finishing
            var path = solveBFS(maze, new Point(1, 1), new Point(ex, ey));
            if (!path.isEmpty()) {
                solvable = true;
            }
        }

        // Add collectibles only after we know the maze is solvable
        addCollectibles(maze, rand);
    }

    // Add extra branches
    private static void addExtraPaths(Maze maze, Random rand) {
        int width = maze.getWidth();
        int height = maze.getHeight();
        // Open up about 10% of remaining walls to create branches/loops
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (maze.getCell(x, y) == Maze.WALL && rand.nextDouble() < 0.1) {
                    // Only open if it connects two path areas horizontally or vertically
                    if (maze.getCell(x-1, y) == Maze.PATH && maze.getCell(x+1, y) == Maze.PATH) {
                        maze.setCell(x, y, Maze.PATH);
                    } else if (maze.getCell(x, y-1) == Maze.PATH && maze.getCell(x, y+1) == Maze.PATH) {
                        maze.setCell(x, y, Maze.PATH);
                    }
                }
            }
        }
    }

    // DFS generation algorithm
    private static void dfs(int x, int y, Maze maze, Random rand) {
        maze.setCell(x, y, Maze.PATH);

        int[][] dirs = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};
        List<int[]> dirList = Arrays.asList(dirs);
        Collections.shuffle(dirList, rand);

        for (int[] dir : dirList) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx > 0 && nx < maze.getWidth() - 1 && ny > 0 && ny < maze.getHeight() - 1 
                && maze.getCell(nx, ny) == Maze.WALL) {
                maze.setCell(x + dir[0]/2, y + dir[1]/2, Maze.PATH);
                dfs(nx, ny, maze, rand);
            }
        }
    }

    // Place random collectibles
    private static void addCollectibles(Maze maze, Random rand) {
        int count = (maze.getWidth() * maze.getHeight()) / 40;
        int exitX = maze.getWidth() - 2;
        int exitY = maze.getHeight() - 2;

        for (int i = 0; i < count; i++) {
            int rx, ry;
            int attempts = 0;
            boolean valid;
            do {
                rx = rand.nextInt(maze.getWidth());
                ry = rand.nextInt(maze.getHeight());
                attempts++;
                
                // Strict validation: Must be PATH, not START, not EXIT
                // AND must be at least 3 cells away from exit and start
                double distToExit = Math.sqrt(Math.pow(rx - exitX, 2) + Math.pow(ry - exitY, 2));
                double distToStart = Math.sqrt(Math.pow(rx - 1, 2) + Math.pow(ry - 1, 2));
                
                valid = maze.getCell(rx, ry) == Maze.PATH && 
                        distToExit > 3.0 && 
                        distToStart > 2.0;

                if (attempts > 200) break; 
            } while (!valid);
            
            if (valid) {
                maze.setCell(rx, ry, Maze.COLLECTIBLE);
            }
        }
    }

    // BFS pathfinding solver
    public static List<Point> solveBFS(Maze maze, Point start, Point end) {
        int width = maze.getWidth();
        int height = maze.getHeight();
        
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parentMap = new HashMap<>();
        
        queue.add(start);
        parentMap.put(start, null);
        
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        
        while (!queue.isEmpty()) {
            Point curr = queue.poll();
            if (curr.equals(end)) {
                return reconstructPath(parentMap, end);
            }
            
            for (int[] dir : dirs) {
                int nx = curr.x + dir[0];
                int ny = curr.y + dir[1];
                Point next = new Point(nx, ny);
                
                if (nx >= 0 && nx < width && ny >= 0 && ny < height 
                    && maze.getCell(nx, ny) != Maze.WALL && !parentMap.containsKey(next)) {
                    parentMap.put(next, curr);
                    queue.add(next);
                }
            }
        }
        return new ArrayList<>();
    }

    private static List<Point> reconstructPath(Map<Point, Point> parentMap, Point end) {
        List<Point> path = new ArrayList<>();
        Point curr = end;
        while (curr != null) {
            path.add(curr);
            curr = parentMap.get(curr);
        }
        Collections.reverse(path);
        return path;
    }
}

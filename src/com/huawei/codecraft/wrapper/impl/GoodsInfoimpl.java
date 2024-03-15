package com.huawei.codecraft.wrapper.impl;

import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.wrapper.GoodsInfo;

import java.util.*;

public class GoodsInfoimpl extends GoodsInfo {
    @Override
    public Good findNearestGood(Robot robot) {
        int minDistance = Integer.MAX_VALUE;
        Good nearestGood = null;
        for (Good availableGood : this.availableGoods) {
            int manhattanDistance = Math.abs(robot.x() - availableGood.x()) + Math.abs(robot.y() - availableGood.y());
            if (minDistance > manhattanDistance) {
                minDistance = manhattanDistance;
                nearestGood = availableGood;
            }
        }
//        System.err.println("nearest good: x:" + nearestGood.x() + " y:" + nearestGood.y() + " value:" + nearestGood.value());
        return nearestGood;
    }

    @Override
    public List<Command> getPath(Robot robot, Good good) {
        List<Pair> path = mazePathBFS(this.map, robot.x(), robot.y(), good.x(), good.y());
        List<Command> movePath = new ArrayList<>();
        int id = robot.id();
        for (int i = 1; i < path.size(); i++) {
            Pair prev = path.get(i - 1);
            Pair cur = path.get(i);
            if (prev.x == cur.x) {
                if (prev.y < cur.y) {
                    movePath.add(Command.move(id, 0));
                } else {
                    movePath.add(Command.move(id, 1));
                }
            } else {
                if (prev.x >= cur.x) {
                    movePath.add(Command.move(id, 2));
                } else {
                    movePath.add(Command.move(id, 3));
                }
            }
        }
        return movePath;
    }

    public List<Pair> mazePathBFS(char[][] maze, int startX, int startY, int endX, int endY) {
        Set<Pair> visited = new HashSet<>();
        Queue<Pair> queue = new LinkedList<>();
        List<Pair> path = new ArrayList<>();
        Map<Pair, Pair> parent = new HashMap<>();

        Pair start = new Pair(startX, startY);
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Pair pos = queue.poll();
            if (pos.x == endX && pos.y == endY) {
                while (parent.get(pos) != null) {
                    path.add(pos);
                    pos = parent.get(pos);
                }
                path.add(start);
                Collections.reverse(path);
                return path;
            } else {
                List<Pair> nbs = possibleNeighbours(maze, pos.x, pos.y);
                for (Pair nb : nbs) {
                    if (!visited.contains(nb)) {
                        visited.add(nb);
                        queue.offer(nb);
                        parent.put(nb, pos);
                    }
                }
            }
        }
        path.add(new Pair(-1, -1)); // Path not found indicator
        return path;
    }

    private List<Pair> possibleNeighbours(char[][] maze, int x, int y) {
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        List<Pair> neighbours = new ArrayList<>();
        for (int[] dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < maze.length && ny >= 0 && ny < maze[0].length && !isObstacle(nx, ny)){
                neighbours.add(new Pair(nx, ny));
            }
        }
        return neighbours;
    }

    /**
     * acquire good synchronously
     * @param robot
     * @param good
     */
    @Override
    public synchronized void acquireGood(Robot robot, Good good) {

    }

    private boolean isObstacle(int x, int y) {
        return this.map[x][y] == '#' || this.map[x][y] == '*' || this.map[x][y] == 'B';
    }


    static class Pair {
        int x, y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pair pair = (Pair) obj;
            return x == pair.x && y == pair.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}

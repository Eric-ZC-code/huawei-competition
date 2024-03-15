package com.huawei.codecraft.wrapper.impl;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.wrapper.MapInfo;

import java.util.*;

public class MapInfoimpl extends MapInfo {
    private MyLogger logger = MyLogger.getLogger("MapInfoimpl");
    @Override
    public Good findBestGood(Robot robot) {
        int minDistance = Integer.MAX_VALUE;
        Good BestGood = null;
        for (Good availableGood : this.availableGoods) {
            int manhattanDistance = Math.abs(robot.x() - availableGood.x()) + Math.abs(robot.y() - availableGood.y());
            if (minDistance > manhattanDistance) {
                minDistance = manhattanDistance;
                BestGood = availableGood;
            }
        }
        return BestGood;
    }

    @Override
    public Berth findBestBerth(Good good) {
        int minDistance = Integer.MAX_VALUE;
        Berth BestBerth = null;
        for (Berth berth : this.berths) {
            logger.info("Berth: " + berth);
            int manhattanDistance = Math.abs(good.x() - berth.x()) + Math.abs(good.y() - berth.y());
            if (minDistance > manhattanDistance) {
                minDistance = manhattanDistance;
                BestBerth = berth;
            }
        }
        return BestBerth;
    }

    @Override
    public List<Command> getFullPath(Robot robot, Good good, Berth berth) {
        if (good.isAcquired()) {
            return new ArrayList<>();
        }

        List<Command> pathToGood = getRobotToGoodPath(robot, good);
        Command getGood = getGood(robot, good);
        List<Command> pathToBerth = getGoodToBerthPath(good, berth, robot);

        // if pathToGood or pathToBerth is empty, return empty list
        if (pathToGood.isEmpty() || pathToBerth.isEmpty()) {
            return new ArrayList<>();
        }

        acquireGood(robot, good);

        List<Command> fullPath = new ArrayList<>(pathToGood);
        fullPath.add(getGood);
        fullPath.addAll(pathToBerth);

        Command pullGood = pullGood(robot, good, berth);
        fullPath.add(pullGood);

        return fullPath;
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
        return path;
    }

    private List<Pair> possibleNeighbours(char[][] maze, int x, int y) {
        int[][] dirs = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
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

    @Override
    public List<Command> getRobotToGoodPath(Robot robot, Good good) {
        List<Pair> path = mazePathBFS(this.map, robot.x(), robot.y(), good.x(), good.y());
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    @Override
    public List<Command> getGoodToBerthPath(Good good, Berth berth, Robot robot) {
        List<Pair> path = mazePathBFS(this.map, good.x(), good.y(), berth.x(), berth.y());
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    @Override
    public Command getGood(Robot robot, Good good) {
        if (robot.x() != good.x() || robot.y() != good.y()) {
            robot.setStatus(1); // robot is acquiring good
            availableGoods.remove(good); // remove good from available goods
            acquiredGoods.add(good); // add good to acquired goods
            good.setAcquired(true); // set good acquired
            return Command.get(robot.id());
        }

        return Command.ignore();
    }
    /**
     * acquire good synchronously
     * @param robot
     * @param good
     */
    @Override
    public synchronized void acquireGood(Robot robot, Good good) {
        acquiredGoods.remove(good); // remove good from acquired goods
        good.setAcquired(false); // set good not acquired
    }

    @Override
    public Command pullGood(Robot robot, Good good, Berth berth) {
        if (robot.x() == berth.x() && robot.y() == berth.y()) {
            robot.setStatus(0); // robot is pulling good
            return Command.pull(robot.id());
        }

        return Command.ignore();
    }

    private boolean isObstacle(int x, int y) {
        return this.map[x][y] == '#' || this.map[x][y] == '*' || this.map[x][y] == 'A';
    }

    // transform path to move commands
    private List<Command> pathTransform(List<Pair> path, int id) {
        List<Command> movePath = new ArrayList<>();
        // if path only contains the start point, return empty list.
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
        logger.info("Move Path: " + movePath);
        return movePath;
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

package com.huawei.codecraft.wrapper.impl;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.wrapper.MapInfo;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapInfoimpl extends MapInfo {
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private MyLogger logger = MyLogger.getLogger("MapInfoimpl");
    @Override
    public Good findBestGood(Robot robot) {
        int minDistance = Integer.MAX_VALUE;
        Good BestGood = null;
        rwLock.readLock().lock();
        try {

            final int size = this.availableGoods.size();
            for (int i = 0; i < size; i++) {
//                System.err.println(i);
                Good availableGood = Optional.of(availableGoods.get(i)).get();
                int manhattanDistance = Math.abs(robot.x() - availableGood.x()) + Math.abs(robot.y() - availableGood.y());
                if (minDistance > manhattanDistance) {
                    minDistance = manhattanDistance;
                    BestGood = availableGood;
                }
            }
        }catch (Exception e){
            System.err.println("availablegoods: " +availableGoods.get(0)+" "+availableGoods.get(1));
            e.printStackTrace();
        }finally {
            rwLock.readLock().unlock();
        }

        return BestGood;
    }

    @Override
    public Berth findBestBerth(Good good) {
        int minDistance = Integer.MAX_VALUE;
        Berth BestBerth = null;
        rwLock.readLock().lock();
        try{
            for (int i = 0; i < this.berths.length; i++) {
                Berth berth = this.berths[i];
                logger.info("Berth: " + berth);
                int manhattanDistance = Math.abs(good.x() - berth.x()) + Math.abs(good.y() - berth.y());
                if (minDistance > manhattanDistance) {
                    minDistance = manhattanDistance;
                    BestBerth = berth;
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            rwLock.readLock().unlock();
        }

        return BestBerth;
    }
    public Integer getAvailableBerth(){
        for (int i = 0; i < berths.length; i++) {
            if(berths[i].acquired()){
                return i;
            }
        }
        return null;
    }

    @Override
    public List<Command> getFullPath(Robot robot, Good good, Berth berth) {
        synchronized (good){
            if (good.acquired()) {
                return new ArrayList<>();
            }

        }


        List<Command> pathToGood = getRobotToGoodPath(robot, good);
        Command getGood = getGood(robot, good);
        List<Command> pathToBerth = getGoodToBerthPath(good, berth, robot);

        // if pathToGood or pathToBerth is empty, return empty list
        if (pathToGood.isEmpty() || pathToBerth.isEmpty()) {
            return new ArrayList<>();
        }
        rwLock.writeLock().lock();
        try {
            acquireGood(robot, good);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rwLock.writeLock().unlock();
        }

        List<Command> fullPath = new ArrayList<>(pathToGood);
        fullPath.add(getGood);
        fullPath.addAll(pathToBerth);

        // pull good
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
        Pair berthPoint = findBerthPoint(berth, good);
        List<Pair> path = mazePathBFS(this.map, good.x(), good.y(), berthPoint.x, berthPoint.y);
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    @Override
    public Command getGood(Robot robot, Good good) {
        rwLock.writeLock().lock();
        try {
            availableGoods.remove(good);
            acquiredGoods.add(good);
            good.setAcquired(true);
            return Command.get(robot.id());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rwLock.writeLock().unlock();
        }
        return Command.ignore();
    }
    /**
     * acquire good synchronously
     * @param robot
     * @param good
     */
    @Override
    public void acquireGood(Robot robot, Good good) {
        acquiredGoods.add(good);
        good.setAcquired(false); // set good not acquired
    }

    @Override
    public Command pullGood(Robot robot, Good good, Berth berth) {
        rwLock.writeLock().lock();
        try {
            acquiredGoods.remove(good);
            return Command.pull(robot.id());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rwLock.writeLock().unlock();
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

    private Pair findBerthPoint(Berth berth, Good good) {
        int minDistance = Integer.MAX_VALUE;
        Pair bestPoint = null;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int x = berth.x() + i;
                int y = berth.y() + j;
                int ManhattanDistance = Math.abs(x - good.x()) + Math.abs(y - good.y());
                if (minDistance > ManhattanDistance) {
                    minDistance = ManhattanDistance;
                    bestPoint = new Pair(x, y);
                }
            }
        }
        return bestPoint;
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

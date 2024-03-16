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
        rwLock.readLock().lock();
        try {

            final int size = this.availableGoods.size();
            for (int i = 0; i < size; i++) {
//                System.err.println(i);
                Good availableGood = Optional.of(this.availableGoods.get(i)).get();
                int manhattanDistance = Math.abs(robot.x() - availableGood.x()) + Math.abs(robot.y() - availableGood.y());
                if (minDistance > manhattanDistance) {
                    minDistance = manhattanDistance;
                    BestGood = availableGood;
                    logger.info("BestGood: " + BestGood);
                }
            }
        }catch (Exception e){
            logger.info("availablegoods: "+ availableGoods);
            e.printStackTrace();
        }finally {
            rwLock.readLock().unlock();
        }
        logger.info("BestGood: " + BestGood+ " availableGoods: "+ availableGoods.size());

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
            logger.info("berths: "+ Arrays.toString(berths));
            logger.info("good: "+ good);
            e.printStackTrace();
        }finally {
            rwLock.readLock().unlock();
        }

        return BestBerth;
    }

    public Integer getAvailableBerth(){
        Random rand = new Random();
        return rand.nextInt(berths.length);
    }

    @Override
    public Integer getMatchedBerth(Integer berthId) {
        return berthId;
    }

    @Override
    public List<Command> getFullPath(Robot robot, Good good, Berth berth) {
        // 判断货物是否已经被获取，获取了就返回空的命令数组
        rwLock.readLock().lock();
        try {
            if (good.acquired()) {
                return new ArrayList<>();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rwLock.readLock().unlock();
        }

        // 寻路逻辑
        if (robot.carrying() == 0) {
            logger.info("Robot is not carrying good");
            List<Command> pathToGood = getRobotToGoodPath(robot, good);
            Command getGood = getGood(robot, good);
            List<Command> pathToBerth = getGoodToBerthPath(good, berth, robot);

            // if pathToGood or pathToBerth is empty, return empty list
            if (pathToGood.isEmpty() || pathToBerth.isEmpty()) {
                return new ArrayList<>();
            }

            List<Command> fullPath = new ArrayList<>(pathToGood);
            fullPath.add(getGood);
            fullPath.addAll(pathToBerth);

            // pull good
            Command pullGood = pullGood(robot, good, berth);
            fullPath.add(pullGood);

            return fullPath;

        } else if (robot.carrying() == 1) {
            logger.info("Robot is carrying good");
            List<Command> pathToBerth = getRobotToBerthPath(robot, berth);
            if (pathToBerth.isEmpty()) {
                return new ArrayList<>();
            }
            // pull good
            Command pullGood = pullGood(robot, good, berth);
            pathToBerth.add(pullGood);

            return pathToBerth;
        }

        return new ArrayList<>();
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
        Pair berthPoint = findBerthPoint(berth, new Pair(good.x(), good.y()));
        List<Pair> path = mazePathBFS(this.map, good.x(), good.y(), berthPoint.x, berthPoint.y);
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    public List<Command> getRobotToBerthPath(Robot robot, Berth berth) {
        Pair berthPoint = findBerthPoint(berth, new Pair(robot.x(), robot.y()));
        List<Pair> path = mazePathBFS(this.map, robot.x(), robot.y(), berthPoint.x, berthPoint.y);
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

    private Pair findBerthPoint(Berth berth, Pair pair) {
        int minDistance = Integer.MAX_VALUE;
        Pair bestPoint = null;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int x = berth.x() + i;
                int y = berth.y() + j;
                int ManhattanDistance = Math.abs(x - pair.x) + Math.abs(y - pair.y);
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

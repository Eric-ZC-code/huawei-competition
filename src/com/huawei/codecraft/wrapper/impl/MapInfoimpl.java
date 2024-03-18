package com.huawei.codecraft.wrapper.impl;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.GoodStrategy;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.util.Pair;
import com.huawei.codecraft.wrapper.MapInfo;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapInfoimpl extends MapInfo {

    private MyLogger logger = MyLogger.getLogger("MapInfoimpl");
    private ReadWriteLock goingPointLock = new ReentrantReadWriteLock();

    @Override
    public Good findBestGood(Robot robot, GoodStrategy goodStrategy) {
        if(availableGoodsMap.size()<20){
            return null;
        }

        Good bestGood = null;
        rwLock.readLock().lock();
        try {
            switch (goodStrategy) {
                case VALUE:
                    bestGood = findGoodByValue(robot);
                    break;
                case RATIO:
                    bestGood = findGoodByRatio(robot);
                    break;
                default:
                    bestGood = findGoodByManhattanDistance(robot);
                    break;
            }
            logger.info("Robot: " + robot.id() + " BestGood: " + bestGood + " [x: " + robot.x() + ", y: " + robot.y() + "]" + "availableGoods: " + availableGoodsMap.size()+ " acquiredGoods: " + acquiredGoodsMap.size());
        } catch (Exception e) {

            logger.info("availablegoods: " + availableGoodsMap);
            e.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }
        logger.info("BestGood: " + bestGood + " availableGoods: " + availableGoodsMap.size());

        return bestGood;
    }

    private Good findGoodByRatio(Robot robot) {
        double max = Double.MIN_VALUE;
        double valueParam = 1.0;
        double distanceParam = 1.0;
        Good bestGood = null;
//        final int size = this.availableGoodsMap.size();
        rwLock.readLock().lock();
        try {
            for (Map.Entry<Pair, Good> pairGoodEntry : availableGoodsMap.entrySet()) {
                Good availableGood = Optional.of(pairGoodEntry.getValue()).get();
                double value = availableGood.value();
                double manhattanDistance = Math.abs(robot.x() - availableGood.x()) + Math.abs(robot.y() - availableGood.y());
                double ratio = 1.0 * (valueParam * value) / (distanceParam * manhattanDistance);
                if (max < ratio) {
                    max = ratio;
                    bestGood = availableGood;
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }
        rwLock.readLock().unlock();
        return bestGood;
    }

    private Good findGoodByManhattanDistance(Robot robot) {
        int minDistance = Integer.MAX_VALUE;
        Good bestGood = null;
        rwLock.readLock().lock();
        try {
            for (Map.Entry<Pair, Good> pairGoodEntry : availableGoodsMap.entrySet()) {
                //                System.err.println(i);
                Good availableGood = Optional.of(pairGoodEntry.getValue()).get();
                int manhattanDistance = Math.abs(robot.x() - availableGood.x()) + Math.abs(robot.y() - availableGood.y());
                if (minDistance > manhattanDistance) {
                    minDistance = manhattanDistance;
                    bestGood = availableGood;
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }

        if(minDistance>100){
            return null;
        }
        return bestGood;

    }

    private Good findGoodByValue(Robot robot) {
        int max = Integer.MIN_VALUE;
        Good bestGood = null;
        rwLock.readLock().lock();

        try {
            for (Map.Entry<Pair, Good> pairGoodEntry : availableGoodsMap.entrySet()) {
                Good availableGood = Optional.of(pairGoodEntry.getValue()).get();
                int value = availableGood.value();
                if (max < value) {
                    max = value;
                    bestGood = availableGood;
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }
        return bestGood;

    }

    public Berth currentBerth(int x, int y) {
        for (Berth berth : berths) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
//                    System.err.println("Robot : "+x+" "+y+" Berth " +berth.id()+" : "+berth.x()+" "+berth.y());
                    if ((berth.x() + i) == x && (berth.y() + j )== y) {
                        return berth;
                    }
                }

            }

        }
        return null;
    }

    @Override
    public boolean acquirePoint(Pair pos) {
        goingPointLock.writeLock().lock();

        try {
            if(!goingPoint.contains(pos)){
                this.goingPoint.add(pos);
                return true;
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            goingPointLock.writeLock().unlock();
        }
    }

    @Override
    public boolean pointIsAvailable(Pair pair) {
        goingPointLock.readLock().lock();

        try {
            return this.goingPoint.contains(pair);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            goingPointLock.readLock().unlock();
        }
    }

    @Override
    public boolean cleanPoints() {
        goingPointLock.writeLock().lock();

        try {
            this.goingPoint = new HashSet<>(10);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            goingPointLock.writeLock().unlock();
        }
    }

    @Override
    public Berth findBestBerth(int x, int y) {

        Berth BestBerth = null;
        rwLock.readLock().lock();
        try {
            int minDistance = Integer.MAX_VALUE;
            for (int i = 0; i < this.berths.length; i++) {
                Berth berth = this.berths[i];
                logger.info("Berth: " + berth);
                int manhattanDistance = Math.abs(x - berth.x()) + Math.abs(y - berth.y());
                if (minDistance > manhattanDistance) {
                    minDistance = manhattanDistance;
                    BestBerth = berth;
                }
            }

        } catch (Exception e) {
            logger.info("berths: " + Arrays.toString(berths));
            e.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }

        return BestBerth;
    }


    public Integer getAvailableBerth() {
        Random rand = new Random();
        return rand.nextInt(berths.length);
    }

    @Override
    public void addItem(int x, int y, char c) {
        this.map[x][y] = c;
    }

    @Override
    public List<Command> getFullPath(Robot robot, Good good, Berth berth) {
        // 寻路逻辑
        if (robot.carrying() == 0) {
            logger.info("Robot is not carrying good");
            if (good == null) {
                return new ArrayList<>();
            }
            // 判断货物是否已经被获取，获取了就返回空的命令数组
            rwLock.readLock().lock();
            try {
                if (good.acquired()) {
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rwLock.readLock().unlock();
            }

            List<Command> pathToGood = getRobotToGoodPath(robot, good);
            if(pathToGood.isEmpty()){
                logger.info("Robot "+ robot.id() + " path to good cannot be found. Goods : ("+ good.x()+" "+ good.y()+")");
            }
            // 就算没路也要去acquire这个物品，因为这个物品极有可能对大家来说不可达。这样没人能再拿到这个物品了
            Command getGood = getGood(robot, good);
            //在acquire后再return
            if(getGood == null|| pathToGood.isEmpty()){
                return new ArrayList<>();
            }
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
//
//            if (robot.x() == berthPoint.x && robot.y() == berthPoint.y) {
//                Command pullGood = pullGood(robot, good, berth);
//                pathToBerth.add(pullGood);
//                return pathToBerth;
//            }
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
            if (pos.x() == endX && pos.y() == endY) {
                while (parent.get(pos) != null) {
                    path.add(pos);
                    pos = parent.get(pos);
                }
                path.add(start);
                Collections.reverse(path);
                return path;
            } else {
                List<Pair> nbs = possibleNeighbours(maze, pos.x(), pos.y());
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
            if (nx >= 0 && nx < maze.length && ny >= 0 && ny < maze[0].length && !isObstacle(nx, ny)) {
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
        Pair berthPoint = findBerthPoint(berth);
        List<Pair> path = mazePathBFS(this.map, good.x(), good.y(), berthPoint.x(), berthPoint.y());
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    public List<Command> getRobotToBerthPath(Robot robot, Berth berth) {
        Pair berthPoint = findBerthPoint(berth);
        List<Pair> path = mazePathBFS(this.map, robot.x(), robot.y(), berthPoint.x(), berthPoint.y());
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    @Override
    public Command getGood(Robot robot, Good good) {
        rwLock.writeLock().lock();
        try {
            if(good.acquired()){
                return null;
            }
            availableGoodsMap.remove(good.pair());
            acquiredGoodsMap.add(good);
            good.setAcquired(true);
            return Command.get(robot.id());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwLock.writeLock().unlock();
        }
        return null;
    }

    @Override
    public Command pullGood(Robot robot, Good good, Berth berth) {
        rwLock.writeLock().lock();
        try {
            acquiredGoodsMap.remove(good);
            return Command.pull(robot.id());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
            if (prev.x() == cur.x()) {
                if (prev.y() < cur.y()) {
                    movePath.add(Command.move(id, 0));
                } else {
                    movePath.add(Command.move(id, 1));
                }
            } else {
                if (prev.x() >= cur.x()) {
                    movePath.add(Command.move(id, 2));
                } else {
                    movePath.add(Command.move(id, 3));
                }
            }
        }
        logger.info("Move Path: " + movePath);
        return movePath;
    }
    private Pair findBerthPoint(Berth berth){
        Random rand = new Random();
        return new Pair(berth.x() + rand.nextInt(4), berth.y() + rand.nextInt(4));
    }

    private Pair findBestBerthPoint(Berth berth, Pair pair) {
        int minDistance = Integer.MAX_VALUE;
        Pair bestPoint = null;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int x = berth.x() + i;
                int y = berth.y() + j;
                int ManhattanDistance = Math.abs(x - pair.x()) + Math.abs(y - pair.y());
                if (minDistance > ManhattanDistance) {
                    minDistance = ManhattanDistance;
                    bestPoint = new Pair(x, y);
                }
            }
        }
        return bestPoint;
    }

}

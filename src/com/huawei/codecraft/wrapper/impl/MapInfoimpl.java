package com.huawei.codecraft.wrapper.impl;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.GoodStrategy;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.util.Pair;
import com.huawei.codecraft.util.Position;
import com.huawei.codecraft.wrapper.MapInfo;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapInfoimpl extends MapInfo {

    private ReadWriteLock goingPointLock = new ReentrantReadWriteLock();

    @Override
    public Good findBestGood(Robot robot, GoodStrategy goodStrategy) {
        if(availableGoodsMap.size()<20){
            return null;
        }

        Good bestGood = null;
        goodRWLock.readLock().lock();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            goodRWLock.readLock().unlock();
        }
        return bestGood;
    }

    private Good findGoodByRatio(Robot robot) {
        double max = Double.MIN_VALUE;
        double valueParam = 1.0;
        double distanceParam = 1.0;
        Good bestGood = null;
//        final int size = this.availableGoodsMap.size();
        goodRWLock.readLock().lock();
        try {
            for (Map.Entry<Position, Good> pairGoodEntry : availableGoodsMap.entrySet()) {
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
            goodRWLock.readLock().unlock();
        }
        goodRWLock.readLock().unlock();
        return bestGood;
    }

    private Good findGoodByManhattanDistance(Robot robot) {
        int minDistance = Integer.MAX_VALUE;
        Good bestGood = null;
        goodRWLock.readLock().lock();
        try {
            for (Map.Entry<Position, Good> pairGoodEntry : availableGoodsMap.entrySet()) {
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
            goodRWLock.readLock().unlock();
        }

        if(minDistance>100){
            return null;
        }
        return bestGood;

    }

    private Good findGoodByValue(Robot robot) {
        int max = Integer.MIN_VALUE;
        Good bestGood = null;
        goodRWLock.readLock().lock();

        try {
            for (Map.Entry<Position, Good> pairGoodEntry : availableGoodsMap.entrySet()) {
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
            goodRWLock.readLock().unlock();
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
    public boolean acquirePoint(Position pos, Robot robot) {
        goingPointLock.writeLock().lock();

        try {
            if(!goingPoint.containsKey(pos)){
                this.goingPoint.put(pos,robot);
                this.goingPoint.put(robot.position(),robot);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            goingPointLock.writeLock().unlock();
        }
    }

    @Override
    public Robot getPositionInfo(Position pos) {
        goingPointLock.readLock().lock();

        try {
            return goingPoint.get(pos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            goingPointLock.readLock().unlock();

        }
    }

    @Override
    public boolean pointIsAvailable(Position position) {
        goingPointLock.readLock().lock();

        try {
            return this.goingPoint.containsKey(position);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            goingPointLock.readLock().unlock();
        }
    }

    @Override
    public boolean removePoint(Position position) {
        goingPointLock.writeLock().lock();

        try {
            return goingPoint.remove(position) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            goingPointLock.writeLock().unlock();
        }
    }

    @Override
    public boolean cleanPoints() {
        goingPointLock.writeLock().lock();

        try {
            this.goingPoint = new HashMap<>(10);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            goingPointLock.writeLock().unlock();
        }
    }

    @Override
    public List<Command> circumventionCommand(Position curPos) {
        return null;
    }

    @Override
    public Berth findBestBerth(int x, int y) {

        Berth BestBerth = null;
        goodRWLock.readLock().lock();
        try {
            int minDistance = Integer.MAX_VALUE;
            for (int i = 0; i < this.berths.length; i++) {
                Berth berth = this.berths[i];
                int manhattanDistance = Math.abs(x - berth.x()) + Math.abs(y - berth.y());
                if (minDistance > manhattanDistance) {
                    minDistance = manhattanDistance;
                    BestBerth = berth;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            goodRWLock.readLock().unlock();
        }

        return BestBerth;
    }

    // 获取最佳的可用泊位
    @Override
    public Integer getAvailableBerth() {
        berthRWLock.writeLock().lock();
        try {
            boolean flag = false;
            List<Berth> availableBerths = new ArrayList<>();
            for (int i = 0; i < this.berths.length; i++) {
                if (!this.berths[i].acquired()) {
                    availableBerths.add(this.berths[i]);
                }
                if (this.berths[0].amount() != this.berths[i].amount()) {
                    flag = true;
                }
            }

//            for (Berth availableBerth : availableBerths) {
//                logger.info("Available Berth: " + availableBerth.id() + " amount: " + availableBerth.amount());
//            }

            int j = 0, id = 0;
            if (flag) {
                // 选择货物最多的泊位
                int maxGoodsNum = Integer.MIN_VALUE;
                for (int i = 0; i < availableBerths.size(); i++) {
                    if (availableBerths.get(i).amount() > maxGoodsNum) {
                        maxGoodsNum = availableBerths.get(i).amount();
                        j = i;
                    }
                }

            } else {
                // 随机选择一个可用泊位
                Random rand = new Random();
                j = rand.nextInt(availableBerths.size());
            }

            id = availableBerths.get(j).id();
            this.berths[id].setAcquired(true);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            berthRWLock.writeLock().unlock();
        }
        return null;
    }

    // 释放泊位
    @Override
    public void setBerthFree(int id) {
        berthRWLock.writeLock().lock();
        try {
            this.berths[id].setAcquired(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            berthRWLock.writeLock().unlock();
        }
    }

    @Override
    public void addItem(int x, int y, char c) {
        this.map[x][y] = c;
    }

    @Override
    public List<Command> getFullPath(Robot robot, Good good, Berth berth) {

        // 寻路逻辑
        // 如果机器人没有搬运货物
        if (robot.carrying() == 0) {

            if (good == null) {
                return new ArrayList<>();
            }
            // 判断货物是否已经被获取，获取了就返回空的命令数组
            goodRWLock.readLock().lock();
            try {
                if (good.acquired()) {
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                goodRWLock.readLock().unlock();
            }

            // 获取机器人到货物的路径
            List<Command> pathToGood = getRobotToGoodPath(robot, good);

            // 获取货物，acquire货物
            Command getGood = getGood(robot, good);
            if(getGood == null){
                return new ArrayList<>();
            }

            // 获取货物到泊位的路径
            List<Command> goodToBerth = getGoodToBerthPath(good, berth, robot);

            // 如果机器人到货物的路径或者货物到泊位的路径为空，返回空的命令数组
            if (pathToGood.isEmpty() || goodToBerth.isEmpty()) {
                return new ArrayList<>();
            }

            // 合并路径
            List<Command> fullPath = new ArrayList<>(pathToGood);
            fullPath.add(getGood);
            fullPath.addAll(goodToBerth);
            Command pullGood = pullGood(robot, good, berth);
            fullPath.add(pullGood);

            return fullPath;

        }
        // 如果机器人正在搬运货物
        else if (robot.carrying() == 1) {


            // 如果机器人不可达泊位，返回空的命令数组
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

    // 无目标good bfs搜索
    @Override
    public List<Command> getFullPath(Robot robot) {
//        // 如果货物数量小于20，返回空的命令数组
//        if(availableGoodsMap.size()<20){
//            return null;
//        }

        // 获取货物和机器人到货物的路径
        Pair<Good, List<Command>> GoodAndPath = getGoodAndPath(robot);
        if (GoodAndPath == null || GoodAndPath.getKey() == null || GoodAndPath.getValue().isEmpty()) {
            return new ArrayList<>();
        }
        Good good = GoodAndPath.getKey();
        List<Command> pathToGood = GoodAndPath.getValue();
        Berth berth = findBestBerth(good.x(), good.y());

        // 如果机器人不可达泊位，返回空的命令数组
        List<Command> pathToBerth = getRobotToBerthPath(robot, berth);
        if (pathToBerth.isEmpty()) {
            return new ArrayList<>();
        }

        // 寻路逻辑
        // 如果机器人没有搬运货物
        if (robot.carrying() == 0) {
            // 判断货物是否已经被获取，获取了就返回空的命令数组
            goodRWLock.readLock().lock();
            try {
                if (good.acquired()) {
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                goodRWLock.readLock().unlock();
            }

            // 获取货物，acquire货物
            Command getGood = getGood(robot, good);
            if(getGood == null){
                return new ArrayList<>();
            }

            // 获取货物到泊位的路径
            List<Command> goodToBerth = getGoodToBerthPath(good, berth, robot);

            // 如果机器人到货物的路径或者货物到泊位的路径为空，返回空的命令数组
            if (goodToBerth.isEmpty() || pathToGood.isEmpty()) {
                return new ArrayList<>();
            }

            // 合并路径
            List<Command> fullPath = new ArrayList<>(pathToGood);
            fullPath.add(getGood);
            fullPath.addAll(goodToBerth);
            Command pullGood = pullGood(robot, good, berth);
            fullPath.add(pullGood);

            return fullPath;

        }
        // 如果机器人正在搬运货物
        else if (robot.carrying() == 1) {


            // pull good
            Command pullGood = pullGood(robot, null, berth);
            pathToBerth.add(pullGood);

            return pathToBerth;
        }

        return new ArrayList<>();
    }

    // bfs洪水泛滥法，无目的地四处搜索
    public Pair<Good,List<Position>> mazePathPool(char[][] maze, int startX, int startY) {
        // availableGoods 需要一个读锁
        goodRWLock.readLock().lock();
        try {
            Pair<Good, List<Position>> GoodAndPath; // 方法返回值: 目标货物和路径
            Set<Position> visited = new HashSet<>();
            List<Position> path = new ArrayList<>();
            Map<Position, Position> parent = new HashMap<>();
            Queue<Position> queue = new LinkedList<>();
            Position start = new Position(startX, startY);
            queue.offer(start);
            visited.add(start);

            while (!queue.isEmpty()) {
                Position pos = queue.poll();
                if (this.availableGoods().get(pos) != null) {
                    Good good = this.availableGoods().get(pos);
                    while (parent.get(pos) != null) {
                        path.add(pos);
                        pos = parent.get(pos);
                    }
                    path.add(start);
                    Collections.reverse(path);
                    GoodAndPath = new Pair<>(good, path);
                    return GoodAndPath;
                } else {
                    List<Position> nbs = possibleNeighbours(maze, pos.x(), pos.y());
                    for (Position nb : nbs) {
                        if (!visited.contains(nb)) {
                            visited.add(nb);
                            queue.offer(nb);
                            parent.put(nb, pos);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            goodRWLock.readLock().unlock();
        }
//        System.err.println("No good found");

        return new Pair<>(null, new ArrayList<>());
    }

    // bfs点对点搜索路径
    public List<Position> mazePathBFS(char[][] maze, int startX, int startY, int endX, int endY) {
        Set<Position> visited = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();
        List<Position> path = new ArrayList<>();
        Map<Position, Position> parent = new HashMap<>();

        Position start = new Position(startX, startY);
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Position pos = queue.poll();
            if (pos.x() == endX && pos.y() == endY) {
                while (parent.get(pos) != null) {
                    path.add(pos);
                    pos = parent.get(pos);
                }
                path.add(start);
                Collections.reverse(path);
                return path;
            } else {
                List<Position> nbs = possibleNeighbours(maze, pos.x(), pos.y());
                for (Position nb : nbs) {
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
    public List<Position> mazePathAStar(char[][] maze, int startX, int startY, int endX, int endY) {
        final int[] dx = {0, 1, 0, -1};
        final int[] dy = {1, 0, -1, 0};
        int n = maze.length, m = maze[0].length;
        Map<Position, Position> cameFrom = new HashMap<>();
        Map<Position, Integer> costSoFar = new HashMap<>();
        PriorityQueue<Position> frontier = new PriorityQueue<>((a, b) -> {
            int costA = costSoFar.getOrDefault(a, Integer.MAX_VALUE) + heuristic(a, endX, endY);
            int costB = costSoFar.getOrDefault(b, Integer.MAX_VALUE) + heuristic(b, endX, endY);
            return Integer.compare(costA, costB);
        });


        Position start = new Position(startX, startY);
        Position end = new Position(endX, endY);
        frontier.add(start);
        cameFrom.put(start, null);
        costSoFar.put(start, 0);

        while (!frontier.isEmpty()) {
            Position current = frontier.poll();
            if (current.equals(end)) {
                return reconstructPath(cameFrom, start, end);
            }

            for (int i = 0; i < 4; i++) {
                int nextX = current.x() + dx[i];
                int nextY = current.y() + dy[i];
                Position next = new Position(nextX, nextY);
                if (nextX >= 0 && nextX < n && nextY >= 0 && nextY < m && !isObstacle(nextX, nextY) && (!costSoFar.containsKey(next) || costSoFar.get(current) + 1 < costSoFar.get(next))) {
                    costSoFar.put(next, costSoFar.get(current) + 1);
                    frontier.add(next);
                    cameFrom.put(next, current);

                }
            }
        }

        return Collections.emptyList(); // 未找到路径时返回空列表
    }

    // 启发式函数：曼哈顿距离
    private static int heuristic(Position node, int endX, int endY) {
        return Math.abs(node.x() - endX) + Math.abs(node.y() - endY);
    }

    // 重建路径
    private static List<Position> reconstructPath(Map<Position, Position> cameFrom, Position start, Position end) {
        List<Position> path = new ArrayList<>();
        for (Position at = end; at != null; at = cameFrom.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    private List<Position> possibleNeighbours(char[][] maze, int x, int y) {
        int[][] dirs = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        List<Position> neighbours = new ArrayList<>();
        for (int[] dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < maze.length && ny >= 0 && ny < maze[0].length && !isObstacle(nx, ny)) {
                neighbours.add(new Position(nx, ny));
            }
        }
        return neighbours;
    }

    @Override
    public List<Command> getRobotToGoodPath(Robot robot, Good good) {
        List<Position> path = mazePathBFS(this.map, robot.x(), robot.y(), good.x(), good.y());
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    @Override
    public Pair<Good, List<Command>> getGoodAndPath(Robot robot) {
        Pair<Good, List<Position>> pathPair = mazePathPool(this.map, robot.x(), robot.y());
        if (pathPair != null) {
            Good good = pathPair.getKey();
            List<Position> path = pathPair.getValue();
            if (good != null && path != null) {
                List<Command> movePath = pathTransform(path, robot.id());
                return new Pair<>(good, movePath);
            }
        }
        return null;
    }


    @Override
    public List<Command> getGoodToBerthPath(Good good, Berth berth, Robot robot) {
        Position berthPoint = findBerthPoint(berth);
        List<Position> path = mazePathBFS(this.map, good.x(), good.y(), berthPoint.x(), berthPoint.y());
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    @Override
    public List<Command> getRobotToBerthPath(Robot robot, Berth berth) {
        Position berthPoint = findBerthPoint(berth);
        List<Position> path = mazePathBFS(this.map, robot.x(), robot.y(), berthPoint.x(), berthPoint.y());
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    @Override
    public Command getGood(Robot robot, Good good) {
        goodRWLock.writeLock().lock();
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
            goodRWLock.writeLock().unlock();
        }
        return null;
    }

    @Override
    public Command pullGood(Robot robot, Good good, Berth berth) {
        goodRWLock.writeLock().lock();
        try {
            acquiredGoodsMap.remove(good);
            return Command.pull(robot.id());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            goodRWLock.writeLock().unlock();
        }
        return Command.ignore();
    }

    public boolean isObstacle(int x, int y) {

        return this.map[x][y] == '#' || this.map[x][y] == '*' || this.map[x][y] == 'A';

    }

    // transform path to move commands
    private List<Command> pathTransform(List<Position> path, int id) {
        List<Command> movePath = new ArrayList<>();
        // if path only contains the start point, return empty list.
        for (int i = 1; i < path.size(); i++) {
            Position prev = path.get(i - 1);
            Position cur = path.get(i);
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

        return movePath;
    }
    private Position findBerthPoint(Berth berth){
        Random rand = new Random();
        return new Position(berth.x() + rand.nextInt(4), berth.y() + rand.nextInt(4));
    }

    private Position findBestBerthPoint(Berth berth, Position position) {
        int minDistance = Integer.MAX_VALUE;
        Position bestPoint = null;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int x = berth.x() + i;
                int y = berth.y() + j;
                int ManhattanDistance = Math.abs(x - position.x()) + Math.abs(y - position.y());
                if (minDistance > ManhattanDistance) {
                    minDistance = ManhattanDistance;
                    bestPoint = new Position(x, y);
                }
            }
        }
        return bestPoint;
    }

}

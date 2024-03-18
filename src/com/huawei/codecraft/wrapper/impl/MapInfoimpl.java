package com.huawei.codecraft.wrapper.impl;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Command;
import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.GoodStrategy;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.wrapper.MapInfo;

import java.util.*;

public class MapInfoimpl extends MapInfo {

    private MyLogger logger = MyLogger.getLogger("MapInfoimpl");

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
        // 如果机器人不可达泊位，返回空的命令数组
        List<Command> pathToBerth = getRobotToBerthPath(robot, berth);
        if (pathToBerth.isEmpty()) {
            return new ArrayList<>();
        }

        // 寻路逻辑
        // 如果机器人没有搬运货物
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
            logger.info("Robot is carrying good");

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
        // 获取货物和机器人到货物的路径
        HashMap<Good, List<Command>> GoodAndPath = getGoodAndPath(robot);
        if (GoodAndPath.isEmpty()) {
            return new ArrayList<>();
        }
        Good good = GoodAndPath.keySet().iterator().next();
        List<Command> pathToGood = GoodAndPath.get(good);
        Berth berth = findBestBerth(good.x(), good.y());

        // 如果机器人不可达泊位，返回空的命令数组
        List<Command> pathToBerth = getRobotToBerthPath(robot, berth);
        if (pathToBerth.isEmpty()) {
            return new ArrayList<>();
        }

        // 寻路逻辑
        // 如果机器人没有搬运货物
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
            logger.info("Robot is carrying good");

            // pull good
            Command pullGood = pullGood(robot, null, berth);
            pathToBerth.add(pullGood);

            return pathToBerth;
        }

        return new ArrayList<>();
    }

    // bfs洪水泛滥法，无目的地四处搜索
    public HashMap<Good,List<Pair>> mazePathPool(char[][] maze, int startX, int startY) {
        // availableGoods 需要一个读锁
        rwLock.readLock().lock();
        try {
            HashMap<Good, List<Pair>> pathMap = new HashMap<>(); // 方法返回值: 目标货物和路径
            Set<Pair> visited = new HashSet<>();
            List<Pair> path = new ArrayList<>();
            Map<Pair, Pair> parent = new HashMap<>();
            Queue<Pair> queue = new LinkedList<>();
            Pair start = new Pair(startX, startY);
            queue.offer(start);
            visited.add(start);

            while (!queue.isEmpty()) {
                Pair pos = queue.poll();
                if (this.availableGoods().get(pos) != null) {
                    System.err.println("Good: " + this.availableGoods().get(pos));
                    while (parent.get(pos) != null) {
                        path.add(pos);
                        pos = parent.get(pos);
                    }
                    path.add(start);
                    Collections.reverse(path);

                    System.err.println("Good: " + this.availableGoods().get(pos) + " Path: " + path);
                    pathMap.put(this.availableGoods().get(pos), path);
                    return pathMap;
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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }
        System.err.println("No good found");

        return new HashMap<>();
    }

    // bfs点对点搜索路径
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

    public List<Pair> mazePathAStar(char[][] maze, int startX, int startY, int endX, int endY) {
        final int[] dx = {0, 1, 0, -1};
        final int[] dy = {1, 0, -1, 0};
        int n = maze.length, m = maze[0].length;
        Map<Pair, Pair> cameFrom = new HashMap<>();
        Map<Pair, Integer> costSoFar = new HashMap<>();
        PriorityQueue<Pair> frontier = new PriorityQueue<>((a, b) -> {
            int costA = costSoFar.getOrDefault(a, Integer.MAX_VALUE) + heuristic(a, endX, endY);
            int costB = costSoFar.getOrDefault(b, Integer.MAX_VALUE) + heuristic(b, endX, endY);
            return Integer.compare(costA, costB);
        });

        Pair start = new Pair(startX, startY);
        Pair end = new Pair(endX, endY);
        frontier.add(start);
        cameFrom.put(start, null);
        costSoFar.put(start, 0);

        while (!frontier.isEmpty()) {
            Pair current = frontier.poll();
            // System.err.println(current.x + "," + current.y + " fs: " + (costSoFar.get(current) + heuristic(current, endX, endY)));
            if (current.equals(end)) {
                return reconstructPath(cameFrom, start, end);
            }

            for (int i = 0; i < 4; i++) {
                int nextX = current.x + dx[i];
                int nextY = current.y + dy[i];
                Pair next = new Pair(nextX, nextY);
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
    private static int heuristic(Pair node, int endX, int endY) {
        return Math.abs(node.x - endX) + Math.abs(node.y - endY);
    }

    // 重建路径
    private static List<Pair> reconstructPath(Map<Pair, Pair> cameFrom, Pair start, Pair end) {
        List<Pair> path = new ArrayList<>();
        for (Pair at = end; at != null; at = cameFrom.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
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
        List<Pair> path = mazePathAStar(this.map, robot.x(), robot.y(), good.x(), good.y());
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    @Override
    public HashMap<Good, List<Command>> getGoodAndPath(Robot robot) {
        HashMap<Good, List<Pair>> pathMap = mazePathPool(this.map, robot.x(), robot.y());
        HashMap<Good, List<Command>> result = new HashMap<>();
        if (pathMap.size() == 1){
            Good good = pathMap.keySet().iterator().next();
            List<Pair> path = pathMap.get(good);
            List<Command> movePath = pathTransform(path, robot.id());
            result.put(good, movePath);
        }
        return result;
    }

    @Override
    public List<Command> getGoodToBerthPath(Good good, Berth berth, Robot robot) {
        Pair berthPoint = findBerthPoint(berth);
        List<Pair> path = mazePathBFS(this.map, good.x(), good.y(), berthPoint.x(), berthPoint.y());
        List<Command> movePath = pathTransform(path, robot.id());
        return movePath;
    }

    @Override
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

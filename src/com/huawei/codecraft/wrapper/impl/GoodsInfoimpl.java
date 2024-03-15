package com.huawei.codecraft.wrapper.impl;

import com.huawei.codecraft.entities.Good;
import com.huawei.codecraft.entities.Robot;
import com.huawei.codecraft.enums.Command;
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
        System.err.println("nearest good: x:" + nearestGood.x() + " y:" + nearestGood.y() + " value:" + nearestGood.value());
        return nearestGood;
    }

    @Override
    public List<Command> getPath(Robot robot, Good good) {
        // bfs 寻找目标货物
        char[][] map = this.map;
        int x = robot.x();
        int y = robot.y();
        int rows = map.length;
        int cols = map[0].length;
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        Triple start = new Triple(x, y, new ArrayList<Command>());
        Deque<Triple> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.offer(start);
        visited.add(x + "," + y);

        while (!queue.isEmpty()) {
            Triple t = queue.poll();;

            if (t.x == good.x() && t.y == good.y()) {
                System.err.println("Robot path found: " + t.path);
                return t.path;
            }

            for (int i = 0; i < directions.length; i++) {
                int dx = directions[i][0];
                int dy = directions[i][1];
                int nx = t.x + dx;
                int ny = t.y + dy;

                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && !isObstacle(nx, ny) && !visited.contains(nx + "," + ny)) {
                    Command newCommand = Command.MOVE.setActorId(robot.id()).setPara2(i);
                    t.path.add(newCommand);
                    Triple newTriple = new Triple(nx, ny, t.path);
                    queue.offer(newTriple);
                    visited.add(nx + "," + ny);
                }
            }
        }

        System.err.println("Robot path not found");
        // 找不到移动路径返回空列表
        return new ArrayList<>();
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

    class Triple {
        int x, y;
        List<Command> path;
        Triple (int x, int y,  List<Command> path) {
            this.x = x;
            this.y = y;
            this.path = path;
        }
    }
}

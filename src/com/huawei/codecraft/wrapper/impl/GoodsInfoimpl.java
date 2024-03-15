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
        Character[][] map = this.map;
        int x = robot.x();
        int y = robot.y();
        int rows = map.length;
        int cols = map[0].length;
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        int[] source = new int[]{x, y, -1};
        Deque<int[]> queue = new ArrayDeque<>();
        Set<int[]> visited = new HashSet<>();
        queue.offer(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            int[] position = queue.poll();
            int prevDirection = position[2];
            List<Command> path = new ArrayList<>();

            if (prevDirection != -1) {
                path.add(Command.MOVE.setActorId(prevDirection).setPara2(prevDirection));
            }

            if (position[0] == good.x() && position[1] == good.y()) {
                System.out.println("Robot path found: " + path);
                return path;
            }

            for (int i = 0; i < directions.length; i++) {
                int dx = directions[i][0];
                int dy = directions[i][1];
                int nx = x + dx;
                int ny = y + dy;
                int[] nextPosition = new int[]{nx, ny, i};

                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && map[nx][ny] == '.' && !visited.contains(nextPosition)) {
                    queue.offer(nextPosition);
                    visited.add(nextPosition);
                }
            }
        }

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
}

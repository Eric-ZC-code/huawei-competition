import sys, os, random
from collections import deque
from datetime import datetime


def manhattan_distance(x1, y1, x2, y2):
    return abs(x1 - x2) + abs(y1 - y2)


# maximum value/distance (mangattan distance) for a robot
def find_good(robots, goods):
    robots_targets = dict()

    for robot in robots:
        max_ratio = 0
        for good in goods:
            distance = manhattan_distance(robot.x, robot.y, good.x, good.y)
            if distance == 0:
                robots_targets[robot] = good
            else:
                ratio = good.val / distance
                if ratio > max_ratio:
                    max_ratio = ratio
                    robots_targets[robot] = good
                
    return robots_targets


# return the sortest path (movements) for a robot position -> a good position
def bfs_robot(map, robot, good):
    rows, cols = len(map), len(map[0])
    directions = [(1, 0), (-1, 0), (0, -1), (0, 1)]

    queue = deque([(robot.x, robot.y, [])])  # deque element: (x, y, path)
    visited = set([(robot.x, robot.y)])

    while queue:
        x, y, path = queue.popleft()

        if (x, y) == (good.x, good.y):
            write_log(f'Robot path found: {path}')
            return path

        for i, (dx, dy) in enumerate(directions):
            nx, ny = x + dx, y + dy
            if 0 <= nx < rows and 0 <= ny < cols and map[nx][ny] == '.' and ((nx, ny) not in visited):
                visited.add((nx, ny))
                queue.append((nx, ny, path + [i]))

    return []


def robots_paths(map, robots, goods):
    paths = dict()

    robots_targets = find_good(robots, goods)
    for robot, good in robots_targets.items():
        paths[robot] = bfs_robot(map, robot, good)

    return paths


def write_log(msg):
    current_time = datetime.now().strftime("%m-%d_%H-%M")
    log_folder = "logs"
    file_name = os.path.join(log_folder, f'error_{current_time}.log')
    sys.stderr = open(file_name, 'a+')
    sys.stderr.write(f'{msg}\n')
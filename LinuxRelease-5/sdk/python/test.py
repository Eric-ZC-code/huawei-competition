from utils import *
from robot import Robot
from good import Good


def test_bfs_robot():
    map_file = "LinuxRelease-5/maps/map1.txt"

    simple_map = []
    with open(map_file, "r") as file:
        for line in file:
            row = list(line.strip())
            simple_map.append(row)

    for row in simple_map:
        print(row)

    robot_position = Robot(36, 173, 0, 1)
    good_position = Good(9, 147, 0, 1)

    path = bfs_robot(simple_map, robot_position, good_position)
    print(path)


if __name__ == "__main__":
    test_bfs_robot()
import sys, random

from robot import Robot
from boat import Boat
from berth import Berth
from good import Good
from utils import robots_paths, write_log


n = 200
robot_num = 10
berth_num = 10
N = 210

robot = [Robot() for _ in range(robot_num)]
berth = [Berth() for _ in range(berth_num)]
boat = [Boat() for _ in range(5)]

money = 0
boat_capacity = 0
id = 0
ch = [] # map
gds = [[0 for _ in range(N)] for _ in range(N)] # goods map
valid_goods = [] # store goods' positions
paths = {} # store robots' paths


def Init():
    for i in range(0, n):
        line = input()
        ch.append([c for c in line])
    for i in range(berth_num):
        line = input()
        berth_list = [int(c) for c in line.split(sep=" ")]
        id = berth_list[0]
        berth[id].x = berth_list[1]
        berth[id].y = berth_list[2]
        berth[id].transport_time = berth_list[3]
        berth[id].loading_speed = berth_list[4]
    boat_capacity = int(input())
    okk = input()
    print(okk)
    sys.stdout.flush()


# input a frame
def Input():
    id, money = map(int, input().split(" "))
    num = int(input())
    for i in range(num):
        x, y, val = map(int, input().split())
        gds[x][y] = val
        good = Good(x, y, 0, val)
        valid_goods.append(good) # store goods
    for i in range(robot_num):
        robot[i].goods, robot[i].x, robot[i].y, robot[i].status = map(int, input().split())
    for i in range(5):
        boat[i].status, boat[i].pos = map(int, input().split())
    okk = input()
    return id


if __name__ == "__main__":  
    Init()
    for zhen in range(1, 15001):
        id = Input()

        if len(paths) == 0:
            paths = robots_paths(ch, robot, valid_goods)

        for i in range(robot_num):
            
            # move the robot to the good
            if paths.get(robot[i]):
                move = paths[robot[i]].pop(0) # get the next move
                print("move", i, move)
                sys.stdout.flush()

            # the robot gets the good
            if paths.get(robot[i]) and len(paths[robot[i]]) == 0:
                print("get", i)
                robot[i].goods = 1
                sys.stdout.flush()         
            

        print("OK")
        sys.stdout.flush()

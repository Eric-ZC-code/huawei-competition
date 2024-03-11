from utils import *

def test_bfs():
    # 测试bfs
    grid = [
        ['#', '.', '.', '#', '.', '#'],
        ['#', '.', '.', '#', '.', '#'],
        ['#', '#', '.', '.', '.', '.'],
        ['#', '#', '#', '#', '.', '#'],
        ['#', '.', '.', '#', '.', '.'],
        ['#', '#', '#', '#', '#', '#']
    ]

    d = bfs(grid)
    # 打印结果示例
    for source in d:
        print("From", source, ":")
        for dest in d[source]:
            print("  To", dest, ":", d[source][dest])

if __name__ == "__main__":
    test_bfs()
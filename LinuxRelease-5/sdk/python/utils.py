from collections import deque

def bfs(grid):
    rows, cols = len(grid), len(grid[0])
    directions = [(1, 0), (-1, 0), (0, 1), (0, -1)]

    # 初始化存储最短路径的字典
    d = {}

    # 遍历地图上的每一个可走单元格，作为起点进行广度优先搜索
    for x1 in range(rows):
        for y1 in range(cols):
            if grid[x1][y1] == '.':
                # 初始化队列和 visited 字典
                queue = deque([(x1, y1)])
                visited = set([(x1, y1)])

                # 初始化距离字典，存储到所有其他单元格的最短路径
                d[(x1, y1)] = {}

                # 广度优先搜索
                while queue:
                    x, y = queue.popleft()

                    # 检查当前位置的四个相邻位置
                    for dx, dy in directions:
                        nx, ny = x + dx, y + dy

                        # 检查相邻位置是否在地图内，且为可走单元格且未被访问过
                        if 0 <= nx < rows and 0 <= ny < cols and grid[nx][ny] == '.' and (nx, ny) not in visited:
                            # 更新距离字典中到相邻位置的最短路径
                            d[(x1, y1)][(nx, ny)] = d[(x1, y1)].get((x, y), []) + [(nx, ny)]
                            # 将相邻位置加入队列和 visited 字典中
                            queue.append((nx, ny))
                            visited.add((nx, ny))

    return d


frame = []
with open('log.txt') as f:
    lines = f.readlines()
    for line in lines:
        if line[:5]!='frame':
            continue
        else:
            frame.append(int(line.split(':')[1]))

def get_skip_frame(frame):
    retval = []
    skip = 0
    for i in range(len(frame)-10):
        if frame[i]+1 != frame[i+1]:
            retval.append((frame[i],frame[i+1],"过了 "+str(frame[i+1]-frame[i])+ " 帧"))
            skip += (frame[i+1]-frame[i])
    return retval,skip
count = 0
points,skip = get_skip_frame(frame)
for r in points:
    count += 1
    print("跳帧点： "+str(r),end="\t")
    if count%5 == 0:
        print("\n")
print("\n")


print("跳帧点 有 "+str(len(points))+"处")
print("总共跳帧 "+str(skip)+" 帧")
        
        
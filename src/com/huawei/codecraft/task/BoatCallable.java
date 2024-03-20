package com.huawei.codecraft.task;

import com.huawei.codecraft.entities.Berth;
import com.huawei.codecraft.entities.Boat;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.wrapper.MapInfo;
import com.huawei.codecraft.wrapper.impl.MapInfoimpl;

import java.util.Optional;
import java.util.concurrent.Callable;

public class BoatCallable implements Callable {
    private Integer frame;
    private final Boat boat;
    private final MapInfo mapInfo;

    public BoatCallable(Boat boat, MapInfo mapInfo,Integer frame) {
        this.boat = boat;
        this.mapInfo = mapInfo;
        this.frame = frame;
    }
    @Override
    public Object call() throws Exception {

        synchronized (boat){
            if(boat.status()==0){
                //运输中
                return null;
            } else if (boat.status()==1) {

                if(boat.pos()==-1){
                    //船在虚拟点
                    Integer realBerth = mapInfo.getAvailableBerth();
                    Optional.ofNullable(realBerth)
                            .ifPresent(boat::ship);
                }
                else {

                    //船在泊位
                    try {
                        Berth berth = mapInfo.berths()[boat.pos()];
                        // 立刻释放泊位，虚拟点的船可以过来占用

                        if(berth.boat()==null){
                            berth.setBoat(boat);
                        }
                        // 装卸货
                        int min = Math.min(berth.loadingSpeed(), berth.amount());
                        berth.unload(min);
                        boat.load(berth.loadingSpeed());
                        if(boat.isFull()){
                            //船满了 再去虚拟点
                            if(boat.go()){
                                // 船成功出发去虚拟点，需让出berth
                                berth.setBoat(null);
                                mapInfo.setBerthFree(berth.id());
                            }

                        }else {
                            return null;
                        }
                    } catch (Exception e) {
                        System.err.println("Boat error");
                        System.err.flush();
                        e.printStackTrace();
                    }

                }
            }
        }

        return null;
    }
}

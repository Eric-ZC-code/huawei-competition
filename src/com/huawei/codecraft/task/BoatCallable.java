package com.huawei.codecraft.task;

import com.huawei.codecraft.entities.Boat;
import com.huawei.codecraft.util.MyLogger;
import com.huawei.codecraft.wrapper.MapInfo;

import java.util.Optional;
import java.util.concurrent.Callable;

public class BoatCallable implements Callable {
    private static final MyLogger logger= MyLogger.getLogger("BoatCallable");
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
            logger.info("BoatCallable call");

            if(boat.status()==0){
                //运输中
                return null;
            } else if (boat.status()==1) {
                if(boat.pos()==-1){
                    //船在 前往虚拟点
                    Integer berth = mapInfo.getAvailableBerth();
                    logger.info("Berth: "+berth);
                    Optional.ofNullable(berth)
                            .ifPresent(boat::ship);
                }
                else {
                    //船在货物点

                    if(boat.go()){
                        //船到达货物点
                        mapInfo.berths()[boat.pos()].release();
                    }
                }
            }
        }

        return null;
    }
}

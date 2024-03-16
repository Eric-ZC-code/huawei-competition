package com.huawei.codecraft.task;

import com.huawei.codecraft.entities.Berth;
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
            if(boat.status()==0){
                //运输中
                return null;
            } else if (boat.status()==1) {

                if(boat.pos()==-1){
                    //船在虚拟点
//                    Integer availableBerth = mapInfo.getAvailableBerth();
                    Integer realBerth = 2* boat.id();
                    logger.info("Berth:"+realBerth);
                    Optional.ofNullable(realBerth)
                            .ifPresent(boat::ship);
                }
                else {

                    //船在货物点

                    try {
                        Berth berth = mapInfo.berths()[boat.pos()/2];
                        if(berth.boat()==null){
                            berth.setBoat(boat);
                        }
//                        int min = Math.min(berth.loadingSpeed(), berth.amount());
//                        berth.unload(min);
                        boat.load(berth.loadingSpeed());
                        logger.info("Boat " +boat.id()+" amount : "+boat.goodsNum());
                        if(boat.isFull()){
                            //船满了 再去虚拟点
                            if(boat.go()){
                                // 船成功出发去虚拟点，需让出berth
                                berth.setBoat(null);
                            }

                        }else {
                            return null;
                        }
                    } catch (Exception e) {
                        System.err.println("Boat error");
                        System.err.flush();
                        e.printStackTrace();
                    } finally {
                    }

                }
            }
        }

        return null;
    }
}

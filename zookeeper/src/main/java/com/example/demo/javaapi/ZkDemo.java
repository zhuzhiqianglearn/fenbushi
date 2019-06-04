package com.example.demo.javaapi;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

class ZkDemo {

    public static ZooKeeper zooKeeper;
    public static CountDownLatch countDownLatch=new CountDownLatch(1);

    public static ZooKeeper getIntanse() throws IOException, InterruptedException {
        if(zooKeeper==null){
            synchronized (Object.class){
                if(zooKeeper==null){
                    zooKeeper=new ZooKeeper("192.168.126.130:2181,192.168.126.133:2181,192.168.126.134:2181", 5000, new Watcher() {
                        @Override
                        public void process(WatchedEvent watchedEvent) {
                            if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
                                countDownLatch.countDown();
                                if (watchedEvent.getType()== Event.EventType.NodeDataChanged){
                                    try {
                                        System.out.println("节点路径:"+watchedEvent.getPath()+"  修改的值是:"+new String(
                                                zooKeeper.getData(watchedEvent.getPath(),true,new Stat())
                                        ));
                                    } catch (KeeperException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }else if (watchedEvent.getType()== Event.EventType.NodeDeleted){
                                    try {
                                        System.out.println("节点路径:"+watchedEvent.getPath()+"  修改的值是:"+new String(
                                                "删除成功"
                                        ));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
        countDownLatch.await();
        return  zooKeeper;
    }
}

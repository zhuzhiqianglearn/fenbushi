package com.example.demo.javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CreateDemo implements Watcher {
    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
            System.out.println("------------"+watchedEvent.toString());

            if (watchedEvent.getType()== Event.EventType.NodeDataChanged){
                try {
                    System.out.println("节点路径:"+watchedEvent.getPath()+"  修改的值是:"+new String(
                            zk.getData(watchedEvent.getPath(),true,new Stat())
                    ));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
   private  static  ZooKeeper zk;
    public static void main(String[] args) throws Exception {
        zk = ZkDemo.getIntanse();
        zk.create("/javaapi3","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.getData("/javaapi3",new CreateDemo(),new Stat());
        zk.setData("/javaapi3","456".getBytes(),-1);
        zk.setData("/javaapi3","457".getBytes(),-1);
        zk.delete("/javaapi3",-1);

        TimeUnit.SECONDS.sleep(2000);
    }
}

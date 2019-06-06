package com.example.demo.curator;

import ch.qos.logback.core.util.TimeUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

public class CuratorDemo {


    public static void main(String[] args) throws Exception {
        // Curator客户端
         CuratorFramework client = null;
        RetryPolicy retryPolicy=new ExponentialBackoffRetry(100,5);
         //zk访问地址
        String zkServerIps = "localhost:2181";
        client= CuratorFrameworkFactory.builder()
                .connectString(zkServerIps)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();

        client.start();

        System.out.println("当前zk的状态是"+client.isStarted());
//        Thread.sleep(10000);

        //创建节点
        client.create().creatingParentsIfNeeded()
                       .withMode(CreateMode.PERSISTENT)
                       .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                       .forPath("/curatordemo/test3","123".getBytes());

        //修改节点
        client.setData().withVersion(-1).forPath("/curatordemo/test3","456".getBytes());

        //获取节点

        byte[] bytes = client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                        System.out.println("----------" + watchedEvent.getPath());
                    }
                }
            }
        }).forPath("/curatordemo/test3");
        byte[] bytes2 = client.getData().usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent watchedEvent) throws Exception {
                 if(watchedEvent.getState()== Watcher.Event.KeeperState.SyncConnected){
                     if(watchedEvent.getType()== Watcher.Event.EventType.NodeDataChanged){
                         System.out.println("+++++++++++"+watchedEvent.getPath());
                     }
                 }
            }
        }).forPath("/curatordemo/test3");
        System.out.println(new String(bytes2));

        //持久监听

        NodeCache nodeCache=new NodeCache(client,"/curatordemo/test3");
        nodeCache.start(true);

        if (nodeCache.getCurrentData() != null) {
            System.out.println("节点初始化数据为：" + new String(nodeCache.getCurrentData().getData()));
        } else {
            System.out.println("节点初始化数据为空...");
        }

        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                // 防止节点被删除时发生错误
                if (nodeCache.getCurrentData() == null) {
                    System.out.println("获取节点数据异常，无法获取当前缓存的节点数据，可能该节点已被删除");
                    return;
                }
                // 获取节点最新的数据
                String data = new String(nodeCache.getCurrentData().getData());
                System.out.println(nodeCache.getCurrentData().getPath() + " 节点的数据发生变化，最新的数据为：" + data);

            }
        });

        client.setData().withVersion(-1).forPath("/curatordemo/test3","4567".getBytes());
        client.setData().withVersion(-1).forPath("/curatordemo/test3","4568".getBytes());


        Thread.sleep(100000);
        client.close();
    }

}

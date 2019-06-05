package com.example.demo.javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreateDemoAcl implements Watcher {
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
        zk.addAuthInfo("digest","zhuzhiqiang:zhuzhiqiang".getBytes());
        ACL acl=new ACL(ZooDefs.Perms.READ,new Id("auth","zhuzhiqiang:zhuzhiqiang"));
        List<ACL> acls=new ArrayList<ACL>();
        acls.add(acl);
        zk.create("/javaapi4","123".getBytes(),acls , CreateMode.PERSISTENT);
        //用客户端的时候必须得添加账户才可以访问   addauth digest 用户名：密码
        TimeUnit.SECONDS.sleep(2000);
    }
}

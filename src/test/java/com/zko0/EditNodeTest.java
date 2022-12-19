package com.zko0;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class EditNodeTest {

    private CuratorFramework client;

    @Before
    //在create前执行
    public void connect(){
        //重试机制
        RetryPolicy retryPolic=new ExponentialBackoffRetry(3000,10);
        client = CuratorFrameworkFactory.builder()
                .connectString("101.43.244.40")
                .retryPolicy(retryPolic)
                //  zookeeper根目录为/zko0，不为/
                .namespace("zko0")
                .build();
        client.start();
    }

    @Test
    //修改节点数据内容
    public void edit() throws Exception {
        client.setData()
                .forPath("/test1","fuckU".getBytes());
    }

    @Test
    //乐观锁，如果edit的时候version有修改了不匹配，那么setData会失败
    public void editWithVersion() throws Exception {
        Stat stat = new Stat();
        client.getData()
                .storingStatIn(stat)
                .forPath("/test1");
        client.setData()
                .withVersion(stat.getVersion())
                .forPath("/test1","fuckMe".getBytes());
    }

    @After
    //在create执行后执行
    public void close(){
        if (client!=null){
            client.close();
        }
    }
}

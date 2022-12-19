package com.zko0;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 创建节点的测试类
 */
@Slf4j
public class CreateNodeTest {

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
    //1.基本创建
    //如果没有指定数据，那么会将客户端的ip作为存储的数据
    public void basicCreate() throws Exception {
        String path = client.create().forPath("/test1");
        log.info(path);
    }

    @Test
    //2.创建,同时添加数据
    public void createWithMessage() throws Exception {
        String path = client.create().forPath("/test2","fuck".getBytes());
        log.info(path);
    }

    @Test
    //3.创建临时节点
    //Client会话结束节点会消息，所以创建完就消失了
    public void createEphemeral() throws Exception {
        client.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/test3");
    }


    @Test
    //3.创建多级节点
    //creatingParentsIfNeeded如果需要创建多级节点
    public void create2() throws Exception {
        client.create()
                .creatingParentsIfNeeded()
                .forPath("/directory/test5");
    }

    @After
    //在create执行后执行
    public void close(){
        if (client!=null){
            client.close();
        }
    }
}

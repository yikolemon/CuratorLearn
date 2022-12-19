package com.zko0;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

/**
 * 创建client连接
 */
public class ConnectTest {

    @Test
    public void method1(){
        //重试机制
        RetryPolicy retryPolic=new ExponentialBackoffRetry(3000,10);
        //客户端连接建立
        CuratorFramework client = CuratorFrameworkFactory.newClient("101.43.244.40", retryPolic);
        //开启连接
        client.start();
    }

    @Test
    public void method2(){
        //重试机制
        RetryPolicy retryPolic=new ExponentialBackoffRetry(3000,10);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("101.43.244.40")
                .retryPolicy(retryPolic)
                //  zookeeper根目录为/zko0，不为/
                .namespace("zko0")
                .build();
        client.start();
    }
}

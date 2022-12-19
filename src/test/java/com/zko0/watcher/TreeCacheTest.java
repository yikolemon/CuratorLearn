package com.zko0.watcher;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TreeCacheTest {
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
    public void test() throws Exception {
        TreeCache cache = TreeCache.newBuilder(client, "/test3").setCacheData(true).build();
        cache.getListenable().addListener((c, event) -> {
            if ( event.getData() != null )
            {
                System.out.println("type=" + event.getType() + " path=" + event.getData().getPath());
            }
            else
            {
                System.out.println("type=" + event.getType());
            }
        });
        cache.start();
        while (true){}
    }

    @After
    //在create执行后执行
    public void close(){
        if (client!=null){
            client.close();
        }
    }
}

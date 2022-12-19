package com.zko0.watcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class PathChildrenCacheTest {
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
    /**
     * PathChildrenCache已经被标记为废弃方法
     * 使用curator5.5官网example推荐写法
     */
    public void method1() throws Exception {
        //true表示是否缓存data信息
        PathChildrenCache cache = new PathChildrenCache(client, "/test2", true);
        PathChildrenCacheListener listener = new PathChildrenCacheListener(){
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                switch ( event.getType() )
                {
                    case CHILD_ADDED:
                    {
                        System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    }

                    case CHILD_UPDATED:
                    {
                        System.out.println("Node changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    }

                    case CHILD_REMOVED:
                    {
                        System.out.println("Node removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    }
                }

            }
        };
        cache.getListenable().addListener(listener);
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

package com.zko0.example;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorWatchTest {

    private CuratorFramework client;

    /**
     * @author Wei
     * @date 2021/7/23 10:34
     * @description 建立连接
     **/
    @Test
    @Before
    public void testConnect() {
        // 重试次数
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        // 第一种方式
        // CuratorFramework client = CuratorFrameworkFactory.newClient(
        //         "localhost:2181",
        //         60 * 1000,
        //         15 * 1000,
        //         retryPolicy
        // );
        // 第二种方式
        this.client = CuratorFrameworkFactory.builder()
                .connectString("101.43.244.40:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolicy)
                .namespace("zko0")
                .build();

        // 开启连接
        this.client.start();
    }

    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * @author Wei
     * @date 2021/7/23 17:40
     * @description 可以监听当前节点和子节点（子节点的子节点）的创建、更新、删除
     **/
    @Test
    public void testNodeCache() throws Exception {
        CuratorCache curatorCache = CuratorCache.build(client, "/app1");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                // 第一个参数：事件类型（枚举）
                // 第二个参数：节点更新前的状态、数据
                // 第三个参数：节点更新后的状态、数据
                // 创建节点时：节点刚被创建，不存在 更新前节点 ，所以第二个参数为 null
                // 删除节点时：节点被删除，不存在 更新后节点 ，所以第三个参数为 null
                // 节点创建时没有赋予值 create /curator/app1 只创建节点，在这种情况下，更新前节点的 data 为 null，获取不到更新前节点的数据
                switch (type.name()) {
                    case "NODE_CREATED": // 监听器第一次执行时节点存在也会触发次事件
                        if (childData != null) {
                            System.out.println("创建了节点: " + childData1.getPath());
                        }
                        break;
                    case "NODE_CHANGED": // 节点更新
                        if (childData.getData() != null) {
                            System.out.println("修改前的数据: " + new String(childData.getData()));
                        } else {
                            System.out.println("节点第一次赋值!");
                        }
                        System.out.println("修改后的数据: " + new String(childData1.getData()));
                        break;
                    case "NODE_DELETED": // 节点删除
                        System.out.println(childData.getPath() + " 节点已删除");
                        break;
                    default:
                        break;
                }
            }
        });

        // 开启监听
        curatorCache.start();
        // 延迟 60 秒结束
        Thread.sleep(60000);
    }
}
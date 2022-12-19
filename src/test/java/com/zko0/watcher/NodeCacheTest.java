package com.zko0.watcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.CuratorCacheListenerBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class NodeCacheTest {

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
     * 写法1.通过addListener将new CuratorCacheListener() 加入，对于create，update，delete等事件
     * 都能进行监听
     */
    public void testNodeCache() throws Exception {
        CuratorCache curatorCache = CuratorCache.build(client, "/test1");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData beforeData, ChildData afterData) {
                // 第一个参数：事件类型（枚举）
                // 第二个参数：节点更新前的状态、数据
                // 第三个参数：节点更新后的状态、数据
                // 创建节点时：节点刚被创建，不存在 更新前节点 ，所以第二个参数为 null
                // 删除节点时：节点被删除，不存在 更新后节点 ，所以第三个参数为 null
                // 节点创建时没有赋予值 create /curator/app1 只创建节点，在这种情况下，更新前节点的 data 为 null，获取不到更新前节点的数据
                switch (type.name()) {
                    case "NODE_CREATED": // 监听器第一次执行时节点存在也会触发次事件
                        if (afterData != null) {
                            System.out.println("创建了节点: " + afterData.getPath());
                        }
                        break;
                    case "NODE_CHANGED": // 节点更新
                        if (beforeData.getData() != null) {
                            System.out.println("修改前的数据: " + new String(beforeData.getData()));
                        } else {
                            System.out.println("节点第一次赋值!");
                        }
                        System.out.println("修改后的数据: " + new String(afterData.getData()));
                        break;
                    case "NODE_DELETED": // 节点删除
                        System.out.println(beforeData.getPath() + " 节点已删除");
                        break;
                    default:
                        break;
                }
            }
        });

        // 开启监听
        curatorCache.start();
        // 线程阻塞防止停止
        while (true){}
    }


    @Test
    /**
     * 方法2，对单个的事件进行监听,来源与curator官网example写法
     */
    public void test2(){
        CuratorCache cache = CuratorCache.build(client,"/test1");
        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forCreates(node -> System.out.println(String.format("Node created: [%s]", node)))
                .forChanges((oldNode, node) -> System.out.println(String.format("Node changed. Old: [%s] New: [%s]", oldNode, node)))
                .forDeletes(oldNode -> System.out.println(String.format("Node deleted. Old value: [%s]", oldNode)))
                .forInitialized(() -> System.out.println("Cache initialized"))
                .build();
        // register the listener
        cache.listenable().addListener(listener);

        // the cache must be started
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

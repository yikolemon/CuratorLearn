package com.zko0;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class DeleteNodeTest {
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
    //删除单个节点
    public void deleteOne() throws Exception {
        client.delete()
                .forPath("/test1");
    }

    @Test
    //删除带有子节点的节点
    public void deleteOnehasChildren() throws Exception {
        client.delete()
                .deletingChildrenIfNeeded()
                .forPath("/directory");
    }

    //必须成功的删除,如果失败会反复重试
    @Test
    public void deleteMustSucc() throws Exception {
        client.delete()
                .guaranteed()//必须的
                .forPath("/test2");
    }

    @Test
    //删除回调
    public void callBack() throws Exception {
        client.delete()
                .guaranteed()//一般都会加上
                .inBackground((client,event)->{
                    //client和evnet都可以进行操作
                    log.info("删除Ok");
                })
                .forPath("/test1");
    }

    @After
    //在create执行后执行
    public void close(){
        if (client!=null){
            client.close();
        }
    }
}

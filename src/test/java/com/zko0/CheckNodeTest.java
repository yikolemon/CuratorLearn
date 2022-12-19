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

import java.util.List;

@Slf4j
public class CheckNodeTest {

    private CuratorFramework client;

    @Before
    //在create前执行
    public void connect(){
        //重试机制

    }

    @Test
    //get方法
    public void get() throws Exception {
        byte[] bytes = client.getData().forPath("/test1");
        log.info(new String(bytes));
    }


    @Test
    //ls方法
    public void getChildren() throws Exception {
        List<String> list = client.getChildren().forPath("/directory");
        log.info(list.toString());
    }


    @Test
    //查询节点信息
    //把状态信息保存在Stat对象中 storingStatIn(xx)
    public void getAbout() throws Exception {
        Stat stat = new Stat();
        client.getData()
                .storingStatIn(stat)
                .forPath("/test1");
        log.info(stat.toString());
    }

    @After
    //在create执行后执行
    public void close(){
        if (client!=null){
            client.close();
        }
    }
}

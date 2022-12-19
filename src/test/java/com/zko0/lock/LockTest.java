package com.zko0.lock;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class LockTest implements Runnable{
    public LockTest() {
        //创建client
        RetryPolicy retryPolic=new ExponentialBackoffRetry(3000,10);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("101.43.244.40")
                .retryPolicy(retryPolic)
                //  zookeeper根目录为/zko0，不为/
                .namespace("zko0")
                .build();
        client.start();
        this.lock=new InterProcessMutex(client,"/lock");
    }

    public static void main(String[] args) {
        LockTest lockTest = new LockTest();
        Thread t1=new Thread(lockTest,"test1");
        Thread t2=new Thread(lockTest,"test2");
        t1.start();
        t2.start();

    }

    private Integer num=10;//对该变量做锁

    private InterProcessLock lock;

    @Override
    public void run() {
        while (true){
            //acquire为等待时间
            try {
                lock.acquire(3, TimeUnit.SECONDS);
                if (num>0){
                    System.out.println(Thread.currentThread()+""+num);
                    num--;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally {
                try {
                    lock.release();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

package server;

import others.RegisterCenter;
import server.MyNettyServer;
import server.RpcServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServerImpl implements RpcServer {
    private int port = 9999;
    private int nThreads = 1;
    private RegisterCenter registerCenter;
    private MyNettyServer myNettyServer;

    private final static ExecutorService executor = Executors.newSingleThreadExecutor();


    @Override
    public void start() {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                myNettyServer = new MyNettyServer(port, nThreads);
                myNettyServer.start();
            }
        });
    }

    @Override
    public void stop() {
        myNettyServer.stop();
    }

    @Override
    public void register(String funcName, Class clazz, String ip) {
        registerCenter.addService(funcName, clazz, ip);
    }

    public RpcServerImpl(int port, int nThreads, String zookeeperHost) {
        this.port = port;
        this.nThreads = nThreads;
        registerCenter = new RegisterCenter(zookeeperHost);
        start();
    }


}

package wangh;

import server.RpcServer;
import server.RpcServerImpl;

public class ServerApp {

    public static void main(String[] args) {

        RpcServer rpcServer = new RpcServerImpl(9999, 1, "127.0.0.1:2181");

        rpcServer.register(AddService.class.getName(), AddServiceImpl.class, "127.0.0.1:9999");

        rpcServer.start();

    }
}

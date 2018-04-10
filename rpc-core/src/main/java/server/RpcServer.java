package server;

public interface RpcServer {

    void start();

    void stop();

    void register(String funcName, Class clazz, String ip);
}

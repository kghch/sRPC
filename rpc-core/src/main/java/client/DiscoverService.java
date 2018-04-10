package client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoverService {
    private static String HOST="127.0.0.1:2181";
    private static Map<String, List<InetSocketAddress>> sevices = new ConcurrentHashMap<>();

    public static void setHost(String host) {
        HOST = host;
    }

    public static String discoverServices(String serviceName) {
        List<InetSocketAddress> results = sevices.get(serviceName);
        System.out.println("--serviceName:"+serviceName);
        if (results != null) {
            return null;
        }

        ZooKeeper zookeeper = null;
        try {
            zookeeper = new ZooKeeper(HOST, 10000, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            byte[] res = zookeeper.getData("/rpc/" + serviceName, true, null);
            if (res == null) {
                System.err.println(serviceName + "服务没有发现...");
            }
            String resIp = new String(res);
            System.out.println("发现服务。服务地址为： " + resIp + "，服务名称为：" + serviceName);

            return resIp;

        } catch (KeeperException e) {
            System.err.println(serviceName + " 服务没有发现...");
        } catch (InterruptedException e) {
            System.err.println(serviceName + " 服务没有发现...");
        } finally {
            try {
                zookeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

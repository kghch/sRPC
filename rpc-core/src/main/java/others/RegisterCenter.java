package others;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.ConcurrentHashMap;

public class RegisterCenter {

    private static ConcurrentHashMap<String, Class> registerServices = new ConcurrentHashMap<String, Class>();
    private static String host;
    private static ZooKeeper zooKeeper;

    public RegisterCenter(String zookeeperHost) {
        init(zookeeperHost);
    }

    private void init(String zookeeperHost) {

        host = zookeeperHost;
        try {
            zooKeeper = new ZooKeeper(host, 10000, null);
        } catch (Exception e) {
            System.out.println("Zookeeper 启动失败。");
        }
    }


    public static void addService(String funcName, Class clazz, String ip) {
        registerServices.put(funcName, clazz);
        try {
            String path = "/rpc/" + funcName;
            if (zooKeeper.exists("/rpc", null) == null) {
                zooKeeper.create("/rpc", "true".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            if (zooKeeper.exists(path, null) == null) {
                zooKeeper.create(path, "true".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            //byte[] ipsByte = zooKeeper.getData(path, false, null);
            //String ips = new String(ipsByte);
            //ips += ";" + ip;
            zooKeeper.setData(path, ip.getBytes(), -1);
            System.out.println("服务注册成功。服务地址为：" + ip +", 服务名称为：" +funcName) ;


        } catch (Exception e) {
            System.out.println("服务注册失败");
        }

    }

    public static Class getService(String funcName) {
        return registerServices.get(funcName);
    }

}

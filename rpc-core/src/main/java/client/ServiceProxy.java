package client;

import java.lang.reflect.Proxy;

public class ServiceProxy {


    public static <T> T newServiceProxy(final Class<?> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new ProxyHandler(service));
    }

    public static void setZookeeperHost(String host) {
        DiscoverService.setHost(host);
    }

}

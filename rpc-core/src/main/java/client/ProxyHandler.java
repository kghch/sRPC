package client;

import common.Request;
import common.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;


public class ProxyHandler implements InvocationHandler {

    private Class<?> service;

    public ProxyHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object object, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setServiceName(service.getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setArgs(args);


        String ips =DiscoverService.discoverServices(request.getServiceName());
        System.out.println("ips: " + ips);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ips.split(":")[0], Integer.valueOf(ips.split(":")[1]));

        Response resp = (Response) MyNettyClient2.send(request, inetSocketAddress);

        return resp.getResult();



    }
}

package wangh;

import client.ServiceProxy;

public class ClientApp {

    public static void main(String[] args) {
        ServiceProxy.setZookeeperHost("127.0.0.1:2181");


        AddService addService = ServiceProxy.newServiceProxy(AddService.class);
        try {
            int c = addService.add(999, 888);
            System.out.println("计算结果是：" + c);
        } catch (Exception e){
            System.out.println(e);
        }

    }


}

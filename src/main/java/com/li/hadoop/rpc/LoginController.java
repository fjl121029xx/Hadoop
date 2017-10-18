package com.li.hadoop.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtocolProxy;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;
import java.net.InetSocketAddress;

public class LoginController {

    public static void main(String[] args) throws IOException {
        LoginService proxy = RPC.getProxy(LoginService.class, 1L, new InetSocketAddress("10.0.1.3", 10000), new Configuration());

        String tom = proxy.dologin("emo", "123");
        System.out.println(tom);
    }
}
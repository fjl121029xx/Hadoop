package com.li.hadoop.rpc;

import com.li.hadoop.rpc.impl.LoginServiceImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;

public class RpcServer {

    public static void main(String[] args) throws IOException {

        RPC.Builder builder = new RPC.Builder(new Configuration());

        builder.setBindAddress("10.0.1.3")
                .setPort(10000)
                .setProtocol(LoginService.class)
                .setInstance(new LoginServiceImpl()) ;

        RPC.Server server = builder.build();

        server.start();

    }
}

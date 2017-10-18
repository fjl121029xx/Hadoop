package com.li.hadoop.rpc;

public interface LoginService {

    public static final long versionID = 1L;

    String dologin(String username,String pwd);
}

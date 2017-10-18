package com.li.hadoop.rpc.impl;

import com.li.hadoop.rpc.LoginService;

public class LoginServiceImpl implements LoginService{
    @Override
    public String dologin(String username, String pwd) {

        return  username + " logged in successfully!";

    }
}

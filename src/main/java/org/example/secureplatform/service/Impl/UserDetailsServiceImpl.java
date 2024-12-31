package org.example.secureplatform.service.Impl;

import org.example.secureplatform.common.util.InMemoryUserStorage;
import org.example.secureplatform.entity.MyTUserDetail;
import org.example.secureplatform.entity.User;
import org.example.secureplatform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = InMemoryUserStorage.getUserPassword(username);
        MyTUserDetail myTUserDetail=new MyTUserDetail();
        myTUserDetail.setUser(user);
        return myTUserDetail;
    }
}
package org.example.secureplatform.common.util;

import org.example.secureplatform.entity.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserStorage {
    private static final Map<String, User> userStore = new HashMap<>();
    // 添加用户

    public static void addUser(String username, String password) {
        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setPassword(encryptedPassword);
        user.setUsername(username);
        user.setStatus(0);
        userStore.put(username, user);
    }

    public static User getUserPassword(String username) {
        User user = userStore.get(username);
        return userStore.get(username);
    }

}

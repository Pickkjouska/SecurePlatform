package org.example.secureplatform.common;

import org.example.secureplatform.common.util.InMemoryUserStorage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class CommandLineStartupRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // 控制台输入用户名和密码
        System.out.println("username:");
        String username = scanner.nextLine();

        System.out.println("password:");
        String password = scanner.nextLine();

        System.out.println("用户信息已保存！");

        InMemoryUserStorage.addUser(username, password);
    }
}

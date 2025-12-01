package edu.sustech.xiangqi;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class InterChecking {
    public static int enteruser(String importUserName){//先扫描对应的用户名
        String RoadN=".\\src\\main\\java\\edu\\sustech\\xiangqi\\Nickname";
        int X=0;
        try{
            Scanner fnscanner=new Scanner(new File(RoadN));

            while(fnscanner.hasNextLine()) {

                String findingname=fnscanner.nextLine();

                // 修正2：像在 rightname 中一样，跳过空行，并对文件内容进行 trim()
                String trimmedName = findingname.trim();
                if (trimmedName.isEmpty()) {
                    // 如果是空行或纯空格行，跳过，不增加 X (行号/索引)
                    continue;
                }
                // 修正3：用户的输入 A0 也应该检查是否为空或纯空格（防止用户输入纯空格登录）
                if (importUserName.trim().isEmpty()) {
                    return -1; // 账号名无效
                }

                // 修正4：比对时使用 trimmedName
                if(Objects.equals(importUserName, trimmedName)){
                    return X; // 找到昵称，返回行号
                }
                else{
                    X++; // 只有比对有效的昵称时才增加行号/索引
                }
            }
            return -1; // 遍历完未找到

        } catch (FileNotFoundException e) {
            System.out.println("找不到该文件");
            return -2;
        }

    }


    public static boolean enterpassword(String importUserPassword,int enteruserIndex){//判断对应的密码是否正确
        if(enteruserIndex<0){
            return false;
        }
        else{
            try {
                String RoadP = ".\\src\\main\\java\\edu\\sustech\\xiangqi\\Password";
                Scanner fpscanner = new Scanner(new File(RoadP));
                int lineCount = 0; // 用于跟踪实际密码行

                while(fpscanner.hasNextLine()) {
                    String findingpassword = fpscanner.nextLine();
                    String trimmedPassword = findingpassword.trim(); // 去除空白

                    if (trimmedPassword.isEmpty()) {
                        continue; // 跳过空白行
                    }

                    // 只有有效密码行才进行比对
                    if (lineCount == enteruserIndex) {
                        if (Objects.equals(trimmedPassword, importUserPassword)) {
                            return true; // 密码匹配
                        } else {
                            return false; // 密码不匹配
                        }
                    }

                    lineCount++; // 只有读取到有效密码行才增加计数
                }

                return false; // 如果循环结束还没找到对应索引的密码，返回 false

            } catch (FileNotFoundException e) {
                System.out.println("找不到文件！！！");
                return true;
            }

        }
    }

    //修改密码的方法
    public static boolean updatePasswordInFile(int targetIndex, String newPassword) {
        String roadP = ".\\src\\main\\java\\edu\\sustech\\xiangqi\\Password";
        File file = new File(roadP);
        List<String> lines = new ArrayList<>();

        // 1. 读取所有行到内存
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // 2. 找到对应的逻辑行（跳过空行）并修改
        int logicalCounter = 0; // 对应 enteruser 返回的索引
        boolean found = false;

        for (int i = 0; i < lines.size(); i++) {
            String currentLine = lines.get(i);

            // 如果是空行，根据您的逻辑直接保留，不计入 index
            if (currentLine.trim().isEmpty()) {
                continue;
            }

            // 如果是有效行，判断是否是我们要改的那一行
            if (logicalCounter == targetIndex) {
                lines.set(i, newPassword); // 修改列表中的密码
                found = true;
                break; // 修改完就退出循环
            }

            logicalCounter++;
        }

        if (!found) {
            return false; // 没找到对应的行（理论上不应该发生，除非并发修改）
        }

        // 3. 将修改后的内容写回文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // 保持原来的换行格式
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}

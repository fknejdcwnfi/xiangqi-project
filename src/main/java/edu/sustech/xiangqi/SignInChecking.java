package edu.sustech.xiangqi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class SignInChecking {
    public static boolean rightname(String importNickName){
        if(Objects.equals(importNickName,"") || Objects.equals(importNickName,"null")){return false;}//I have change this for no null Username!
        else if(importNickName.length()>=15){return false;}
        else {
            try {
                Scanner nicknamescanner = new Scanner(new File(".\\src\\main\\java\\edu\\sustech\\xiangqi\\Nickname"));
                if(!nicknamescanner.hasNextLine()){return true;}
                while(nicknamescanner.hasNextLine()){
                    String remainnickname=nicknamescanner.nextLine();

                    // 确保文件中的行不是空行或纯空格行///////////////////
                    String trimmedNickname = remainnickname.trim();
                    if (trimmedNickname.isEmpty()) {
                        // 如果文件行是纯空格或空行，跳过这一行，不参与比对
                        continue;
                    }///////////////////////////////////////////////

                    if(Objects.equals(importNickName,trimmedNickname))
                    {return false;}
                    //  else if(Objects.equals(remainnickname,"")){return true;}
                }
                return true;
            } catch (FileNotFoundException e) {
                System.out.println("未找到相应文件！！！");
                return false;
            }

        }
    }

    public static boolean passworkcheck(String importSetPassword){
        if(importSetPassword.length()<8){return false;}
        else{return true;}
    }
}
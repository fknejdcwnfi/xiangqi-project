package edu.sustech.xiangqi.ui;

import javax.sound.sampled.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AudioPlayer {
    // 1. 普通音效：防止同时播放
    private static final Map<String, Clip> playingClips = new HashMap<>();
    // 2. 循环音效：全局单例（key=音效路径，value=循环播放的Clip）
    private static final Map<String, Clip> loopingClips = new HashMap<>();

    public static void playSound(String soundPath) {
        // 新增：检查文件是否存在
        // 先检查该音效是否正在播放，若正在播放则直接返回
        File soundFile = new File(soundPath);
        if (playingClips.containsKey(soundPath) && playingClips.get(soundPath).isRunning()) {
            return;
        }

        if (!soundFile.exists()) {
            System.out.println("错误：音效文件不存在 → " + soundFile.getAbsolutePath());
            return;
        }


        new Thread(() -> {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(audioStream);
                System.out.println("开始播放音效"); // 新增：确认播放开始
                clip.start();

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        System.out.println("音效播放完毕"); // 新增：确认播放结束
                        clip.close();
                        try {
                            audioStream.close();
                        } catch (IOException e) {
                            System.out.println("无相关文件");
                        }
                    }
                });
            } catch (UnsupportedAudioFileException e) {
                System.out.println("错误：音频格式不支持 → " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("错误：读取文件失败 → " + e.getMessage());
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                System.out.println("错误：音频设备不可用 → " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("未知错误：" + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    // 循环音效播放（全局单例，避免叠加）
    public static void playLoopingSound(String soundPath) {
        // 若已存在循环实例且正在播放，直接返回（避免二重奏）
        if (loopingClips.containsKey(soundPath) && loopingClips.get(soundPath).isRunning()) {
            return;
        }

        new Thread(() -> {
            try {
                File soundFile = new File(soundPath);
                if (!soundFile.exists()) {
                    System.out.println("音效文件不存在：" + soundPath);
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(audioStream);

                // 先停止旧的循环实例（如果有）
                if (loopingClips.containsKey(soundPath)) {
                    Clip oldClip = loopingClips.get(soundPath);
                    oldClip.stop();
                    oldClip.close();
                }
                // 保存新的循环实例
                loopingClips.put(soundPath, clip);

                // 设置循环播放（LOOP_CONTINUOUSLY = 无限循环）
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();

            } catch (Exception e) {
                e.printStackTrace();
                loopingClips.remove(soundPath);
            }
        }).start();
    }
    // 停止指定循环音效
    public static void stopLoopingSound(String soundPath) {
        if (loopingClips.containsKey(soundPath)) {
            Clip clip = loopingClips.get(soundPath);
            clip.stop();
            clip.close();
            loopingClips.remove(soundPath);
        }
    }

    // 停止所有循环音效（比如游戏获胜时）
    public static void stopAllLoopingSounds() {
        for (Clip clip : loopingClips.values()) {
            clip.stop();
            clip.close();
        }
        loopingClips.clear();
    }


}
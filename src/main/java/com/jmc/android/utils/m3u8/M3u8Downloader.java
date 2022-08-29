package com.jmc.android.utils.m3u8;

import com.jmc.io.*;
import com.jmc.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public class M3u8Downloader {
    public static void download(String m3u8Url, String fileStoragePath) {
        System.out.println("\n结果将输出到" + fileStoragePath + "\n");
        Tries.tryThis(() -> {
            var resFile = new File(fileStoragePath);
            if (resFile.exists()) {
                throw new RuntimeException("目标文件已存在！");
            }

            var m3u8Content = new String(new URL(m3u8Url).openStream().readAllBytes()).split("\n");
            var tsUrls = Arrays.stream(m3u8Content)
                    .filter(t -> !t.startsWith("#"))
                    .map(p -> p.contains("://") ? p : m3u8Url.substring(0, m3u8Url.lastIndexOf('/') + 1) + p)
                    .toList();

            var tmpPath = fileStoragePath.substring(0, fileStoragePath.lastIndexOf('/') + 1)
                    + "tmp_" + System.currentTimeMillis() + "/";

            System.out.println("正在下载...");
            var downloadCount = new AtomicInteger(tsUrls.size());

            var tsPaths = tsUrls.parallelStream()
                    .map(url -> new Thread(() -> {
                        var tsTmpPath = tmpPath + url.substring(url.lastIndexOf('/') + 1);
                        Tries.tryThis(() -> Files.out(new URL(url).openStream().readAllBytes(), tsTmpPath));
                        System.out.println("剩余" + downloadCount.decrementAndGet() + "个");
                    }, tmpPath + url.substring(url.lastIndexOf('/'))))
                    .peek(Thread::start)
                    .peek(Tries.throwsE(Thread::join))
                    .map(Thread::getName)
                    .sorted(Comparator.comparingInt(tsUrl -> {
                        for (var prefix : List.of("index", "out")) {
                            String idxStr;
                            if (Strs.isNum((idxStr = Strs.subExclusive(tsUrl, prefix, ".ts")))) {
                                return Integer.parseInt(idxStr);
                            }
                        }
                        throw new RuntimeException("Index extraction error: " + tsUrl);
                    }))
                    .toList();


            int count = tsPaths.size();
            for (var path : tsPaths) {
                System.out.print("正在合并：" + path.substring(path.lastIndexOf("/") + 1));
                System.out.println("，剩余" + --count + "个");

                Files.out(Files.readToBytes(path), fileStoragePath, true);
            }

            Files.delete(tmpPath);
            System.out.println("已完成！");
        });
    }
}

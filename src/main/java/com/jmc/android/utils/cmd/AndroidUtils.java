package com.jmc.android.utils.cmd;

import com.jmc.android.utils.m3u8.M3u8Downloader;
import com.jmc.io.Files;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class AndroidUtils {
    @ShellMethod(key = "renamesByTime", value = "Rename files by time.")
    public void renamesByTime(@ShellOption(help = "Directory root path.") String dirPath,
                              @ShellOption(help = "The suffix of target files (includes .).") String suffix) {
        Files.renameByTime(dirPath, suffix);
    }

    @ShellMethod(key = {"ntree", "normalTree"}, value = "Show normalTree of a path.")
    public String normalTree(@ShellOption(help = "Directory path.") String path) {
        return Files.normalTree(path).toString();
    }

    @ShellMethod(key = {"stree", "singleTree"}, value = "Show normalTree of a path.")
    public String singleTree(@ShellOption(help = "Directory path.") String path) {
        return Files.singleTree(path).toString();
    }

    @ShellMethod(key = "fileInfo", value = "Show file info of a path.")
    public String fileInfo(@ShellOption(help = "Directory path.") String path) {
        return Files.fileInfo(path);
    }

    @ShellMethod(key = "m3u8", value = "Download m3u8 video from a url.")
    public void fileInfo(@ShellOption(help = "M3u8 url.") String m3u8Url,
                         @ShellOption(help = "Target file path.") String targetFilePath) {
        M3u8Downloader.download(m3u8Url, targetFilePath);
    }
}

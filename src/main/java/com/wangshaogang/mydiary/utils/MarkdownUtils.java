package com.wangshaogang.mydiary.utils;

import com.youbenzi.mdtool.tool.MDTool;

import java.io.File;
import java.io.IOException;

public class MarkdownUtils {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/test.md");
        String s = MDTool.markdown2Html(file, "gbk");

        System.out.println(s);

    }
}

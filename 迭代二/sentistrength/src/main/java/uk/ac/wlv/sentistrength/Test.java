// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   Test.java

package uk.ac.wlv.sentistrength;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Test类，test字符串是否为纯ASCII编码
 * @author ruohao.zhang
 **/
public class Test {

    /**
     * Test构造器
     * @author ruohao.zhang
     */
    public Test() {
    }

    /**
     * Test类的main函数，对字符串进行ASCII编码的测试
     * asciiEncoder.canEncode()方法测试字符串是否为纯ASCII编码
     * 如果字符串不是纯ASCII编码，程序将打印出所有非ASCII字符的值。
     * 最后进行URL编码，并将其打印到控制台上。
     * @param args 命令行参数
     * @author ruohao.zhang
     */
    public static void main(String[] args) {
        CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
        String test = "R\351al";
        System.err.println((new StringBuilder(String.valueOf(test))).append(" isPureAscii() : ")
                .append(asciiEncoder.canEncode(test)).toString());
        for (int i = 0; i < test.length(); i++) {
            if (!asciiEncoder.canEncode(test.charAt(i))) {
                System.err.println((new StringBuilder(String.valueOf(test.charAt(i)))).append(" isn't Ascii() : ").toString());
            }
        }
        test = "Real";
        System.err.println((new StringBuilder(String.valueOf(test))).append(" isPureAscii() : ")
                .append(asciiEncoder.canEncode(test)).toString());
        test = "a\u2665c";
        System.err.println((new StringBuilder(String.valueOf(test))).append(" isPureAscii() : ")
                .append(asciiEncoder.canEncode(test)).toString());
        for (int i = 0; i < test.length(); i++) {
            if (!asciiEncoder.canEncode(test.charAt(i))) {
                System.err.println((new StringBuilder(String.valueOf(test.charAt(i)))).append(" isn't Ascii() : ").toString());
            }
        }
        System.err.println((new StringBuilder("Encoded Word = ")).append(URLEncoder.encode(test)).toString());
    }
}

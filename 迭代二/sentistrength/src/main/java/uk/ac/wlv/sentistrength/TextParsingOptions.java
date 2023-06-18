// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   TextParsingOptions.java

package uk.ac.wlv.sentistrength;

/**
 * UC-11
 * 文本转化选项类，用来存取与文本相关的选项
 * @author ruohao.zhang
 **/
public class TextParsingOptions {
    /**
     * 一个布尔值，表示是否包括标点符号。
     **/
    public boolean bgIncludePunctuation;
    /**
     * 一个整数，表示N-Gram模型中的N值。
     **/
    public int igNgramSize;
    /**
     * 一个布尔值，表示是否使用翻译。
     **/
    public boolean bgUseTranslations;
    /**
     * 一个布尔值，表示是否使用强调代码。
     **/
    public boolean bgAddEmphasisCode;

    /**
     * TextParsingOptions（文本转化选项）的构造器
     * 分别有包括分隔，N-Gram模型中的N值，使用翻译，强调代码
     */
    public TextParsingOptions() {
        bgIncludePunctuation = true;
        igNgramSize = 1;
        bgUseTranslations = true;
        bgAddEmphasisCode = false;
    }
}

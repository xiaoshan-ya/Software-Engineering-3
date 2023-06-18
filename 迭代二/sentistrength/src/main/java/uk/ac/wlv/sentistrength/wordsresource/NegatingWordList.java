// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   NegatingWordList.java

package uk.ac.wlv.sentistrength.wordsresource;

import java.io.*;


import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

/**
 * 该类表示否定词列表
 * UC5
 *
 */
public class NegatingWordList implements WordResource {
    /**
     * 否定词数组
     */
    private String[] sgNegatingWord;

    /**
     * 列表中否定词数量
     */
    private int igNegatingWordCount;

    /**
     * 列表中能存最大否定词容量
     */
    private int igNegatingWordMax;

    /**
     * 构造一个否定词列表
     */
    public NegatingWordList() {
        igNegatingWordCount = 0;
        igNegatingWordMax = 0;
    }

    /**
     * UC5
     * 使用输入的文件名、分类选项，初始化否定词列表
     * @param sFilename 文件名
     * @param options 一个ClassificationOptions类型的分类选项
     * @return 初始化成功返回true，反之返回false
     */
    public boolean initialise(String sFilename, ClassificationOptions options) {
        boolean flag = true;
        if (igNegatingWordMax > 0) {
            flag = true;
        }
        File f = new File(sFilename);
        if (!f.exists()) {
            System.err.println((new StringBuilder("Could not find the negating words file: ")).append(sFilename).toString());
            flag = false;
        }
        igNegatingWordMax = FileOps.i_CountLinesInTextFile(sFilename) + 2;
        sgNegatingWord = new String[igNegatingWordMax];
        igNegatingWordCount = 0;
        try {
            BufferedReader rReader;
            if (options.bgForceUTF8) {
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFilename), "UTF8"));
            } else {
                rReader = new BufferedReader(new FileReader(sFilename));
            }
            String sLine;
            while ((sLine = rReader.readLine()) != null) {
                if (!"".equals(sLine)) {
                    igNegatingWordCount++;
                    sgNegatingWord[igNegatingWordCount] = sLine;
                }
            }
            rReader.close();
            Sort.quickSortStrings(sgNegatingWord, 1, igNegatingWordCount);
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Could not find negating words file: ")).append(sFilename).toString());
            e.printStackTrace();
            flag = false;
        } catch (IOException e) {
            System.err.println((new StringBuilder("Found negating words file but could not read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 判断输入词是否为否定词
     * @param sWord 要判断的单词
     * @return 是否定词则返回true，反之返回false
     */
    public boolean negatingWord(String sWord) {
        return Sort.i_FindStringPositionInSortedArray(sWord, sgNegatingWord, 1, igNegatingWordCount) >= 0;
    }
}

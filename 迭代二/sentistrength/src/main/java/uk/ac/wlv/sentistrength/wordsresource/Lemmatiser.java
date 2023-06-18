// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   Lemmatiser.java

package uk.ac.wlv.sentistrength.wordsresource;

import java.io.*;


import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

/**
 * 将单词换原为其原形的工具类
 * @author DaiXuezheng
 */
public class Lemmatiser {

    /** 单词数组 **/
    private String[] sgWord;
    /** 单词对应的原形的数组 **/
    private String[] sgLemma;
    /** 存储数组中最后一个单词的位置 **/
    private int igWordLast;

    /**
     * 构造函数
     */
    public Lemmatiser() {
        igWordLast = -1;
    }

    /**
     * 从文件中读取单词和它们对应的原形，并将它们存储在sgWord和sgLemma数组中。
     * @param sFileName 文件名
     * @param bForceUTF8 是否为UTF8编码
     * @return 成功读取文件返回 true，否则返回false
     * @author DaiXuezheng
     */
    public boolean initialise(String sFileName, boolean bForceUTF8) {
        int iLinesInFile = 0;
        if (!check(sFileName, bForceUTF8)) {
            return false;
        }
        sgWord = new String[iLinesInFile + 1]; sgLemma = new String[iLinesInFile + 1];
        igWordLast = -1;
        boolean pass = true;
        try {
            BufferedReader rReader;
            if (bForceUTF8) {
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFileName), "UTF8"));
            } else {
                rReader = new BufferedReader(new FileReader(sFileName));
            }
            String sLine;
            while ((sLine = rReader.readLine()) != null) {
                if (!sLine.equals("")) {
                    int iFirstTabLocation = sLine.indexOf("\t");
                    if (iFirstTabLocation >= 0) {
                        int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
                        sgWord[++igWordLast] = sLine.substring(0, iFirstTabLocation);
                        if (iSecondTabLocation > 0) {
                            sgLemma[igWordLast] = sLine.substring(iFirstTabLocation + 1, iSecondTabLocation);
                        } else {
                            sgLemma[igWordLast] = sLine.substring(iFirstTabLocation + 1);
                            if (sgWord[igWordLast].indexOf(" ") >= 0) {
                                sgWord[igWordLast] = sgWord[igWordLast].trim();
                            }
                            if (sgLemma[igWordLast].indexOf(" ") >= 0) {
                                sgLemma[igWordLast] = sgLemma[igWordLast].trim();
                            }
                        }
                    }
                }
                rReader.close();
                Sort.quickSortStringsWithStrings(sgWord, sgLemma, 0, igWordLast);
            }
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Couldn't find lemma file: ")).append(sFileName).toString());
            e.printStackTrace();
            pass = false;
        } catch (IOException e) {
            System.err.println((new StringBuilder("Found lemma file but couldn't read from it: ")).append(sFileName).toString());
            e.printStackTrace();
            pass = false;
        } finally {
            return pass;
        }
    }

    public boolean check(String sFileName, boolean bForceUTF8) {
        int iLinesInFile = 0;
        if (sFileName.equals("")) {
            System.err.println("No lemma file specified!");
            return false;
        }
        File f = new File(sFileName);
        boolean pass = true;
        if (!f.exists()) {
            System.err.println((new StringBuilder("Could not find lemma file: ")).append(sFileName).toString());
            pass = false;
        } else {
            iLinesInFile = FileOps.i_CountLinesInTextFile(sFileName);
            if (iLinesInFile < 2) {
                System.err.println((new StringBuilder("Less than 2 lines in sentiment file: ")).append(sFileName).toString());
                pass = false;
            }
        }
        return pass;
    }

    /**
     * 将单词转换为其原形
     * 实现UC1、UC3（用于Term类）
     * @param sWord 要转换的单词
     * @return 单词对应的原形
     * @author DaiXuezheng
     */
    public String lemmatise(String sWord) {
        int iLemmaID = Sort.i_FindStringPositionInSortedArray(sWord, sgWord, 0, igWordLast);
        if (iLemmaID >= 0) {
            return sgLemma[iLemmaID];
        } else {
            return sWord;
        }
    }
}

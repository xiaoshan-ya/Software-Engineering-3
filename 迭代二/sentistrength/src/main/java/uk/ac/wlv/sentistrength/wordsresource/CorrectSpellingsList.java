// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst
// Source File Name:   CorrectSpellingsList.java

package uk.ac.wlv.sentistrength.wordsresource;

import java.io.*;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

/**
 * 拼写检查的字典类，用于检查给定字符串是否拼写正确
 * @author haofeng.Yu
 */
public class CorrectSpellingsList implements WordResource {
    private String[] sgCorrectWord;
    private int igCorrectWordCount;
    private int igCorrectWordMax;

    public CorrectSpellingsList() {
        igCorrectWordCount = 0;
        igCorrectWordMax = 0;
    }

    /**
     * UC-3 Spelling Correction
     * 该方法从指定的文本文件初始化拼写检查字典SpellingList
     * 该方法然后返回 true 以指示字典已成功初始化。
     * 如果在读取文件或初始化字典时出现错误，该方法将返回 false。
     * @param sFilename 文件名
     * @param options 分类选项
     * @return boolean 如果没有发生异常，则返回true，否则返回false
     *
     * @author haofeng.Yu
     */
    public boolean initialise(String sFilename, ClassificationOptions options) {
        if (igCorrectWordMax > 0
                || !options.bgCorrectSpellingsUsingDictionary) {
            return true;
        }
//        if (!options.bgCorrectSpellingsUsingDictionary) {
//            return true;
//        }
        igCorrectWordMax = FileOps.i_CountLinesInTextFile(sFilename) + 2;
        sgCorrectWord = new String[igCorrectWordMax];
        igCorrectWordCount = 0;
        File f = new File(sFilename);
        if (!f.exists()) {
            //System.err.println((new StringBuilder("Could not find the spellings file: ")).append(sFilename).toString());
            return false;
        }
        try {
            BufferedReader rReader;
            if (options.bgForceUTF8) {
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFilename), "UTF8"));
            } else {
                rReader = new BufferedReader(new FileReader(sFilename));
            }
            String sLine;
            while ((sLine = rReader.readLine()) != null) {
                if (!sLine.equals("")) {
                    igCorrectWordCount++;
                    sgCorrectWord[igCorrectWordCount] = sLine;
                }
            }
            rReader.close();
            Sort.quickSortStrings(sgCorrectWord, 1, igCorrectWordCount);
        } catch (FileNotFoundException e) {
            //System.err.println((new StringBuilder("Could not find the spellings file: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            //System.err.println((new StringBuilder("Found spellings file but could not read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * UC-3 Spelling Correction
     * 检查一个字符串是否拼写正确
     * @param sWord 待检查字符串
     * @return boolean
     * @author haofeng.Yu
     */
    public boolean correctSpelling(String sWord) {
        return Sort.i_FindStringPositionInSortedArray(sWord, sgCorrectWord, 1, igCorrectWordCount) >= 0;
    }
}

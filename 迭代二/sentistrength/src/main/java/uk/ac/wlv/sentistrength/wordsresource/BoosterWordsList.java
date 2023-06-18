// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   BoosterWordsList.java

package uk.ac.wlv.sentistrength.wordsresource;

import java.io.*;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 该类用于存放、翻译、替换加强/减弱情绪表达的词汇和符号，对应了SentStrength_Data中的BoosterWordList.txt
 * @author zhengjie
 */
public class BoosterWordsList implements WordResource {
    private String[] sgBoosterWords; //加强情绪表达的词汇
    private int[] igBoosterWordStrength; //词汇所对应的情绪值
    private int igBoosterWordsCount; //词汇个数

    /**
     * 构造函数
     * @author zhengjie
     */
    public BoosterWordsList() {
        igBoosterWordsCount = 0;
    }

    /**
     * UC-1;UC-4
     * @param sFilename 存放booster words文件的文件名。
     * @param options 由ClassficationOptions类确定，分类选项。
     * @param iExtraBlankArrayEntriesToInclude 额外开辟的用于存放新的booster words的空间大小。
     * @return true(当成功初始化时返回) 或者false(当初始化失败时返回，通常情况为找不到对应的sFilename、文件为空或无法读文件)。
     * @author zhengjie
     */
    public boolean initialises(String sFilename, ClassificationOptions options, int iExtraBlankArrayEntriesToInclude) {
        int iLinesInFile = 0;
        iLinesInFile = FileOps.i_CountLinesInTextFile(sFilename);
        if (iLinesInFile < 1) {
            //System.err.println("No booster words specified");
            return false;
        }
        sgBoosterWords = new String[iLinesInFile + 1 + iExtraBlankArrayEntriesToInclude];
        igBoosterWordStrength = new int[iLinesInFile + 1 + iExtraBlankArrayEntriesToInclude];
        return initialise(sFilename, options);
    }

    public boolean initialise(String sFilename, ClassificationOptions options) {
        int iWordStrength = 0;
        if (sFilename.equals("")) {
            //System.err.println("No booster words file specified");
            return false;
        }
        File f = new File(sFilename);
        if (!f.exists()) {
            //System.err.println((new StringBuilder("Could not find booster words file: ")).append(sFilename).toString());
            return false;
        }
        igBoosterWordsCount = 0;
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
                    int iFirstTabLocation = sLine.indexOf("\t");
                    if (iFirstTabLocation >= 0) {
                        int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
                        try {
                            if (iSecondTabLocation > 0) {
                                iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation));
                            } else {
                                iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1).trim());
                            }
                        } catch (NumberFormatException e) {
                            //System.err.println("Failed to identify integer weight for booster word! Assuming it is zero");
                            //System.err.println((new StringBuilder("Line: ")).append(sLine).toString());
                            iWordStrength = 0;
                        }
                        sLine = sLine.substring(0, iFirstTabLocation);
                        if (sLine.indexOf(" ") >= 0) {
                            sLine = sLine.trim();
                        }
                        if (!sLine.equals("")) {
                            igBoosterWordsCount++;
                            sgBoosterWords[igBoosterWordsCount] = sLine;
                            igBoosterWordStrength[igBoosterWordsCount] = iWordStrength;
                        }
                    }
                }
            }
            Sort.quickSortStringsWithInt(sgBoosterWords, igBoosterWordStrength, 1, igBoosterWordsCount);
            rReader.close();
        } catch (FileNotFoundException e) {
            //System.err.println((new StringBuilder("Could not find booster words file: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            //System.err.println((new StringBuilder("Found booster words file but could not read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 该方法用于添加额外的放大情绪的词汇短语
     * @param sText 需要新添加的放大情绪的词汇
     * @param iWordStrength 该词汇长度
     * @param bSortBoosterListAfterAddingTerm 为true则表示在添加后需要对BoosterWords数组进行排序，为false则不需要
     * @return 添加成功时返回true，失败时返回false
     * @author zhengjie
     */
    public boolean addExtraTerm(String sText, int iWordStrength, boolean bSortBoosterListAfterAddingTerm) {
        try {
            igBoosterWordsCount++;
            sgBoosterWords[igBoosterWordsCount] = sText;
            igBoosterWordStrength[igBoosterWordsCount] = iWordStrength;
            if (bSortBoosterListAfterAddingTerm) {
                Sort.quickSortStringsWithInt(sgBoosterWords, igBoosterWordStrength, 1, igBoosterWordsCount);
            }
        } catch (Exception e) {
            //System.err.println((new StringBuilder("Could not add extra booster word: ")).append(sText).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *该方法对BoosterWords数组进行了排序
     * @author zhengjie
     */
    public void sortBoosterWordList() {
        Sort.quickSortStringsWithInt(sgBoosterWords, igBoosterWordStrength, 1, igBoosterWordsCount);
    }

    /**
     * 该方法用于获取存在于BoosterWords数组中的某个单词的长度
     * @param sWord 单词
     * @return 返回单词长度
     * @author zhengjie
     */
    public int getBoosterStrength(String sWord) {
        int iWordID = Sort.i_FindStringPositionInSortedArray(sWord.toLowerCase(), sgBoosterWords, 1, igBoosterWordsCount);
        if (iWordID >= 0) {
            return igBoosterWordStrength[iWordID];
        } else {
            return 0;
        }
    }
}

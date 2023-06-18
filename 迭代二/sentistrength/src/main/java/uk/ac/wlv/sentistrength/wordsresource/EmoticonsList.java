// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst
// Source File Name:   EmoticonsList.java

package uk.ac.wlv.sentistrength.wordsresource;

import java.io.*;


import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 情感符号表极其对应强度的实现类
 * @author haofeng.Yu
 */
public class EmoticonsList implements WordResource {

    private String[] sgEmoticon;
    private int[] igEmoticonStrength;
    private int igEmoticonCount;
    private int igEmoticonMax;

    /**
     * 默认构造函数，将igEmoticonCount和igEmoticonMax初始化为0
     * @author haofeng.Yu
     */
    public EmoticonsList() {
        igEmoticonCount = 0;
        igEmoticonMax = 0;
    }

    /**
     * UC-1 Assigning Sentiment Scores for Words
     * UC-7 Emoji Rule
     * 接受一个情感符号并且返回一个表示情感强度的整数，如果无法找到该情感符号，则返回999
     * @param emoticon 情感符号
     * @return int 情感强度
     * @author haofeng.Yu
     */
    public int getEmoticon(String emoticon) {
        int iEmoticon = Sort.i_FindStringPositionInSortedArray(emoticon, sgEmoticon, 1, igEmoticonCount);
        if (iEmoticon >= 0) {
            return igEmoticonStrength[iEmoticon];
        } else {
            return 999;
        }
    }

    /**
     * 初始化EmoticonsList对象，同时更新在文件中找到的情感符号的数量
     * @param sSourceFile 待读取情感符号的文件名
     * @param options 分类选项
     * @return boolean 若无异常，则返回true，否则返回false
     * @author haofeng.Yu
     */
    public boolean initialise(String sSourceFile, ClassificationOptions options) {
        if (igEmoticonCount > 0) {
            return true;
        }
        File f = new File(sSourceFile);
        if (!f.exists()) {
            //System.err.println((new StringBuilder("Could not find file: ")).append(sSourceFile).toString());
            return false;
        }
        try {
            igEmoticonMax = FileOps.i_CountLinesInTextFile(sSourceFile) + 2;
            igEmoticonCount = 0;
            String[] sEmoticonTemp = new String[igEmoticonMax];
            sgEmoticon = sEmoticonTemp;
            int[] iEmoticonStrengthTemp = new int[igEmoticonMax];
            igEmoticonStrength = iEmoticonStrengthTemp;
            BufferedReader rReader;
            if (options.bgForceUTF8) {
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSourceFile), "UTF8"));
            } else {
                rReader = new BufferedReader(new FileReader(sSourceFile));
            }
            String sLine;
            while ((sLine = rReader.readLine()) != null) {
                if (!sLine.equals("")) {
                    String[] sData = sLine.split("\t");
                    if (sData.length > 1) {
                        igEmoticonCount++;
                        sgEmoticon[igEmoticonCount] = sData[0];
                        try {
                            igEmoticonStrength[igEmoticonCount] = Integer.parseInt(sData[1].trim());
                        } catch (NumberFormatException e) {
                            //System.err.println("Failed to identify integer weight for emoticon! Ignoring emoticon");
                            //System.err.println((new StringBuilder("Line: ")).append(sLine).toString());
                            igEmoticonCount--;
                        }
                    }
                }
            }
            rReader.close();
        } catch (FileNotFoundException e) {
            //System.err.println((new StringBuilder("Could not find emoticon file: ")).append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            //System.err.println((new StringBuilder("Found emoticon file but could not read from it: ")).append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        }
        if (igEmoticonCount > 1) {
            Sort.quickSortStringsWithInt(sgEmoticon, igEmoticonStrength, 1, igEmoticonCount);
        }
        return true;
    }
}

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   QuestionWords.java

package uk.ac.wlv.sentistrength.wordsresource;

import java.io.*;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 初始化和查询问题词
 * @author DaiXuezheng
 */
public class QuestionWords implements WordResource {
    /** 问题词数组 **/
    private String[] sgQuestionWord;
    /** 已读取问题词个数 **/
    private int igQuestionWordCount;
    /** 问题词数组最大容量 **/
    private int igQuestionWordMax;

    /**
     * 构造函数，已读取和最大容量初始化为0
     * @author DaiXuezheng
     */
    public QuestionWords() {
        igQuestionWordCount = 0;
        igQuestionWordMax = 0;
    }

    /**
     * 初始化QuestionWords类的实例变量
     * 实现UC11
     * @param sFilename 要读取的文件名
     * @param options 编码选项
     * @return 成功初始化返回true，否则返回false
     * @author DaiXuezheng
     */
    public boolean initialise(String sFilename, ClassificationOptions options) {
        if (!check(sFilename, options)) {
            return false;
        }
        igQuestionWordMax = FileOps.i_CountLinesInTextFile(sFilename) + 2;
        sgQuestionWord = new String[igQuestionWordMax];
        igQuestionWordCount = 0;
        boolean pass = true;
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
                    igQuestionWordCount++;
                    sgQuestionWord[igQuestionWordCount] = sLine;
                }
            }
            rReader.close();
            Sort.quickSortStrings(sgQuestionWord, 1, igQuestionWordCount);
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Could not find the question word file: ")).append(sFilename).toString());
            e.printStackTrace();
            pass = false;
        } catch (IOException e) {
            System.err.println((new StringBuilder("Found question word file but could not read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            pass = false;
        } finally {
            return pass;
        }
    }

    public boolean check(String sFilename, ClassificationOptions options) {
        if (igQuestionWordMax > 0) {
            return true;
        }
        File f = new File(sFilename);
        if (!f.exists()) {
            System.err.println((new StringBuilder("Could not find the question word file: ")).append(sFilename).toString());
            return false;
        }
        return true;
    }

    /**
     * 判断单词是否为问题词
     * 实现UC10
     * @param sWord 要判断的单词
     * @return 是返回true，否则返回false
     * @author DaiXuezheng
     */
    public boolean questionWord(String sWord) {
        return Sort.i_FindStringPositionInSortedArray(sWord, sgQuestionWord, 1, igQuestionWordCount) >= 0;
    }
}

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   SentimentWords.java

package uk.ac.wlv.sentistrength.wordsresource;

import java.io.*;


import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.sentistrength.Corpus;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            Corpus, ClassificationOptions

/**
 * 处理情感词汇
 *
 * @author DaiXuezheng
 */
public class SentimentWords {
    /**
     * 有标准情感强度的情感词
     **/
    private String[] sgSentimentWords;
    /**
     * 对于有标准情感强度的情感词，使用take1的强度值
     **/
    private int[] igSentimentWordsStrengthTake1;
    /**
     * 有标准情感强度的情感词的个数
     **/
    private int igSentimentWordsCount;
    /**
     * 以星号开头的情感词
     **/
    private String[] sgSentimentWordsWithStarAtStart;
    /**
     * 对于以星号开头的情感词，使用take1的强度值
     **/
    private int[] igSentimentWordsWithStarAtStartStrengthTake1;
    /**
     * 以星号开头的情感词的个数
     **/
    private int igSentimentWordsWithStarAtStartCount;
    /**
     * 标志一个以星号开头的情感词是否以星号结尾
     **/
    private boolean[] bgSentimentWordsWithStarAtStartHasStarAtEnd;

    /**
     * SentimentWords类的构造方法，初始化情感词个数和以星号开头的情感词个数为0
     *
     * @author DaiXuezheng
     */
    public SentimentWords() {
        igSentimentWordsCount = 0;
        igSentimentWordsWithStarAtStartCount = 0;
    }

    /**
     * 获取情感词汇表中指定ID的情感词
     *
     * @param iWordID 情感词ID
     * @return 指定ID的情感词，如果ID超出范围则返回空字符串
     * @author DaiXuezheng
     */
    public String getSentimentWord(int iWordID) {
        if (iWordID > 0) {
            if (iWordID <= igSentimentWordsCount) {
                return sgSentimentWords[iWordID];
            }
            if (iWordID <= igSentimentWordsCount + igSentimentWordsWithStarAtStartCount) {
                return sgSentimentWordsWithStarAtStart[iWordID - igSentimentWordsCount];
            }
        }
        return "";
    }


    /**
     * 获取给定单词的情感值
     *
     * @param sWord 给定的单词
     * @return 给定单词的情感值
     * @author DaiXuezheng
     */
    public int getSentiment(String sWord) {
        int iWordID =
                Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord.toLowerCase(), sgSentimentWords, 1, igSentimentWordsCount);
        if (iWordID >= 0) {
            return igSentimentWordsStrengthTake1[iWordID];
        }
        int iStarWordID = getMatchingStarAtStartRawWordID(sWord);
        if (iStarWordID >= 0) {
            return igSentimentWordsWithStarAtStartStrengthTake1[iStarWordID];
        } else {
            return 999;
        }
    }

    /**
     * 将指定单词的情感值设置为给定的值。
     *
     * @param sWord         要设置情感值的单词
     * @param iNewSentiment 新的情感值，正数表示正面情感，负数表示负面情感，0表示中性情感
     * @return 如果设置成功，则返回true；否则返回false
     * @author DaiXuezheng
     */
    public boolean setSentiment(String sWord, int iNewSentiment) {
        // 搜索单词在情感词数组中的位置
        int iWordID =
                Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord.toLowerCase(), sgSentimentWords, 1, igSentimentWordsCount);
        if (iWordID >= 0) {
            // 如果单词在情感词数组中，则更新其情感值
            if (iNewSentiment > 0) {
                setSentiment(iWordID, iNewSentiment - 1);
            } else {
                setSentiment(iWordID, iNewSentiment + 1);
            }
            return true;
        }
        // 如果单词不在情感词数组中，则检查是否有通配符，如果有，则匹配相应的单词
        if (sWord.indexOf("*") == 0) {
            sWord = sWord.substring(1);
            if (sWord.indexOf("*") > 0) {
                sWord.substring(0, sWord.length() - 1);
            }
        }
        if (igSentimentWordsWithStarAtStartCount > 0) {
            for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
                if (sWord == sgSentimentWordsWithStarAtStart[i]) {
                    // 如果找到匹配的单词，则更新其情感值
                    if (iNewSentiment > 0) {
                        setSentiment(igSentimentWordsCount + i, iNewSentiment - 1);
                    } else {
                        setSentiment(igSentimentWordsCount + i, iNewSentiment + 1);
                    }
                    return true;
                }
            }
        }
        // 如果单词不存在或无法匹配，则返回false
        return false;
    }


    /**
     * 保存情感词典
     *
     * @param sFilename 文件名
     * @param c         语料库对象
     * @return 保存成功返回true，保存失败返回false
     * @author DaiXuezheng
     */
    public boolean saveSentimentList(String sFilename, Corpus c) {
        try {
            //创建一个缓冲输出流写入指定文件中
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sFilename));
            //遍历情感词典中的每一个情感词
            for (int i = 1; i <= igSentimentWordsCount; i++) {
                int iSentimentStrength = igSentimentWordsStrengthTake1[i];
                if (iSentimentStrength < 0) {
                    iSentimentStrength--;
                } else {
                    iSentimentStrength++;
                }
                //构建输出字符串，格式为“情感词\t情感强度\n”
                String sOutput = output(iSentimentStrength, c, i);
                //将构建好的输出字符串写入文件中
                wWriter.write(sOutput);
            }
            for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
                int iSentimentStrength = igSentimentWordsWithStarAtStartStrengthTake1[i];
                if (iSentimentStrength < 0) {
                    iSentimentStrength--;
                } else {
                    iSentimentStrength++;
                }
                String sOutput = (new StringBuilder("*")).append(sgSentimentWordsWithStarAtStart[i]).toString();
                if (bgSentimentWordsWithStarAtStartHasStarAtEnd[i]) {
                    sOutput = (new StringBuilder(String.valueOf(sOutput))).append("*").toString();
                }
                sOutput = (new StringBuilder(String.valueOf(sOutput))).append("\t").append(iSentimentStrength).append("\n").toString();
                if (c.options.bgForceUTF8) {
                    try {
                        sOutput = new String(sOutput.getBytes("UTF-8"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        System.err.println("UTF-8 not found on your system!");
                        e.printStackTrace();
                    }
                }
                wWriter.write(sOutput);
            }
            wWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 成功保存情感词典后返回true
        return true;
    }

    public String output(int iSentimentStrength, Corpus c, int i) {
        //构建输出字符串，格式为“情感词\t情感强度\n”
        String sOutput = (new StringBuilder(String.valueOf(sgSentimentWords[i]))).append("\t")
                .append(iSentimentStrength).append("\n").toString();
        //如果需要强制使用UTF-8编码，对输出字符串进行转换
        if (c.options.bgForceUTF8) {
            try {
                sOutput = new String(sOutput.getBytes("UTF-8"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println("UTF-8 not found on your system!");
                e.printStackTrace();
            }
        }
        return sOutput;
    }

    /**
     * 以单行输出情感值
     *
     * @param wWriter 输出流
     * @return 打印成功返回true，打印失败返回false
     * @author DaiXuezheng
     */
    public boolean printSentimentValuesInSingleRow(BufferedWriter wWriter) {
        try {
            for (int i = 1; i <= igSentimentWordsCount; i++) {
                int iSentimentStrength = igSentimentWordsStrengthTake1[i];
                wWriter.write((new StringBuilder("\t")).append(iSentimentStrength).toString());
            }

            for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
                int iSentimentStrength = igSentimentWordsWithStarAtStartStrengthTake1[i];
                wWriter.write((new StringBuilder("\t")).append(iSentimentStrength).toString());
            }

            wWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 以单行输出情感词汇表的词语
     *
     * @param wWriter 输出流
     * @return 打印成功返回true，打印失败返回false
     * @author DaiXuezheng
     */
    public boolean printSentimentTermsInSingleHeaderRow(BufferedWriter wWriter) {
        try {
            for (int i = 1; i <= igSentimentWordsCount; i++) {
                wWriter.write((new StringBuilder("\t")).append(sgSentimentWords[i]).toString());
            }
            for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
                wWriter.write((new StringBuilder("\t*")).append(sgSentimentWordsWithStarAtStart[i]).toString());
                if (bgSentimentWordsWithStarAtStartHasStarAtEnd[i]) {
                    wWriter.write("*");
                }
            }

            wWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取单词对应的情感词
     *
     * @param iWordID 单词id
     * @return 单词id对应的情感词，id不合法则返回999
     * @author DaiXuezheng
     */
    public int getSentiment(int iWordID) {
        if (iWordID > 0) {
            if (iWordID <= igSentimentWordsCount) {
                return igSentimentWordsStrengthTake1[iWordID];
            } else {
                return igSentimentWordsWithStarAtStartStrengthTake1[iWordID - igSentimentWordsCount];
            }
        } else {
            return 999;
        }
    }

    /**
     * 设置单词的情感值
     * 实现UC27
     *
     * @param iWordID       单词id
     * @param iNewSentiment 要设置的情感值
     * @author DaiXuezheng
     */
    public void setSentiment(int iWordID, int iNewSentiment) {
        if (iWordID <= igSentimentWordsCount) {
            igSentimentWordsStrengthTake1[iWordID] = iNewSentiment;
        } else {
            igSentimentWordsWithStarAtStartStrengthTake1[iWordID - igSentimentWordsCount] = iNewSentiment;
        }
    }

    /**
     * 获得单词情感值id
     *
     * @param sWord 单词
     * @return 单词情感值对应的id，不匹配返回-1
     * @author DaiXuezheng
     */
    public int getSentimentID(String sWord) {
        int iWordID =
                Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord.toLowerCase(), sgSentimentWords, 1, igSentimentWordsCount);
        if (iWordID >= 0) {
            return iWordID;
        }
        iWordID = getMatchingStarAtStartRawWordID(sWord);
        if (iWordID >= 0) {
            return iWordID + igSentimentWordsCount;
        } else {
            return -1;
        }
    }

    /**
     * 查找在情感词汇表中，是否存在一个以星号开头的单词，与给定的单词的开头匹配
     *
     * @param sWord 给定的单词
     * @return 存在则返回词汇表的索引，不存在返回-1
     * @author DaiXuezheng
     */
    private int getMatchingStarAtStartRawWordID(String sWord) {
        int iSubStringPos = 0;
        if (igSentimentWordsWithStarAtStartCount > 0) {
            for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
                iSubStringPos = sWord.indexOf(sgSentimentWordsWithStarAtStart[i]);
                if (iSubStringPos >= 0 && (bgSentimentWordsWithStarAtStartHasStarAtEnd[i]
                        || iSubStringPos + sgSentimentWordsWithStarAtStart[i].length() == sWord.length())) {
                    return i;
                }
            }

        }
        return -1;
    }

    /**
     * 获取情感词个数
     *
     * @return 情感词个数
     */
    public int getSentimentWordCount() {
        return igSentimentWordsCount;
    }

    /**
     * 从情感词典文件中初始化情感词列表、情感强度值列表并排序
     * 实现UC1、UC2、UC6、UC7、UC11
     *
     * @param sFilename                        情感词典文件
     * @param options                          编码选项
     * @param iExtraBlankArrayEntriesToInclude 额外包含的空白数组数量
     * @return 初始化成功返回true，否则false
     * @author DaiXuezheng
     */

    public boolean initialises(String sFilename, ClassificationOptions options, int iExtraBlankArrayEntriesToInclude) {
        int iLinesInFile = FileOps.i_CountLinesInTextFile(sFilename);
        igSentimentWordsStrengthTake1 = new int[iLinesInFile + 1 + iExtraBlankArrayEntriesToInclude];
        sgSentimentWords = new String[iLinesInFile + 1 + iExtraBlankArrayEntriesToInclude];
        return initialise(sFilename, options);
    }

    public boolean initialise(String sFilename, ClassificationOptions options) {
        int iWordStrength = 0, iWordsWithStarAtStart = 0;
        if (!check(sFilename)) {
            return false;
        }
        boolean pass = true;
        igSentimentWordsCount = 0;
        try {
            BufferedReader rReader;
            if (options.bgForceUTF8) {
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFilename), "UTF8"));
            } else {
                rReader = new BufferedReader(new FileReader(sFilename));
            }
            String sLine;
            while ((sLine = rReader.readLine()) != null) {
                if (sLine.equals("")) {
                    continue;
                }
                if (sLine.indexOf("*") == 0) {
                    iWordsWithStarAtStart++;
                } else {
                    loop(sLine, iWordStrength);
                }
            }
            rReader.close();
            Sort.quickSortStringsWithInt(sgSentimentWords, igSentimentWordsStrengthTake1, 1, igSentimentWordsCount);
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Couldn't find sentiment file: ")).append(sFilename).toString());
            e.printStackTrace();
            pass = false;
        } catch (IOException e) {
            System.err.println((new StringBuilder("Found sentiment file but couldn't read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            pass = false;
        } finally {
            if (pass) {
                if (iWordsWithStarAtStart > 0) {
                    int iLinesInFile = FileOps.i_CountLinesInTextFile(sFilename);
                    int iExtraBlankArrayEntriesToInclude = igSentimentWordsStrengthTake1.length - 1 - iLinesInFile;
                    return initialiseWordsWithStarAtStart(sFilename, options, iWordsWithStarAtStart, iExtraBlankArrayEntriesToInclude);
                }
            }
            return pass;
        }
    }

    public void loop(String sLine, int iWordStrength) {
        int iFirstTabLocation = sLine.indexOf("\t");
        if (iFirstTabLocation >= 0) {
            int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
            try {
                if (iSecondTabLocation > 0) {
                    iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation).trim());
                } else {
                    iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1).trim());
                }
            } catch (NumberFormatException e) {
                System.err.println((new StringBuilder(
                        "Failed to identify integer weight for sentiment word! Ignoring word\nLine: "))
                        .append(sLine).toString());
                iWordStrength = 0;
            }
            sLine = sLine.substring(0, iFirstTabLocation);
            if (sLine.indexOf(" ") >= 0) {
                sLine = sLine.trim();
            }
            if (!sLine.equals("")) {
                sgSentimentWords[++igSentimentWordsCount] = sLine;
                if (iWordStrength > 0) {
                    iWordStrength--;
                } else if (iWordStrength < 0) {
                    iWordStrength++;
                }
                igSentimentWordsStrengthTake1[igSentimentWordsCount] = iWordStrength;
            }
        }
    }

    public boolean check(String sFilename) {
        if (sFilename.equals("")) {
            System.err.println("No sentiment file specified");
            return false;
        }
        File f = new File(sFilename);
        boolean pass = true;
        if (!f.exists()) {
            System.err.println((new StringBuilder("Could not find sentiment file: ")).append(sFilename).toString());
            pass = false;
        } else {
            int iLinesInFile = FileOps.i_CountLinesInTextFile(sFilename);
            if (iLinesInFile < 2) {
                System.err.println((new StringBuilder("Less than 2 lines in sentiment file: ")).append(sFilename).toString());
                pass = false;
            }
        }
        return pass;
    }

    /**
     * 从指定的文件初始化带星号开头的词语，并存储在类的实例变量中
     * 实现UC1、UC2、UC6、UC7、UC11
     *
     * @param sFilename                        情感词典文件路径
     * @param options                          编码选项
     * @param iWordsWithStarAtStart            带星号开头的词语的数组的长度
     * @param iExtraBlankArrayEntriesToInclude 额外包含的空白数组条目数量
     * @return 初始化是否成功
     * @author DaiXuezheng
     */
    public boolean initialiseWordsWithStarAtStart(
            String sFilename, ClassificationOptions options, int iWordsWithStarAtStart, int iExtraBlankArrayEntriesToInclude) {
        int iWordStrength = 0;
        File f = new File(sFilename);
        if (!f.exists()) {
            System.err.println((new StringBuilder("Could not find sentiment file: ")).append(sFilename).toString());
            return false;
        }
        boolean pass = true;
        igSentimentWordsWithStarAtStartStrengthTake1 = new int[iWordsWithStarAtStart + 1 + iExtraBlankArrayEntriesToInclude];
        sgSentimentWordsWithStarAtStart = new String[iWordsWithStarAtStart + 1 + iExtraBlankArrayEntriesToInclude];
        bgSentimentWordsWithStarAtStartHasStarAtEnd = new boolean[iWordsWithStarAtStart + 1 + iExtraBlankArrayEntriesToInclude];
        igSentimentWordsWithStarAtStartCount = 0;
        try {
            BufferedReader rReader;
            if (options.bgForceUTF8) {
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFilename), "UTF8"));
            } else {
                rReader = new BufferedReader(new FileReader(sFilename));
            }
            while (rReader.ready()) {
                starLoop(rReader, iWordStrength);
            }
            rReader.close();
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Couldn't find *sentiment file*: ")).append(sFilename).toString());
            e.printStackTrace();
            pass = false;
        } catch (IOException e) {
            System.err.println((new StringBuilder("Found *sentiment file* but couldn't read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            pass = false;
        } finally {
            return pass;
        }
    }

    public void starLoop(BufferedReader rReader, int iWordStrength) throws IOException {
        String sLine = rReader.readLine();
        if (!sLine.equals("") && sLine.indexOf("*") == 0) {
            int iFirstTabLocation = sLine.indexOf("\t");
            if (iFirstTabLocation >= 0) {
                int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
                try {
                    if (iSecondTabLocation > 0) {
                        iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation));
                    } else {
                        iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1));
                    }
                } catch (NumberFormatException e) {
                    System.err.println((new StringBuilder(
                            "Failed to identify integer weight for *sentiment* word! Ignoring word\nLine: "))
                            .append(sLine).toString());
                    iWordStrength = 0;
                }
                sLine = sLine.substring(1, iFirstTabLocation);
                if (sLine.indexOf("*") > 0) {
                    sLine = sLine.substring(0, sLine.indexOf("*"));
                    bgSentimentWordsWithStarAtStartHasStarAtEnd[++igSentimentWordsWithStarAtStartCount] = true;
                } else {
                    bgSentimentWordsWithStarAtStartHasStarAtEnd[++igSentimentWordsWithStarAtStartCount] = false;
                }
                if (sLine.indexOf(" ") >= 0) {
                    sLine = sLine.trim();
                }
                if (!sLine.equals("")) {
                    sgSentimentWordsWithStarAtStart[igSentimentWordsWithStarAtStartCount] = sLine;
                    if (iWordStrength > 0) {
                        iWordStrength--;
                    } else if (iWordStrength < 0) {
                        iWordStrength++;
                    }
                    igSentimentWordsWithStarAtStartStrengthTake1[igSentimentWordsWithStarAtStartCount] = iWordStrength;
                } else {
                    igSentimentWordsWithStarAtStartCount--;
                }
            }
        }
    }

    /**
     * 添加或修改情感词汇，同时根据需要对情感词汇进行排序
     * 实现UC27
     *
     * @param sTerm                             添加或修改的词汇
     * @param iTermStrength                     情感词强度
     * @param bSortSentimentListAfterAddingTerm 是否排序
     * @return 成功添加或修改情感词汇返回true，否则返回false
     * @author DaiXuezheng
     */
    public boolean addOrModifySentimentTerm(String sTerm, int iTermStrength, boolean bSortSentimentListAfterAddingTerm) {
        int iTermPosition = getSentimentID(sTerm);
        if (iTermPosition > 0) {
            if (iTermStrength > 0) {
                iTermStrength--;
            } else if (iTermStrength < 0) {
                iTermStrength++;
            }
            igSentimentWordsStrengthTake1[iTermPosition] = iTermStrength;
        } else {
            try {
                sgSentimentWords[++igSentimentWordsCount] = sTerm;
                if (iTermStrength > 0) {
                    iTermStrength--;
                } else if (iTermStrength < 0) {
                    iTermStrength++;
                }
                igSentimentWordsStrengthTake1[igSentimentWordsCount] = iTermStrength;
                if (bSortSentimentListAfterAddingTerm) {
                    Sort.quickSortStringsWithInt(sgSentimentWords, igSentimentWordsStrengthTake1, 1, igSentimentWordsCount);
                }
            } catch (Exception e) {
                System.err.println((new StringBuilder("Could not add extra sentiment term: ")).append(sTerm).toString());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 排序情感词
     */
    public void sortSentimentList() {
        Sort.quickSortStringsWithInt(sgSentimentWords, igSentimentWordsStrengthTake1, 1, igSentimentWordsCount);
    }
}

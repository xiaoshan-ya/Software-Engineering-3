// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst
// Source File Name:   EvaluativeTerms.java

package uk.ac.wlv.sentistrength.wordsresource;

import java.io.*;


import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;

/**
 * 情感分析评价的初始化与存储类
 * @author haofeng.Yu
 */
public class EvaluativeTerms {
    /**存储最大评价值**/
    private int igObjectEvaluationMax;
    /** 存储从源文件中读取到的评价对象 **/
    public String[] sgObject;
    /** 存储从源文件中读取到的评价性词语 **/
    public String[] sgObjectEvaluation;
    /** 存储从源文件中读取到的每个对象及其评估结构 object-evaluation pair **/
    public int[] igObjectEvaluationStrength;
    /** 成功读取并且评价的对象数目 **/
    public int igObjectEvaluationCount;

    /**
     * 默认构造函数，将igObjectEvaluationMax,igObjectEvaluationCount初始化为0。
     * @author haofeng.Yu
     */
    public EvaluativeTerms() {
        igObjectEvaluationMax = 0;
        igObjectEvaluationCount = 0;
    }

    /**
     * UC-1 Assigning Sentiment Scores for Words
     * UC-27 Optimise sentiment strengths of existing sentiment terms
     * UC-28 Suggest new sentiment terms (from terms in misclassified texts)
     * 初始化评价词，并且为对应的对象添加额外的SentimentWord和idiom
     * @param sSourceFile 包含情感词的源文件名称
    * @param options ClassificationOptions对象
    * @param idiomList IdiomList对象
    * @param sentimentWords 情感词SentimentWords对象
     * @return boolean 指示初始化是否成功
     * @author haofeng.Yu
     */
    public boolean initialise(String sSourceFile, ClassificationOptions options, IdiomList idiomList, SentimentWords sentimentWords) {
        if (igObjectEvaluationCount > 0) {
            return true;
        }
        File f = new File(sSourceFile);
        if (!f.exists()) {
            //System.err.println((new StringBuilder("Could not find additional (object/evaluation) file: ")).append(sSourceFile).toString());
            return false;
        }
        int iStrength = 0;
        boolean bIdiomsAdded = false;
        boolean bSentimentWordsAdded = false;
        try {
            igObjectEvaluationMax = FileOps.i_CountLinesInTextFile(sSourceFile) + 2;
            igObjectEvaluationCount = 0;
            sgObject = new String[igObjectEvaluationMax];
            sgObjectEvaluation = new String[igObjectEvaluationMax];
            igObjectEvaluationStrength = new int[igObjectEvaluationMax];
            BufferedReader rReader;
            if (options.bgForceUTF8) {
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSourceFile), "UTF8"));
            } else {
                rReader = new BufferedReader(new FileReader(sSourceFile));
            }
            String sLine;
            while ((sLine = rReader.readLine()) != null) {
                if (!sLine .equals("") && sLine.indexOf("##") != 0 && sLine.indexOf("\t") > 0) {
                    String[] sData = sLine.split("\t");
                    if (sData.length > 2 && sData[2].indexOf("##") != 0) {
                        sgObject[++igObjectEvaluationCount] = sData[0];
                        sgObjectEvaluation[igObjectEvaluationCount] = sData[1];
                        try {
                            igObjectEvaluationStrength[igObjectEvaluationCount] = Integer.parseInt(sData[2].trim());
                            if (igObjectEvaluationStrength[igObjectEvaluationCount] > 0) {
                                igObjectEvaluationStrength[igObjectEvaluationCount]--;
                            } else if (igObjectEvaluationStrength[igObjectEvaluationCount] < 0) {
                                igObjectEvaluationStrength[igObjectEvaluationCount]++;
                            }
                        } catch (NumberFormatException e) {
                            //System.err.println("Failed to identify integer weight for object/evaluation! Ignoring object/evaluation");
                            //System.err.println((new StringBuilder("Line: ")).append(sLine).toString());
                            igObjectEvaluationCount--;
                        }
                    } else if (sData[0].indexOf(" ") > 0) {
                        try {
                            iStrength = Integer.parseInt(sData[1].trim());
                            idiomList.addExtraIdiom(sData[0], iStrength, false);
                            bIdiomsAdded = true;
                        } catch (NumberFormatException e) {
                            //System.err.println("Failed to identify integer weight for idiom in additional file! Ignoring it");
                            //System.err.println((new StringBuilder("Line: ")).append(sLine).toString());
                        }
                    } else {
                        try {
                            iStrength = Integer.parseInt(sData[1].trim());
                            sentimentWords.addOrModifySentimentTerm(sData[0], iStrength, false);
                            bSentimentWordsAdded = true;
                        } catch (NumberFormatException e) {
                            //System.err.println("Failed to identify integer weight for sentiment term in additional file! Ignoring it");
                            //System.err.println((new StringBuilder("Line: ")).append(sLine).toString());
                            igObjectEvaluationCount--;
                        }
                    }
                }
            }
            rReader.close();
            if (igObjectEvaluationCount > 0) {
                options.bgUseObjectEvaluationTable = true;
            }
            if (bSentimentWordsAdded) {
                sentimentWords.sortSentimentList();
            }
            if (bIdiomsAdded) {
                idiomList.convertIdiomStringsToWordLists();
            }
        } catch (FileNotFoundException e) {
            //System.err.println((new StringBuilder("Could not find additional (object/evaluation) file: ")).append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            //System.err.println((new StringBuilder("Found additional (object/evaluation) file but could not read from it: "))
//                    .append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   UnusedTermsClassificationIndex.java

package uk.ac.wlv.sentistrength.classificationresource;

import java.io.*;

import uk.ac.wlv.utilities.Trie;

/**
 * 未使用的术语分类索引
 */
public class UnusedTermsClassificationIndex {

    private String[] sgTermList;
    private int igTermListCount;
    private int igTermListMax;
    private int[] igTermListLessPtr;
    private int[] igTermListMorePtr;
    private int[] igTermListFreq;
    private int[] igTermListFreqTemp;
    private int[] igTermListPosClassDiff;
    private int[] iTermsAddedIDTemp;
    private int[] igTermListNegClassDiff;
    private int[] igTermListScaleClassDiff;
    private int[] igTermListBinaryClassDiff;
    private int[] igTermListTrinaryClassDiff;
    private int iTermsAddedIDTempCount;
    private int[][] igTermListPosCorrectClass;
    private int[][] igTermListNegCorrectClass;
    private int[][] igTermListScaleCorrectClass;
    private int[][] igTermListBinaryCorrectClass;
    private int[][] igTermListTrinaryCorrectClass;

    /**
     * 未使用术语分类索引.
     */
    public UnusedTermsClassificationIndex() {
        sgTermList = null;
        igTermListCount = 0;
        igTermListMax = 50000;
    }
    /**
     * main
     * @param args1 description
     * @author zhangsong
     */
//    public static void main(String[] args1) {
//    }

    /**
     * UC-17 将术语添加到新术语索引
     * <p>
     *     如果传入的字符串为空，直接返回。
     * </p>
     * <p>
     *     bDontAddMoreElements用于判断是否还能往术语数组中添加元素。如果为true，则表明需要数组已满，无法添加新的元素。
     * </p>
     * <p>
     *     通过Trie中的方法查找到sTerm的ID，当ID大于0时表示sTerm存在，并将ID添加到术语添加数组（iTermsAddedIDTemp）中。
     *     并且在术语频率数组（igTermListFreqTemp）中记录频率。如果超出数组长度，则扩展数组。
     * </p>
     * @param sTerm                 新术语的字符串
     * @author zhangsong
     */
    public void addTermToNewTermIndex(String sTerm) {
        if (sgTermList == null) {
            initialise(true, true, true, true);
        }
        if (sTerm.equals("")) {
            return;
        }
        boolean bDontAddMoreElements = false;
        if (igTermListCount == igTermListMax) {
            bDontAddMoreElements = true;
        }
        int iTermID = Trie.i_GetTriePositionForString(
                sTerm,
                sgTermList,
                igTermListLessPtr,
                igTermListMorePtr,
                1,
                igTermListCount,
                bDontAddMoreElements);
        if (iTermID > 0) {
            iTermsAddedIDTemp[++iTermsAddedIDTempCount] = iTermID;
            igTermListFreqTemp[iTermID]++;
            if (iTermID > igTermListCount) {
                igTermListCount = iTermID;
            }
        }
    }

    /**
     * 添加新的索引到主索引与Pos Neg值，新的索引包含情绪正负值和强度
     * <p>
     *     将每一个索引都加入到主索引中，并记录新索引的情绪政府和情绪强度
     * </p>
     * @param iCorrectPosClass          积极的类
     * @param iEstPosClass              估计的积极情绪类
     * @param iCorrectNegClass          消极的类
     * @param iEstNegClass              估计的消极情绪类
     * @author zhangsong
     */
    public void addNewIndexToMainIndexWithPosNegValues(int iCorrectPosClass, int iEstPosClass, int iCorrectNegClass, int iEstNegClass) {
        if (iCorrectNegClass > 0 && iCorrectPosClass > 0) {
            for (int iTerm = 1; iTerm <= iTermsAddedIDTempCount; iTerm++) {
                int iTermID = iTermsAddedIDTemp[iTerm];
                if (igTermListFreqTemp[iTermID] != 0) {
                    try {
                        igTermListNegCorrectClass[iTermID][iCorrectNegClass - 1]++;
                        igTermListPosCorrectClass[iTermID][iCorrectPosClass - 1]++;
                        igTermListPosClassDiff[iTermID] += iCorrectPosClass - iEstPosClass;
                        igTermListNegClassDiff[iTermID] += iCorrectNegClass + iEstNegClass;
                        igTermListFreq[iTermID]++;
                        iTermsAddedIDTemp[iTerm] = 0;
                    } catch (Exception e) {
                        System.err.println((new StringBuilder("[UnusedTermsClassificationIndex] Error trying to add Pos + Neg to index. "))
                                .append(e.getMessage()).toString());
                    }
                }
            }

        }
        iTermsAddedIDTempCount = 0;
    }
    
    /**
     * 将单尺度值的新索引添加到主索引中
     * <p>
     *     对每一个索引都加入到主索引中，并记录新索引的单尺度值
     * </p>
     * @param iCorrectScaleClass            单尺度
     * @param iEstScaleClass                估计的单尺度类
     * @author zhangsong
     */
    public void addNewIndexToMainIndexWithScaleValues(int iCorrectScaleClass, int iEstScaleClass) {
        for (int iTerm = 1; iTerm <= iTermsAddedIDTempCount; iTerm++) {
            int iTermID = iTermsAddedIDTemp[iTerm];
            if (igTermListFreqTemp[iTermID] != 0) {
                try {
                    igTermListScaleCorrectClass[iTermID][iCorrectScaleClass + 4]++;
                    igTermListScaleClassDiff[iTermID] += iCorrectScaleClass - iEstScaleClass;
                    igTermListFreq[iTermID]++;
                    iTermsAddedIDTemp[iTerm] = 0;
                } catch (Exception e) {
                    System.err.println((new StringBuilder("Error trying to add scale values to index. "))
                            .append(e.getMessage()).toString());
                }
            }
        }

        iTermsAddedIDTempCount = 0;
    }
    
    /**
     * 将新索引添加到主索引中，并记录新索引的三位一体值和强度
     * @param iCorrectTrinaryClass              三位一体的值
     * @param iEstTrinaryClass                  估计的三位一体
     * @author zhangsong
     */
    public void addNewIndexToMainIndexWithTrinaryValues(int iCorrectTrinaryClass, int iEstTrinaryClass) {
        for (int iTerm = 1; iTerm <= iTermsAddedIDTempCount; iTerm++) {
            int iTermID = iTermsAddedIDTemp[iTerm];
            if (igTermListFreqTemp[iTermID] != 0) {
                try {
                    igTermListTrinaryCorrectClass[iTermID][iCorrectTrinaryClass + 1]++;
                    igTermListTrinaryClassDiff[iTermID] += iCorrectTrinaryClass - iEstTrinaryClass;
                    igTermListFreq[iTermID]++;
                    iTermsAddedIDTemp[iTerm] = 0;
                } catch (Exception e) {
                    System.err.println((new StringBuilder("Error trying to add trinary values to index. "))
                            .append(e.getMessage()).toString());
                }
            }
        }

        iTermsAddedIDTempCount = 0;
    }

    /**
     * 将新索引添加到主索引中，并记录新索引的双精度二进制值和强度
     * @param iCorrectBinaryClass               二进制的值
     * @param iEstBinaryClass                   估计的二进制
     * @author zhangsong
     */
    public void addNewIndexToMainIndexWithBinaryValues(int iCorrectBinaryClass, int iEstBinaryClass) {
        for (int iTerm = 1; iTerm <= iTermsAddedIDTempCount; iTerm++) {
            int iTermID = iTermsAddedIDTemp[iTerm];
            if (igTermListFreqTemp[iTermID] != 0) {
                try {
                    igTermListBinaryClassDiff[iTermID] += iCorrectBinaryClass - iEstBinaryClass;
                    if (iCorrectBinaryClass == -1) {
                        iCorrectBinaryClass = 0;
                    }
                    igTermListBinaryCorrectClass[iTermID][iCorrectBinaryClass]++;
                    igTermListFreq[iTermID]++;
                    iTermsAddedIDTemp[iTerm] = 0;
                } catch (Exception e) {
                    System.err.println((new StringBuilder("Error trying to add scale values to index. "))
                            .append(e.getMessage()).toString());
                }
            }
        }

        iTermsAddedIDTempCount = 0;
    }

    /**
     * 变量初始化
     * <p>
     *     传入四个布尔类型的参数，通过true/false来对各个变量进行初始化
     * </p>
     * @param bInitialiseBinary         二进制
     * @param bInitialisePosNeg         情绪
     * @param bInitialiseScale          单尺度
     * @param bInitialiseTrinary        三位一体
     *
     * */
    public void initialise(boolean bInitialiseScale, boolean bInitialisePosNeg, boolean bInitialiseBinary, boolean bInitialiseTrinary) {
        igTermListCount = 0;
        igTermListMax = 50000;
        iTermsAddedIDTempCount = 0;
        sgTermList = new String[igTermListMax];
        igTermListLessPtr = new int[igTermListMax + 1];
        igTermListMorePtr = new int[igTermListMax + 1];
        igTermListFreq = new int[igTermListMax + 1];
        igTermListFreqTemp = new int[igTermListMax + 1];
        iTermsAddedIDTemp = new int[igTermListMax + 1];
        if (bInitialisePosNeg) {
            igTermListNegCorrectClass = new int[igTermListMax + 1][5];
            igTermListPosCorrectClass = new int[igTermListMax + 1][5];
            igTermListNegClassDiff = new int[igTermListMax + 1];
            igTermListPosClassDiff = new int[igTermListMax + 1];
        }
        if (bInitialiseScale) {
            igTermListScaleCorrectClass = new int[igTermListMax + 1][9];
            igTermListScaleClassDiff = new int[igTermListMax + 1];
        }
        if (bInitialiseBinary) {
            igTermListBinaryCorrectClass = new int[igTermListMax + 1][2];
            igTermListBinaryClassDiff = new int[igTermListMax + 1];
        }
        if (bInitialiseTrinary) {
            igTermListTrinaryCorrectClass = new int[igTermListMax + 1][3];
            igTermListTrinaryClassDiff = new int[igTermListMax + 1];
        }
    }

    /**
     * UC-19 打印积极情绪和消极情绪的检测结果
     * @param sOutputFile               输出到的文件路径
     * @param iMinFreq                  最小频率
     * @author zhangsong
     */
    public void printIndexWithPosNegValues(String sOutputFile, int iMinFreq) {
        try {
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
            wWriter.write((new StringBuilder("Term\tTermFreq >= ")).append(iMinFreq)
                    .append("\t").append("PosClassDiff (correct-estimate)\t")
                    .append("NegClassDiff\t").append("PosClassAvDiff\t")
                    .append("NegClassAvDiff\t").toString());
            for (int i = 1; i <= 5; i++) {
                wWriter.write((new StringBuilder("CorrectClass")).append(i).append("pos\t").toString());
            }
            for (int i = 1; i <= 5; i++) {
                wWriter.write((new StringBuilder("CorrectClass")).append(i).append("neg\t").toString());
            }
            wWriter.write("\n");
            if (igTermListCount > 0) {
                for (int iTerm = 1; iTerm <= igTermListCount; iTerm++) {
                    if (igTermListFreq[iTerm] >= iMinFreq) {
                        wWriter.write((new StringBuilder(String.valueOf(sgTermList[iTerm]))).append("\t")
                                .append(igTermListFreq[iTerm])
                                .append("\t").append(igTermListPosClassDiff[iTerm])
                                .append("\t").append(igTermListNegClassDiff[iTerm])
                                .append("\t").append((float) igTermListPosClassDiff[iTerm] / (float) igTermListFreq[iTerm])
                                .append("\t").append((float) igTermListNegClassDiff[iTerm] / (float) igTermListFreq[iTerm])
                                .append("\t").toString());
                        for (int i = 0; i < 5; i++) {
                            wWriter.write((new StringBuilder(String.valueOf(igTermListPosCorrectClass[iTerm][i]))).append("\t").toString());
                        }
                        for (int i = 0; i < 5; i++) {
                            wWriter.write((new StringBuilder(String.valueOf(igTermListNegCorrectClass[iTerm][i]))).append("\t").toString());
                        }
                        wWriter.write("\n");
                    }
                }
            } else {
                wWriter.write("No terms found in corpus!\n");
            }
            wWriter.close();
        } catch (IOException e) {
            System.err.println((new StringBuilder("Error printing index to "))
                    .append(sOutputFile).toString());
            e.printStackTrace();
        }
    }

    /**
     * UC-19 打印单尺度检测的结果
     * @param sOutputFile 输出到的文件路径
     * @param iMinFreq 最小频率
     * @author zhangsong
     */
    public void printIndexWithScaleValues(String sOutputFile, int iMinFreq) {
        try {
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
            wWriter.write("Term\tTermFreq\tScaleClassDiff (correct-estimate)\tScaleClassAvDiff\t");
            for (int i = -4; i <= 4; i++) {
                wWriter.write((new StringBuilder("CorrectClass")).append(i).append("\t").toString());
            }
            wWriter.write("\n");
            for (int iTerm = 1; iTerm <= igTermListCount; iTerm++) {
                if (igTermListFreq[iTerm] > iMinFreq) {
                    wWriter.write((new StringBuilder(String.valueOf(sgTermList[iTerm])))
                            .append("\t").append(igTermListFreq[iTerm]).append("\t")
                            .append(igTermListScaleClassDiff[iTerm]).append("\t")
                            .append((float) igTermListScaleClassDiff[iTerm] / (float) igTermListFreq[iTerm])
                            .append("\t").toString());
                    for (int i = 0; i < 9; i++) {
                        wWriter.write((new StringBuilder(String.valueOf(igTermListScaleCorrectClass[iTerm][i]))).append("\t").toString());
                    }
                    wWriter.write("\n");
                }
            }
            wWriter.close();
        } catch (IOException e) {
            System.err.println((new StringBuilder("Error printing Scale index to "))
                    .append(sOutputFile).toString());
            e.printStackTrace();
        }
    }

    /**
     * UC-19 打印三位一体检测的结果
     * @param sOutputFile 输出到的文件路径
     * @param iMinFreq 最小频率
     * @author zhangsong
     */
    public void printIndexWithTrinaryValues(String sOutputFile, int iMinFreq) {
        try {
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
            wWriter.write("Term\tTermFreq\tTrinaryClassDiff (correct-estimate)\tTrinaryClassAvDiff\t");
            for (int i = -1; i <= 1; i++) {
                wWriter.write((new StringBuilder("CorrectClass")).append(i).append("\t").toString());
            }
            wWriter.write("\n");
            for (int iTerm = 1; iTerm <= igTermListCount; iTerm++) {
                if (igTermListFreq[iTerm] > iMinFreq) {
                    wWriter.write((new StringBuilder(String.valueOf(sgTermList[iTerm]))).append("\t")
                            .append(igTermListFreq[iTerm]).append("\t")
                            .append(igTermListTrinaryClassDiff[iTerm]).append("\t")
                            .append((float) igTermListTrinaryClassDiff[iTerm] / (float) igTermListFreq[iTerm])
                            .append("\t").toString());
                    for (int i = 0; i < 3; i++) {
                        wWriter.write((new StringBuilder(String.valueOf(igTermListTrinaryCorrectClass[iTerm][i]))).append("\t").toString());
                    }
                    wWriter.write("\n");
                }
            }
            wWriter.close();
        } catch (IOException e) {
            System.err.println((new StringBuilder("Error printing Trinary index to "))
                    .append(sOutputFile).toString());
            e.printStackTrace();
        }
    }

    /**
     * UC-19 打印双精度二进制检测的结果
     * @param sOutputFile 输出到的文件路径
     * @param iMinFreq 最小频率
     * @author zhangsong
     */
    public void printIndexWithBinaryValues(String sOutputFile, int iMinFreq) {
        try {
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
            wWriter.write("Term\tTermFreq\tBinaryClassDiff (correct-estimate)\tBinaryClassAvDiff\t");
            wWriter.write("CorrectClass-1\tCorrectClass1\t");
            wWriter.write("\n");
            for (int iTerm = 1; iTerm <= igTermListCount; iTerm++) {
                if (igTermListFreq[iTerm] > iMinFreq) {
                    wWriter.write((new StringBuilder(String.valueOf(sgTermList[iTerm])))
                            .append("\t").append(igTermListFreq[iTerm]).append("\t")
                            .append(igTermListBinaryClassDiff[iTerm]).append("\t")
                            .append((float) igTermListBinaryClassDiff[iTerm] / (float) igTermListFreq[iTerm])
                            .append("\t").toString());
                    for (int i = 0; i < 2; i++) {
                        wWriter.write((new StringBuilder(String.valueOf(igTermListBinaryCorrectClass[iTerm][i]))).append("\t").toString());
                    }
                    wWriter.write("\n");
                }
            }
            wWriter.close();
        } catch (IOException e) {
            System.err.println((new StringBuilder("Error printing Binary index to ")).append(sOutputFile).toString());
            e.printStackTrace();
        }
    }
}

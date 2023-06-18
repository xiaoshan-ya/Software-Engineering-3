// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   Corpus.java

package uk.ac.wlv.sentistrength;

import java.io.*;


import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.sentistrength.classificationresource.ClassificationResources;
import uk.ac.wlv.sentistrength.classificationresource.ClassificationStatistics;
import uk.ac.wlv.sentistrength.classificationresource.UnusedTermsClassificationIndex;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions, ClassificationResources, UnusedTermsClassificationIndex, Paragraph, 
//            ClassificationStatistics, SentimentWords

/**
 * 语料库
 */
public class Corpus {
    /**
     * 分类选项
     */
    public ClassificationOptions options;
    /**
     * 分类资源
     */
    public ClassificationResources resources;
    private Paragraph[] paragraph;
    private int igParagraphCount;
    private int[] igPosCorrect;
    private int[] igNegCorrect;
    private int[] igTrinaryCorrect;
    private int[] igScaleCorrect;
    private int[] igPosClass;
    private int[] igNegClass;
    private int[] igTrinaryClass;
    private int[] igScaleClass;
    private boolean bgCorpusClassified;
    private int[] igSentimentIDList;
    private int igSentimentIDListCount;
    private int[] igSentimentIDParagraphCount;
    private boolean bSentimentIDListMade;
    UnusedTermsClassificationIndex unusedTermsClassificationIndex;
    private boolean[] bgSupcorpusMember;
    /**
     * 子语料库成员数量
     */
    int igSupcorpusMemberCount;

    /**
     * 语料库构造函数
     * @author zhangsong
     */
    public Corpus() {
        options = new ClassificationOptions();
        resources = new ClassificationResources();
        igParagraphCount = 0;
        bgCorpusClassified = false;
        igSentimentIDListCount = 0;
        bSentimentIDListMade = false;
        unusedTermsClassificationIndex = null;
        this.initialise();
    }

    /**
     * 根据指定的分类选项为语料库编制索引
     * <p>
     *     首先创建一个未使用术语分类索引对象。根据不同的模式进行语料库索引编制
     *     （四种模式：单尺度、双精度二进制、三元值、正负情绪值）
     *     如果单尺度模式==true，则对于单尺度模式进行术语分类，然后将段落添加到具有单尺度的索引中
     * </p>
     * @author zhangsong
     */
    public void indexClassifiedCorpus() {
        // 未使用术语分类索引
        unusedTermsClassificationIndex = new UnusedTermsClassificationIndex();
        if (options.bgScaleMode) {
            unusedTermsClassificationIndex.initialise(true, false, false, false);
            for (int i = 1; i <= igParagraphCount; i++) {
                paragraph[i].addParagraphToIndexWithScaleValues(unusedTermsClassificationIndex, igScaleCorrect[i], igScaleClass[i]);
            }
        } else
        if (options.bgTrinaryMode && options.bgBinaryVersionOfTrinaryMode) {
            unusedTermsClassificationIndex.initialise(false, false, true, false);
            for (int i = 1; i <= igParagraphCount; i++) {
                paragraph[i].addParagraphToIndexWithBinaryValues(unusedTermsClassificationIndex, igTrinaryCorrect[i], igTrinaryClass[i]);
            }
        } else
        if (options.bgTrinaryMode && !options.bgBinaryVersionOfTrinaryMode) {
            unusedTermsClassificationIndex.initialise(false, false, false, true);
            for (int i = 1; i <= igParagraphCount; i++) {
                paragraph[i].addParagraphToIndexWithTrinaryValues(unusedTermsClassificationIndex, igTrinaryCorrect[i], igTrinaryClass[i]);
            }
        } else {
            unusedTermsClassificationIndex.initialise(false, true, false, false);
            for (int i = 1; i <= igParagraphCount; i++) {
                paragraph[i].addParagraphToIndexWithPosNegValues(
                        unusedTermsClassificationIndex,
                        igPosCorrect[i],
                        igPosClass[i],
                        igNegCorrect[i],
                        igNegClass[i]
                );
            }
        }
    }

    /**
     * 打印语料库中未使用的术语分类索引
     * <p>
     *     如果还没有分类，则首先计算情感分数。
     *     如果未使用的术语分类索引没有被索引，它索引分类语料库。
     *     然后它根据比例模式、三元模式的二进制版本、三元模式或正负值打印索引。
     *     最后打印术语权重已保存到文件
     * </p>
     * @param saveFile 文件名
     * @param iMinFreq 最小频率
     * @author zhangsong
     */
    public void printCorpusUnusedTermsClassificationIndex(String saveFile, int iMinFreq) {
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        if (unusedTermsClassificationIndex == null) {
            indexClassifiedCorpus();
        }
        if (options.bgScaleMode) {
            unusedTermsClassificationIndex.printIndexWithScaleValues(saveFile, iMinFreq);
        } else
        if (options.bgTrinaryMode && options.bgBinaryVersionOfTrinaryMode) {
            unusedTermsClassificationIndex.printIndexWithBinaryValues(saveFile, iMinFreq);
        } else
        if (options.bgTrinaryMode && !options.bgBinaryVersionOfTrinaryMode) {
            unusedTermsClassificationIndex.printIndexWithTrinaryValues(saveFile, iMinFreq);
        } else {
            unusedTermsClassificationIndex.printIndexWithPosNegValues(saveFile, iMinFreq);
        }
        System.err.println((new StringBuilder("Term weights saved to ")).append(saveFile).toString());
    }

    /**
     * 指示特定段落是否属于子语料库
     * <p>
     *     如果bSubcorpusMember==ture（属于子语料库），则将bgSupcorpusMember[i]==true，同时用igSupcorpusMemberCount记录段落数
     * </p>
     * @param bSubcorpusMember 子语料库成员
     * @author zhangsong
     */
    public void setSubcorpus(boolean[] bSubcorpusMember) {
        igSupcorpusMemberCount = 0;
        for (int i = 0; i <= igParagraphCount; i++) {
            if (bSubcorpusMember[i]) {
                bgSupcorpusMember[i] = true;
                igSupcorpusMemberCount++;
            } else {
                bgSupcorpusMember[i] = false;
            }
        }

    }

    /**
     * 将整个语料库用作子语料库
     * <p>
     *     将整个段落中的bgSupcorpusMember=true，igSupcorpusMemberCount反映了段落中枢
     * </p>
     * @author zhangsong
     */
    public void useWholeCorpusNotSubcorpus() {
        for (int i = 0; i <= igParagraphCount; i++) {
            bgSupcorpusMember[i] = true;
        }
        igSupcorpusMemberCount = igParagraphCount;
    }

    /**
     * 得到语料库的大小

     * @return int 段落总数
     * @author zhangsong
     */
    public int getCorpusSize() {
        return igParagraphCount;
    }

    /**
     * 将单个输入文本设置为要分析的语料库
     * <p>
     *     为sText创建一个Paragraph对象，并设置积极和消极两个情感标签，
     *     然后将此 Paragraph 对象设置为段落数组的唯一元素。
     * </p>
     * <p>
     *     将变量设置为适合但文本语料库的值，最后将整个语料作为子语料。
     * </p>
     * @param sText 文本字符串
     * @param iPosCorrect 正确的积极情感标签
     * @param iNegCorrect 正确的消极情感标签
     * @return boolean 是否设置成功
     * @author zhangsong
     */
    public boolean setSingleTextAsCorpus(String sText, int iPosCorrect, int iNegCorrect) {
        if (resources == null && !resources.initialise(options)) {
            return false;
        }
        igParagraphCount = 2; //？？？？？？？？？？？？？？？？
        paragraph = new Paragraph[igParagraphCount];
        igPosCorrect = new int[igParagraphCount];
        igNegCorrect = new int[igParagraphCount];
        igTrinaryCorrect = new int[igParagraphCount];
        igScaleCorrect = new int[igParagraphCount];
        bgSupcorpusMember = new boolean[igParagraphCount];
        igParagraphCount = 1;
        paragraph[igParagraphCount] = new Paragraph();
        paragraph[igParagraphCount].setParagraph(sText, resources, options);
        igPosCorrect[igParagraphCount] = iPosCorrect;
        if (iNegCorrect < 0) {
            iNegCorrect *= -1;
        }
        igNegCorrect[igParagraphCount] = iNegCorrect;
        useWholeCorpusNotSubcorpus();
        return true;
    }

    /**
     * 设置语料库，处理成带有相关分类信息的语料库
     * <p>
     *     首先检查资源是否被初始化，没有返回false。
     *     然后计算文本的行数，如果&lt;=2则返回false，如果==true则初始化数组保存每个段落的信息。
     *     接着进行一系列初始化。
     * </p>
     * @param sInFilenameAndPath 文件名和文件路径
     * @return boolean 返回语料库是否设置成功
     * @author zhangsong
     */
    public boolean setCorpus(String sInFilenameAndPath) {
        if (resources == null && !resources.initialise(options)) {
            return false;
        }
        igParagraphCount = FileOps.i_CountLinesInTextFile(sInFilenameAndPath) + 1;
        if (igParagraphCount <= 2) {
            igParagraphCount = 0;
            return false;
        }
        paragraph = new Paragraph[igParagraphCount];
        igPosCorrect = new int[igParagraphCount];
        igNegCorrect = new int[igParagraphCount];
        igTrinaryCorrect = new int[igParagraphCount];
        igScaleCorrect = new int[igParagraphCount];
        bgSupcorpusMember = new boolean[igParagraphCount];
        igParagraphCount = 0;
        try {
            BufferedReader rReader = new BufferedReader(new FileReader(sInFilenameAndPath));
            String sLine;
            if (rReader.ready()) {
                sLine = rReader.readLine();
            }
            while ((sLine = rReader.readLine()) != null) {
                if (!sLine.equals("")) {
                    paragraph[++igParagraphCount] = new Paragraph();
                    int iLastTabPos = sLine.lastIndexOf("\t");
                    int iFirstTabPos = sLine.indexOf("\t");
                    if (iFirstTabPos < iLastTabPos || iFirstTabPos > 0 && (options.bgTrinaryMode || options.bgScaleMode)) {
                        paragraph[igParagraphCount].setParagraph(sLine.substring(iLastTabPos + 1), resources, options);
                        if (options.bgTrinaryMode) {
                            try {
                                igTrinaryCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(0, iFirstTabPos).trim());
                            } catch (Exception e) {
                                System.err.println((new StringBuilder("Trinary classification could not be read and will be ignored!: "))
                                        .append(sLine).toString());
                                igTrinaryCorrect[igParagraphCount] = 999;
                            }
                            if (igTrinaryCorrect[igParagraphCount] > 1 || igTrinaryCorrect[igParagraphCount] < -1) {
                                System.err.println((new StringBuilder("Trinary classification out of bounds and will be ignored!: "))
                                        .append(sLine).toString());
                                igParagraphCount--;
                            } else if (options.bgBinaryVersionOfTrinaryMode && igTrinaryCorrect[igParagraphCount] == 0) {
                                System.err.println((new StringBuilder("Warning, unexpected 0 in binary classification!: "))
                                        .append(sLine).toString());
                            }
                        } else if (options.bgScaleMode) {
                            try {
                                igScaleCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(0, iFirstTabPos).trim());
                            } catch (Exception e) {
                                System.err.println((new StringBuilder("Scale classification could not be read and will be ignored!: "))
                                        .append(sLine).toString());
                                igScaleCorrect[igParagraphCount] = 999;
                            }
                            if (igScaleCorrect[igParagraphCount] > 4 || igScaleCorrect[igParagraphCount] < -4) {
                                System.err.println((new StringBuilder(
                                        "Scale classification out of bounds (-4 to +4) and will be ignored!: "))
                                        .append(sLine).toString());
                                igParagraphCount--;
                            }
                        } else {
                            try {
                                igPosCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(0, iFirstTabPos).trim());
                                igNegCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(iFirstTabPos + 1, iLastTabPos).trim());
                                if (igNegCorrect[igParagraphCount] < 0) {
                                    igNegCorrect[igParagraphCount] = -igNegCorrect[igParagraphCount];
                                }
                            } catch (Exception e) {
                                System.err.println((new StringBuilder(
                                        "Positive or negative classification could not be read and will be ignored!: "))
                                        .append(sLine).toString());
                                igPosCorrect[igParagraphCount] = 0;
                            }
                            if (igPosCorrect[igParagraphCount] > 5 || igPosCorrect[igParagraphCount] < 1) {
                                System.err.println((new StringBuilder(
                                        "Warning, positive classification out of bounds and line will be ignored!: "))
                                        .append(sLine).toString());
                                igParagraphCount--;
                            } else if (igNegCorrect[igParagraphCount] > 5 || igNegCorrect[igParagraphCount] < 1) {
                                System.err.println((new StringBuilder(
                                        "Warning, negative classification out of bounds (must be 1,2,3,4, or 5, with or without -) "
                                                + "and line will be ignored!: "))
                                        .append(sLine).toString());
                                igParagraphCount--;
                            }
                        }
                    } else {
                        if (iFirstTabPos >= 0) {
                            if (options.bgTrinaryMode) {
                                igTrinaryCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(0, iFirstTabPos).trim());
                            }
                            sLine = sLine.substring(iFirstTabPos + 1);
                        } else if (options.bgTrinaryMode) {
                            igTrinaryCorrect[igParagraphCount] = 0;
                            paragraph[igParagraphCount].setParagraph(sLine, resources, options);
                        }
                        igPosCorrect[igParagraphCount] = 0;
                        igNegCorrect[igParagraphCount] = 0;
                    }
                }
            }
            rReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        useWholeCorpusNotSubcorpus();
        System.err.println((new StringBuilder("Number of texts in corpus: ")).append(igParagraphCount).toString());
        return true;
    }

    /**
     * 资源是否初始化
     * @return boolean 如果资源初始化返回true
     * @author zhangsong
     */
    public boolean initialise() {
        return resources.initialise(options);
    }

    /**
     * 重新计算（更新）语料库的情感分数
     * <p>
     *    首先循环遍历语料库中的所有段落，并检查当前段落是否是子语料库的成员，
     *    如果是，则重新计算情感分数
     * </p>
     * @author zhangsong
     */
    public void reCalculateCorpusSentimentScores() {
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i]) {
                paragraph[i].recalculateParagraphSentimentScores();
            }
        }

        calculateCorpusSentimentScores();
    }

    /**
     * UC-1 得到语料库某个成员积极情感分数
     * <p>
     *      计算paragraph[i]的积极情感分数
     * </p>
     * @param i 语料库成员索引
     * @return int 查询成员的积极情感得分
     * @author zhangsong
     */
    public int getCorpusMemberPositiveSentimentScore(int i) {
        if (i < 1 || i > igParagraphCount) {
            return 0;
        } else {
            return paragraph[i].getParagraphPositiveSentiment();
        }
    }

    /**
     * UC-1 得到语料库某个成员消极情感分数
     * <p>
     *      计算paragraph[i]的消极情感分数
     * </p>
     * @param i 语料库成员索引
     * @return int 查询成员的消极情感得分
     * @author zhangsong
     */
    public int getCorpusMemberNegativeSentimentScore(int i) {
        if (i < 1 || i > igParagraphCount) {
            return 0;
        } else {
            return paragraph[i].getParagraphNegativeSentiment();
        }
    }

    /**
     * UC-1 计算整个语料库情感分数
     * <p>
     *     首先检查语料库中是否有段落，如果没有直接返回。
     *     然后创建四种数组分别存储分数
     * </p>
     * <p>
     *     遍历语料库中的每个段落，检查它是否是超级语料库（包含当前语料库的更大语料库）的成员。
     *     如果是，它会检索该段落的正面、负面、三元和尺度情绪分数，并将它们存储在相应的数组中。
     * </p>
     * <p>
     *     最后用一个bgCorpusClassified来指示语料库情感分数已被计算
     * </p>
     * @author zhangsong
     */
    public void calculateCorpusSentimentScores() {
        if (igParagraphCount == 0) {
            return;
        }
        if (igPosClass == null || igPosClass.length < igPosCorrect.length) {
            igPosClass = new int[igParagraphCount + 1];
            igNegClass = new int[igParagraphCount + 1];
            igTrinaryClass = new int[igParagraphCount + 1];
            igScaleClass = new int[igParagraphCount + 1];
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i]) {
                igPosClass[i] = paragraph[i].getParagraphPositiveSentiment();
                igNegClass[i] = paragraph[i].getParagraphNegativeSentiment();
                if (options.bgTrinaryMode) {
                    igTrinaryClass[i] = paragraph[i].getParagraphTrinarySentiment();
                }
                if (options.bgScaleMode) {
                    igScaleClass[i] = paragraph[i].getParagraphScaleSentiment();
                }
            }
        }

        bgCorpusClassified = true;
    }

    /**
     * 根据特定情感词 ID 和包含该词所需的最少段落数对分类语料库进行重新分类
     * <p>
     *     如果语料库中没有段落、情感词ID列表没有制作、没有找到指定的ID、段落数少于最少段落数都将直接返回。
     * </p>
     * <p>
     *     遍历每一个段落，如果段落属于超级语料库成员，
     *     则调用paragraph[i].reClassifyClassifiedParagraphForSentimentChange(iSentimentWordID)对于当前段落重新分类
     *     同时对四种情感数组的分类进行分类
     * </p>
     * <p>
     *     最后将语料库情感词分类标志=true
     * </p>
     * @param iSentimentWordID 指定情感词的ID
     * @param iMinParasToContainWord 包含情感词的最少段落数（条件）
     * @author zhangsong
     */
    public void reClassifyClassifiedCorpusForSentimentChange(int iSentimentWordID, int iMinParasToContainWord) {
        if (igParagraphCount == 0) {
            return;
        }
        if (!bSentimentIDListMade) {
            makeSentimentIDListForCompleteCorpusIgnoringSubcorpus();
        }
        int iSentimentWordIDArrayPos = Sort.i_FindIntPositionInSortedArray(iSentimentWordID, igSentimentIDList, 1, igSentimentIDListCount);
        if (iSentimentWordIDArrayPos == -1 || igSentimentIDParagraphCount[iSentimentWordIDArrayPos] < iMinParasToContainWord) {
            return;
        }
        igPosClass = new int[igParagraphCount + 1];
        igNegClass = new int[igParagraphCount + 1];
        if (options.bgTrinaryMode) {
            igTrinaryClass = new int[igParagraphCount + 1];
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i]) {
                paragraph[i].reClassifyClassifiedParagraphForSentimentChange(iSentimentWordID);
                igPosClass[i] = paragraph[i].getParagraphPositiveSentiment();
                igNegClass[i] = paragraph[i].getParagraphNegativeSentiment();
                if (options.bgTrinaryMode) {
                    igTrinaryClass[i] = paragraph[i].getParagraphTrinarySentiment();
                }
                if (options.bgScaleMode) {
                    igScaleClass[i] = paragraph[i].getParagraphScaleSentiment();
                }
            }
        }

        bgCorpusClassified = true;
    }

    /**
     * 打印语料库情感分数
     * <p>
     *      遍历每一个段落，如果属于超级语料库，则制表打印所有信息
     * </p>
     * @param sOutFilenameAndPath 输出到的文件名和文件路径
     * @return boolean 返回是否成功打印
     * @author zhangsong
     */
    public boolean printCorpusSentimentScores(String sOutFilenameAndPath) {
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        try {
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutFilenameAndPath));
            wWriter.write("Correct+\tCorrect-\tPredict+\tPredict-\tText\n");
            for (int i = 1; i <= igParagraphCount; i++) {
                if (bgSupcorpusMember[i]) {
                    wWriter.write((new StringBuilder(String.valueOf(igPosCorrect[i]))).append("\t")
                            .append(igNegCorrect[i]).append("\t")
                            .append(igPosClass[i]).append("\t")
                            .append(igNegClass[i]).append("\t")
                            .append(paragraph[i].getTaggedParagraph()).append("\n")
                            .toString());
                }
            }
            wWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * UC-24、12 计算语料库中正确分类的积极情绪的比例
     * <p>
     *     如果没有高级语料库成员返回0。
     *     如果有，将正确分类的积极情绪的数量 / 语料库中的段落总数
     * </p>

     * @return float 正确分类的积极情绪的比例
     * @author zhangsong
     */
    public float getClassificationPositiveAccuracyProportion() {
        if (igSupcorpusMemberCount == 0) {
            return 0.0F;
        } else {
            return (float) getClassificationPositiveNumberCorrect() / (float) igSupcorpusMemberCount;
        }
    }

    /**
     * UC-24、12 计算语料库中正确分类的消极情绪的比例
     * <p>
     *     如果没有高级语料库成员返回0。
     *     如果有，将正确分类的消极情绪的数量 / 语料库中的段落总数
     * </p>

     * @return float 正确分类的消极情绪的比例
     * @author zhangsong
     */
    public float getClassificationNegativeAccuracyProportion() {
        if (igSupcorpusMemberCount == 0) {
            return 0.0F;
        } else {
            return (float) getClassificationNegativeNumberCorrect() / (float) igSupcorpusMemberCount;
        }
    }

    /**
     * UC-12 预测消极情感的基线准确率比例

     * @return double 基线负准确率
     * @author zhangsong
     */
    public double getBaselineNegativeAccuracyProportion() {
        if (igParagraphCount == 0) {
            return 0.0D;
        } else {
            return ClassificationStatistics.baselineAccuracyMajorityClassProportion(igNegCorrect, igParagraphCount);
        }
    }

    /**
     * UC-12 预测积极情感的基线准确率比例

     * @return double 基线正准确率
     * @author zhangsong
     */
    public double getBaselinePositiveAccuracyProportion() {
        if (igParagraphCount == 0) {
            return 0.0D;
        } else {
            return ClassificationStatistics.baselineAccuracyMajorityClassProportion(igPosCorrect, igParagraphCount);
        }
    }

    /**
     * 得到消极情感正确分类的数量
     * <p>
     *     它循环遍历作为超级语料库成员的每个段落，
     *     并检查该段落的真实负面情绪 (igNegCorrect[i]) 是否与分类器 (-igNegClass[i]) 分配的负面情绪相匹配
     *     如果匹配则数量++
     * </p>

     * @return int 消极情感正确分类的数量
     * @author zhangsong
     */
    public int getClassificationNegativeNumberCorrect() {
        if (igParagraphCount == 0) {
            return 0;
        }
        int iMatches = 0;
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i] && igNegCorrect[i] == -igNegClass[i]) {
                iMatches++;
            }
        }

        return iMatches;
    }

    /**
     * 得到积极情感正确分类的数量
     * <p>
     *     它循环遍历作为超级语料库成员的每个段落，
     *     并检查该段落的真实积极情绪 (igNegCorrect[i]) 是否与分类器 (-igNegClass[i]) 分配的积极情绪相匹配
     *     如果匹配则数量++
     * </p>

     * @return int 积极情感正确分类的数量
     * @author zhangsong
     */
    public int getClassificationPositiveNumberCorrect() {
        if (igParagraphCount == 0) {
            return 0;
        }
        int iMatches = 0;
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i] && igPosCorrect[i] == igPosClass[i]) {
                iMatches++;
            }
        }

        return iMatches;
    }

    /**
     * 得到正确积极情绪分类与预测积极情绪分类之间的平均差
     * <p>
     *     得到|igPosCorrect-igPosClass|的差值，最后计算平均值
     * </p>

     * @return double 正确积极情绪分类与预测积极情绪分类之间的平均差
     * @author zhangsong
     */
    public double getClassificationPositiveMeanDifference() {
        if (igParagraphCount == 0) {
            return 0.0D;
        }
        double fTotalDiff = 0.0D;
        int iTotal = 0;
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i]) {
                fTotalDiff += Math.abs(igPosCorrect[i] - igPosClass[i]);
                iTotal++;
            }
        }

        if (iTotal > 0) {
            return fTotalDiff / (double) iTotal;
        } else {
            return 0.0D;
        }
    }

    /**
     * 得到正确积极情绪分类与预测积极情绪分类之间的总差值
     * <p>
     *     得到|igPosCorrect-igPosClass|的差值累加和
     * </p>

     * @return double 积极情感正确差值的总差值
     * @author zhangsong
     */
    public int getClassificationPositiveTotalDifference() {
        if (igParagraphCount == 0) {
            return 0;
        }
        int iTotalDiff = 0;
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i]) {
                iTotalDiff += Math.abs(igPosCorrect[i] - igPosClass[i]);
            }
        }
        return iTotalDiff;
    }

    /**
     * 得到三元情绪分类与预测分类完全符合的正确数
     * <p>
     *     如果三元情绪分类与预测分类完全一致，数量++
     * </p>

     * @return int 三元情绪分类与预测分类完全符合的正确数
     * @author zhangsong
     */
    public int getClassificationTrinaryNumberCorrect() {
        if (igParagraphCount == 0) {
            return 0;
        }
        int iTrinaryCorrect = 0;
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i] && igTrinaryCorrect[i] == igTrinaryClass[i]) {
                iTrinaryCorrect++;
            }
        }
        return iTrinaryCorrect;
    }

    /**
     * 计算整个语料库的正确量表分数与预测量表分数之间的相关性

     * @return float 返回整个语料库正确分数与预测分数相关性
     * @author zhangsong
     */
    public float getClassificationScaleCorrelationWholeCorpus() {
        if (igParagraphCount == 0) {
            return 0.0F;
        } else {
            return (float) ClassificationStatistics.correlation(igScaleCorrect, igScaleClass, igParagraphCount);
        }
    }

    /**
     * 计算正确分类段落的比例

     * @return float 正确分类段落的比例
     * @author zhangsong
     */
    public float getClassificationScaleAccuracyProportion() {
        if (igSupcorpusMemberCount == 0) {
            return 0.0F;
        } else {
            return (float) getClassificationScaleNumberCorrect() / (float) igSupcorpusMemberCount;
        }
    }

    /**
     * 计算分类器预测的积极情绪得分与语料库中实际积极情绪得分之间的相关性

     * @return float 积极情绪得分与语料库中实际积极情绪得分之间的相关性
     * @author zhangsong
     */
    public float getClassificationPosCorrelationWholeCorpus() {
        if (igParagraphCount == 0) {
            return 0.0F;
        } else {
            return (float) ClassificationStatistics.correlationAbs(igPosCorrect, igPosClass, igParagraphCount);
        }
    }

    /**
     * 计算分类器预测的消极情绪得分与语料库中实际消极情绪得分之间的相关性

     * @return float 消极情绪得分与语料库中实际消极情绪得分之间的相关性
     * @author zhangsong
     */
    public float getClassificationNegCorrelationWholeCorpus() {
        if (igParagraphCount == 0) {
            return 0.0F;
        } else {
            return (float) ClassificationStatistics.correlationAbs(igNegCorrect, igNegClass, igParagraphCount);
        }
    }

    /**
     * 计算比例情绪的预测正确数
     * <p>
     *     如果比例情绪与预测比例情绪分类一致，数量++
     * </p>

     * @return int 比例情绪的预测正确数
     * @author zhangsong
     */
    public int getClassificationScaleNumberCorrect() {
        if (igParagraphCount == 0) {
            return 0;
        }
        int iScaleCorrect = 0;
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i] && igScaleCorrect[i] == igScaleClass[i]) {
                iScaleCorrect++;
            }
        }
        return iScaleCorrect;
    }

    /**
     * 计算语料库中所有段落的正确负面情绪分数和预测负面情绪分数之间的总差

     * @return int 正确负面情绪分数和预测负面情绪分数之间的总差
     * @author zhangsong
     */
    public int getClassificationNegativeTotalDifference() {
        if (igParagraphCount == 0) {
            return 0;
        }
        int iTotalDiff = 0;
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i]) {
                iTotalDiff += Math.abs(igNegCorrect[i] + igNegClass[i]);
            }
        }
        return iTotalDiff;
    }

    /**
     * 计算语料库中所有段落的正确负面情绪分数和预测负面情绪分数之间的总平均差

     * @return double 正确负面情绪分数和预测负面情绪分数之间的总平均差
     * @author zhangsong
     */
    public double getClassificationNegativeMeanDifference() {
        if (igParagraphCount == 0) {
            return 0.0D;
        }
        double fTotalDiff = 0.0D;
        int iTotal = 0;
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        for (int i = 1; i <= igParagraphCount; i++) {
            if (bgSupcorpusMember[i]) {
                fTotalDiff += Math.abs(igNegCorrect[i] + igNegClass[i]);
                iTotal++;
            }
        }
        if (iTotal > 0) {
            return fTotalDiff / (double) iTotal;
        } else {
            return 0.0D;
        }
    }

    /**
     * printClassificationResultsSummary_NOT_DONE
     * @param sOutFilenameAndPath description
     * @return boolean
     * @author zhangsong
     */
    public boolean printClassificationResultsSummaryNOTDONE(String sOutFilenameAndPath) {
        if (!bgCorpusClassified) {
            calculateCorpusSentimentScores();
        }
        try {
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutFilenameAndPath));
            for (int i = 1; i <= igParagraphCount; i++) {
                boolean tmp = bgSupcorpusMember[i];
            }

            wWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 为完整语料库创建情感 ID 列表，忽略任何子语料库划分
     * <p>
     *      先遍历语料库的每个段落，为每个段落创建一个情感ID列表，
     *      如果列表不为空，它会将情绪 ID 的数量添加到 igSentimentIDListCount
     * </p>
     * <p>
     *     如果语料库存在情感ID，则再次遍历每个段落，
     *     获取情绪 ID 列表，如果它不为空，则将情绪 ID 添加到 igSentimentIDList 数组。
     * </p>
     * <p>
     *     接着对于情绪ID列表igSentimentIDList进行你升序排序，
     *     再次遍历每个段落，。
     *     bSentimentIDListMade=true，指示已创建情绪 ID 列表
     * </p>

     * @author zhangsong
     */
    public void makeSentimentIDListForCompleteCorpusIgnoringSubcorpus() {
        igSentimentIDListCount = 0;
        for (int i = 1; i <= igParagraphCount; i++) {
            paragraph[i].makeSentimentIDList();
            if (paragraph[i].getSentimentIDList() != null) {
                igSentimentIDListCount += paragraph[i].getSentimentIDList().length;
            }
        }

        if (igSentimentIDListCount > 0) {
            igSentimentIDList = new int[igSentimentIDListCount + 1];
            igSentimentIDParagraphCount = new int[igSentimentIDListCount + 1];
            igSentimentIDListCount = 0;
            for (int i = 1; i <= igParagraphCount; i++) {
                int[] sentenceIDList = paragraph[i].getSentimentIDList();
                if (sentenceIDList != null) {
                    for (int j = 0; j < sentenceIDList.length; j++) {
                        if (sentenceIDList[j] != 0) {
                            igSentimentIDList[++igSentimentIDListCount] = sentenceIDList[j];
                        }
                    }
                }
            }

            Sort.quickSortInt(igSentimentIDList, 1, igSentimentIDListCount);
            for (int i = 1; i <= igParagraphCount; i++) {
                int[] sentenceIDList = paragraph[i].getSentimentIDList();
                if (sentenceIDList != null) {
                    for (int j = 0; j < sentenceIDList.length; j++) {
                        if (sentenceIDList[j] != 0) {
                            igSentimentIDParagraphCount[Sort.i_FindIntPositionInSortedArray(
                                    sentenceIDList[j],
                                    igSentimentIDList,
                                    1,
                                    igSentimentIDListCount)]++;
                        }
                    }
                }
            }

        }
        bSentimentIDListMade = true;
    }

    /**
     * 运行 10 折交叉验证的具体实现
     * @param iMinImprovement description
     * @param bUseTotalDifference description
     * @param iReplications description
     * @param iMultiOptimisations description
     * @param sWriter description
     * @param wTermStrengthWriter description
     * @author zhangsong
     */
    private void run10FoldCrossValidationMultipleTimes(int iMinImprovement,
                                                       boolean bUseTotalDifference,
                                                       int iReplications,
                                                       int iMultiOptimisations,
                                                       BufferedWriter sWriter,
                                                       BufferedWriter wTermStrengthWriter) {
        for (int i = 1; i <= iReplications; i++) {
            run10FoldCrossValidationOnce(iMinImprovement, bUseTotalDifference, iMultiOptimisations, sWriter, wTermStrengthWriter);
        }
        System.err.println((new StringBuilder("Set of ")).append(iReplications).append(" 10-fold cross validations finished").toString());
    }

    /**
     * 运行 10 折交叉验证的公开调用方法
     * @param iMinImprovement description
     * @param bUseTotalDifference description
     * @param iReplications description
     * @param iMultiOptimisations description
     * @param sOutFileName description
     * @author zhangsong
     */
    public void run10FoldCrossValidationMultipleTimes(int iMinImprovement,
                                                      boolean bUseTotalDifference,
                                                      int iReplications,
                                                      int iMultiOptimisations,
                                                      String sOutFileName) {
        try {
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutFileName));
            BufferedWriter wTermStrengthWriter = new BufferedWriter(
                    new FileWriter((
                            new StringBuilder(
                                    String.valueOf(FileOps.s_ChopFileNameExtension(sOutFileName))))
                    .append("_termStrVars.txt").toString()));
            options.printClassificationOptionsHeadings(wWriter);
            writeClassificationStatsHeadings(wWriter);
            options.printClassificationOptionsHeadings(wTermStrengthWriter);
            resources.sentimentWords.printSentimentTermsInSingleHeaderRow(wTermStrengthWriter);
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement,
                    bUseTotalDifference,
                    iReplications,
                    iMultiOptimisations,
                    wWriter,
                    wTermStrengthWriter);
            wWriter.close();
            wTermStrengthWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 对文本进行分类
     * <p>
     *     创建新的 Paragraph 对象，调用相关方法获得情绪分数，
     *     最后捕获一些可能的异常
     * </p>
     * @param sInputFile 读取文件路径
     * @param iTextCol description
     * @param iIDCol description
     * @param sOutputFile 输出文件路径
     * @author zhangsong
     */
    public void classifyAllLinesAndRecordWithID(String sInputFile, int iTextCol, int iIDCol, String sOutputFile) {
        int iPos = 0;
        int iNeg = 0;
        int iTrinary = -3;
        int iScale = -10;
        int iCount1 = 0;
        String sLine = "";
        try {
            BufferedReader rReader = new BufferedReader(new FileReader(sInputFile));
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
            while (rReader.ready()) {
                sLine = rReader.readLine();
                iCount1++;
                if (!sLine.equals("")) {
                    String[] sData = sLine.split("\t");
                    if (sData.length > iTextCol && sData.length > iIDCol) {
                        Paragraph paragraph = new Paragraph();
                        paragraph.setParagraph(sData[iTextCol], resources, options);
                        if (options.bgTrinaryMode) {
                            iTrinary = paragraph.getParagraphTrinarySentiment();
                            wWriter.write((new StringBuilder(String.valueOf(sData[iIDCol])))
                                    .append("\t").append(iTrinary).append("\n").toString());
                        } else
                        if (options.bgScaleMode) {
                            iScale = paragraph.getParagraphScaleSentiment();
                            wWriter.write((new StringBuilder(String.valueOf(sData[iIDCol])))
                                    .append("\t").append(iScale).append("\n").toString());
                        } else {
                            iPos = paragraph.getParagraphPositiveSentiment();
                            iNeg = paragraph.getParagraphNegativeSentiment();
                            wWriter.write((new StringBuilder(String.valueOf(sData[iIDCol])))
                                    .append("\t").append(iPos).append("\t").append(iNeg).append("\n").toString());
                        }
                    }
                }
            }
            Thread.sleep(10L);
            if (rReader.ready()) {
                System.err.println("Reader ready again after pause!");
            }
            int character;
            character = rReader.read();
            if (character != -1) {
                System.err.println((new StringBuilder("Reader returns char after reader.read() false! ")).append(character).toString());
            }
            rReader.close();
            wWriter.close();
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Could not find input file: ")).append(sInputFile).toString());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println((new StringBuilder("Error reading or writing from file: ")).append(sInputFile).toString());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println((new StringBuilder("Error reading from or writing to file: ")).append(sInputFile).toString());
            e.printStackTrace();
        }
        System.err.println((new StringBuilder("Processed "))
                .append(iCount1).append(" lines from file: ")
                .append(sInputFile)
                .append(". Last line was:\n")
                .append(sLine).toString());
    }

    /**
     * 将情绪分析结果注释输入文件中的所有行
     * <p>
     *     将不同模式的情绪分析结果添加在每一行末尾
     * </p>
     * @param sInputFile 输入文件路径
     * @param iTextCol 要分析的文本索引
     * @author zhangsong
     */
    public void annotateAllLinesInInputFile(String sInputFile, int iTextCol) {
        int iPos = 0, iNeg = 0, iTrinary = -3, iScale = -10;
        String sTempFile = (new StringBuilder(String.valueOf(sInputFile))).append("_temp").toString();
        try {
            BufferedReader rReader = new BufferedReader(new FileReader(sInputFile));
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sTempFile));
            while (rReader.ready()) {
                String sLine = rReader.readLine();
                if (!sLine.equals("")) {
                    String[] sData = sLine.split("\t");
                    if (sData.length > iTextCol) {
                        Paragraph paragraph = new Paragraph();
                        paragraph.setParagraph(sData[iTextCol], resources, options);
                        if (options.bgTrinaryMode) {
                            iTrinary = paragraph.getParagraphTrinarySentiment();
                            wWriter.write((new StringBuilder(String.valueOf(sLine))).append("\t").append(iTrinary).append("\n").toString());
                        } else if (options.bgScaleMode) {
                            iScale = paragraph.getParagraphScaleSentiment();
                            wWriter.write((new StringBuilder(String.valueOf(sLine))).append("\t").append(iScale).append("\n").toString());
                        } else {
                            iPos = paragraph.getParagraphPositiveSentiment();
                            iNeg = paragraph.getParagraphNegativeSentiment();
                            wWriter.write((new StringBuilder(String.valueOf(sLine)))
                                    .append("\t").append(iPos).append("\t").append(iNeg).append("\n").toString());
                        }
                    } else {
                        wWriter.write((new StringBuilder(String.valueOf(sLine))).append("\n").toString());
                    }
                }
            }
            rReader.close();
            wWriter.close();
            File original = new File(sInputFile);
            original.delete();
            File newFile = new File(sTempFile);
            newFile.renameTo(original);
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Could not find input file: ")).append(sInputFile).toString());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println((new StringBuilder("Error reading or writing from file: ")).append(sInputFile).toString());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println((new StringBuilder("Error reading from or writing to file: ")).append(sInputFile).toString());
            e.printStackTrace();
        }
    }
    
    /**
     * 对输入文件的所有进行分类
     * <p>
     *     首先初始化变量，对于文件的每一行分析文本并提取情感信息，对相关信息进行分类，
     *     根据四种模式是否开启分别进行情绪分析
     *     最后将所有信息写入输出文本中
     * </p>
     * @param sInputFile 输入文件名
     * @param iTextCol 文件索引
     * @param sOutputFile 输出文件名
     * @author zhangsong
     */
    public void classifyAllLinesInInputFile(String sInputFile, int iTextCol, String sOutputFile) {
        int iPos = 0;
        int iNeg = 0;
        int iTrinary = -3;
        int iScale = -10;
        int iFileTrinary = -2;
        int iFileScale = -9;
        int iClassified = 0;
        int iCorrectPosCount = 0;
        int iCorrectNegCount = 0;
        int iCorrectTrinaryCount = 0;
        int iCorrectScaleCount = 0;
        int iPosAbsDiff = 0;
        int iNegAbsDiff = 0;
        int[][] confusion = {
            new int[3], new int[3], new int[3]
        };
        int maxClassifyForCorrelation = 20000;
        int[] iPosClassCorr = new int[maxClassifyForCorrelation];
        int[] iNegClassCorr = new int[maxClassifyForCorrelation];
        int[] iPosClassPred = new int[maxClassifyForCorrelation];
        int[] iNegClassPred = new int[maxClassifyForCorrelation];
        int[] iScaleClassCorr = new int[maxClassifyForCorrelation];
        int[] iScaleClassPred = new int[maxClassifyForCorrelation];
        String sRationale = "";
        String sOutput = "";
        try {
            BufferedReader rReader;
            BufferedWriter wWriter;
            if (options.bgForceUTF8) {
                wWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sOutputFile), "UTF8"));
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sInputFile), "UTF8"));
            } else {
                wWriter = new BufferedWriter(new FileWriter(sOutputFile));
                rReader = new BufferedReader(new FileReader(sInputFile));
            }
            if (options.bgTrinaryMode || options.bgScaleMode) {
                wWriter.write("Overall\tText");
            } else if (options.bgTensiStrength) {
                wWriter.write("Relax\tStress\tText");
            } else {
                wWriter.write("Positive\tNegative\tText");
            }
            if (options.bgExplainClassification) {
                wWriter.write("\tExplanation\n");
            } else {
                wWriter.write("\n");
            }
            while (rReader.ready()) {
                String sLine = rReader.readLine();
                if (!sLine.equals("")) {
                    int iTabPos = sLine.lastIndexOf("\t");
                    int iFilePos = 0;
                    int iFileNeg = 0;
                    if (iTabPos >= 0) {
                        String[] sData = sLine.split("\t");
                        if (sData.length > 1) {
                            if (iTextCol > -1) {
                                wWriter.write((new StringBuilder(String.valueOf(sLine))).append("\t").toString());
                                if (iTextCol < sData.length) {
                                    sLine = sData[iTextCol];
                                }
                            } else if (options.bgTrinaryMode) {
                                iFileTrinary = -2;
                                try {
                                    iFileTrinary = Integer.parseInt(sData[0].trim());
                                    if (iFileTrinary > 1 || iFileTrinary < -1) {
                                        System.err.println((new StringBuilder("Invalid trinary sentiment "))
                                                .append(iFileTrinary)
                                                .append(" (expected -1,0,1) at line: ")
                                                .append(sLine)
                                                .toString());
                                        iFileTrinary = 0;
                                    }
                                } catch (NumberFormatException numberformatexception) {
                                }
                            } else if (options.bgScaleMode) {
                                iFileScale = -9;
                                try {
                                    iFileScale = Integer.parseInt(sData[0].trim());
                                    if (iFileScale > 4 || iFileScale < -4) {
                                        System.err.println((new StringBuilder("Invalid overall sentiment "))
                                                .append(iFileScale)
                                                .append(" (expected -4 to +4) at line: ")
                                                .append(sLine)
                                                .toString());
                                        iFileScale = 0;
                                    }
                                } catch (NumberFormatException numberformatexception1) {
                                }
                            } else {
                                try {
                                    iFilePos = Integer.parseInt(sData[0].trim());
                                    iFileNeg = Integer.parseInt(sData[1].trim());
                                    if (iFileNeg < 0) {
                                        iFileNeg = -iFileNeg;
                                    }
                                } catch (NumberFormatException numberformatexception2) {
                                }
                            }
                        }
                        sLine = sLine.substring(iTabPos + 1);
                    }
                    Paragraph paragraph = new Paragraph();
                    paragraph.setParagraph(sLine, resources, options);
                    if (options.bgTrinaryMode) {
                        iTrinary = paragraph.getParagraphTrinarySentiment();
                        if (options.bgExplainClassification) {
                            sRationale = (new StringBuilder("\t")).append(paragraph.getClassificationRationale()).toString();
                        }
                        sOutput = (new StringBuilder(String.valueOf(iTrinary)))
                                .append("\t").append(sLine).append(sRationale).append("\n").toString();
                    } else if (options.bgScaleMode) {
                        iScale = paragraph.getParagraphScaleSentiment();
                        if (options.bgExplainClassification) {
                            sRationale = (new StringBuilder("\t"))
                                    .append(paragraph.getClassificationRationale()).toString();
                        }
                        sOutput = (new StringBuilder(String.valueOf(iScale)))
                                .append("\t").append(sLine).append(sRationale).append("\n").toString();
                    } else {
                        iPos = paragraph.getParagraphPositiveSentiment();
                        iNeg = paragraph.getParagraphNegativeSentiment();
                        if (options.bgExplainClassification) {
                            sRationale = (new StringBuilder("\t"))
                                    .append(paragraph.getClassificationRationale()).toString();
                        }
                        sOutput = (new StringBuilder(String.valueOf(iPos)))
                                .append("\t").append(iNeg).append("\t").append(sLine).append(sRationale).append("\n").toString();
                    }
                    wWriter.write(sOutput);
                    if (options.bgTrinaryMode) {
                        if (iFileTrinary > -2 && iFileTrinary < 2 && iTrinary > -2 && iTrinary < 2) {
                            iClassified++;
                            if (iFileTrinary == iTrinary) {
                                iCorrectTrinaryCount++;
                            }
                            confusion[iTrinary + 1][iFileTrinary + 1]++;
                        }
                    } else if (options.bgScaleMode) {
                        if (iFileScale > -9) {
                            iClassified++;
                            if (iFileScale == iScale) {
                                iCorrectScaleCount++;
                            }
                            if (iClassified < maxClassifyForCorrelation) {
                                iScaleClassCorr[iClassified] = iFileScale;
                            }
                            iScaleClassPred[iClassified] = iScale;
                        }
                    } else if (iFileNeg != 0) {
                        iClassified++;
                        if (iPos == iFilePos) {
                            iCorrectPosCount++;
                        }
                        iPosAbsDiff += Math.abs(iPos - iFilePos);
                        if (iClassified < maxClassifyForCorrelation) {
                            iPosClassCorr[iClassified] = iFilePos;
                        }
                        iPosClassPred[iClassified] = iPos;
                        if (iNeg == -iFileNeg) {
                            iCorrectNegCount++;
                        }
                        iNegAbsDiff += Math.abs(iNeg + iFileNeg);
                        if (iClassified < maxClassifyForCorrelation) {
                            iNegClassCorr[iClassified] = iFileNeg;
                        }
                        iNegClassPred[iClassified] = iNeg;
                    }
                }
            }
            rReader.close();
            wWriter.close();
            if (iClassified > 0) {
                if (options.bgTrinaryMode) {
                    System.err.println((new StringBuilder("Trinary correct: "))
                            .append(iCorrectTrinaryCount).append(" (").append(((float) iCorrectTrinaryCount / (float) iClassified) * 100F)
                            .append("%).").toString());
                    System.err.println("Correct -> -1   0   1");
                    System.err.println((new StringBuilder("Est = -1   "))
                            .append(confusion[0][0]).append(" ").append(confusion[0][1]).append(" ").append(confusion[0][2]).toString());
                    System.err.println((new StringBuilder("Est =  0   "))
                            .append(confusion[1][0]).append(" ").append(confusion[1][1]).append(" ").append(confusion[1][2]).toString());
                    System.err.println((new StringBuilder("Est =  1   "))
                            .append(confusion[2][0]).append(" ").append(confusion[2][1]).append(" ").append(confusion[2][2]).toString());
                } else if (options.bgScaleMode) {
                    System.err.println((new StringBuilder("Scale correct: "))
                            .append(iCorrectScaleCount).append(" (").append(((float) iCorrectScaleCount / (float) iClassified) * 100F)
                            .append("%) out of ").append(iClassified).toString());
                    System.err.println((new StringBuilder("  Correlation: "))
                            .append(ClassificationStatistics.correlation(iScaleClassCorr, iScaleClassPred, iClassified)).toString());
                } else {
                    System.out.print((new StringBuilder(String.valueOf(options.sgProgramPos)))
                            .append(" correct: ").append(iCorrectPosCount).append(" (")
                            .append(((float) iCorrectPosCount / (float) iClassified) * 100F).append("%).").toString());
                    System.err.println((new StringBuilder(" Mean abs diff: "))
                            .append((float) iPosAbsDiff / (float) iClassified).toString());
                    if (iClassified < maxClassifyForCorrelation) {
                        System.err.println((new StringBuilder(" Correlation: "))
                                .append(ClassificationStatistics.correlationAbs(iPosClassCorr, iPosClassPred, iClassified)).toString());
                        int corrWithin1 = ClassificationStatistics.accuracyWithin1(iPosClassCorr, iPosClassPred, iClassified, false);
                        System.err.println((new StringBuilder(" Correct +/- 1: "))
                                .append(corrWithin1).append(" (").append((float) (100 * corrWithin1) / (float) iClassified)
                                .append("%)").toString());
                    }
                    System.err.print((new StringBuilder(String.valueOf(options.sgProgramNeg)))
                            .append(" correct: ").append(iCorrectNegCount).append(" (")
                            .append(((float) iCorrectNegCount / (float) iClassified) * 100F)
                            .append("%).").toString());
                    System.err.println((new StringBuilder(" Mean abs diff: ")).
                            append((float) iNegAbsDiff / (float) iClassified).toString());
                    if (iClassified < maxClassifyForCorrelation) {
                        System.err.println((new StringBuilder(" Correlation: "))
                                .append(ClassificationStatistics.correlationAbs(iNegClassCorr, iNegClassPred, iClassified)).toString());
                        int corrWithin1 = ClassificationStatistics.accuracyWithin1(iNegClassCorr, iNegClassPred, iClassified, true);
                        System.err.println((new StringBuilder(" Correct +/- 1: ")).append(corrWithin1)
                                .append(" (").append((float) (100 * corrWithin1) / (float) iClassified).append("%)").toString());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Could not find input file: ")).append(sInputFile).toString());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println((new StringBuilder("Error reading from input file: "))
                    .append(sInputFile).append(" or writing to output file ").append(sOutputFile).toString());
            e.printStackTrace();
        }
    }

    public File classifyAllLinesInInputFile(File sInputFile, int iTextCol, String sOutputFile) {
        int iPos = 0;
        int iNeg = 0;
        int iTrinary = -3;
        int iScale = -10;
        int iFileTrinary = -2;
        int iFileScale = -9;
        int iClassified = 0;
        int iCorrectPosCount = 0;
        int iCorrectNegCount = 0;
        int iCorrectTrinaryCount = 0;
        int iCorrectScaleCount = 0;
        int iPosAbsDiff = 0;
        int iNegAbsDiff = 0;
        int[][] confusion = {
                new int[3], new int[3], new int[3]
        };
        int maxClassifyForCorrelation = 20000;
        int[] iPosClassCorr = new int[maxClassifyForCorrelation];
        int[] iNegClassCorr = new int[maxClassifyForCorrelation];
        int[] iPosClassPred = new int[maxClassifyForCorrelation];
        int[] iNegClassPred = new int[maxClassifyForCorrelation];
        int[] iScaleClassCorr = new int[maxClassifyForCorrelation];
        int[] iScaleClassPred = new int[maxClassifyForCorrelation];
        String sRationale = "";
        String sOutput = "";
        try {
            BufferedReader rReader;
            BufferedWriter wWriter;
            File outPut = new File(sOutputFile);
            if (options.bgForceUTF8) {
                wWriter = new BufferedWriter(new FileWriter(outPut));
                rReader = new BufferedReader(new FileReader(sInputFile));
            } else {
                wWriter = new BufferedWriter(new FileWriter(outPut));
                rReader = new BufferedReader(new FileReader(sInputFile));
            }
            if (options.bgTrinaryMode || options.bgScaleMode) {
                wWriter.write("Overall\tText");
            } else if (options.bgTensiStrength) {
                wWriter.write("Relax\tStress\tText");
            } else {
                wWriter.write("Positive\tNegative\tText");
            }
            if (options.bgExplainClassification) {
                wWriter.write("\tExplanation\n");
            } else {
                wWriter.write("\n");
            }
            while (rReader.ready()) {
                String sLine = rReader.readLine();
                if (!sLine.equals("")) {
                    int iTabPos = sLine.lastIndexOf("\t");
                    int iFilePos = 0;
                    int iFileNeg = 0;
                    if (iTabPos >= 0) {
                        String[] sData = sLine.split("\t");
                        if (sData.length > 1) {
                            if (iTextCol > -1) {
                                wWriter.write((new StringBuilder(String.valueOf(sLine))).append("\t").toString());
                                if (iTextCol < sData.length) {
                                    sLine = sData[iTextCol];
                                }
                            } else if (options.bgTrinaryMode) {
                                iFileTrinary = -2;
                                try {
                                    iFileTrinary = Integer.parseInt(sData[0].trim());
                                    if (iFileTrinary > 1 || iFileTrinary < -1) {
                                        System.err.println((new StringBuilder("Invalid trinary sentiment "))
                                                .append(iFileTrinary)
                                                .append(" (expected -1,0,1) at line: ")
                                                .append(sLine)
                                                .toString());
                                        iFileTrinary = 0;
                                    }
                                } catch (NumberFormatException numberformatexception) {
                                }
                            } else if (options.bgScaleMode) {
                                iFileScale = -9;
                                try {
                                    iFileScale = Integer.parseInt(sData[0].trim());
                                    if (iFileScale > 4 || iFileScale < -4) {
                                        System.err.println((new StringBuilder("Invalid overall sentiment "))
                                                .append(iFileScale)
                                                .append(" (expected -4 to +4) at line: ")
                                                .append(sLine)
                                                .toString());
                                        iFileScale = 0;
                                    }
                                } catch (NumberFormatException numberformatexception1) {
                                }
                            } else {
                                try {
                                    iFilePos = Integer.parseInt(sData[0].trim());
                                    iFileNeg = Integer.parseInt(sData[1].trim());
                                    if (iFileNeg < 0) {
                                        iFileNeg = -iFileNeg;
                                    }
                                } catch (NumberFormatException numberformatexception2) {
                                }
                            }
                        }
                        sLine = sLine.substring(iTabPos + 1);
                    }
                    Paragraph paragraph = new Paragraph();
                    paragraph.setParagraph(sLine, resources, options);
                    if (options.bgTrinaryMode) {
                        iTrinary = paragraph.getParagraphTrinarySentiment();
                        if (options.bgExplainClassification) {
                            sRationale = (new StringBuilder("\t")).append(paragraph.getClassificationRationale()).toString();
                        }
                        sOutput = (new StringBuilder(String.valueOf(iTrinary)))
                                .append("\t").append(sLine).append(sRationale).append("\n").toString();
                    } else if (options.bgScaleMode) {
                        iScale = paragraph.getParagraphScaleSentiment();
                        if (options.bgExplainClassification) {
                            sRationale = (new StringBuilder("\t"))
                                    .append(paragraph.getClassificationRationale()).toString();
                        }
                        sOutput = (new StringBuilder(String.valueOf(iScale)))
                                .append("\t").append(sLine).append(sRationale).append("\n").toString();
                    } else {
                        iPos = paragraph.getParagraphPositiveSentiment();
                        iNeg = paragraph.getParagraphNegativeSentiment();
                        if (options.bgExplainClassification) {
                            sRationale = (new StringBuilder("\t"))
                                    .append(paragraph.getClassificationRationale()).toString();
                        }
                        sOutput = (new StringBuilder(String.valueOf(iPos)))
                                .append("\t").append(iNeg).append("\t").append(sLine).append(sRationale).append("\n").toString();
                    }
                    wWriter.write(sOutput);
                    if (options.bgTrinaryMode) {
                        if (iFileTrinary > -2 && iFileTrinary < 2 && iTrinary > -2 && iTrinary < 2) {
                            iClassified++;
                            if (iFileTrinary == iTrinary) {
                                iCorrectTrinaryCount++;
                            }
                            confusion[iTrinary + 1][iFileTrinary + 1]++;
                        }
                    } else if (options.bgScaleMode) {
                        if (iFileScale > -9) {
                            iClassified++;
                            if (iFileScale == iScale) {
                                iCorrectScaleCount++;
                            }
                            if (iClassified < maxClassifyForCorrelation) {
                                iScaleClassCorr[iClassified] = iFileScale;
                            }
                            iScaleClassPred[iClassified] = iScale;
                        }
                    } else if (iFileNeg != 0) {
                        iClassified++;
                        if (iPos == iFilePos) {
                            iCorrectPosCount++;
                        }
                        iPosAbsDiff += Math.abs(iPos - iFilePos);
                        if (iClassified < maxClassifyForCorrelation) {
                            iPosClassCorr[iClassified] = iFilePos;
                        }
                        iPosClassPred[iClassified] = iPos;
                        if (iNeg == -iFileNeg) {
                            iCorrectNegCount++;
                        }
                        iNegAbsDiff += Math.abs(iNeg + iFileNeg);
                        if (iClassified < maxClassifyForCorrelation) {
                            iNegClassCorr[iClassified] = iFileNeg;
                        }
                        iNegClassPred[iClassified] = iNeg;
                    }
                }
            }
            rReader.close();
            wWriter.close();
            return outPut;
//            if (iClassified > 0) {
//                if (options.bgTrinaryMode) {
//                    System.err.println((new StringBuilder("Trinary correct: "))
//                            .append(iCorrectTrinaryCount).append(" (").append(((float) iCorrectTrinaryCount / (float) iClassified) * 100F)
//                            .append("%).").toString());
//                    System.err.println("Correct -> -1   0   1");
//                    System.err.println((new StringBuilder("Est = -1   "))
//                            .append(confusion[0][0]).append(" ").append(confusion[0][1]).append(" ").append(confusion[0][2]).toString());
//                    System.err.println((new StringBuilder("Est =  0   "))
//                            .append(confusion[1][0]).append(" ").append(confusion[1][1]).append(" ").append(confusion[1][2]).toString());
//                    System.err.println((new StringBuilder("Est =  1   "))
//                            .append(confusion[2][0]).append(" ").append(confusion[2][1]).append(" ").append(confusion[2][2]).toString());
//                } else if (options.bgScaleMode) {
//                    System.err.println((new StringBuilder("Scale correct: "))
//                            .append(iCorrectScaleCount).append(" (").append(((float) iCorrectScaleCount / (float) iClassified) * 100F)
//                            .append("%) out of ").append(iClassified).toString());
//                    System.err.println((new StringBuilder("  Correlation: "))
//                            .append(ClassificationStatistics.correlation(iScaleClassCorr, iScaleClassPred, iClassified)).toString());
//                } else {
//                    System.out.print((new StringBuilder(String.valueOf(options.sgProgramPos)))
//                            .append(" correct: ").append(iCorrectPosCount).append(" (")
//                            .append(((float) iCorrectPosCount / (float) iClassified) * 100F).append("%).").toString());
//                    System.err.println((new StringBuilder(" Mean abs diff: "))
//                            .append((float) iPosAbsDiff / (float) iClassified).toString());
//                    if (iClassified < maxClassifyForCorrelation) {
//                        System.err.println((new StringBuilder(" Correlation: "))
//                                .append(ClassificationStatistics.correlationAbs(iPosClassCorr, iPosClassPred, iClassified)).toString());
//                        int corrWithin1 = ClassificationStatistics.accuracyWithin1(iPosClassCorr, iPosClassPred, iClassified, false);
//                        System.err.println((new StringBuilder(" Correct +/- 1: "))
//                                .append(corrWithin1).append(" (").append((float) (100 * corrWithin1) / (float) iClassified)
//                                .append("%)").toString());
//                    }
//                    System.err.print((new StringBuilder(String.valueOf(options.sgProgramNeg)))
//                            .append(" correct: ").append(iCorrectNegCount).append(" (")
//                            .append(((float) iCorrectNegCount / (float) iClassified) * 100F)
//                            .append("%).").toString());
//                    System.err.println((new StringBuilder(" Mean abs diff: ")).
//                            append((float) iNegAbsDiff / (float) iClassified).toString());
//                    if (iClassified < maxClassifyForCorrelation) {
//                        System.err.println((new StringBuilder(" Correlation: "))
//                                .append(ClassificationStatistics.correlationAbs(iNegClassCorr, iNegClassPred, iClassified)).toString());
//                        int corrWithin1 = ClassificationStatistics.accuracyWithin1(iNegClassCorr, iNegClassPred, iClassified, true);
//                        System.err.println((new StringBuilder(" Correct +/- 1: ")).append(corrWithin1)
//                                .append(" (").append((float) (100 * corrWithin1) / (float) iClassified).append("%)").toString());
//                    }
//                }
//            }
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Could not find input file: ")).append(sInputFile).toString());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println((new StringBuilder("Error reading from input file: "))
                    .append(sInputFile).append(" or writing to output file ").append(sOutputFile).toString());
            e.printStackTrace();
        }
        return sInputFile;
    }

    /**
     * UC-26 将分类算法写入标题中
     * @param w description
     * @author zhangsong
     */
    private void writeClassificationStatsHeadings(BufferedWriter w)
        throws IOException {
        String sPosOrScale;
        if (options.bgScaleMode) {
            sPosOrScale = "ScaleCorrel";
        } else {
            sPosOrScale = "PosCorrel";
        }
        w.write((new StringBuilder(
                "\tPosCorrect\tiPosCorrect/Total\tNegCorrect\tNegCorrect/Total\t"
                        + "PosWithin1\tPosWithin1/Total\tNegWithin1\tNegWithin1/Total\t"))
                .append(sPosOrScale).append("\tNegCorrel").append("\tPosMPE\tNegMPE\tPosMPEnoDiv\tNegMPEnoDiv")
                .append("\tTrinaryOrScaleCorrect\tTrinaryOrScaleCorrect/TotalClassified")
                .append("\tTrinaryOrScaleCorrectWithin1\tTrinaryOrScaleCorrectWithin1/TotalClassified")
                .append("\test-1corr-1\test-1corr0\test-1corr1").append("\test0corr-1\test0corr0\test0corr1")
                .append("\test1corr-1\test1corr0\test1corr1").append("\tTotalClassified\n").toString());
    }

    /**
     * UC-26 多个选项变体执行 10 折交叉验证（机器学习分类算法）
     * @param iMinImprovement description
     * @param bUseTotalDifference description
     * @param iReplications description
     * @param iMultiOptimisations description
     * @param sOutFileName description
     * @author zhangsong
     */
    public void run10FoldCrossValidationForAllOptionVariations(int iMinImprovement,
                                                               boolean bUseTotalDifference,
                                                               int iReplications,
                                                               int iMultiOptimisations,
                                                               String sOutFileName) {
        try {
            BufferedWriter wResultsWriter = new BufferedWriter(new FileWriter(sOutFileName));
            BufferedWriter wTermStrengthWriter = new BufferedWriter(new FileWriter((
                    new StringBuilder(String.valueOf(FileOps.s_ChopFileNameExtension(sOutFileName))))
                    .append("_termStrVars.txt").toString()));
            if (igPosClass == null || igPosClass.length < igPosCorrect.length) {
                igPosClass = new int[igParagraphCount + 1];
                igNegClass = new int[igParagraphCount + 1];
                igTrinaryClass = new int[igParagraphCount + 1];
            }
            options.printClassificationOptionsHeadings(wResultsWriter);
            writeClassificationStatsHeadings(wResultsWriter);
            options.printClassificationOptionsHeadings(wTermStrengthWriter);
            resources.sentimentWords.printSentimentTermsInSingleHeaderRow(wTermStrengthWriter);
            System.err.println("About to start classifications for 20 different option variations");
            if (options.bgTrinaryMode) {
                ClassificationStatistics.baselineAccuracyMakeLargestClassPrediction(
                        igTrinaryCorrect, igTrinaryClass, igParagraphCount, false);
            } else if (options.bgScaleMode) {
                ClassificationStatistics.baselineAccuracyMakeLargestClassPrediction(
                        igScaleCorrect, igScaleClass, igParagraphCount, false);
            } else {
                ClassificationStatistics.baselineAccuracyMakeLargestClassPrediction(
                        igPosCorrect, igPosClass, igParagraphCount, false);
                ClassificationStatistics.baselineAccuracyMakeLargestClassPrediction(
                        igNegCorrect, igNegClass, igParagraphCount, true);
            }
            options.printBlankClassificationOptions(wResultsWriter);
            if (options.bgTrinaryMode) {
                printClassificationResultsRow(igPosClass, igNegClass, igTrinaryClass, wResultsWriter);
            } else {
                printClassificationResultsRow(igPosClass, igNegClass, igScaleClass, wResultsWriter);
            }
            options.printClassificationOptions(wResultsWriter, igParagraphCount, bUseTotalDifference, iMultiOptimisations);
            calculateCorpusSentimentScores();
            if (options.bgTrinaryMode) {
                printClassificationResultsRow(igPosClass, igNegClass, igTrinaryClass, wResultsWriter);
            } else {
                printClassificationResultsRow(igPosClass, igNegClass, igScaleClass, wResultsWriter);
            }
            options.printBlankClassificationOptions(wTermStrengthWriter);
            resources.sentimentWords.printSentimentValuesInSingleRow(wTermStrengthWriter);
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.igEmotionParagraphCombineMethod = 1 - options.igEmotionParagraphCombineMethod;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.igEmotionParagraphCombineMethod = 1 - options.igEmotionParagraphCombineMethod;
            options.igEmotionSentenceCombineMethod = 1 - options.igEmotionSentenceCombineMethod;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.igEmotionSentenceCombineMethod = 1 - options.igEmotionSentenceCombineMethod;
            options.bgReduceNegativeEmotionInQuestionSentences = !options.bgReduceNegativeEmotionInQuestionSentences;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgReduceNegativeEmotionInQuestionSentences = !options.bgReduceNegativeEmotionInQuestionSentences;
            options.bgMissCountsAsPlus2 = !options.bgMissCountsAsPlus2;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgMissCountsAsPlus2 = !options.bgMissCountsAsPlus2;
            options.bgYouOrYourIsPlus2UnlessSentenceNegative = !options.bgYouOrYourIsPlus2UnlessSentenceNegative;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgYouOrYourIsPlus2UnlessSentenceNegative = !options.bgYouOrYourIsPlus2UnlessSentenceNegative;
            options.bgExclamationInNeutralSentenceCountsAsPlus2 = !options.bgExclamationInNeutralSentenceCountsAsPlus2;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgExclamationInNeutralSentenceCountsAsPlus2 = !options.bgExclamationInNeutralSentenceCountsAsPlus2;
            options.bgUseIdiomLookupTable = !options.bgUseIdiomLookupTable;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgUseIdiomLookupTable = !options.bgUseIdiomLookupTable;
            int iTemp = options.igMoodToInterpretNeutralEmphasis;
            options.igMoodToInterpretNeutralEmphasis = -options.igMoodToInterpretNeutralEmphasis;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.igMoodToInterpretNeutralEmphasis = 0;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.igMoodToInterpretNeutralEmphasis = iTemp;
            System.err.println("About to start 10th option variation classification");
            options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = !options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = !options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion;
            options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = !options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = !options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion;
            options.bgIgnoreBoosterWordsAfterNegatives = !options.bgIgnoreBoosterWordsAfterNegatives;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgIgnoreBoosterWordsAfterNegatives = !options.bgIgnoreBoosterWordsAfterNegatives;
            options.bgMultipleLettersBoostSentiment = !options.bgMultipleLettersBoostSentiment;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgMultipleLettersBoostSentiment = !options.bgMultipleLettersBoostSentiment;
            options.bgBoosterWordsChangeEmotion = !options.bgBoosterWordsChangeEmotion;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgBoosterWordsChangeEmotion = !options.bgBoosterWordsChangeEmotion;
            if (options.bgNegatingWordsFlipEmotion) {
                options.bgNegatingWordsFlipEmotion = !options.bgNegatingWordsFlipEmotion;
                run10FoldCrossValidationMultipleTimes(
                        iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
                options.bgNegatingWordsFlipEmotion = !options.bgNegatingWordsFlipEmotion;
            } else {
                options.bgNegatingPositiveFlipsEmotion = !options.bgNegatingPositiveFlipsEmotion;
                run10FoldCrossValidationMultipleTimes(
                        iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
                options.bgNegatingPositiveFlipsEmotion = !options.bgNegatingPositiveFlipsEmotion;
                options.bgNegatingNegativeNeutralisesEmotion = !options.bgNegatingNegativeNeutralisesEmotion;
                run10FoldCrossValidationMultipleTimes(
                        iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
                options.bgNegatingNegativeNeutralisesEmotion = !options.bgNegatingNegativeNeutralisesEmotion;
            }
            options.bgCorrectSpellingsWithRepeatedLetter = !options.bgCorrectSpellingsWithRepeatedLetter;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgCorrectSpellingsWithRepeatedLetter = !options.bgCorrectSpellingsWithRepeatedLetter;
            options.bgUseEmoticons = !options.bgUseEmoticons;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgUseEmoticons = !options.bgUseEmoticons;
            options.bgCapitalsBoostTermSentiment = !options.bgCapitalsBoostTermSentiment;
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            options.bgCapitalsBoostTermSentiment = !options.bgCapitalsBoostTermSentiment;
            if (iMinImprovement > 1) {
                run10FoldCrossValidationMultipleTimes(
                        iMinImprovement - 1, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            }
            run10FoldCrossValidationMultipleTimes(
                    iMinImprovement + 1, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
            wResultsWriter.close();
            wTermStrengthWriter.close();
            summariseMultiple10FoldValidations(sOutFileName, (new StringBuilder(String.valueOf(sOutFileName)))
                    .append("_sum.txt").toString());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * UC-29 执行 10 折交叉验证的更大情绪分析系统的十次迭代
     * <p>
     *     运行 10 折交叉验证过程的一次迭代，循环运行十次之后重新计算整个语料的情感分数，最后打印
     * </p>
     * @param iMinImprovement description
     * @param bUseTotalDifference description
     * @param iMultiOptimisations description
     * @param wWriter description
     * @param wTermStrengthWriter description
     * @author zhangsong
     */
    private void run10FoldCrossValidationOnce(int iMinImprovement,
                                              boolean bUseTotalDifference,
                                              int iMultiOptimisations,
                                              BufferedWriter wWriter,
                                              BufferedWriter wTermStrengthWriter) {
        int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
        int[] iParagraphRand = new int[igParagraphCount + 1];
        int[] iPosClassAll = new int[igParagraphCount + 1];
        int[] iNegClassAll = new int[igParagraphCount + 1];
        int[] iTrinaryOrScaleClassAll = new int[igParagraphCount + 1];
        int iTotalClassified = 0;
        Sort.makeRandomOrderList(iParagraphRand);
        int[] iOriginalSentimentStrengths = new int[iTotalSentimentWords + 1];
        for (int i = 1; i < iTotalSentimentWords; i++) {
            iOriginalSentimentStrengths[i] = resources.sentimentWords.getSentiment(i);
        }

        for (int iFold = 1; iFold <= 10; iFold++) {
            selectDecileAsSubcorpus(iParagraphRand, iFold, true);
            reCalculateCorpusSentimentScores();
            optimiseDictionaryWeightingsForCorpusMultipleTimes(iMinImprovement, bUseTotalDifference, iMultiOptimisations);
            options.printClassificationOptions(wTermStrengthWriter, iMinImprovement, bUseTotalDifference, iMultiOptimisations);
            resources.sentimentWords.printSentimentValuesInSingleRow(wTermStrengthWriter);
            selectDecileAsSubcorpus(iParagraphRand, iFold, false);
            reCalculateCorpusSentimentScores();
            for (int i = 1; i <= igParagraphCount; i++) {
                if (bgSupcorpusMember[i]) {
                    iPosClassAll[i] = igPosClass[i];
                    iNegClassAll[i] = igNegClass[i];
                    if (options.bgTrinaryMode) {
                        iTrinaryOrScaleClassAll[i] = igTrinaryClass[i];
                    } else {
                        iTrinaryOrScaleClassAll[i] = igScaleClass[i];
                    }
                }
            }
            iTotalClassified += igSupcorpusMemberCount;
            for (int i = 1; i < iTotalSentimentWords; i++) {
                resources.sentimentWords.setSentiment(i, iOriginalSentimentStrengths[i]);
            }

        }

        useWholeCorpusNotSubcorpus();
        options.printClassificationOptions(wWriter, iMinImprovement, bUseTotalDifference, iMultiOptimisations);
        printClassificationResultsRow(iPosClassAll, iNegClassAll, iTrinaryOrScaleClassAll, wWriter);
    }

    /**
     * 将分类统计信息写入文件
     * <p>
     *     将所有模式的情绪分类结果写入文件，
     *     分类结果包括实例比例、平均百分比错误等
     * </p>
     * @param iPosClassAll 积极情绪分类
     * @param iNegClassAll 消极情绪分类
     * @param iTrinaryOrScaleClassAll 三元情绪分类
     * @param wWriter 写入文件
     * @return boolean 表示是否成功写入
     * @author zhangsong
     */
    private boolean printClassificationResultsRow(int[] iPosClassAll,
                                                  int[] iNegClassAll,
                                                  int[] iTrinaryOrScaleClassAll,
                                                  BufferedWriter wWriter) {
        int iPosCorrect = -1;
        int iNegCorrect = -1;
        int iPosWithin1 = -1;
        int iNegWithin1 = -1;
        int iTrinaryCorrect = -1;
        int iTrinaryCorrectWithin1 = -1;
        double fPosCorrectPoportion = -1D;
        double fNegCorrectPoportion = -1D;
        double fPosWithin1Poportion = -1D;
        double fNegWithin1Poportion = -1D;
        double fTrinaryCorrectPoportion = -1D;
        double fTrinaryCorrectWithin1Poportion = -1D;
        double fPosOrScaleCorr = 9999D;
        double fNegCorr = 9999D;
        double fPosMPE = 9999D;
        double fNegMPE = 9999D;
        double fPosMPEnoDiv = 9999D;
        double fNegMPEnoDiv = 9999D;
        int[][] estCorr = {
            new int[3], new int[3], new int[3]
        };
        try {
            if (options.bgTrinaryMode) {
                iTrinaryCorrect = ClassificationStatistics.accuracy(igTrinaryCorrect, iTrinaryOrScaleClassAll, igParagraphCount, false);
                iTrinaryCorrectWithin1 = ClassificationStatistics.accuracyWithin1(
                        igTrinaryCorrect, iTrinaryOrScaleClassAll, igParagraphCount, false);
                fTrinaryCorrectPoportion = (float) iTrinaryCorrect / (float) igParagraphCount;
                fTrinaryCorrectWithin1Poportion = (float) iTrinaryCorrectWithin1 / (float) igParagraphCount;
                ClassificationStatistics.TrinaryOrBinaryConfusionTable(
                        iTrinaryOrScaleClassAll, igTrinaryCorrect, igParagraphCount, estCorr);
            } else
            if (options.bgScaleMode) {
                iTrinaryCorrect = ClassificationStatistics.accuracy(
                        igScaleCorrect, iTrinaryOrScaleClassAll, igParagraphCount, false);
                iTrinaryCorrectWithin1 = ClassificationStatistics.accuracyWithin1(
                        igScaleCorrect, iTrinaryOrScaleClassAll, igParagraphCount, false);
                fTrinaryCorrectPoportion = (float) iTrinaryCorrect / (float) igParagraphCount;
                fTrinaryCorrectWithin1Poportion = (float) iTrinaryCorrectWithin1 / (float) igParagraphCount;
                fPosOrScaleCorr = ClassificationStatistics.correlation(
                        igScaleCorrect, iTrinaryOrScaleClassAll, igParagraphCount);
            } else {
                iPosCorrect = ClassificationStatistics.accuracy(
                        igPosCorrect, iPosClassAll, igParagraphCount, false);
                iNegCorrect = ClassificationStatistics.accuracy(
                        igNegCorrect, iNegClassAll, igParagraphCount, true);
                iPosWithin1 = ClassificationStatistics.accuracyWithin1(
                        igPosCorrect, iPosClassAll, igParagraphCount, false);
                iNegWithin1 = ClassificationStatistics.accuracyWithin1(
                        igNegCorrect, iNegClassAll, igParagraphCount, true);
                fPosOrScaleCorr = ClassificationStatistics.correlationAbs(
                        igPosCorrect, iPosClassAll, igParagraphCount);
                fNegCorr = ClassificationStatistics.correlationAbs(
                        igNegCorrect, iNegClassAll, igParagraphCount);
                fPosMPE = ClassificationStatistics.absoluteMeanPercentageError(
                        igPosCorrect, iPosClassAll, igParagraphCount, false);
                fNegMPE = ClassificationStatistics.absoluteMeanPercentageError(
                        igNegCorrect, iNegClassAll, igParagraphCount, true);
                fPosMPEnoDiv = ClassificationStatistics.absoluteMeanPercentageErrorNoDivision(
                        igPosCorrect, iPosClassAll, igParagraphCount, false);
                fNegMPEnoDiv = ClassificationStatistics.absoluteMeanPercentageErrorNoDivision(
                        igNegCorrect, iNegClassAll, igParagraphCount, true);
                fPosCorrectPoportion = (float) iPosCorrect / (float) igParagraphCount;
                fNegCorrectPoportion = (float) iNegCorrect / (float) igParagraphCount;
                fPosWithin1Poportion = (float) iPosWithin1 / (float) igParagraphCount;
                fNegWithin1Poportion = (float) iNegWithin1 / (float) igParagraphCount;
            }
            wWriter.write((new StringBuilder("\t")).append(iPosCorrect).append("\t").append(fPosCorrectPoportion)
                    .append("\t").append(iNegCorrect).append("\t").append(fNegCorrectPoportion).append("\t")
                    .append(iPosWithin1).append("\t").append(fPosWithin1Poportion).append("\t").append(iNegWithin1)
                    .append("\t").append(fNegWithin1Poportion).append("\t").append(fPosOrScaleCorr).append("\t")
                    .append(fNegCorr).append("\t").append(fPosMPE).append("\t").append(fNegMPE).append("\t")
                    .append(fPosMPEnoDiv).append("\t").append(fNegMPEnoDiv).append("\t").append(iTrinaryCorrect)
                    .append("\t").append(fTrinaryCorrectPoportion).append("\t").append(iTrinaryCorrectWithin1)
                    .append("\t").append(fTrinaryCorrectWithin1Poportion).append("\t").append(estCorr[0][0])
                    .append("\t").append(estCorr[0][1]).append("\t").append(estCorr[0][2]).append("\t")
                    .append(estCorr[1][0]).append("\t").append(estCorr[1][1]).append("\t").append(estCorr[1][2])
                    .append("\t").append(estCorr[2][0]).append("\t").append(estCorr[2][1]).append("\t")
                    .append(estCorr[2][2]).append("\t").append(igParagraphCount).append("\n").toString());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据 iDecile 参数选择 iParagraphRand 数组的一个子集作为子语料库
     * @param iParagraphRand description
     * @param iDecile description
     * @param bInvert 用于确定是选择指定十分位数内的段落还是排除它们
     * @author zhangsong
     */
    private void selectDecileAsSubcorpus(int[] iParagraphRand, int iDecile, boolean bInvert) {
        if (igParagraphCount == 0) {
            return;
        }
        int iMin = (int) (((float) igParagraphCount / 10F) * (float) (iDecile - 1)) + 1;
        int iMax = (int) (((float) igParagraphCount / 10F) * (float) iDecile);
        if (iDecile == 10) {
            iMax = igParagraphCount;
        }
        if (iDecile == 0) {
            iMin = 0;
        }
        igSupcorpusMemberCount = 0;
        for (int i = 1; i <= igParagraphCount; i++) {
            if (i >= iMin && i <= iMax) {
                bgSupcorpusMember[iParagraphRand[i]] = !bInvert;
                if (!bInvert) {
                    igSupcorpusMemberCount++;
                }
            } else {
                bgSupcorpusMember[iParagraphRand[i]] = bInvert;
                if (bInvert) {
                    igSupcorpusMemberCount++;
                }
            }
        }
    }

    /**
     * UC-27 通过多次运行优化过程来优化语料库中情感词的权重
     * @param iMinImprovement 它确定继续优化权重所需的最小改进
     * @param bUseTotalDifference 决定是使用总差还是平均差来决定是否应该继续优化
     * @param iOptimisationTotal 用于确定优化过程应运行的次数
     * @author zhangsong
     */
    public void optimiseDictionaryWeightingsForCorpusMultipleTimes(int iMinImprovement,
                                                                   boolean bUseTotalDifference,
                                                                   int iOptimisationTotal) {
        boolean flag = false;
        if (iOptimisationTotal < 1) {
            flag = true;
        }
        if (iOptimisationTotal == 1 && !flag) {
            optimiseDictionaryWeightingsForCorpus(iMinImprovement, bUseTotalDifference);
            flag = true;
        }
        if (flag) {
            return;
        }

        int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
        int[] iOriginalSentimentStrengths = new int[iTotalSentimentWords + 1];
        for (int j = 1; j <= iTotalSentimentWords; j++) {
            iOriginalSentimentStrengths[j] = resources.sentimentWords.getSentiment(j);
        }
        int[] iTotalWeight = new int[iTotalSentimentWords + 1];
        for (int j = 1; j <= iTotalSentimentWords; j++) {
            iTotalWeight[j] = 0;
        }
        for (int i = 0; i < iOptimisationTotal; i++) {
            optimiseDictionaryWeightingsForCorpus(iMinImprovement, bUseTotalDifference);
            for (int j = 1; j <= iTotalSentimentWords; j++) {
                iTotalWeight[j] += resources.sentimentWords.getSentiment(j);
            }
            for (int j = 1; j <= iTotalSentimentWords; j++) {
                resources.sentimentWords.setSentiment(j, iOriginalSentimentStrengths[j]);
            }
        }

        for (int j = 1; j <= iTotalSentimentWords; j++) {
            resources.sentimentWords.setSentiment(j, (int) ((double) ((float) iTotalWeight[j] / (float) iOptimisationTotal) + 0.5D));
        }
        optimiseDictionaryWeightingsForCorpus(iMinImprovement, bUseTotalDifference);
    }

    /**
     * UC-27 优化语料库中情感词的权重
     * <p>
     *     根据三种模式选择不同的优化方法
     * </p>
     * @param iMinImprovement 更新单词所需的最小改进
     * @param bUseTotalDifference 在更新单词权重时是否使用正面和负面情感词之间的总差
     * @author zhangsong
     */
    public void optimiseDictionaryWeightingsForCorpus(int iMinImprovement, boolean bUseTotalDifference) {
        if (options.bgTrinaryMode) {
            optimiseDictionaryWeightingsForCorpusTrinaryOrBinary(iMinImprovement);
        } else if (options.bgScaleMode) {
            optimiseDictionaryWeightingsForCorpusScale(iMinImprovement);
        } else {
            optimiseDictionaryWeightingsForCorpusPosNeg(iMinImprovement, bUseTotalDifference);
        }
    }

    /**
     * UC-27 针对语料库规模优化情感词典权重的方法
     * @param iMinImprovement 用于确定继续调整权重所需的分类等级的最小改进
     * @author zhangsong
     */
    public void optimiseDictionaryWeightingsForCorpusScale(int iMinImprovement) {
        boolean bFullListChanges = true;
        int iLastScaleNumberCorrect = getClassificationScaleNumberCorrect();
        int iNewScaleNumberCorrect = 0;
        int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
        int[] iWordRand = new int[iTotalSentimentWords + 1];
        while (bFullListChanges) {
            Sort.makeRandomOrderList(iWordRand);
            bFullListChanges = false;
            for (int i = 1; i <= iTotalSentimentWords; i++) {
                int iOldTermSentimentStrength = resources.sentimentWords.getSentiment(iWordRand[i]);
                boolean bCurrentIDChange = false;
                int iAddOneImprovement = 0;
                int iSubtractOneImprovement = 0;
                if (iOldTermSentimentStrength < 4) {
                    resources.sentimentWords.setSentiment(iWordRand[i], iOldTermSentimentStrength + 1);
                    reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
                    iNewScaleNumberCorrect = getClassificationScaleNumberCorrect();
                    iAddOneImprovement = iNewScaleNumberCorrect - iLastScaleNumberCorrect;
                    if (iAddOneImprovement >= iMinImprovement) {
                        bCurrentIDChange = true;
                        iLastScaleNumberCorrect += iAddOneImprovement;
                    }
                }
                if (iOldTermSentimentStrength > -4 && !bCurrentIDChange) {
                    resources.sentimentWords.setSentiment(iWordRand[i], iOldTermSentimentStrength - 1);
                    reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
                    iNewScaleNumberCorrect = getClassificationScaleNumberCorrect();
                    iSubtractOneImprovement = iNewScaleNumberCorrect - iLastScaleNumberCorrect;
                    if (iSubtractOneImprovement >= iMinImprovement) {
                        bCurrentIDChange = true;
                        iLastScaleNumberCorrect += iSubtractOneImprovement;
                    }
                }
                if (bCurrentIDChange) {
                    bFullListChanges = true;
                } else {
                    resources.sentimentWords.setSentiment(iWordRand[i], iOldTermSentimentStrength);
                    reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
                }
            }

        }
    }

    /**
     * UC-27 该方法使用三元或二元分类方案来优化给定语料库的情感词词典权重
     * @param iMinImprovement 用于确定继续调整权重所需的分类等级的最小改进
     * @author zhangsong
     */
    public void optimiseDictionaryWeightingsForCorpusTrinaryOrBinary(int iMinImprovement) {
        boolean bFullListChanges = true;
        int iLastTrinaryCorrect = getClassificationTrinaryNumberCorrect();
        int iNewTrinary = 0;
        int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
        int[] iWordRand = new int[iTotalSentimentWords + 1];
        while (bFullListChanges) {
            Sort.makeRandomOrderList(iWordRand);
            bFullListChanges = false;
            for (int i = 1; i <= iTotalSentimentWords; i++) {
                int iOldSentimentStrength = resources.sentimentWords.getSentiment(iWordRand[i]);
                boolean bCurrentIDChange = false;
                int iAddOneImprovement = 0;
                int iSubtractOneImprovement = 0;
                if (iOldSentimentStrength < 4) {
                    resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength + 1);
                    reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
                    iNewTrinary = getClassificationTrinaryNumberCorrect();
                    iAddOneImprovement = iNewTrinary - iLastTrinaryCorrect;
                    if (iAddOneImprovement >= iMinImprovement) {
                        bCurrentIDChange = true;
                        iLastTrinaryCorrect += iAddOneImprovement;
                    }
                }
                if (iOldSentimentStrength > -4 && !bCurrentIDChange) {
                    resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength - 1);
                    reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
                    iNewTrinary = getClassificationTrinaryNumberCorrect();
                    iSubtractOneImprovement = iNewTrinary - iLastTrinaryCorrect;
                    if (iSubtractOneImprovement >= iMinImprovement) {
                        bCurrentIDChange = true;
                        iLastTrinaryCorrect += iSubtractOneImprovement;
                    }
                }
                if (bCurrentIDChange) {
                    bFullListChanges = true;
                } else {
                    resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength);
                    reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
                }
            }

        }
    }

    /**
     * UC-27 可以针对给定的已分类为正面或负面的文本语料库优化情感分析词典中单词的权重
     * @param iMinImprovement 指定要考虑的权重变化所需的最小改进
     * @param bUseTotalDifference 指示是否在正负分类中使用总差 分数作为改进的指标或使用每个类别的正确分类数
     * @author zhangsong
     */
    public void optimiseDictionaryWeightingsForCorpusPosNeg(int iMinImprovement, boolean bUseTotalDifference) {
        boolean bFullListChanges = true;
        int iLastPos = 0;
        int iLastNeg = 0;
        int iLastPosTotalDiff = 0;
        int iLastNegTotalDiff = 0;
        if (bUseTotalDifference) {
            iLastPosTotalDiff = getClassificationPositiveTotalDifference();
            iLastNegTotalDiff = getClassificationNegativeTotalDifference();
        } else {
            iLastPos = getClassificationPositiveNumberCorrect();
            iLastNeg = getClassificationNegativeNumberCorrect();
        }
        int iNewPos = 0;
        int iNewNeg = 0;
        int iNewPosTotalDiff = 0;
        int iNewNegTotalDiff = 0;
        int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
        int[] iWordRand = new int[iTotalSentimentWords + 1];
        while (bFullListChanges) {
            Sort.makeRandomOrderList(iWordRand);
            bFullListChanges = false;
            for (int i = 1; i <= iTotalSentimentWords; i++) {
                int iOldSentimentStrength = resources.sentimentWords.getSentiment(iWordRand[i]);
                boolean bCurrentIDChange = false;
                if (iOldSentimentStrength < 4) {
                    resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength + 1);
                    reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
                    if (bUseTotalDifference) {
                        iNewPosTotalDiff = getClassificationPositiveTotalDifference();
                        iNewNegTotalDiff = getClassificationNegativeTotalDifference();
                        if (((iNewPosTotalDiff - iLastPosTotalDiff) + iNewNegTotalDiff) - iLastNegTotalDiff <= -iMinImprovement) {
                            bCurrentIDChange = true;
                        }
                    } else {
                        iNewPos = getClassificationPositiveNumberCorrect();
                        iNewNeg = getClassificationNegativeNumberCorrect();
                        if (((iNewPos - iLastPos) + iNewNeg) - iLastNeg >= iMinImprovement) {
                            bCurrentIDChange = true;
                        }
                    }
                }
                if (iOldSentimentStrength > -4 && !bCurrentIDChange) {
                    resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength - 1);
                    reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
                    if (bUseTotalDifference) {
                        iNewPosTotalDiff = getClassificationPositiveTotalDifference();
                        iNewNegTotalDiff = getClassificationNegativeTotalDifference();
                        if (((iNewPosTotalDiff - iLastPosTotalDiff) + iNewNegTotalDiff) - iLastNegTotalDiff <= -iMinImprovement) {
                            bCurrentIDChange = true;
                        }
                    } else {
                        iNewPos = getClassificationPositiveNumberCorrect();
                        iNewNeg = getClassificationNegativeNumberCorrect();
                        if (((iNewPos - iLastPos) + iNewNeg) - iLastNeg >= iMinImprovement) {
                            bCurrentIDChange = true;
                        }
                    }
                }
                if (bCurrentIDChange) {
                    if (bUseTotalDifference) {
                        iLastNegTotalDiff = iNewNegTotalDiff;
                        iLastPosTotalDiff = iNewPosTotalDiff;
                        bFullListChanges = true;
                    } else {
                        iLastNeg = iNewNeg;
                        iLastPos = iNewPos;
                        bFullListChanges = true;
                    }
                } else {
                    resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength);
                    reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
                }
            }

        }
    }

    /**
     * 从指定文件中读取数据，总结计算后写入输出文件
     * @param sInputFile 输入文件
     * @param sOutputFile 输出文件
     * @author zhangsong
     */
    public void summariseMultiple10FoldValidations(String sInputFile, String sOutputFile) {
        int iDataRows = 28;
        int iLastOptionCol = 24;
        BufferedReader rResults = null;
        BufferedWriter wSummary = null;
        String sLine = null;
        String[] sPrevData = null;
        String[] sData = null;
        float[] total = new float[iDataRows];
        int iRows = 0;
        int i = 0;
        try {
            rResults = new BufferedReader(new FileReader(sInputFile));
            wSummary = new BufferedWriter(new FileWriter(sOutputFile));
            sLine = rResults.readLine();
            wSummary.write((new StringBuilder(String.valueOf(sLine))).append("\tNumber\n").toString());
            while (rResults.ready()) {
                sLine = rResults.readLine();
                sData = sLine.split("\t");
                boolean bMatching = true;
                if (sPrevData != null) {
                    for (i = 0; i < iLastOptionCol; i++) {
                        if (!sData[i].equals(sPrevData[i])) {
                            bMatching = false;
                        }
                    }
                }

                if (!bMatching) {
                    for (i = 0; i < iLastOptionCol; i++) {
                        wSummary.write((new StringBuilder(String.valueOf(sPrevData[i]))).append("\t").toString());
                    }
                    for (i = 0; i < iDataRows; i++) {
                        wSummary.write((new StringBuilder(String.valueOf(total[i] / (float) iRows))).append("\t").toString());
                    }
                    wSummary.write((new StringBuilder(String.valueOf(iRows))).append("\n").toString());
                    for (i = 0; i < iDataRows; i++) {
                        total[i] = 0.0F;
                    }
                    iRows = 0;
                }
                for (i = iLastOptionCol; i < iLastOptionCol + iDataRows; i++) {
                    try {
                        total[i - iLastOptionCol] += Float.parseFloat(sData[i]);
                    } catch (Exception e) {
                        total[i - iLastOptionCol] += 9999999F;
                    }
                }
                iRows++;
                sPrevData = sLine.split("\t");
            }
            for (i = 0; i < iLastOptionCol; i++) {
                wSummary.write((new StringBuilder(String.valueOf(sPrevData[i]))).append("\t").toString());
            }
            for (i = 0; i < iDataRows; i++) {
                wSummary.write((new StringBuilder(String.valueOf(total[i] / (float) iRows))).append("\t").toString());
            }
            wSummary.write((new StringBuilder(String.valueOf(iRows))).append("\n").toString());
            wSummary.close();
            rResults.close();
        } catch (IOException e) {
            System.err.println((new StringBuilder("SummariseMultiple10FoldValidations: File I/O error: ")).append(sInputFile).toString());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println((new StringBuilder("SummariseMultiple10FoldValidations: Error at line: ")).append(sLine).toString());
            System.err.println((new StringBuilder("Value of i: ")).append(i).toString());
            e.printStackTrace();
        }
    }
}

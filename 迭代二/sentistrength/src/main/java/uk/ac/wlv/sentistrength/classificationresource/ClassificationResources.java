// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   ClassificationResources.java

package uk.ac.wlv.sentistrength.classificationresource;

import java.io.File;


import uk.ac.wlv.sentistrength.wordsresource.*;
import uk.ac.wlv.utilities.FileOps;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            EmoticonsList, CorrectSpellingsList, SentimentWords, NegatingWordList, 
//            QuestionWords, BoosterWordsList, IdiomList, EvaluativeTerms, 
//            IronyList, Lemmatiser, ClassificationOptions

/**
 * 用于分类处理的所需资源类
 *
 * @author zhengjie
 */
public class ClassificationResources {

    /**
     * 表情符号
     */
    public EmoticonsList emoticons;
    /**
     * 正确拼写
     */
    public CorrectSpellingsList correctSpellings;
    /**
     * 情感词汇表
     */
    public SentimentWords sentimentWords;
    /**
     * 否定词列表
     */
    public NegatingWordList negatingWords;
    /**
     * 疑问词列表
     */
    public QuestionWords questionWords;
    /**
     * 情绪加强词汇表
     */
    public BoosterWordsList boosterWords;
    /**
     * 俚语列表
     */
    public IdiomList idiomList;
    /**
     * 情感分析评价
     */
    public EvaluativeTerms evaluativeTerms;
    /**
     * 讽刺词汇列表
     */
    public IronyList ironyList;
    /**
     * 词形还原
     */
    public Lemmatiser lemmatiser;
    public SlangLookupTable slangLookupTable;
    /**
     * 项目data对应的目录文件夹
     */
    public String sgSentiStrengthFolder;
    /**
     * 情绪词汇表，初始命名为EmotionLookupTable.txt
     */
    public String sgSentimentWordsFile;
    /**
     * 情绪词汇表，需要自己创建，文件名命名为SentimentLookupTable.txt，用于替换EmotionLookupTable.txt
     */
    public String sgSentimentWordsFile2;
    /**
     * EmoticonLookupTable.txt
     */
    public String sgEmoticonLookupTable;
    /**
     * 词典文件名，需要自己创建，初始命名为Dictionary.txt，用于替换EnglishWordList.txt
     */
    public String sgCorrectSpellingFileName;
    /**
     * 英语单词词典文件名，初始化为EnglishWordList.txt
     */
    public String sgCorrectSpellingFileName2;
    /**
     * 缩写词汇表对应文件名，初始化为SlangLookupTable_NOT_USED.txt
     */
    public String sgSlangLookupTable;
    /**
     * 否定词词汇表的文件名
     */
    public String sgNegatingWordListFile;
    /**
     * 情绪强化/减弱词汇表的文件名
     */
    public String sgBoosterListFile;
    /**
     * 习语词汇表的文件名
     */
    public String sgIdiomLookupTableFile;
    /**
     * 疑问词汇表的文件名
     */
    public String sgQuestionWordListFile;
    /**
     * 讽刺词汇表的文件名
     */
    public String sgIronyWordListFile;
    /**
     * 额外文件的文件名，需要自己创建
     */
    public String sgAdditionalFile;
    /**
     * 词形还原的文件名，需要自己创建
     */
    public String sgLemmaFile;

    /**
     * 构造函数，用于初始化引入需要使用的对象和变量。
     */
    public ClassificationResources() {
        emoticons = new EmoticonsList();
        correctSpellings = new CorrectSpellingsList();
        sentimentWords = new SentimentWords();
        negatingWords = new NegatingWordList();
        questionWords = new QuestionWords();
        boosterWords = new BoosterWordsList();
        idiomList = new IdiomList();
        evaluativeTerms = new EvaluativeTerms();
        ironyList = new IronyList();
        lemmatiser = new Lemmatiser();
        slangLookupTable = new SlangLookupTable();
        sgSentiStrengthFolder = System.getProperty("user.dir") + "/src/main/java/SentStrength_Data/";
        sgSentimentWordsFile = "EmotionLookupTable.txt";
        sgSentimentWordsFile2 = "SentimentLookupTable.txt";
        sgEmoticonLookupTable = "EmoticonLookupTable.txt";
        sgCorrectSpellingFileName = "Dictionary.txt";
        sgCorrectSpellingFileName2 = "EnglishWordList.txt";
        sgSlangLookupTable = "SlangLookupTable.txt";
        sgNegatingWordListFile = "NegatingWordList.txt";
        sgBoosterListFile = "BoosterWordList.txt";
        sgIdiomLookupTableFile = "IdiomLookupTable.txt";
        sgQuestionWordListFile = "QuestionWords.txt";
        sgIronyWordListFile = "IronyTerms.txt";
        sgAdditionalFile = "";
        sgLemmaFile = "";
    }

    /**
     * UC-1;UC-2;UC-3;UC-4;UC5;UC-11;UC12;UC-13;UC-17;UC-18;UC-25;UC-26
     *
     * @param options 由ClassificationOptions类确认，是用于initialise初始化时的分类相关的选项。
     * @return 创建成功则返回true，失败则返回flase。
     * @author zhengjie
     */
    public boolean initialise(ClassificationOptions options) {
        int iExtraLinesToReserve = 0;
        //不相等,即不为空
        if (sgAdditionalFile.compareTo("") != 0) {
            iExtraLinesToReserve =
                    FileOps.i_CountLinesInTextFile((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgAdditionalFile).toString());
            if (iExtraLinesToReserve < 0) {
                //System.err.println((new StringBuilder("No lines found in additional file! Ignoring ")).append(sgAdditionalFile).toString());
                return false;
            }
        }
        if (options.bgUseLemmatisation && !lemmatiser.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgLemmaFile).toString(), false)) {
            //System.err.println((new StringBuilder("Can't load lemma file! ")).append(sgLemmaFile).toString());
            return false;
        }
        File f =
                new File((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgSentimentWordsFile).toString());
        if (!f.exists() || f.isDirectory()) {
            sgSentimentWordsFile = sgSentimentWordsFile2;
        }
        File f2 =
                new File((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgCorrectSpellingFileName).toString());
        if (!f2.exists() || f2.isDirectory()) {
            sgCorrectSpellingFileName = sgCorrectSpellingFileName2;
        }
        if (emoticons.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgEmoticonLookupTable).toString(), options)
                && correctSpellings.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgCorrectSpellingFileName).toString(), options)
                && sentimentWords.initialises((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgSentimentWordsFile).toString(), options, iExtraLinesToReserve)
                && negatingWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgNegatingWordListFile).toString(), options)
                && slangLookupTable.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgSlangLookupTable).toString(), options)
                && questionWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgQuestionWordListFile).toString(), options)
                && ironyList.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgIronyWordListFile).toString(), options)
                && boosterWords.initialises((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgBoosterListFile).toString(), options, iExtraLinesToReserve)
                && idiomList.initialises((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgIdiomLookupTableFile).toString(), options, iExtraLinesToReserve)) {
            if (iExtraLinesToReserve > 0) {
                return evaluativeTerms.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgAdditionalFile).toString(), options, idiomList, sentimentWords);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}

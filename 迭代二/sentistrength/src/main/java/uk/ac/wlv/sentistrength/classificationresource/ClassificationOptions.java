//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package uk.ac.wlv.sentistrength.classificationresource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 分类选项类，用来存取与分类相关的选项和预处理代码
 * UC-26
 * @author ruohao.zhang
 **/
public class ClassificationOptions {
    /**
     * 一个布尔值，指示是否使用背景Tensi强度的布尔值。
     **/
    public boolean bgTensiStrength = false;
    /**
     * 一个字符串，情绪分类程序的名称。
     **/
    public String sgProgramName = "SentiStrength";
    /**
     * 一个字符串，情绪分类的测量类型。
     **/
    public String sgProgramMeasuring = "sentiment";
    /**
     * 一个字符串，积极情绪的字符串表示。
     **/
    public String sgProgramPos = "positive sentiment";
    /**
     * 一个字符串，消极情绪的字符串表示。
     **/
    public String sgProgramNeg = "negative sentiment";
    /**
     * 一个布尔值，指示是否使用缩放模式的布尔值。
     **/
    public boolean bgScaleMode = false;
    /**
     * 一个布尔值，指示是否使用trinary模式的布尔值。
     **/
    public boolean bgTrinaryMode = false;
    /**
     * 一个布尔值，指示是否使用三元模式的二元模式。
     **/
    public boolean bgBinaryVersionOfTrinaryMode = false;
    /**
     * 一个整数，默认二进制分类。
     **/
    public int igDefaultBinaryClassification = 1;
    /**
     * 一个整数，用于在段落中组合情感的方法。
     **/
    public int igEmotionParagraphCombineMethod = 0;
    /**
     * 未使用，在段落中组合情感的最大价值。
     **/
    final int igCombineMax = 0;
    /**
     * 未使用，一个整数，在段落中组合情绪的平均值。
     **/
    final int igCombineAverage = 1;
    /**
     * 未使用，一个整数，在段落中组合情感的总价值。
     **/
    final int igCombineTotal = 2;
    /**
     * 一个整数，用于在句子中组合情感的方法。
     **/
    public int igEmotionSentenceCombineMethod = 0;
    /**
     * 一个浮点数，负面情绪的乘数。
     **/
    public float fgNegativeSentimentMultiplier = 1.5F;
    /**
     * 一个布尔值，指示是否在有问题的句子中减少负面情绪。
     **/
    public boolean bgReduceNegativeEmotionInQuestionSentences = false;
    /**
     * 一个布尔值，指示是否将错过的分类算作加2。
     **/
    public boolean bgMissCountsAsPlus2 = true;
    /**
     * 一个布尔值，表示在句子中使用“你”还是“你的”表示积极的情绪，除非句子是否定的。
     **/
    public boolean bgYouOrYourIsPlus2UnlessSentenceNegative = false;
    /**
     * 一个布尔值，指示中性句子中的感叹号是否算作加2。
     **/
    public boolean bgExclamationInNeutralSentenceCountsAsPlus2 = false;
    /**
     * 一个整数，感叹号后改变句子情绪所需的最小标点符号。
     **/
    public int igMinPunctuationWithExclamationToChangeSentenceSentiment = 0;
    /**
     * 一个布尔值，指示是否使用成语查找表的布尔值。
     **/
    public boolean bgUseIdiomLookupTable = true;
    /**
     * 一个布尔值，指示是否使用对象评估表的布尔值。
     **/
    public boolean bgUseObjectEvaluationTable = false;
    /**
     * 未使用，一个布尔值，表示是否将中性情绪视为积极的强调1。
     **/
    public boolean bgCountNeutralEmotionsAsPositiveForEmphasis1 = true;
    /**
     * 一个整数，强调中性情绪的值。
     **/
    public int igMoodToInterpretNeutralEmphasis = 1;
    /**
     * 一个布尔值，指示是否允许多个积极的单词来增加积极的情绪。
     **/
    public boolean bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = true;
    /**
     * 一个布尔值，指示是否允许多个负面单词来增加负面情绪。
     **/
    public boolean bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = true;
    /**
     * 一个布尔值，指示是否在否定词后忽略助推器词。
     **/
    public boolean bgIgnoreBoosterWordsAfterNegatives = true;
    /**
     * 一个布尔值，指示是否使用字典更正拼写的布尔值。
     **/
    public boolean bgCorrectSpellingsUsingDictionary = true;
    /**
     * 一个布尔值，指示是否要更正额外的字母拼写错误。
     **/
    public boolean bgCorrectExtraLetterSpellingErrors = true;
    /**
     * 一个字符串，在单词中间表示非法双字母的字符串。
     **/
    public String sgIllegalDoubleLettersInWordMiddle = "ahijkquvxyz";
    /**
     * 一个字符串，在单词末尾表示非法双字母的字符串。
     **/
    public String sgIllegalDoubleLettersAtWordEnd = "achijkmnpqruvwxyz";
    /**
     * 一个布尔值，指示多个字母是否提升情绪。
     **/
    public boolean bgMultipleLettersBoostSentiment = true;
    /**
     * 一个布尔值，表示助推器词是否会改变情绪。
     **/
    public boolean bgBoosterWordsChangeEmotion = true;
    /**
     * 一个布尔值，指示是否总是在撇号上拆分单词。
     **/
    public boolean bgAlwaysSplitWordsAtApostrophes = false;
    /**
     * 一个布尔值，表示否定词是否出现在情绪之前。
     **/
    public boolean bgNegatingWordsOccurBeforeSentiment = true;
    /**
     * 一个整数，表示要否定的情绪之前的最大单词数。
     **/
    public int igMaxWordsBeforeSentimentToNegate = 0;
    /**
     * 一个布尔值，表示否定词是否出现在情绪之后。
     **/
    public boolean bgNegatingWordsOccurAfterSentiment = false;
    /**
     * 一个整数，表示情绪后要否定的最大单词数。
     **/
    public int igMaxWordsAfterSentimentToNegate = 0;
    /**
     * 一个布尔值，表示否定积极翻转情绪是否。
     **/
    public boolean bgNegatingPositiveFlipsEmotion = true;
    /**
     * 一个布尔值，表示否定否定是否中和了情绪。
     **/
    public boolean bgNegatingNegativeNeutralisesEmotion = true;
    /**
     * 一个布尔值，表示否定单词是否翻转情绪。
     **/
    public boolean bgNegatingWordsFlipEmotion = false;
    /**
     * 一个浮点数，表示否定单词的强度乘数。
     **/
    public float fgStrengthMultiplierForNegatedWords = 0.5F;
    /**
     * 一个布尔值，指示是否用重复的字母纠正拼写。
     **/
    public boolean bgCorrectSpellingsWithRepeatedLetter = true;
    /**
     * 一个布尔值，指示是否使用表情符号的布尔值。
     **/
    public boolean bgUseEmoticons = true;
    /**
     * 一个布尔值，表示资本是否提升了术语情绪。
     **/
    public boolean bgCapitalsBoostTermSentiment = false;
    /**
     * 一个整数，表示提升情绪的最小重复字符数。
     **/
    public int igMinRepeatedLettersForBoost = 2;
    /**
     * 一个字符串数组，当出现在句子中时，可能会影响情绪分类的关键字数组。
     **/
    public String[] sgSentimentKeyWords = null;
    /**
     * 一个布尔值，是否忽略不包含sgSentimentKeyWords中指定的任何关键字的句子。
     **/
    public boolean bgIgnoreSentencesWithoutKeywords = false;
    /**
     * 一个整数，在处理情感分析时，句子中要包含在关键字之前的单词数。
     **/
    public int igWordsToIncludeBeforeKeyword = 4;
    /**
     * 一个整数，处理情绪分析时，句子中关键字后要包含的单词数。
     **/
    public int igWordsToIncludeAfterKeyword = 4;
    /**
     * 一个布尔值，是否提供情绪分类的解释。
     **/
    public boolean bgExplainClassification = false;
    /**
     * 一个布尔值，是否将输入文本作为输出的重复。
     **/
    public boolean bgEchoText = false;
    /**
     * 一个布尔值，是否强制使用UTF-8编码。
     **/
    public boolean bgForceUTF8 = false;
    /**
     * 一个布尔值，是否使用lemmatisation来提高情绪分析的准确性。
     **/
    public boolean bgUseLemmatisation = false;
    /**
     * 一个整数，将引号视为讽刺的最小句子位置。
     **/
    public int igMinSentencePosForQuotesIrony = 10;
    /**
     * 一个整数，将标点符号视为讽刺的最低句子位置。
     **/
    public int igMinSentencePosForPunctuationIrony = 10;
    /**
     * 一个整数，将特定术语视为表示讽刺的最低句子位置。
     **/
    public int igMinSentencePosForTermsIrony = 10;
    
    /**
     * 构造函数
     * @author ruohao.zhang
     */
    public ClassificationOptions() {
    }

    /**
     * 传递keywordlist（要求格式为用","隔开），随后的处理中要忽略没有关键词的句子
     * @param sKeywordList 由keyword和","组成的字符串
     * @author ruohao.zhang
     */
    public void parseKeywordList(String sKeywordList) {
        this.sgSentimentKeyWords = sKeywordList.split(",");
        this.bgIgnoreSentencesWithoutKeywords = true;
    }

    /**
     * 此方法将一组分类选项写入指定的BufferedWriter对象。这些选项包括控制情绪分析算法工作原理的各种布尔值和整数值。
     * @param wWriter 用于写入分类选项信息的输出流
     * @param iMinImprovement 使用分类器对情感分类时的最小改进值
     * @param bUseTotalDifference 是否使用总差异而不是精确计数来计算情感分类结果
     * @param iMultiOptimisations 情感分类器中应用的优化级别
     * @return boolean 指示选项是否已成功写入BufferedWriter。如果有IOException，它将打印堆栈跟踪并返回false。
     * @author ruohao.zhang
     */
    public boolean printClassificationOptions(BufferedWriter wWriter, int iMinImprovement,
                                              boolean bUseTotalDifference, int iMultiOptimisations) {
        try {
            if (this.igEmotionParagraphCombineMethod == 0) {
                wWriter.write("Max");
            } else if (this.igEmotionParagraphCombineMethod == 1) {
                wWriter.write("Av");
            } else {
                wWriter.write("Tot");
            }

            if (this.igEmotionSentenceCombineMethod == 0) {
                wWriter.write("\tMax");
            } else if (this.igEmotionSentenceCombineMethod == 1) {
                wWriter.write("\tAv");
            } else {
                wWriter.write("\tTot");
            }

            if (bUseTotalDifference) {
                wWriter.write("\tTotDiff");
            } else {
                wWriter.write("\tExactCount");
            }

            wWriter.write("\t" + iMultiOptimisations + "\t" + this.bgReduceNegativeEmotionInQuestionSentences
                    + "\t" + this.bgMissCountsAsPlus2 + "\t" + this.bgYouOrYourIsPlus2UnlessSentenceNegative + "\t"
                    + this.bgExclamationInNeutralSentenceCountsAsPlus2 + "\t" + this.bgUseIdiomLookupTable + "\t"
                    + this.igMoodToInterpretNeutralEmphasis + "\t"
                    + this.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion
                    + "\t" + this.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion + "\t"
                    + this.bgIgnoreBoosterWordsAfterNegatives
                    + "\t" + this.bgMultipleLettersBoostSentiment + "\t" + this.bgBoosterWordsChangeEmotion + "\t"
                    + this.bgNegatingWordsFlipEmotion + "\t" + this.bgNegatingPositiveFlipsEmotion + "\t"
                    + this.bgNegatingNegativeNeutralisesEmotion + "\t" + this.bgCorrectSpellingsWithRepeatedLetter
                    + "\t" + this.bgUseEmoticons + "\t" + this.bgCapitalsBoostTermSentiment + "\t"
                    + this.igMinRepeatedLettersForBoost + "\t" + this.igMaxWordsBeforeSentimentToNegate
                    + "\t" + iMinImprovement);
            return true;
        } catch (IOException var6) {
            var6.printStackTrace();
            return false;
        }
    }

    /**
     * 用于打印空白的分类选项，目的是在输出中保持格式的一致性
     * @param wWriter 写入文本文件的输出流
     * @return boolean 如果正常写入就返回true，有异常就返回false
     * @author ruohao.zhang
     */
    public boolean printBlankClassificationOptions(BufferedWriter wWriter) {
        try {
            wWriter.write("~");
            wWriter.write("\t~");
            wWriter.write("\tBaselineMajorityClass");
            wWriter.write("\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~");
            return true;
        } catch (IOException var3) {
            var3.printStackTrace();
            return false;
        }
    }

    /**
     * 为打印的参数编写标题或标签
     * @param wWriter 写入文本文件的输出流
     * @return boolean 如果正常写入就返回true，有异常就返回false
     * @author ruohao.zhang
     */
    public boolean printClassificationOptionsHeadings(BufferedWriter wWriter) {
        try {
            wWriter.write("EmotionParagraphCombineMethod\tEmotionSentenceCombineMethod\t"
                    + "DifferenceCalculationMethodForTermWeightAdjustments\tMultiOptimisations\t"
                    + "ReduceNegativeEmotionInQuestionSentences\tMissCountsAsPlus2\t"
                    + "YouOrYourIsPlus2UnlessSentenceNegative\tExclamationCountsAsPlus2\t"
                    + "UseIdiomLookupTable\tMoodToInterpretNeutralEmphasis\t"
                    + "AllowMultiplePositiveWordsToIncreasePositiveEmotion\t"
                    + "AllowMultipleNegativeWordsToIncreaseNegativeEmotion\tIgnoreBoosterWordsAfterNegatives\t"
                    + "MultipleLettersBoostSentiment\tBoosterWordsChangeEmotion\tNegatingWordsFlipEmotion\t"
                    + "NegatingPositiveFlipsEmotion\tNegatingNegativeNeutralisesEmotion\t"
                    + "CorrectSpellingsWithRepeatedLetter\tUseEmoticons\tCapitalsBoostTermSentiment\t"
                    + "MinRepeatedLettersForBoost\tWordsBeforeSentimentToNegate\tMinImprovement");
            return true;
        } catch (IOException var3) {
            var3.printStackTrace();
            return false;
        }
    }

    /**
     * 读取文件的内容，解析文件中指定的选项，并在对象（即自己）中设置相应的变量（成员）
     * @param sFilename 要读取的文件名
     * @return boolean 遇到它无法识别的选项名称，它将返回false，表明解析文件时出错。否则，该方法返回true
     * @author ruohao.zhang
     */
    public boolean setClassificationOptions(String sFilename) {
        try {
            BufferedReader rReader = new BufferedReader(new FileReader(sFilename));

            while (rReader.ready()) {
                String sLine = rReader.readLine();
                int iTabPos = sLine.indexOf("\t");
                if (iTabPos > 0) {
                    String[] sData = sLine.split("\t");
                    if (sData[0].equals("EmotionParagraphCombineMethod")) {
                        if (sData[1].indexOf("Max") >= 0) {
                            this.igEmotionParagraphCombineMethod = 0;
                        }

                        if (sData[1].indexOf("Av") >= 0) {
                            this.igEmotionParagraphCombineMethod = 1;
                        }

                        if (sData[1].indexOf("Tot") >= 0) {
                            this.igEmotionParagraphCombineMethod = 2;
                        }
                    } else if (sData[0].equals("EmotionSentenceCombineMethod")) {
                        if (sData[1].indexOf("Max") >= 0) {
                            this.igEmotionSentenceCombineMethod = 0;
                        }

                        if (sData[1].indexOf("Av") >= 0) {
                            this.igEmotionSentenceCombineMethod = 1;
                        }

                        if (sData[1].indexOf("Tot") >= 0) {
                            this.igEmotionSentenceCombineMethod = 2;
                        }
                    } else if (sData[0].equals("IgnoreNegativeEmotionInQuestionSentences")) {
                        this.bgReduceNegativeEmotionInQuestionSentences = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("MissCountsAsPlus2")) {
                        this.bgMissCountsAsPlus2 = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("YouOrYourIsPlus2UnlessSentenceNegative")) {
                        this.bgYouOrYourIsPlus2UnlessSentenceNegative = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("ExclamationCountsAsPlus2")) {
                        this.bgExclamationInNeutralSentenceCountsAsPlus2 = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("UseIdiomLookupTable")) {
                        this.bgUseIdiomLookupTable = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("Mood")) {
                        this.igMoodToInterpretNeutralEmphasis = Integer.parseInt(sData[1]);
                    } else if (sData[0].equals("AllowMultiplePositiveWordsToIncreasePositiveEmotion")) {
                        this.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("AllowMultipleNegativeWordsToIncreaseNegativeEmotion")) {
                        this.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("IgnoreBoosterWordsAfterNegatives")) {
                        this.bgIgnoreBoosterWordsAfterNegatives = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("MultipleLettersBoostSentiment")) {
                        this.bgMultipleLettersBoostSentiment = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("BoosterWordsChangeEmotion")) {
                        this.bgBoosterWordsChangeEmotion = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("NegatingWordsFlipEmotion")) {
                        this.bgNegatingWordsFlipEmotion = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("NegatingWordsFlipEmotion")) {
                        this.bgNegatingPositiveFlipsEmotion = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("NegatingWordsFlipEmotion")) {
                        this.bgNegatingNegativeNeutralisesEmotion = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("CorrectSpellingsWithRepeatedLetter")) {
                        this.bgCorrectSpellingsWithRepeatedLetter = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("UseEmoticons")) {
                        this.bgUseEmoticons = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("CapitalsAreSentimentBoosters")) {
                        this.bgCapitalsBoostTermSentiment = Boolean.parseBoolean(sData[1]);
                    } else if (sData[0].equals("MinRepeatedLettersForBoost")) {
                        this.igMinRepeatedLettersForBoost = Integer.parseInt(sData[1]);
                    } else if (sData[0].equals("WordsBeforeSentimentToNegate")) {
                        this.igMaxWordsBeforeSentimentToNegate = Integer.parseInt(sData[1]);
                    } else if (sData[0].equals("Trinary")) {
                        this.bgTrinaryMode = true;
                    } else if (sData[0].equals("Binary")) {
                        this.bgTrinaryMode = true;
                        this.bgBinaryVersionOfTrinaryMode = true;
                    } else {
                        if (!sData[0].equals("Scale")) {
                            rReader.close();
                            return false;
                        }

                        this.bgScaleMode = true;
                    }
                }
            }

            rReader.close();
            return true;
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
            return false;
        } catch (IOException var8) {
            var8.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据bTensiStrength选择不同处理方法，其中一种与压力放松有关，还有一种与情绪有关
     * @param bTensiStrength 如果为true，与压力放松有关；如果为false则与情绪有关
     * @author ruohao.zhang
     */
    public void nameProgram(boolean bTensiStrength) {
        this.bgTensiStrength = bTensiStrength;
        if (bTensiStrength) {
            this.sgProgramName = "TensiStrength";
            this.sgProgramMeasuring = "stress and relaxation";
            this.sgProgramPos = "relaxation";
            this.sgProgramNeg = "stress";
        } else {
            this.sgProgramName = "SentiStrength";
            this.sgProgramMeasuring = "sentiment";
            this.sgProgramPos = "positive sentiment";
            this.sgProgramNeg = "negative sentiment";
        }

    }
}

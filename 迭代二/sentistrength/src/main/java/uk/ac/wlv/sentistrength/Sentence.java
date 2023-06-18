//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package uk.ac.wlv.sentistrength;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.sentistrength.classificationresource.ClassificationResources;
import uk.ac.wlv.sentistrength.classificationresource.UnusedTermsClassificationIndex;
import uk.ac.wlv.utilities.Sort;
import uk.ac.wlv.utilities.StringIndex;
import uk.ac.wlv.wkaclass.Arff;

public class Sentence {
    /** Term对象数组，其中每个对象代表sentence中的一个词、标点符号或情感符号 **/
    private Term[] term;
    /** 指示空格是否应该跟在每个术语之后 **/
    private boolean[] bgSpaceAfterTerm;
    /** 表示sentence中的term数 **/
    private int igTermCount = 0;
    /**
     * 返回igTermCount，sentence中的term数。
     * @return igTermCount sentence中的term数
     * @author ruohao.zhang
     */
    public int getIgTermCount() {
		return igTermCount;
	}
    /**
     * 返回igSentiCount，sentence中情感词的数量。
     * @return igSentiCount sentence中情感词的数量
     * @author ruohao.zhang
     */
    public int getIgSentiCount() {
		return igSentiCount;
	}
    /**表示sentence中情感词的数量 **/
    private int igSentiCount = 0;
    /** 积极情绪词的数量 **/
    private int igPositiveSentiment = 0;
    /** 消极情绪词的数量 **/
    private int igNegativeSentiment = 0;
    /** 表示一个语句中是否没有可分类的 **/
    private boolean bgNothingToClassify = true;
    /** 分类资源的对象 **/
    private ClassificationResources resources;
    /** 分类选项的对象 **/
    private ClassificationOptions options;
    /** 情感词的情感 ID **/
    private int[] igSentimentIDList;
    /** 情感 ID 的数量 **/
    private int igSentimentIDListCount = 0;
    /** 是否已经创建了igSentimentIDList **/
    private boolean bSentimentIDListMade = false;
    /** 每个term是否应包含在情感分析中 **/
    private boolean[] bgIncludeTerm;
    /** idioms是否已应用于sentence **/
    private boolean bgIdiomsApplied = false;
    /** 对象评估是否已应用于sentence **/
    private boolean bgObjectEvaluationsApplied = false;
    /** sentence分类原理的字符串 **/
    private String sgClassificationRationale = "";

    /**
     * 默认构造函数
     * @author haofeng.Yu
     */
    public Sentence() {
    }

    /**
     * 将sentence中的term加入到index中
     * @param unusedTermClassificationIndex 未使用术语的分类索引对象
     * @author haofeng.Yu
     */
    public void addSentenceToIndex(UnusedTermsClassificationIndex unusedTermClassificationIndex) {
        for (int i = 1; i <= this.igTermCount; ++i) {
            unusedTermClassificationIndex.addTermToNewTermIndex(this.term[i].getText());
        }

    }

    /**
     * 将sentence中的每个term加到字符串索引并返回检查的term数
     * @param stringIndex 要使用的字符串索引
     * @param textParsingOptions 字符转换选项
     * @param bRecordCount 是否应记录term的频率计数
     * @param bArffIndex 是否应创建索引以用于 Weka 机器学习库的 ARFF 文件格式
     * @return 检查的term数
     * @author haofeng.Yu
     */
    public int addToStringIndex(StringIndex stringIndex, TextParsingOptions textParsingOptions, boolean bRecordCount, boolean bArffIndex) {
        String sEncoded = "";
        int iStringPos = 1;
        int iTermsChecked = 0;
        if (textParsingOptions.bgIncludePunctuation
                && textParsingOptions.igNgramSize == 1
                && !textParsingOptions.bgUseTranslations
                && !textParsingOptions.bgAddEmphasisCode) {
            for (int i = 1; i <= this.igTermCount; ++i) {
                stringIndex.addString(this.term[i].getText(), bRecordCount);
            }

            iTermsChecked = this.igTermCount;
        } else {
            String sText = "";
            int iCurrentTerm = 0;
            int iTermCount = 0;

            while (iCurrentTerm < this.igTermCount) {
                ++iCurrentTerm;
                if (textParsingOptions.bgIncludePunctuation || !this.term[iCurrentTerm].isPunctuation()) {
                    ++iTermCount;
                    if (iTermCount > 1) {
                        sText = sText + " ";
                    } else {
                        sText = "";
                    }

                    if (textParsingOptions.bgUseTranslations) {
                        sText = sText + this.term[iCurrentTerm].getTranslation();
                    } else {
                        sText = sText + this.term[iCurrentTerm].getOriginalText();
                    }

                    if (textParsingOptions.bgAddEmphasisCode && this.term[iCurrentTerm].containsEmphasis()) {
                        sText = sText + "+";
                    }
                }

                if (iTermCount == textParsingOptions.igNgramSize) {
                    if (bArffIndex) {
                        sEncoded = Arff.arffSafeWordEncode(sText.toLowerCase(), false);
                        iStringPos = stringIndex.findString(sEncoded);
                        iTermCount = 0;
                        if (iStringPos > -1) {
                            stringIndex.add1ToCount(iStringPos);
                        }
                    } else {
                        stringIndex.addString(sText.toLowerCase(), bRecordCount);
                        iTermCount = 0;
                    }

                    iCurrentTerm += 1 - textParsingOptions.igNgramSize;
                    ++iTermsChecked;
                }
            }
        }

        return iTermsChecked;
    }

    /**
     * UC-11 Classify a single text
     * 设置分类器处理的sentence以及分类资源和选项
     * @param sSentence 要处理的sentence字符串
     * @param classResources 分类资源的实例（词典、情感词字典、idiomList等）
     * @param newClassificationOptions 分类选项的实例
     * @author haofeng.Yu
     */
    public void setSentence(String sSentence, ClassificationResources classResources, ClassificationOptions newClassificationOptions) {
        this.resources = classResources;
        this.options = newClassificationOptions;
        if (this.options.bgAlwaysSplitWordsAtApostrophes && sSentence.indexOf("'") >= 0) {
            sSentence = sSentence.replace("'", " ");
        }

        String[] sSegmentList = sSentence.split(" ");
        int iSegmentListLength = sSegmentList.length;
        int iMaxTermListLength = sSentence.length() + 1;
        this.term = new Term[iMaxTermListLength];
        this.bgSpaceAfterTerm = new boolean[iMaxTermListLength];
        int iPos = 0;
        this.igTermCount = 0;

        for (int iSegment = 0; iSegment < iSegmentListLength; ++iSegment) {
            for (iPos = 0; iPos >= 0 && iPos < sSegmentList[iSegment].length(); this.bgSpaceAfterTerm[this.igTermCount] = false) {
                this.term[++this.igTermCount] = new Term();
                int iOffset = this.term[this.igTermCount]
                        .extractNextWordOrPunctuationOrEmoticon(sSegmentList[iSegment].substring(iPos), this.resources, this.options);
                if (iOffset < 0) {
                    iPos = iOffset;
                } else {
                    iPos += iOffset;
                }
            }

            this.bgSpaceAfterTerm[this.igTermCount] = true;
        }

        this.bgSpaceAfterTerm[this.igTermCount] = false;
    }

    /**
     * 返回sentence中情感 ID 数组
     * @return int[] sentence中情感 ID 数组
     * @author haofeng.Yu
     */
    public int[] getSentimentIDList() {
        if (!this.bSentimentIDListMade) {
            this.makeSentimentIDList();
        }

        return this.igSentimentIDList;
    }

    /**
     * UC-2 Assigning Sentiment Scores for Phrases
     * 在sentence中创建情感 ID 数组
     * @author haofeng.Yu
     */
    public void makeSentimentIDList() {
        int iSentimentIDTemp = 0;
        this.igSentimentIDListCount = 0;

        int i;
        for (i = 1; i <= this.igTermCount; ++i) {
            if (this.term[i].getSentimentID() > 0) {
                ++this.igSentimentIDListCount;
            }
        }

        if (this.igSentimentIDListCount > 0) {
            this.igSentimentIDList = new int[this.igSentimentIDListCount + 1];
            this.igSentimentIDListCount = 0;

            for (i = 1; i <= this.igTermCount; ++i) {
                iSentimentIDTemp = this.term[i].getSentimentID();
                if (iSentimentIDTemp > 0) {
                    for (int j = 1; j <= this.igSentimentIDListCount; ++j) {
                        if (iSentimentIDTemp == this.igSentimentIDList[j]) {
                            iSentimentIDTemp = 0;
                            break;
                        }
                    }

                    if (iSentimentIDTemp > 0) {
                        this.igSentimentIDList[++this.igSentimentIDListCount] = iSentimentIDTemp;
                    }
                }
            }

            Sort.quickSortInt(this.igSentimentIDList, 1, this.igSentimentIDListCount);
        }

        this.bSentimentIDListMade = true;
    }

    /**
     * 返回标记sentence的字符串，其中每个term都用其相应的词性标记进行注释
     * @return java.lang.String 标记后的字符串
     * @author haofeng.Yu
     */
    public String getTaggedSentence() {
        String sTagged = "";

        for (int i = 1; i <= this.igTermCount; ++i) {
            if (this.bgSpaceAfterTerm[i]) {
                sTagged = sTagged + this.term[i].getTag() + " ";
            } else {
                sTagged = sTagged + this.term[i].getTag();
            }
        }

        return sTagged + "<br>";
    }

    /**
     * 获取分类原理实例
     * @return java.lang.String 分类原理实例
     * @author haofeng.Yu
     */
    public String getClassificationRationale() {
        return this.sgClassificationRationale;
    }

    /**
     * 返回翻译后sentence的字符串，其中每个单词都被其翻译替换
     * @return java.lang.String
     * @author haofeng.Yu
     */
    public String getTranslatedSentence() {
        String sTranslated = "";

        for (int i = 1; i <= this.igTermCount; ++i) {
            if (this.term[i].isWord()) {
                sTranslated = sTranslated + this.term[i].getTranslatedWord();
            } else if (this.term[i].isPunctuation()) {
                sTranslated = sTranslated + this.term[i].getTranslatedPunctuation();
            } else if (this.term[i].isEmoticon()) {
                sTranslated = sTranslated + this.term[i].getEmoticon();
            }

            if (this.bgSpaceAfterTerm[i]) {
                sTranslated = sTranslated + " ";
            }
        }

        return sTranslated + "<br>";
    }

    /**
     * 重新计算sentence的情感得分
     * @author haofeng.Yu
     */
    public void recalculateSentenceSentimentScore() {
        this.calculateSentenceSentimentScore();
    }

    /**
     * UC-24 Use a single positive-negative scale classification
     * 重新分类sentence的情感变化
     * @param iSentimentWordID 情感词id，如果这个词出现在sentence中，则重新计算情感分数
     * @author haofeng.Yu
     */
    public void reClassifyClassifiedSentenceForSentimentChange(int iSentimentWordID) {
        if (this.igNegativeSentiment == 0) {
            this.calculateSentenceSentimentScore();
        } else {
            if (!this.bSentimentIDListMade) {
                this.makeSentimentIDList();
            }

            if (this.igSentimentIDListCount != 0) {
                if (Sort.i_FindIntPositionInSortedArray(iSentimentWordID, this.igSentimentIDList, 1, this.igSentimentIDListCount) >= 0) {
                    this.calculateSentenceSentimentScore();
                }

            }
        }
    }

    /**
     * UC-24 Use a single positive-negative scale classification
     * 返回sentence的积极情感得分
     * @return int
     * @author haofeng.Yu
     */
    public int getSentencePositiveSentiment() {
        if (this.igPositiveSentiment == 0) {
            this.calculateSentenceSentimentScore();
        }

        return this.igPositiveSentiment;
    }

    /**
     * UC-24 Use a single positive-negative scale classification
     * 返回sentence的消极情感得分
     * @return int
     * @author haofeng.Yu
     */
    public int getSentenceNegativeSentiment() {
        if (this.igNegativeSentiment == 0) {
            this.calculateSentenceSentimentScore();
        }

        return this.igNegativeSentiment;
    }

    /**
     * UC-1Assigning Sentiment Scores for Words
     * 标记sentence中的有效term数
     * @author haofeng.Yu
     */
    private void markTermsValidToClassify() {
        this.bgIncludeTerm = new boolean[this.igTermCount + 1];
        int iTermsSinceValid;
        if (this.options.bgIgnoreSentencesWithoutKeywords) {
            this.bgNothingToClassify = true;

            int iTerm;
            for (iTermsSinceValid = 1; iTermsSinceValid <= this.igTermCount; ++iTermsSinceValid) {
                this.bgIncludeTerm[iTermsSinceValid] = false;
                if (this.term[iTermsSinceValid].isWord()) {
                    for (iTerm = 0; iTerm < this.options.sgSentimentKeyWords.length; ++iTerm) {
                        if (this.term[iTermsSinceValid].matchesString(this.options.sgSentimentKeyWords[iTerm], true)) {
                            this.bgIncludeTerm[iTermsSinceValid] = true;
                            this.bgNothingToClassify = false;
                        }
                    }
                }
            }

            if (!this.bgNothingToClassify) {
                iTermsSinceValid = 100000;

                for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                    if (this.bgIncludeTerm[iTerm]) {
                        iTermsSinceValid = 0;
                    } else if (iTermsSinceValid < this.options.igWordsToIncludeAfterKeyword) {
                        this.bgIncludeTerm[iTerm] = true;
                        if (this.term[iTerm].isWord()) {
                            ++iTermsSinceValid;
                        }
                    }
                }

                iTermsSinceValid = 100000;

                for (iTerm = this.igTermCount; iTerm >= 1; --iTerm) {
                    if (this.bgIncludeTerm[iTerm]) {
                        iTermsSinceValid = 0;
                    } else if (iTermsSinceValid < this.options.igWordsToIncludeBeforeKeyword) {
                        this.bgIncludeTerm[iTerm] = true;
                        if (this.term[iTerm].isWord()) {
                            ++iTermsSinceValid;
                        }
                    }
                }
            }
        } else {
            for (iTermsSinceValid = 1; iTermsSinceValid <= this.igTermCount; ++iTermsSinceValid) {
                this.bgIncludeTerm[iTermsSinceValid] = true;
            }

            this.bgNothingToClassify = false;
        }

    }

    /**
     * UC-4 Booster Word Rule
     * UC-5 Negating Word Rule
     * UC-6 Repeated Letter Rule
     * UC-9 Repeated Punctuation Rule
     * UC-11 Classify a single text
     * 计算sentence的情感得分的核心函数
     * @author haofeng.Yu
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    private void calculateSentenceSentimentScore() {
        if (this.options.bgExplainClassification && this.sgClassificationRationale.length() > 0) {
            this.sgClassificationRationale = "";
        }

        this.igNegativeSentiment = 1;
        this.igPositiveSentiment = 1;
        int iWordTotal = 0;
        int iLastBoosterWordScore = 0;
        int iTemp = 0;
        if (this.igTermCount == 0) {
            this.bgNothingToClassify = true;
            this.igNegativeSentiment = -1;
            //this.igPositiveSentiment = 1;
        } else {
            this.markTermsValidToClassify();
            if (this.bgNothingToClassify) {
                this.igNegativeSentiment = -1;
                this.igPositiveSentiment = 1;
            } else {
                boolean bSentencePunctuationBoost = false;
                int iWordsSinceNegative = this.options.igMaxWordsBeforeSentimentToNegate + 2;
                float[] fSentiment = new float[this.igTermCount + 1];
                if (this.options.bgUseIdiomLookupTable) {
                    this.overrideTermStrengthsWithIdiomStrengths(false);
                }

                if (this.options.bgUseObjectEvaluationTable) {
                    this.overrideTermStrengthsWithObjectEvaluationStrengths(false);
                }

                for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                    if (this.bgIncludeTerm[iTerm]) {
                        int iTermsChecked;
                        if (!this.term[iTerm].isWord()) {
                            if (this.term[iTerm].isEmoticon()) {
                                iTermsChecked = this.term[iTerm].getEmoticonSentimentStrength();
                                if (iTermsChecked != 0) {
                                    if (iWordTotal > 0) {
                                        fSentiment[iWordTotal] += (float) this.term[iTerm].getEmoticonSentimentStrength();
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + this.term[iTerm].getEmoticon()
                                                    + " ["
                                                    + this.term[iTerm].getEmoticonSentimentStrength()
                                                    + " emoticon] ";
                                        }
                                    } else {
                                        ++iWordTotal;
                                        fSentiment[iWordTotal] = (float) iTermsChecked;
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + this.term[iTerm].getEmoticon()
                                                    + " ["
                                                    + this.term[iTerm].getEmoticonSentimentStrength()
                                                    + " emoticon]";
                                        }
                                    }
                                }
                            } else if (this.term[iTerm].isPunctuation()) {
                            	if (this.term[iTerm].getPunctuationEmphasisLength()
                                        >= this.options.igMinPunctuationWithExclamationToChangeSentenceSentiment
                                        && this.term[iTerm].punctuationContains("!") && iWordTotal > 0) {
                                    bSentencePunctuationBoost = true;
                                    if (this.options.bgExplainClassification) {
                                        this.sgClassificationRationale = this.sgClassificationRationale
                                                + this.term[iTerm].getOriginalText();
                                    }
                                } else if (this.options.bgExplainClassification) {
                                    this.sgClassificationRationale = this.sgClassificationRationale + this.term[iTerm].getOriginalText();
                                }
                            }
                        } else {
                            ++iWordTotal;
                            if (iTerm == 1
                                    || !this.term[iTerm].isProperNoun()
                                    || this.term[iTerm - 1].getOriginalText().equals(":")
                                    || this.term[iTerm - 1].getOriginalText().length() > 3
                                    && this.term[iTerm - 1].getOriginalText().substring(0, 1).equals("@")) {
                                fSentiment[iWordTotal] = (float) this.term[iTerm].getSentimentValue();

                                if (this.options.bgExplainClassification) {
                                    iTemp = this.term[iTerm].getSentimentValue();
                                    if (iTemp < 0) {
                                        --iTemp;
                                    } else {
                                        ++iTemp;
                                    }

                                    if (iTemp == 1) {
                                        this.sgClassificationRationale = this.sgClassificationRationale
                                                + this.term[iTerm].getOriginalText() + " ";
                                    } else {
                                        this.sgClassificationRationale = this.sgClassificationRationale
                                                + this.term[iTerm].getOriginalText()
                                                + "[" + iTemp + "] ";
                                    }
                                }
                            } else if (this.options.bgExplainClassification) {
                                this.sgClassificationRationale = this.sgClassificationRationale
                                        + this.term[iTerm].getOriginalText()
                                        + " [proper noun] ";
                            }

                            if (this.options.bgMultipleLettersBoostSentiment
                                    && this.term[iTerm].getWordEmphasisLength() >= this.options.igMinRepeatedLettersForBoost
                                    && (iTerm == 1
                                        || !this.term[iTerm - 1].isPunctuation()
                                        || !this.term[iTerm - 1].getOriginalText().equals("@"))) {
                                String sEmphasis = this.term[iTerm].getWordEmphasis().toLowerCase();
                                if (sEmphasis.indexOf("xx") < 0 && sEmphasis.indexOf("ww") < 0 && sEmphasis.indexOf("ha") < 0) {
                                    if (fSentiment[iWordTotal] < 0.0F) {
                                        fSentiment[iWordTotal] = (float) ((double) fSentiment[iWordTotal] - 0.6D);
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale + "[-0.6 spelling emphasis] ";
                                        }
                                    } else if (fSentiment[iWordTotal] > 0.0F) {
                                        fSentiment[iWordTotal] = (float) ((double) fSentiment[iWordTotal] + 0.6D);
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + "[+0.6 spelling emphasis] ";
                                        }
                                    } else if (this.options.igMoodToInterpretNeutralEmphasis > 0) {
                                        fSentiment[iWordTotal] = (float) ((double) fSentiment[iWordTotal] + 0.6D);
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + "[+0.6 spelling mood emphasis] ";
                                        }
                                    } else if (this.options.igMoodToInterpretNeutralEmphasis < 0) {
                                        fSentiment[iWordTotal] = (float) ((double) fSentiment[iWordTotal] - 0.6D);
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + "[-0.6 spelling mood emphasis] ";
                                        }
                                    }
                                }
                            }

                            int var10002;
                            if (this.options.bgCapitalsBoostTermSentiment
                                    && fSentiment[iWordTotal] != 0.0F
                                    && this.term[iTerm].isAllCapitals()) {
                                if (fSentiment[iWordTotal] > 0.0F) {
                                    var10002 = (int) fSentiment[iWordTotal]++;
                                    if (this.options.bgExplainClassification) {
                                        this.sgClassificationRationale = this.sgClassificationRationale + "[+1 CAPITALS] ";
                                    }
                                } else {
                                    var10002 = (int) fSentiment[iWordTotal]--;
                                    if (this.options.bgExplainClassification) {
                                        this.sgClassificationRationale = this.sgClassificationRationale + "[-1 CAPITALS] ";
                                    }
                                }
                            }

                            if (this.options.bgBoosterWordsChangeEmotion) {
                                if (iLastBoosterWordScore != 0) {
                                    if (fSentiment[iWordTotal] > 0.0F) {
                                        fSentiment[iWordTotal] += (float) iLastBoosterWordScore;
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + "[+" + iLastBoosterWordScore + " booster word] ";
                                        }
                                    } else if (fSentiment[iWordTotal] < 0.0F) {
                                        fSentiment[iWordTotal] -= (float) iLastBoosterWordScore;
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + "[-" + iLastBoosterWordScore + " booster word] ";
                                        }
                                    }
                                }

                                iLastBoosterWordScore = this.term[iTerm].getBoosterWordScore();
                            }

                            if (this.options.bgNegatingWordsOccurBeforeSentiment) {
                                if (this.options.bgNegatingWordsFlipEmotion) {
                                    if (iWordsSinceNegative <= this.options.igMaxWordsBeforeSentimentToNegate) {
                                        fSentiment[iWordTotal] = -fSentiment[iWordTotal] * this.options.fgStrengthMultiplierForNegatedWords;
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + "[*-" + this.options.fgStrengthMultiplierForNegatedWords
                                                    + " approx. negated multiplier] ";
                                        }
                                    }
                                } else {
                                    if (this.options.bgNegatingNegativeNeutralisesEmotion
                                            && fSentiment[iWordTotal] < 0.0F
                                            && iWordsSinceNegative <= this.options.igMaxWordsBeforeSentimentToNegate) {
                                        fSentiment[iWordTotal] = 0.0F;
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale + "[=0 negation] ";
                                        }
                                    }

                                    if (this.options.bgNegatingPositiveFlipsEmotion
                                            && fSentiment[iWordTotal] > 0.0F
                                            && iWordsSinceNegative <= this.options.igMaxWordsBeforeSentimentToNegate) {
                                        fSentiment[iWordTotal] = -fSentiment[iWordTotal] * this.options.fgStrengthMultiplierForNegatedWords;
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + "[*-" + this.options.fgStrengthMultiplierForNegatedWords
                                                    + " approx. negated multiplier] ";
                                        }
                                    }
                                }
                            }

                            if (this.term[iTerm].isNegatingWord()) {
                                iWordsSinceNegative = -1;
                            }

                            if (iLastBoosterWordScore == 0) {
                                ++iWordsSinceNegative;
                            }

                            if (this.term[iTerm].isNegatingWord() && this.options.bgNegatingWordsOccurAfterSentiment) {
                                iTermsChecked = 0;

                                for (int iPriorWord = iWordTotal - 1; iPriorWord > 0; --iPriorWord) {
                                    if (this.options.bgNegatingWordsFlipEmotion) {
                                        fSentiment[iPriorWord] = -fSentiment[iPriorWord] * this.options.fgStrengthMultiplierForNegatedWords;
                                        if (this.options.bgExplainClassification) {
                                            this.sgClassificationRationale = this.sgClassificationRationale
                                                    + "[*-" + this.options.fgStrengthMultiplierForNegatedWords
                                                    + " approx. negated multiplier] ";
                                        }
                                    } else {
                                        if (this.options.bgNegatingNegativeNeutralisesEmotion && fSentiment[iPriorWord] < 0.0F) {
                                            fSentiment[iPriorWord] = 0.0F;
                                            if (this.options.bgExplainClassification) {
                                                this.sgClassificationRationale = this.sgClassificationRationale + "[=0 negation] ";
                                            }
                                        }

                                        if (this.options.bgNegatingPositiveFlipsEmotion && fSentiment[iPriorWord] > 0.0F) {
                                            fSentiment[iPriorWord] = -fSentiment[iPriorWord]
                                                    * this.options.fgStrengthMultiplierForNegatedWords;
                                            if (this.options.bgExplainClassification) {
                                                this.sgClassificationRationale = this.sgClassificationRationale
                                                        + "[*-" + this.options.fgStrengthMultiplierForNegatedWords
                                                        + " approx. negated multiplier] ";
                                            }
                                        }
                                    }

                                    ++iTermsChecked;
                                    if (iTermsChecked > this.options.igMaxWordsAfterSentimentToNegate) {
                                        break;
                                    }
                                }
                            }

                            if (this.options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion
                                    && fSentiment[iWordTotal] < -1.0F
                                    && iWordTotal > 1
                                    && fSentiment[iWordTotal - 1] < -1.0F) {
                                var10002 = (int) fSentiment[iWordTotal]--;
                                if (this.options.bgExplainClassification) {
                                    this.sgClassificationRationale = this.sgClassificationRationale + "[-1 consecutive negative words] ";
                                }
                            }

                            if (this.options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion
                                    && fSentiment[iWordTotal] > 1.0F
                                    && iWordTotal > 1
                                    && fSentiment[iWordTotal - 1] > 1.0F) {
                                var10002 = (int) fSentiment[iWordTotal]++;
                                if (this.options.bgExplainClassification) {
                                    this.sgClassificationRationale = this.sgClassificationRationale + "[+1 consecutive positive words] ";
                                }
                            }

                        }
                    }
                }

                float fTotalNeg = 0.0F;
                float fTotalPos = 0.0F;
                float fMaxNeg = 0.0F;
                float fMaxPos = 0.0F;
                int iPosWords = 0;
                int iNegWords = 0;

                int iTerm;
                for (iTerm = 1; iTerm <= iWordTotal; ++iTerm) {
                    if (fSentiment[iTerm] < 0.0F) {
                        fTotalNeg += fSentiment[iTerm];
                        ++iNegWords;
                        if (fMaxNeg > fSentiment[iTerm]) {
                            fMaxNeg = fSentiment[iTerm];
                        }
                    } else if (fSentiment[iTerm] > 0.0F) {
                        fTotalPos += fSentiment[iTerm];
                        ++iPosWords;
                        if (fMaxPos < fSentiment[iTerm]) {
                            fMaxPos = fSentiment[iTerm];
                        }
                    }
                }
                igSentiCount = iNegWords + iPosWords;
                --fMaxNeg;
                ++fMaxPos;
                int var10000 = this.options.igEmotionSentenceCombineMethod;
                this.options.getClass();
                if (var10000 == 1) {
                    if (iPosWords == 0) {
                        this.igPositiveSentiment = 1;
                    } else {
                        this.igPositiveSentiment = (int) Math.round(
                                ((double) (fTotalPos + (float) iPosWords) + 0.45D) / (double) iPosWords);
                    }

                    if (iNegWords == 0) {
                        this.igNegativeSentiment = -1;
                    } else {
                        this.igNegativeSentiment = (int) Math.round(
                                ((double) (fTotalNeg - (float) iNegWords) + 0.55D) / (double) iNegWords);
                    }
                } else {
                    //var10000 = this.options.igEmotionSentenceCombineMethod;
                    this.options.getClass();
                    if (var10000 == 2) {
                        this.igPositiveSentiment = Math.round(fTotalPos) + iPosWords;
                        this.igNegativeSentiment = Math.round(fTotalNeg) - iNegWords;
                    } else {
                        this.igPositiveSentiment = Math.round(fMaxPos);
                        this.igNegativeSentiment = Math.round(fMaxNeg);
                    }
                }

                if (this.options.bgReduceNegativeEmotionInQuestionSentences && this.igNegativeSentiment < -1) {
                    for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                        if (this.term[iTerm].isWord()) {
                            if (this.resources.questionWords.questionWord(this.term[iTerm].getTranslatedWord().toLowerCase())) {
                                ++this.igNegativeSentiment;
                                if (this.options.bgExplainClassification) {
                                    this.sgClassificationRationale = this.sgClassificationRationale + "[+1 negative for question word]";
                                }
                                break;
                            }
                        } else if (this.term[iTerm].isPunctuation() && this.term[iTerm].punctuationContains("?")) {
                            ++this.igNegativeSentiment;
                            if (this.options.bgExplainClassification) {
                                this.sgClassificationRationale = this.sgClassificationRationale + "[+1 negative for question mark ?]";
                            }
                            break;
                        }
                    }
                }

                if (this.igPositiveSentiment == 1 && this.options.bgMissCountsAsPlus2) {
                    for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                        if (this.term[iTerm].isWord() && this.term[iTerm].getTranslatedWord().toLowerCase().compareTo("miss") == 0) {
                            this.igPositiveSentiment = 2;
                            if (this.options.bgExplainClassification) {
                                this.sgClassificationRationale = this.sgClassificationRationale + "[pos = 2 for term 'miss']";
                            }
                            break;
                        }
                    }
                }

                if (bSentencePunctuationBoost) {
                    if (this.igPositiveSentiment < -this.igNegativeSentiment) {
                        --this.igNegativeSentiment;
                        if (this.options.bgExplainClassification) {
                            this.sgClassificationRationale = this.sgClassificationRationale + "[-1 punctuation emphasis] ";
                        }
                    } else if (this.igPositiveSentiment > -this.igNegativeSentiment) {
                        ++this.igPositiveSentiment;
                        if (this.options.bgExplainClassification) {
                            this.sgClassificationRationale = this.sgClassificationRationale + "[+1 punctuation emphasis] ";
                        }
                    } else if (this.options.igMoodToInterpretNeutralEmphasis > 0) {
                        ++this.igPositiveSentiment;
                        if (this.options.bgExplainClassification) {
                            this.sgClassificationRationale = this.sgClassificationRationale + "[+1 punctuation mood emphasis] ";
                        }
                    } else if (this.options.igMoodToInterpretNeutralEmphasis < 0) {
                        --this.igNegativeSentiment;
                        if (this.options.bgExplainClassification) {
                            this.sgClassificationRationale = this.sgClassificationRationale + "[-1 punctuation mood emphasis] ";
                        }
                    }
                }

                if (this.igPositiveSentiment == 1
                        && this.igNegativeSentiment == -1
                        && this.options.bgExclamationInNeutralSentenceCountsAsPlus2) {
                    for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                        if (this.term[iTerm].isPunctuation() && this.term[iTerm].punctuationContains("!")) {
                            this.igPositiveSentiment = 2;
                            if (this.options.bgExplainClassification) {
                                this.sgClassificationRationale = this.sgClassificationRationale + "[pos = 2 for !]";
                            }
                            break;
                        }
                    }
                }

                if (this.igPositiveSentiment == 1
                        && this.igNegativeSentiment == -1
                        && this.options.bgYouOrYourIsPlus2UnlessSentenceNegative) {
                    for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                        if (this.term[iTerm].isWord()) {
                            String sTranslatedWord = this.term[iTerm].getTranslatedWord().toLowerCase();
                            if (sTranslatedWord.compareTo("you") == 0
                                    || sTranslatedWord.compareTo("your") == 0
                                    || sTranslatedWord.compareTo("whats") == 0) {
                                this.igPositiveSentiment = 2;
                                if (this.options.bgExplainClassification) {
                                    this.sgClassificationRationale = this.sgClassificationRationale + "[pos = 2 for you/your/whats]";
                                }
                                break;
                            }
                        }
                    }
                }

                this.adjustSentimentForIrony();
                var10000 = this.options.igEmotionSentenceCombineMethod;
                this.options.getClass();
                if (var10000 != 2) {
                    if (this.igPositiveSentiment > 5) {
                        this.igPositiveSentiment = 5;
                    }

                    if (this.igNegativeSentiment < -5) {
                        this.igNegativeSentiment = -5;
                    }
                }

                if (this.options.bgExplainClassification) {
                    this.sgClassificationRationale = this.sgClassificationRationale
                            + "[sentence: "
                            + this.igPositiveSentiment + ","
                            + this.igNegativeSentiment + "]";
                }

            }
        }
    }

    /**
     * UC-11 Classify a single text
     * 根据反讽调整sentence的情感得分
     * @author haofeng.Yu
     */
    private void adjustSentimentForIrony() {
        int iTerm;
        if (this.igPositiveSentiment >= this.options.igMinSentencePosForQuotesIrony) {
            for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                if (this.term[iTerm].isPunctuation() && this.term[iTerm].getText().indexOf(34) >= 0) {
                    if (this.igNegativeSentiment > -this.igPositiveSentiment) {
                        this.igNegativeSentiment = 1 - this.igPositiveSentiment;
                    }

                    this.igPositiveSentiment = 1;
                    this.sgClassificationRationale = this.sgClassificationRationale
                            + "[Irony change: pos = 1, neg = "
                            + this.igNegativeSentiment + "]";
                    return;
                }
            }
        }

        if (this.igPositiveSentiment >= this.options.igMinSentencePosForPunctuationIrony) {
            for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                if (this.term[iTerm].isPunctuation()
                        && this.term[iTerm].punctuationContains("!")
                        && this.term[iTerm].getPunctuationEmphasisLength() > 0) {
                    if (this.igNegativeSentiment > -this.igPositiveSentiment) {
                        this.igNegativeSentiment = 1 - this.igPositiveSentiment;
                    }

                    this.igPositiveSentiment = 1;
                    this.sgClassificationRationale = this.sgClassificationRationale
                            + "[Irony change: pos = 1, neg = "
                            + this.igNegativeSentiment + "]";
                    return;
                }
            }
        }

        if (this.igPositiveSentiment >= this.options.igMinSentencePosForTermsIrony) {
            for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                if (this.resources.ironyList.termIsIronic(this.term[iTerm].getText())) {
                    if (this.igNegativeSentiment > -this.igPositiveSentiment) {
                        this.igNegativeSentiment = 1 - this.igPositiveSentiment;
                    }

                    this.igPositiveSentiment = 1;
                    this.sgClassificationRationale = this.sgClassificationRationale
                            + "[Irony change: pos = 1, neg = "
                            + this.igNegativeSentiment + "]";
                    return;
                }
            }
        }

    }

    /**
     * UC-1Assigning Sentiment Scores for Words
     * 用相应对象评估的强度覆盖sentence中每个term的强度
     * @param recalculateIfAlreadyDone 是否重新计算情绪得分
     * @author haofeng.Yu
     */
    public void overrideTermStrengthsWithObjectEvaluationStrengths(boolean recalculateIfAlreadyDone) {
        boolean bMatchingObject = false;
        boolean bMatchingEvaluation = false;
        if (!this.bgObjectEvaluationsApplied || recalculateIfAlreadyDone) {
            for (int iObject = 1; iObject < this.resources.evaluativeTerms.igObjectEvaluationCount; ++iObject) {
                bMatchingObject = false;
                bMatchingEvaluation = false;

                int iTerm;
                for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                    if (this.term[iTerm].isWord()
                            && this.term[iTerm].matchesStringWithWildcard(this.resources.evaluativeTerms.sgObject[iObject], true)) {
                        bMatchingObject = true;
                        break;
                    }
                }

                if (bMatchingObject) {
                    for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                        if (this.term[iTerm].isWord()
                                && this.term[iTerm]
                                    .matchesStringWithWildcard(this.resources.evaluativeTerms.sgObjectEvaluation[iObject], true)) {
                            bMatchingEvaluation = true;
                            break;
                        }
                    }
                }

                if (bMatchingEvaluation) {
                    if (this.options.bgExplainClassification) {
                        this.sgClassificationRationale = this.sgClassificationRationale + "[term weight changed by object/evaluation]";
                    }

                    this.term[iTerm].setSentimentOverrideValue(this.resources.evaluativeTerms.igObjectEvaluationStrength[iObject]);
                }
            }

            this.bgObjectEvaluationsApplied = true;
        }

    }

    /**
     * UC-1Assigning Sentiment Scores for Words
     * 用idiom的强度覆盖sentence中每个term的强度
     * @param recalculateIfAlreadyDone 是否重新计算情绪得分
     * @author haofeng.Yu
     */
    public void overrideTermStrengthsWithIdiomStrengths(boolean recalculateIfAlreadyDone) {
        if (!this.bgIdiomsApplied || recalculateIfAlreadyDone) {
            for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
                if (this.term[iTerm].isWord()) {
                    for (int iIdiom = 1; iIdiom <= this.resources.idiomList.igIdiomCount; ++iIdiom) {
                        if (iTerm + this.resources.idiomList.igIdiomWordCount[iIdiom] - 1 <= this.igTermCount) {
                            boolean bMatchingIdiom = true;

                            int iIdiomTerm;
                            for (iIdiomTerm = 0; iIdiomTerm < this.resources.idiomList.igIdiomWordCount[iIdiom]; ++iIdiomTerm) {
                                if (!this.term[iTerm + iIdiomTerm]
                                        .matchesStringWithWildcard(this.resources.idiomList.sgIdiomWords[iIdiom][iIdiomTerm], true)) {
                                    bMatchingIdiom = false;
                                    break;
                                }
                            }

                            if (bMatchingIdiom) {
                                if (this.options.bgExplainClassification) {
                                    this.sgClassificationRationale = this.sgClassificationRationale
                                            + "[term weight(s) changed by idiom "
                                            + this.resources.idiomList.getIdiom(iIdiom) + "]";
                                }

                                this.term[iTerm].setSentimentOverrideValue(this.resources.idiomList.igIdiomStrength[iIdiom]);

                                for (iIdiomTerm = 1; iIdiomTerm < this.resources.idiomList.igIdiomWordCount[iIdiom]; ++iIdiomTerm) {
                                    this.term[iTerm + iIdiomTerm].setSentimentOverrideValue(0);
                                }
                            }
                        }
                    }
                }
            }

            this.bgIdiomsApplied = true;
        }

    }
}

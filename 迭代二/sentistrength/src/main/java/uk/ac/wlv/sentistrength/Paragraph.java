package uk.ac.wlv.sentistrength;

import java.util.Random;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.sentistrength.classificationresource.ClassificationResources;
import uk.ac.wlv.sentistrength.classificationresource.UnusedTermsClassificationIndex;
import uk.ac.wlv.utilities.Sort;
import uk.ac.wlv.utilities.StringIndex;

/**
 * 段落
 */
public class Paragraph {
	private Sentence[] sentence;
	private int igSentenceCount = 0;
	private int[] igSentimentIDList;
	private int igSentimentIDListCount = 0;
	private boolean bSentimentIDListMade = false;
	private int igPositiveSentiment = 0;
	private int igNegativeSentiment = 0;
	private int igTrinarySentiment = 0;
	private int igScaleSentiment = 0;
	private ClassificationResources resources;
	private ClassificationOptions options;
	private Random generator = new Random();
	private String sgClassificationRationale = "";

	/**
	 * 将段落添加到具有正/负值的主索引
	 * <p>
	 *     先循环遍历段落中的每一个句子，将段落中的每一个句子添加到索引中。接着将每个句子的索引都添加到主索引中，同时包括正/负情绪的相关信息
	 * </p>
	 * @param unusedTermsClassificationIndex 对象，表示段落将被添加到的索引
	 * @param iCorrectPosClass 正确分类的正例数
	 * @param iEstPosClass 正例的估计数
	 * @param iCorrectNegClass 正确分类的负例数
	 * @param iEstNegClass 负实例的估计数量
	 * @author zhangsong
	 */
	public void addParagraphToIndexWithPosNegValues(UnusedTermsClassificationIndex unusedTermsClassificationIndex,
													int iCorrectPosClass,
													int iEstPosClass,
													int iCorrectNegClass,
													int iEstNegClass) {
		for (int i = 1; i <= this.igSentenceCount; ++i) {
			this.sentence[i].addSentenceToIndex(unusedTermsClassificationIndex);
		}

		unusedTermsClassificationIndex.addNewIndexToMainIndexWithPosNegValues(
				iCorrectPosClass,
				iEstPosClass,
				iCorrectNegClass,
				iEstNegClass);
	}

	/**
	 * 将段落添加到具有单尺度值的主索引
	 * <p>
	 *     先遍历段落中的每一个句子，将句子添加到索引中。再将整个索引类添加到主索引中，同时添加单尺度的值
	 * </p>
	 * @param unusedTermsClassificationIndex 对象，表示段落将被添加到的索引
	 * @param iCorrectScaleClass 正确的单尺度值
	 * @param iEstScaleClass 预估的单尺度值
	 * @author zhangsong
	 */
	public void addParagraphToIndexWithScaleValues(UnusedTermsClassificationIndex unusedTermsClassificationIndex,
												   int iCorrectScaleClass,
												   int iEstScaleClass) {
		for (int i = 1; i <= this.igSentenceCount; ++i) {
			this.sentence[i].addSentenceToIndex(unusedTermsClassificationIndex);
		}

		unusedTermsClassificationIndex.addNewIndexToMainIndexWithScaleValues(iCorrectScaleClass, iEstScaleClass);
	}

	/**
	 * 将段落添加到具有双精度二进制的索引
	 * <p>
	 *     先遍历段落中的每一个句子，将句子添加到索引中。再将整个索引类添加到主索引中，同时添加二进制的值
	 * </p>
	 * @param unusedTermsClassificationIndex 对象，表示段落将被添加到的索引
	 * @param iCorrectBinaryClass 正确的二进制值
	 * @param iEstBinaryClass 估计的二进制值
	 * @author zhangsong
	 */
	public void addParagraphToIndexWithBinaryValues(UnusedTermsClassificationIndex unusedTermsClassificationIndex,
													int iCorrectBinaryClass,
													int iEstBinaryClass) {
		for (int i = 1; i <= this.igSentenceCount; ++i) {
			this.sentence[i].addSentenceToIndex(unusedTermsClassificationIndex);
		}

		unusedTermsClassificationIndex.addNewIndexToMainIndexWithBinaryValues(iCorrectBinaryClass, iEstBinaryClass);
	}

	/**
	 * 添加句子中字符串的索引
	 * <p>
	 *     遍历段落中的所有句子，将句子中的术语添加到索引中
	 * </p>
	 * @param stringIndex 字符串索引
	 * @param textParsingOptions description
	 * @param bRecordCount description
	 * @param bArffIndex description
	 * @return int 返回段落中所有句子中添加的术语总数
	 * @author zhangsong
	 */
	public int addToStringIndex(StringIndex stringIndex,
								TextParsingOptions textParsingOptions,
								boolean bRecordCount,
								boolean bArffIndex) {
		int iTermsChecked = 0;

		for (int i = 1; i <= this.igSentenceCount; ++i) {
			iTermsChecked += this.sentence[i].addToStringIndex(stringIndex, textParsingOptions, bRecordCount, bArffIndex);
		}

		return iTermsChecked;
	}

	/**
	 * 将段落添加到具有三位一体值的主索引中
	 * <p>
	 *     遍历段落中的每一个句子，将句子添加到索引中。再将新的索引添加到主索引中，同时添加三位一体值
	 * </p>
	 * @param unusedTermsClassificationIndex 对象，表示段落将被添加到的索引
	 * @param iCorrectTrinaryClass 正确的三位一体值
	 * @param iEstTrinaryClass 估计的三位一体值
	 * @author zhangsong
	 */
	public void addParagraphToIndexWithTrinaryValues(UnusedTermsClassificationIndex unusedTermsClassificationIndex,
													 int iCorrectTrinaryClass,
													 int iEstTrinaryClass) {
		for (int i = 1; i <= this.igSentenceCount; ++i) {
			this.sentence[i].addSentenceToIndex(unusedTermsClassificationIndex);
		}

		unusedTermsClassificationIndex.addNewIndexToMainIndexWithTrinaryValues(iCorrectTrinaryClass, iEstTrinaryClass);
	}

	/**
	 * 设置对象为当前段落
	 * <p>
	 *		首先根据参数为对象设置分类资源和分类选项。然后计算感叹号、问号和句号出现的次数。
	 *		通过查找来计算段落中句末的数量，同时识别句子边界，然后创建一个新的Sentence 对象，并将七设置为相应的句子文本。
	 *		最后将句子创建为sentence对象并添加到数组中。
	 * </p>
	 * @param sParagraph 段落字符串
	 * @param classResources 分类资源
	 * @param newClassificationOptions 新的分类选项
	 * @author zhangsong
	 */
	public void setParagraph(String sParagraph,
							 ClassificationResources classResources,
							 ClassificationOptions newClassificationOptions) {
		this.resources = classResources;
		this.options = newClassificationOptions;
		if (sParagraph.indexOf("\"") >= 0) { // 将所有出现的双引号替换为单引号
			sParagraph = sParagraph.replace("\"", "'");
		}
		int iSentenceEnds = 2;
		int iPos = 0;

		int[] temp = getPodSentence(iPos, iSentenceEnds, sParagraph);
		iPos = temp[0];
		iSentenceEnds = temp[1];

		this.sentence = new Sentence[iSentenceEnds];
		this.igSentenceCount = 0;
		int iLastSentenceEnd = -1;
		boolean bPunctuationIndicatesSentenceEnd = false;
		int iNextBr = sParagraph.indexOf("<br>");
		String sNextSentence = "";
		for (iPos = 0; iPos < sParagraph.length(); ++iPos) {
			String sNextChar = sParagraph.substring(iPos, iPos + 1);
			if (iPos == sParagraph.length() - 1) {
				sNextSentence = sParagraph.substring(iLastSentenceEnd + 1);
			} else if (iPos == iNextBr) {
				sNextSentence = sParagraph.substring(iLastSentenceEnd + 1, iPos);
				iLastSentenceEnd = iPos + 3;
				iNextBr = sParagraph.indexOf("<br>", iNextBr + 2);
			} else if (this.bIsSentenceEndPunctuation(sNextChar)) {
				bPunctuationIndicatesSentenceEnd = true;
			} else if (sNextChar.compareTo(" ") == 0) {
				if (bPunctuationIndicatesSentenceEnd) {
					sNextSentence = sParagraph.substring(iLastSentenceEnd + 1, iPos);
					iLastSentenceEnd = iPos;
				}
			} else if (this.bIsAlphanumeric(sNextChar) && bPunctuationIndicatesSentenceEnd) {
				sNextSentence = sParagraph.substring(iLastSentenceEnd + 1, iPos);
				iLastSentenceEnd = iPos - 1;
			}
			if (!sNextSentence.equals("")) {
				++this.igSentenceCount;
				this.sentence[this.igSentenceCount] = new Sentence();
				this.sentence[this.igSentenceCount].setSentence(sNextSentence, this.resources, this.options);
				sNextSentence = "";
				bPunctuationIndicatesSentenceEnd = false;
			}
		}
	}
	public int[] getPodSentence(int iPos, int iSentenceEnds, String sParagraph) {
		int[] result = new int[2];

		while (iPos >= 0 && iPos < sParagraph.length()) {
			iPos = sParagraph.indexOf("<br>", iPos);
			if (iPos >= 0) {
				iPos += 3;
				++iSentenceEnds;
			}
		}
		iPos = 0;
		while (iPos >= 0 && iPos < sParagraph.length()) {
			iPos = sParagraph.indexOf(".", iPos);
			if (iPos >= 0) {
				++iPos;
				++iSentenceEnds;
			}
		}
		iPos = 0;
		while (iPos >= 0 && iPos < sParagraph.length()) {
			iPos = sParagraph.indexOf("!", iPos);
			if (iPos >= 0) {
				++iPos;
				++iSentenceEnds;
			}
		}
		iPos = 0;
		while (iPos >= 0 && iPos < sParagraph.length()) {
			iPos = sParagraph.indexOf("?", iPos);
			if (iPos >= 0) {
				++iPos;
				++iSentenceEnds;
			}
		}
		result[0] = iPos;
		result[1] = iSentenceEnds;

		return result;
	}

	/**
	 * 获取情绪ID列表

	 * @return int[] 情绪ID列表
	 * @author zhangsong
	 */
	public int[] getSentimentIDList() {
		if (!this.bSentimentIDListMade) {
			this.makeSentimentIDList();
		}

		return this.igSentimentIDList;
	}

	/**
	 * UC-11、25
	 * 得到分类原理
	 * @return java.lang.String
	 * @author zhangsong
	 */
	public String getClassificationRationale() {
		return this.sgClassificationRationale;
	}

	/**
	 * 根据句子数组生成情绪 ID 列表
	 * <p>
	 *     首先初始化跟踪列表中情感ID数量igSentimentIDListCount，以及用于检查重复情感ID的变量bIsDuplicate。
	 * </p>
	 * <p>
	 *     然后遍历数组中的每个句子，如果句子有情感ID列表，则统计情感ID的数量。
	 * </p>
	 * <p>
	 *     如果情感ID数量大于0（即存在情感ID），则再次遍历每一个句子并检查是否该句子有情感ID列表，
	 *     如果该句子有一个情感ID列表，则遍历列表中的每个情感ID，检查该ID是否已在igSentimentIDList列表中，如果不在，则将其添加到igSentimentIDList中。
	 * </p>
	 * <p>
	 *     最后使用快速排序对igSentimentIDList数组进行排序，并且将bSentimentIDListMade设置为true，表示已生成情感ID列表。
	 * </p>

	 * @author zhangsong
	 */
	public void makeSentimentIDList() {
		boolean bIsDuplicate = false;
		this.igSentimentIDListCount = 0;

		int i;
		for (i = 1; i <= this.igSentenceCount; ++i) {
			if (this.sentence[i].getSentimentIDList() != null) {
				this.igSentimentIDListCount += this.sentence[i].getSentimentIDList().length;
			}
		}

		if (this.igSentimentIDListCount > 0) {
			this.igSentimentIDList = new int[this.igSentimentIDListCount + 1];
			this.igSentimentIDListCount = 0;

			for (i = 1; i <= this.igSentenceCount; ++i) {
				int[] sentenceIDList = this.sentence[i].getSentimentIDList();
				if (sentenceIDList != null) { //如果该句子有一个情感ID列表
					for (int j = 1; j < sentenceIDList.length; ++j) {
						if (sentenceIDList[j] != 0) {
							bIsDuplicate = false;

							for (int k = 1; k <= this.igSentimentIDListCount; ++k) {
								if (sentenceIDList[j] == this.igSentimentIDList[k]) {
									bIsDuplicate = true;
									break;
								}
							}

							if (!bIsDuplicate) {
								this.igSentimentIDList[++this.igSentimentIDListCount] = sentenceIDList[j];
							}
						}
					}
				}
			}

			Sort.quickSortInt(this.igSentimentIDList, 1, this.igSentimentIDListCount);
		}

		this.bSentimentIDListMade = true;
	}

	/**
	 * 得到段落中所有标记句子的字符串
	 * <p>
	 *     初始化一个sTagged的空字符串，遍历段落中每个句子，调用getTaggedSentence得到每一个标记的句子
	 * </p>

	 * @return 返回一个包含段落中所有标记句子的字符串
	 * @author zhangsong
	 */
	public String getTaggedParagraph() {
		String sTagged = "";

		for (int i = 1; i <= this.igSentenceCount; ++i) {
			sTagged = sTagged + this.sentence[i].getTaggedSentence();
		}

		return sTagged;
	}

	/**
	 * 返回段落中所有已翻译的句子
	 * <p>
	 *     初始化一个sTranslated字符串，遍历段落中每个句子，调用getTranslatedSentence得到已翻译的句子
	 * </p>

	 * @return java.lang.String
	 * @author zhangsong
	 */
	public String getTranslatedParagraph() {
		String sTranslated = "";

		for (int i = 1; i <= this.igSentenceCount; ++i) {
			sTranslated = sTranslated + this.sentence[i].getTranslatedSentence();
		}

		return sTranslated;
	}

	/**
	 * UC-1 重新计算段落中情感分数
	 * <p>
	 *     遍历整个段落，调用recalculateSentenceSentimentScore重新计算句子的情感分数，最后计算段落的情感分数
	 * </p>

	 * @author zhangsong
	 */
	public void recalculateParagraphSentimentScores() {
		for (int iSentence = 1; iSentence <= this.igSentenceCount; ++iSentence) {
			this.sentence[iSentence].recalculateSentenceSentimentScore();
		}

		this.calculateParagraphSentimentScores();
	}

	/**
	 * 根据给定的情感词 ID 对情感变化的分类段落进行重新分类
	 * <p>
	 *     如果没有负面词，则调用calculateParagraphSentimentScores
	 * </p>
	 * <p>
	 *     否则，再先检查是否生成情感ID列表。如果有列表，则使用Sort搜索给定的情感词ID。
	 *     如果存在，则继续对段落中的分类句子进行重新分类，并重新计算情感分数
	 * </p>
	 * @param iSentimentWordID 给定的情感词ID
	 * @author zhangsong
	 */
	public void reClassifyClassifiedParagraphForSentimentChange(int iSentimentWordID) {
		if (this.igNegativeSentiment == 0) {
			this.calculateParagraphSentimentScores();
		} else {
			if (!this.bSentimentIDListMade) {
				this.makeSentimentIDList();
			}

			if (this.igSentimentIDListCount != 0) {
				if (Sort.i_FindIntPositionInSortedArray(
						iSentimentWordID,
						this.igSentimentIDList,
						1,
						this.igSentimentIDListCount) >= 0) {
					for (int iSentence = 1; iSentence <= this.igSentenceCount; ++iSentence) {
						this.sentence[iSentence].reClassifyClassifiedSentenceForSentimentChange(iSentimentWordID);
					}

					this.calculateParagraphSentimentScores();
				}

			}
		}
	}

	/**
	 * UC-1、21 得到段落的积极情绪分数
	 * <p>
	 *     如果积极情绪==0，则调用calculateParagraphSentimentScores计算段落分数。
	 *     然后使用igPositiveSentiment返回段落的积极情绪分数
	 * </p>

	 * @return int 积极情绪分数
	 * @author zhangsong
	 */
	public int getParagraphPositiveSentiment() {
		if (this.igPositiveSentiment == 0) {
			this.calculateParagraphSentimentScores();
		}

		return this.igPositiveSentiment;
	}

	/**
	 * UC-1、21 得到段落的消极情绪分数
	 * <p>
	 *     如果消极情绪==0，则调用calculateParagraphSentimentScores计算段落分数。
	 *     然后使用igNegativeSentiment返回段落的消极情绪分数
	 * </p>

	 * @return int 消极情绪分数
	 * @author zhangsong
	 */
	public int getParagraphNegativeSentiment() {
		if (this.igNegativeSentiment == 0) {
			this.calculateParagraphSentimentScores();
		}

		return this.igNegativeSentiment;
	}

	/**
	 * UC-1、22 得到段落的三位一体情绪分数
	 * <p>
	 *     如果消极情绪==0，则调用calculateParagraphSentimentScores计算段落分数。
	 *     然后使用igTrinarySentiment返回段落的消极情绪分数
	 * </p>

	 * @return int 消极情绪分数
	 * @author zhangsong
	 */
	public int getParagraphTrinarySentiment() {
		if (this.igNegativeSentiment == 0) {
			this.calculateParagraphSentimentScores();
		}

		return this.igTrinarySentiment;
	}

	/**
	 * UC-1、24 得到段落的单范围情绪分数
	 * <p>
	 *     如果消极情绪==0，则调用calculateParagraphSentimentScores计算段落分数。
	 *     然后使用igScaleSentiment返回段落的消极情绪分数
	 * </p>

	 * @return int 消极情绪分数
	 * @author zhangsong
	 */
	public int getParagraphScaleSentiment() {
		if (this.igNegativeSentiment == 0) {
			this.calculateParagraphSentimentScores();
		}

		return this.igScaleSentiment;
	}

	/**
	 * UC-8 判断句尾标点符号
	 * @param sChar 句子字符串
	 * @return boolean 句尾是否有./!/?，有为false
	 * @author zhangsong
	 */
	private boolean bIsSentenceEndPunctuation(String sChar) {
		return sChar.compareTo(".") == 0 || sChar.compareTo("!") == 0 || sChar.compareTo("?") == 0;
	}

	/**
	 * 判断是否全为字母和数字
	 * @param sChar 句子字符串
	 * @return boolean 全为字母数字为true
	 * @author zhangsong
	 */
	private boolean bIsAlphanumeric(String sChar) {
		return sChar.compareToIgnoreCase("a") >= 0 && sChar.compareToIgnoreCase("z") <= 0
				|| sChar.compareTo("0") >= 0 && sChar.compareTo("9") <= 0
				|| sChar.compareTo("$") == 0
				|| sChar.compareTo("£") == 0
				|| sChar.compareTo("'") == 0;
	}

	/**
	 * UC-1 计算段落文本情感
	 * <p>
	 *     情感分数是根据段落中每个句子的正面和负面情感计算的。
	 *     循环遍历段落中的每个句子，检索每个句子的正面和负面情绪分数。
	 *     该段落的正面和负面情绪总分是通过将每个句子的正面和负面情绪得分相加来计算的。
	 * </p>
	 * <p>
	 *     igEmotionParagraphCombineMethod变量用于确定应如何组合段落中每个句子的情感分数以获得整个段落的情感分数。
	 *     一、如果 igEmotionParagraphCombineMethod 等于 1，则计算每个句子的平均正面和负面情感分数，并将其用作该段落的正面和负面情感分数。
	 *     二、如果 igEmotionParagraphCombineMethod 等于 2，则将每个句子的正面和负面情感总分作为该段落的正面和负面情感分数。
	 *     三、如果 igEmotionParagraphCombineMethod 是其他任何东西，每个句子的最大正面和负面情绪分数将用作该段落的正面和负面情绪分数。
	 *     四、如果 igEmotionParagraphCombineMethod 不等于 2，则该段落的正面和负面情绪分数分别设置为 1 和 -1。
	 *     	  igScaleSentiment 变量设置为该段落的正面和负面情绪分数的总和。
	 *     五、如果 bgBinaryVersionOfTrinaryMode 为真且该段落的正面和负面情绪得分为 0，则 igTrinarySentiment 设置为 igDefaultBinaryClassification。
	 *     六、否则，如果该段落的积极情绪得分大于或等于消极情绪得分，则 igTrinarySentiment 设置为 0，否则设置为 -1。
	 * </p>
	 * <p>
	 *     sgClassificationRationale 变量用于存储一个字符串，该字符串解释如果 bgExplainClassification 为真，该段落的情感分数是如何计算的。
	 *     段落中每个句子的分类原理附加到段落中每个句子循环内的 sgClassificationRationale。
	 *     在该方法的最后，根据段落的情感分数的计算方式，将最终分类原理附加到 sgClassificationRationale。
	 * </p>

	 * @author zhangsong
	 */
	@SuppressWarnings("checkstyle:NestedIfDepth")
	private void calculateParagraphSentimentScores() {
		this.igPositiveSentiment = 1;
		this.igNegativeSentiment = -1;
		this.igTrinarySentiment = 0;
		if (this.options.bgExplainClassification && this.sgClassificationRationale.length() > 0) {
			this.sgClassificationRationale = "";
		}

		int iPosTotal = 0;
		int iPosMax = 0;
		int iNegTotal = 0;
		int iNegMax = 0;
		int iPosTemp = 0;
		int iNegTemp = 0;
		int iSentencesUsed = 0;
		int wordNum = 0;
		int sentiNum = 0;
		if (this.igSentenceCount != 0) {
			int iNegTot;
			for (iNegTot = 1; iNegTot <= this.igSentenceCount; ++iNegTot) {
				iNegTemp = this.sentence[iNegTot].getSentenceNegativeSentiment();
				iPosTemp = this.sentence[iNegTot].getSentencePositiveSentiment();
				wordNum += this.sentence[iNegTot].getIgTermCount();
				sentiNum += this.sentence[iNegTot].getIgSentiCount();
				if (iNegTemp != 0 || iPosTemp != 0) {
					iNegTotal += iNegTemp;
					++iSentencesUsed;
					if (iNegMax > iNegTemp) {
						iNegMax = iNegTemp;
					}

					iPosTotal += iPosTemp;
					if (iPosMax < iPosTemp) {
						iPosMax = iPosTemp;
					}
				}

				if (this.options.bgExplainClassification) {
					this.sgClassificationRationale = this.sgClassificationRationale
							+ this.sentence[iNegTot].getClassificationRationale()
							+ " ";
				}
			}

			int var10000;
			if (iNegTotal == 0) {
				var10000 = this.options.igEmotionParagraphCombineMethod;
				this.options.getClass();
				if (var10000 != 2) {
					this.igPositiveSentiment = 0;
					this.igNegativeSentiment = 0;
					this.igTrinarySentiment = this.binarySelectionTieBreaker();
					return;
				}
			}

			var10000 = this.options.igEmotionParagraphCombineMethod;
			this.options.getClass();
			if (var10000 == 1) {
				this.igPositiveSentiment = (int) ((double) ((float) iPosTotal / (float) iSentencesUsed) + 0.5D);
				this.igNegativeSentiment = (int) ((double) ((float) iNegTotal / (float) iSentencesUsed) - 0.5D);
				if (this.options.bgExplainClassification) {
					this.sgClassificationRationale = this.sgClassificationRationale
							+ "[result = average ("
							+ iPosTotal
							+ " and "
							+ iNegTotal + ") of "
							+ iSentencesUsed
							+ " sentences]";
				}
			} else {
				var10000 = this.options.igEmotionParagraphCombineMethod;
				this.options.getClass();
				if (var10000 == 2) {
					this.igPositiveSentiment = iPosTotal;
					this.igNegativeSentiment = iNegTotal;
					if (this.options.bgExplainClassification) {
						this.sgClassificationRationale = this.sgClassificationRationale
								+ "[result: total positive; total negative]";
					}
				} else {
					this.igPositiveSentiment = iPosMax;
					this.igNegativeSentiment = iNegMax;
					if (this.options.bgExplainClassification) {
						this.sgClassificationRationale = this.sgClassificationRationale
								+ "[result: max + and - of any sentence]";
					}
				}
			}

			var10000 = this.options.igEmotionParagraphCombineMethod;
			this.options.getClass();
			if (var10000 != 2) {
				if (this.igPositiveSentiment == 0) {
					this.igPositiveSentiment = 1;
				}

				if (this.igNegativeSentiment == 0) {
					this.igNegativeSentiment = -1;
				}
			}

			if (this.options.bgScaleMode) {
				this.igScaleSentiment = this.igPositiveSentiment + this.igNegativeSentiment;
				if (this.options.bgExplainClassification) {
					this.sgClassificationRationale = this.sgClassificationRationale
							+ "[scale result = sum of pos and neg scores]";
				}

			} else {
				var10000 = this.options.igEmotionParagraphCombineMethod;
				this.options.getClass();
				if (var10000 == 2) {
					if (this.igPositiveSentiment == 0 && this.igNegativeSentiment == 0) {
						if (this.options.bgBinaryVersionOfTrinaryMode) {
							this.igTrinarySentiment = this.options.igDefaultBinaryClassification;
							if (this.options.bgExplainClassification) {
								this.sgClassificationRationale = this.sgClassificationRationale
										+ "[binary result set to default value]";
							}
						} else {
							this.igTrinarySentiment = 0;
							if (this.options.bgExplainClassification) {
								this.sgClassificationRationale = this.sgClassificationRationale
										+ "[trinary result 0 as pos=1, neg=-1]";
							}
						}
					} else {
						if ((float) this.igPositiveSentiment > this.options.fgNegativeSentimentMultiplier * (float) (-this.igNegativeSentiment)) {
							this.igTrinarySentiment = 1;
							if (this.options.bgExplainClassification) {
								this.sgClassificationRationale = this.sgClassificationRationale
										+ "[overall result 1 as pos > -neg * "
										+ this.options.fgNegativeSentimentMultiplier
										+ "]";
							}

							return;
						}

						if ((float) this.igPositiveSentiment < this.options.fgNegativeSentimentMultiplier * (float) (-this.igNegativeSentiment)) {
							this.igTrinarySentiment = -1;
							if (this.options.bgExplainClassification) {
								this.sgClassificationRationale = this.sgClassificationRationale
										+ "[overall result -1 as pos < -neg * "
										+ this.options.fgNegativeSentimentMultiplier
										+ "]";
							}

							return;
						}

						if (this.options.bgBinaryVersionOfTrinaryMode) {
							this.igTrinarySentiment = this.options.igDefaultBinaryClassification;
							if (this.options.bgExplainClassification) {
								this.sgClassificationRationale = this.sgClassificationRationale
										+ "[binary result = default value as pos = -neg * "
										+ this.options.fgNegativeSentimentMultiplier
										+ "]";
							}
						} else {
							this.igTrinarySentiment = 0;
							if (this.options.bgExplainClassification) {
								this.sgClassificationRationale = this.sgClassificationRationale
										+ "[trinary result = 0 as pos = -neg * "
										+ this.options.fgNegativeSentimentMultiplier
										+ "]";
							}
						}
					}
				} else {
					if (this.igPositiveSentiment == 1 && this.igNegativeSentiment == -1) {
						if (this.options.bgBinaryVersionOfTrinaryMode) {
							this.igTrinarySentiment = this.binarySelectionTieBreaker();
							if (this.options.bgExplainClassification) {
								this.sgClassificationRationale = this.sgClassificationRationale
										+ "[binary result = default value as pos=1 neg=-1]";
							}
						} else {
							this.igTrinarySentiment = 0;
							if (this.options.bgExplainClassification) {
								this.sgClassificationRationale = this.sgClassificationRationale
										+ "[trinary result = 0 as pos=1 neg=-1]";
							}
						}

						return;
					}

					if (this.igPositiveSentiment > -this.igNegativeSentiment) {
						this.igTrinarySentiment = 1;
						if (this.options.bgExplainClassification) {
							this.sgClassificationRationale = this.sgClassificationRationale
									+ "[overall result = 1 as pos>-neg]";
						}

						return;
					}

					if (this.igPositiveSentiment < -this.igNegativeSentiment) {
						this.igTrinarySentiment = -1;
						if (this.options.bgExplainClassification) {
							this.sgClassificationRationale = this.sgClassificationRationale
									+ "[overall result = -1 as pos<-neg]";
						}

						return;
					}

					iNegTot = 0;
					int iPosTot = 0;

					for (int iSentence = 1; iSentence <= this.igSentenceCount; ++iSentence) {
						iNegTot += this.sentence[iSentence].getSentenceNegativeSentiment();
						iPosTot = this.sentence[iSentence].getSentencePositiveSentiment();
					}

					if (this.options.bgBinaryVersionOfTrinaryMode && iPosTot == -iNegTot) {
						this.igTrinarySentiment = this.binarySelectionTieBreaker();
						if (this.options.bgExplainClassification) {
							this.sgClassificationRationale = this.sgClassificationRationale
									+ "[binary result = default as posSentenceTotal>-negSentenceTotal]";
						}
					} else {
						if (this.options.bgExplainClassification) {
							this.sgClassificationRationale = this.sgClassificationRationale
									+ "[overall result = largest of posSentenceTotal, negSentenceTotal]";
						}

						if (iPosTot > -iNegTot) {
							this.igTrinarySentiment = 1;
						} else {
							this.igTrinarySentiment = -1;
						}
					}
				}

			}
		}
	}

	/**
	 * binarySelectionTieBreaker

	 * @return int
	 * @author zhangsong
	 */
	private int binarySelectionTieBreaker() {
		if (this.options.igDefaultBinaryClassification != 1 && this.options.igDefaultBinaryClassification != -1) {
			return this.generator.nextDouble() > 0.5D ? 1 : -1;
		} else {
			return this.options.igDefaultBinaryClassification != 1
					&& this.options.igDefaultBinaryClassification != -1
					? this.options.igDefaultBinaryClassification : this.options.igDefaultBinaryClassification;
		}
	}
}

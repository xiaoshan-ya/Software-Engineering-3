package uk.ac.wlv.sentistrength;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.sentistrength.classificationresource.ClassificationResources;

/**
 * 该类（术语）表示文本中的单个单词、标点符号或表情符号，提供了从文本中提取下一个术语的方法，以及各种判断形式是否规范，与文本是否匹配等方法
 * UC1,UC3,UC4,UC11
 *
 */
public class Term {
   private final int igContentTypeWord = 1;
   private final int igContentTypePunctuation = 2;
   private final int igContentTypeEmoticon = 3;
   private int igContentType = 0;
   private String sgOriginalWord = "";
   private String sgLCaseWord = "";
   private String sgTranslatedWord = "";
   private String sgWordEmphasis = "";
   private int igWordSentimentID = 0;
   private boolean bgNegatingWord = false;
   private boolean bgNegatingWordCalculated = false;
   private boolean bgWordSentimentIDCalculated = false;
   private boolean bgProperNoun = false;
   private boolean bgProperNounCalculated = false;
   private String sgPunctuation = "";
   private String sgPunctuationEmphasis = "";
   private String sgEmoticon = "";
   int igEmoticonStrength = 0;
   private int igBoosterWordScore = 999;
   private ClassificationResources resources;
   private ClassificationOptions options;
   private boolean bgAllCapitals = false;
   private boolean bgAllCaptialsCalculated = false;
   private boolean bgOverrideSentimentScore = false;
   private int igOverrideSentimentScore = 0;

   /**
    * 从给定的文本中提取下一个术语，并设置改术语的属性
    *
    * @param sWordAndPunctuation 给定的文本
    * @param classResources 分类资源
    * @param classOptions 分类选项
    * @return 文本中提取的术语结尾的位置，如果没有则返回-1
    */
   public int extractNextWordOrPunctuationOrEmoticon(
           String sWordAndPunctuation, ClassificationResources classResources, ClassificationOptions classOptions) {
      int posTemp = -1;  boolean posFlag = false;
      int iWordCharOrAppostrophe = 1;
      int iPunctuation = 1;
      int iPos = 0; int iLastCharType = 0;
      String sChar = "";
      this.resources = classResources;
      this.options = classOptions;
      int iTextLength = sWordAndPunctuation.length();
      if (this.codeEmoticon(sWordAndPunctuation)) {
         return -1;
      } else {
         for (; iPos < iTextLength; ++iPos) {
            sChar = sWordAndPunctuation.substring(iPos, iPos + 1);
            if (!Character.isLetterOrDigit(sWordAndPunctuation.charAt(iPos))
                    && (this.options.bgAlwaysSplitWordsAtApostrophes
                    || !sChar.equals("'") || iPos <= 0 || iPos >= iTextLength - 1
                    || !Character.isLetter(sWordAndPunctuation.charAt(iPos + 1)))
                    && !sChar.equals("$") && !sChar.equals("£") && !sChar.equals("@")
                    && !sChar.equals("_")) {
               if (iLastCharType == 1) {
                  this.codeWord(sWordAndPunctuation.substring(0, iPos));
                  posTemp = iPos;
                  posFlag = true;
               }
               iLastCharType = 2;
            } else {
               if (iLastCharType == 2) {
                  this.codePunctuation(sWordAndPunctuation.substring(0, iPos));
                  posTemp = iPos;
                  posFlag = true;
               }
               iLastCharType = 1;
            }
            if (posFlag) {
               return posTemp;
            }
         }
         switch (iLastCharType) {
            case 1:
               this.codeWord(sWordAndPunctuation);
               break;
            case 2:
               this.codePunctuation(sWordAndPunctuation);
            default:
               break;
         }
         return -1;
      }
   }

   /**
    * 获取代表这个术语的HTML标签
    * @return 代表这个术语的HTML标签
    */
   public String getTag() {
      String tagString = "";
      switch (this.igContentType) {
      case 1:
         if (!"".equals(this.sgWordEmphasis)) {
            tagString = "<w equiv=\"" + this.sgTranslatedWord + "\" em=\"" + this.sgWordEmphasis + "\">" + this.sgOriginalWord + "</w>";
         } else {
            tagString = "<w>" + this.sgOriginalWord + "</w>";
         }
      case 2:
         if (!"".equals(this.sgPunctuationEmphasis)) {
            tagString = "<p equiv=\""
                    + this.sgPunctuation
                    + "\" em=\""
                    + this.sgPunctuationEmphasis
                    + "\">" + this.sgPunctuation
                    + this.sgPunctuationEmphasis
                    + "</p>";
         } else {
            tagString = "<p>" + this.sgPunctuation + "</p>";
         }
      case 3:
         if (this.igEmoticonStrength == 0) {
            tagString = "<e>" + this.sgEmoticon + "</e>";
         } else  {
            if (this.igEmoticonStrength == 1) {
               tagString = "<e em=\"+\">" + this.sgEmoticon + "</e>";
            } else {
               tagString = "<e em=\"-\">" + this.sgEmoticon + "</e>";
            }
         }
      default:
         return tagString;
      }
   }

   /**
    * 获取这个术语的情绪id
    * @return 这个术语的情绪id
    */
   public int getSentimentID() {
      if (!this.bgWordSentimentIDCalculated) {
         this.igWordSentimentID = this.resources.sentimentWords.getSentimentID(this.sgTranslatedWord.toLowerCase());
         this.bgWordSentimentIDCalculated = true;
      }

      return this.igWordSentimentID;
   }

   /**
    * 为这个术语的情绪分设置覆盖值
    * @param iSentiment 设定的覆盖值
    */
   public void setSentimentOverrideValue(int iSentiment) {
      this.bgOverrideSentimentScore = true;
      this.igOverrideSentimentScore = iSentiment;
   }

   /**
    * UC1，UC11
    * 获取这个术语的情绪值
    * 如果设置了情绪覆盖标志，则返回其覆盖值；
    * 如果情绪值为正，则根据情绪id返回情绪值；如果为负数或为零，则返回0
    * @return 术语情绪值
    */
   public int getSentimentValue() {
      if (this.bgOverrideSentimentScore) {
         return this.igOverrideSentimentScore;
      } else {
         return this.getSentimentID() < 1 ? 0 : this.resources.sentimentWords.getSentiment(this.igWordSentimentID);
      }
   }

   /**
    * 获取这个术语（单词）的强调的长度
    *
    * @return 这个术语的强调的长度
    */
   public int getWordEmphasisLength() {
      return this.sgWordEmphasis.length();
   }

   /**
    * 获取这个术语（单词）的强调
    *
    * @return 这个术语的强调
    */
   public String getWordEmphasis() {
      return this.sgWordEmphasis;
   }

   /**
    * 检查这个术语是否含有强调
    *
    * @return 有则返回true，反之返回false
    */
   public boolean containsEmphasis() {
      if (this.igContentType == 1) {
         return this.sgWordEmphasis.length() > 1;
      } else if (this.igContentType == 2) {
         return this.sgPunctuationEmphasis.length() > 1;
      } else {
         return false;
      }
   }

   /**
    * 获取这个术语的翻译词
    * @return 这个术语的翻译词
    */
   public String getTranslatedWord() {
      return this.sgTranslatedWord;
   }

   /**
    * 获取这个术语的翻译
    * 根据术语类型给出翻译
    * @return 各自类型的翻译词，如果都不是则返回空表情符号“”
    */
   public String getTranslation() {
      if (this.igContentType == 1) {
         return this.sgTranslatedWord;
      } else if (this.igContentType == 2) {
         return this.sgPunctuation;
      } else {
         return this.igContentType == 3 ? this.sgEmoticon : "";
      }
   }


   /**
    * 获取这个术语的加强语气词数值
    * 如果加强语气词数值为999，则重新设定该术语加强词数值
    * @return 这个术语的加强词数值
    */
   public int getBoosterWordScore() {
      if (this.igBoosterWordScore == 999) {
         this.setBoosterWordScore();
      }

      return this.igBoosterWordScore;
   }

   /**
    * 判断该术语是否是全大写
    * @return 是全大写则返回true，反之返回false
    */
   public boolean isAllCapitals() {
      if (!this.bgAllCaptialsCalculated) {
         if (this.sgOriginalWord == this.sgOriginalWord.toUpperCase()) {
            this.bgAllCapitals = true;
         } else {
            this.bgAllCapitals = false;
         }

         this.bgAllCaptialsCalculated = true;
      }

      return this.bgAllCapitals;
   }

   /**
    * UC4
    * 为这个术语设置这个术语的加强语气词数值
    */
   public void setBoosterWordScore() {
      this.igBoosterWordScore = this.resources.boosterWords.getBoosterStrength(this.sgTranslatedWord);
   }

   /**
    * 判断术语（标点符号类型）是否含有给定标点符号
    * @param sPunctuation 给定标点符号
    * @return 包含则返回true，反之返回false
    */
   public boolean punctuationContains(String sPunctuation) {
      if (this.igContentType != 2) {
         return false;
      } else if (this.sgPunctuation.indexOf(sPunctuation) > -1) {
         return true;
      } else {
         return !"".equals(this.sgPunctuationEmphasis) && this.sgPunctuationEmphasis.indexOf(sPunctuation) > -1;
      }
   }

   /**
    * 获取这个术语（标点）的强调的长度
    * @return 这个术语（标点）的强调的长度
    */
   public int getPunctuationEmphasisLength() {
      return this.sgPunctuationEmphasis.length();
   }

   /**
    * 获取这个术语（表情符号）的情绪强度
    * @return 这个术语（表情符号）的情绪强度
    */
   public int getEmoticonSentimentStrength() {
      return this.igEmoticonStrength;
   }

   /**
    * 获取这个术语（表情符号）
    * @return 这个术语（表情符号）
    */
   public String getEmoticon() {
      return this.sgEmoticon;
   }


   /**
    * 获取这个术语翻译后的标点符号
    * @return 这个术语翻译后的标点符号
    */
   public String getTranslatedPunctuation() {
      return this.sgPunctuation;
   }

   /**
    * 判断该术语是否是单词
    * @return 是单词则返回true，反之返回false
    */
   public boolean isWord() {
      return this.igContentType == 1;
   }

   /**
    * 判断该术语是否是标点
    * @return 是单词则返回true，反之返回false
    */
   public boolean isPunctuation() {
      return this.igContentType == 2;
   }

   /**
    * 判断该术语是否是符合要求的名词
    * @return 是符合要求的名词则返回true，反之返回false
    */
   public boolean isProperNoun() {
      if (this.igContentType != 1) {
         return false;
      } else {
         if ((!this.bgProperNounCalculated) && (this.sgOriginalWord.length() > 1)) {
               String sFirstLetter = this.sgOriginalWord.substring(0, 1);
               if (!sFirstLetter.toLowerCase().equals(sFirstLetter.toUpperCase())
                       && !this.sgOriginalWord.substring(0, 2).toUpperCase().equals("I'")) {
                  String sWordRemainder = this.sgOriginalWord.substring(1);
                  if (sFirstLetter.equals(sFirstLetter.toUpperCase()) && sWordRemainder.equals(sWordRemainder.toLowerCase())) {
                     this.bgProperNoun = true;
                  }
               }

            this.bgProperNounCalculated = true;
         }

         return this.bgProperNoun;
      }
   }

   /**
    * 判断该术语是否是表情符号
    * @return 是单词则返回true，反之返回false
    */
   public boolean isEmoticon() {
      return this.igContentType == 3;
   }

   /**
    * 以对应形式获取该术语的内容
    * @return 是单词则返回其小写翻译词，是标点则返回标点，是表情符号则返回表情符号，如果都不是则返回空表情符号“”
    */
   public String getText() {
      if (this.igContentType == 1) {
         return this.sgTranslatedWord.toLowerCase();
      } else if (this.igContentType == 2) {
         return this.sgPunctuation;
      } else {
         return this.igContentType == 3 ? this.sgEmoticon : "";
      }
   }


   /**
    * 根据术语类型，获取这个术语的原文本
    * 单词则返回单词，标点符号则返回标点符号和其强调，表情符号则返回表情符号，都不是则返回“”
    * @return 这个术语的原文
    */
   public String getOriginalText() {
      if (this.igContentType == 1) {
         return this.sgOriginalWord;
      } else if (this.igContentType == 2) {
         return this.sgPunctuation + this.sgPunctuationEmphasis;
      } else {
         return this.igContentType == 3 ? this.sgEmoticon : "";
      }
   }

   /**
    * UC-5
    * 判断该术语是否是否定词
    * @return 是否定词则返回true，反之返回false
    */
   public boolean isNegatingWord() {
      if (!this.bgNegatingWordCalculated) {
         if (this.sgLCaseWord.length() == 0) {
            this.sgLCaseWord = this.sgTranslatedWord.toLowerCase();
         }

         this.bgNegatingWord = this.resources.negatingWords.negatingWord(this.sgLCaseWord);
         this.bgNegatingWordCalculated = true;
      }

      return this.bgNegatingWord;
   }

   /**
    * 判断给定文本是否与这个术语匹配
    * @param sText 给定文本
    * @param bConvertToLowerCase 是否需要将这个术语转小写
    * @return 匹配则返回true，反之返回false
    */
   public boolean matchesString(String sText, boolean bConvertToLowerCase) {
      boolean flag = false;
      if (sText.length() != this.sgTranslatedWord.length()) {
         flag = false;
      } else {
         if (bConvertToLowerCase) {
            if (this.sgLCaseWord.length() == 0) {
               this.sgLCaseWord = this.sgTranslatedWord.toLowerCase();
            }

            if (sText.equals(this.sgLCaseWord)) {
               flag = true;
            }
         } else if (sText.equals(this.sgTranslatedWord)) {
            flag = true;
         } else {
            flag = false;
         }
      }
      return flag;
   }

   /**
    * 判断给定带通配符的文本是否与这个术语匹配
    * @param sTextWithWildcard 给定带通配符的文本
    * @param bConvertToLowerCase 是否需要将这个术语转小写
    * @return 匹配则返回true，反之返回false
    */
   public boolean matchesStringWithWildcard(String sTextWithWildcard, boolean bConvertToLowerCase) {
      int iStarPos = sTextWithWildcard.lastIndexOf("*");
      if (iStarPos >= 0 && iStarPos == sTextWithWildcard.length() - 1) {
         sTextWithWildcard = sTextWithWildcard.substring(0, iStarPos);
         boolean flag = false;
         if (bConvertToLowerCase) {
            if (this.sgLCaseWord.length() == 0) {
               this.sgLCaseWord = this.sgTranslatedWord.toLowerCase();
            }

            if (sTextWithWildcard.equals(this.sgLCaseWord)) {
               flag = true;
            }

            if (sTextWithWildcard.length() >= this.sgLCaseWord.length()) {
               flag = false;
            }

            if (sTextWithWildcard.equals(this.sgLCaseWord.substring(0, sTextWithWildcard.length()))) {
               flag = true;
            }
         } else {
            if (sTextWithWildcard.equals(this.sgTranslatedWord)) {
               flag = true;
            }

            if (sTextWithWildcard.length() >= this.sgTranslatedWord.length()) {
               flag = false;
            }

            if (sTextWithWildcard.equals(this.sgTranslatedWord.substring(0, sTextWithWildcard.length()))) {
               flag = true;
            }
         }

         return flag;
      } else {
         return this.matchesString(sTextWithWildcard, bConvertToLowerCase);
      }
   }

   /**
    * UC-6
    * 为给定给定文本编码成术语形式
    * @param sWord 给定文本
    */
   private void codeWord(String sWord) {
      String sWordNew = "";
      String sEm = "";
      if (this.options.bgCorrectExtraLetterSpellingErrors) {
         int iSameCount = 0;
         int iLastCopiedPos = 0;
         int iWordEnd = sWord.length() - 1;
         int iPos;
         for (iPos = 1; iPos <= iWordEnd; ++iPos) {
            if (sWord.substring(iPos, iPos + 1).compareToIgnoreCase(sWord.substring(iPos - 1, iPos)) == 0) {
               ++iSameCount;
            } else {
               if (iSameCount > 0 && this.options.sgIllegalDoubleLettersInWordMiddle.indexOf(sWord.substring(iPos - 1, iPos)) >= 0) {
                  ++iSameCount;
               }
               if (iSameCount > 1) {
                  if ("".equals(sEm)) {
                     sWordNew = sWord.substring(0, iPos - iSameCount + 1);
                     sEm = sWord.substring(iPos - iSameCount, iPos - 1);
                     iLastCopiedPos = iPos;
                  } else {
                     sWordNew = sWordNew + sWord.substring(iLastCopiedPos, iPos - iSameCount + 1);
                     sEm = sEm + sWord.substring(iPos - iSameCount, iPos - 1);
                     iLastCopiedPos = iPos;
                  }
               }
               iSameCount = 0;
            }
         }
         if (iSameCount > 0 && this.options.sgIllegalDoubleLettersAtWordEnd.indexOf(sWord.substring(iPos - 1, iPos)) >= 0) {
            ++iSameCount;
         }
         if (iSameCount > 1) {
            if ("".equals(sEm)) {
               sWordNew = sWord.substring(0, iPos - iSameCount + 1);
               sEm = sWord.substring(iPos - iSameCount + 1);
            } else {
               sWordNew = sWordNew + sWord.substring(iLastCopiedPos, iPos - iSameCount + 1);
               sEm = sEm + sWord.substring(iPos - iSameCount + 1);
            }
         } else if (!"".equals(sEm)) {
            sWordNew = sWordNew + sWord.substring(iLastCopiedPos);
         }
      }
      if ("".equals(sWordNew)) {
         sWordNew = sWord;
      }
      codeProcess(sWord, sEm, sWordNew);
   }

   /**
    * 为简化codeWord拆分出的方法
    * @param sWord 给定文本
    * @param sEm 中间产物
    * @param sWordNew 编码产物
    */
   void codeProcess(String sWord, String sEm, String sWordNew) {
      this.igContentType = 1;
      this.sgOriginalWord = sWord;
      this.sgWordEmphasis = sEm;
      this.sgTranslatedWord = sWordNew;
      if (this.sgTranslatedWord.indexOf("@") < 0) {
         if (this.options.bgCorrectSpellingsUsingDictionary) {
            this.correctSpellingInTranslatedWord();
         }

         if (this.options.bgUseLemmatisation) {
            if (this.sgTranslatedWord.equals("")) {
               sWordNew = this.resources.lemmatiser.lemmatise(this.sgOriginalWord);
               if (!sWordNew.equals(this.sgOriginalWord)) {
                  this.sgTranslatedWord = sWordNew;
               }
            } else {
               this.sgTranslatedWord = this.resources.lemmatiser.lemmatise(this.sgTranslatedWord);
            }
         }
      }
   }

   /**
    * UC3
    * 为术语的翻译词纠正拼写
    */
   private void correctSpellingInTranslatedWord() {
      if (!this.resources.correctSpellings.correctSpelling(this.sgTranslatedWord.toLowerCase())) {
         int iLastChar = this.sgTranslatedWord.length() - 1;

         for (int iPos = 1; iPos <= iLastChar - 1; ++iPos) {
            if (this.sgTranslatedWord.substring(iPos, iPos + 1).compareTo(this.sgTranslatedWord.substring(iPos - 1, iPos)) == 0) {
               String sReplaceWord = this.sgTranslatedWord.substring(0, iPos) + this.sgTranslatedWord.substring(iPos + 1);
               if (this.resources.correctSpellings.correctSpelling(sReplaceWord.toLowerCase())) {
                  this.sgWordEmphasis = this.sgWordEmphasis + this.sgTranslatedWord.substring(iPos, iPos + 1);
                  this.sgTranslatedWord = sReplaceWord;
               }
            }
         }

         if (iLastChar > 5) {
            if (this.sgTranslatedWord.indexOf("haha") > 0) {
               this.sgWordEmphasis = this.sgWordEmphasis + this.sgTranslatedWord.substring(3, this.sgTranslatedWord.indexOf("haha") + 2);
               this.sgTranslatedWord = "haha";

            }

            if (this.sgTranslatedWord.indexOf("hehe") > 0) {
               this.sgWordEmphasis = this.sgWordEmphasis + this.sgTranslatedWord.substring(3, this.sgTranslatedWord.indexOf("hehe") + 2);
               this.sgTranslatedWord = "hehe";

            }
         }

      }
   }

   /**
    * UC-7
    * 为给定可能表情符号编码成术语形式
    * @param sPossibleEmoticon 给定的可能的表情符号
    * @return 编码成功则返回true，反之返回false
    */
   private boolean codeEmoticon(String sPossibleEmoticon) {
      int iEmoticonStrength = this.resources.emoticons.getEmoticon(sPossibleEmoticon);
      if (iEmoticonStrength != 999) {
         this.igContentType = 3;
         this.sgEmoticon = sPossibleEmoticon;
         this.igEmoticonStrength = iEmoticonStrength;
         return true;
      } else {
         return false;
      }
   }

   /**
    * 为给定标点符号编码成术语形式
    * @param sPunctuation 给定标点符号
    */
   private void codePunctuation(String sPunctuation) {
      if (sPunctuation.length() > 1) {
         this.sgPunctuation = sPunctuation.substring(0, 1);
         this.sgPunctuationEmphasis = sPunctuation.substring(1);
      } else {
         this.sgPunctuation = sPunctuation;
         this.sgPunctuationEmphasis = "";
      }

      this.igContentType = 2;
   }
}

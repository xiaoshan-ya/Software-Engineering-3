package uk.ac.wlv.sentistrength;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;

/**
 * SentiStrength类,主要目的是根据命令行参数初始化语料库，并提供情感分析方法。
 * @author ruohao.zhang
 **/
public class SentiStrength {
   Corpus corpus = new Corpus();


   /**
    * SentiStrength的构造方法，在其中一同初始化了语料库Corpus
    * @author ruohao.zhang
    */
   public SentiStrength() {
      this.corpus = new Corpus();
   }

   /**
    * SentiStrength构造方法，利用args参数调用了initialiseAndRun方法。
    * @param args 命令行参数
    * @author ruohao.zhang
    */
   public SentiStrength(String[] args) {
      this.corpus = new Corpus();
      this.initialiseAndRun(args);
   }

   /**
    * main方法，来运行initialiseAndRun
    * @param args 命令行参数
    * @author ruohao.zhang
    */
   public static void main(String[] args) {
      SentiStrength classifier = new SentiStrength();
      classifier.initialiseAndRun(args);
   }

   /**
    * 无使用情况
    * @return null
    * @author ruohao.zhang
    */
   public String[] getinput() {
      return null;
   }

   /**
    * 用于处理args，然后运行代码。
    * UC-11 to UC-28
    * @param args 命令行参数
    * @author ruohao.zhang
    */
   public void initialiseAndRun(String[] args) {
      Corpus c = this.corpus;
      String sInputFile = ""; //输入文件路径
      String sInputFolder = ""; //输入目录路径
      String sTextToParse = "";
      String sOptimalTermStrengths = "";
      String sFileSubString = "\t";
      String sResultsFolder = ""; //输出目录路径
      String sResultsFileExtension = "_out.txt";
      boolean[] bArgumentRecognised = new boolean[args.length]; //用于判断args中的参数有没有判断正确
      int iIterations = 1;
      int iMinImprovement = 2;
      int iMultiOptimisations = 1;
      int iListenPort = 0;
      int iTextColForAnnotation = -1;
      int iIdCol = -1;
      int iTextCol = -1;
      boolean bDoAll = false;
      boolean bOkToOverwrite = false;
      boolean bTrain = false;
      boolean bReportNewTermWeightsForBadClassifications = false;
      boolean bStdIn = false;
      boolean bCmd = false;
      boolean bWait = false;
      boolean bUseTotalDifference = true;
      boolean bURLEncoded = false;
      String sLanguage = "";

      int i;
      for (i = 0; i < args.length; ++i) {
         bArgumentRecognised[i] = false;
      }
      //String rooty="/Users/mac/Documents/workspace/SentiStrength/src/input/";
      for (i = 0; i < args.length; ++i) {
         try {
            /*
              input后面紧跟输入路径
             */
            if (args[i].equalsIgnoreCase("input")) {
               sInputFile = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            /*
              inputfolder后面跟输入文件夹路径
             */
            if (args[i].equalsIgnoreCase("inputfolder")) {
               sInputFolder = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            /*
              outputfolder后面跟输出目录路径
             */
            if (args[i].equalsIgnoreCase("outputfolder")) {
               sResultsFolder = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            /*
              输出文件的扩展格式
             */
            if (args[i].equalsIgnoreCase("resultextension")) {
               sResultsFileExtension = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }
            /*
              输出文件的扩展格式
             */
            if (args[i].equalsIgnoreCase("resultsextension")) {
               sResultsFileExtension = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("filesubstring")) {
               sFileSubString = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("overwrite")) {
               bOkToOverwrite = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("text")) {
               sTextToParse = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("urlencoded")) {
               bURLEncoded = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("listen")) {
               iListenPort = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("stdin")) {
               bStdIn = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("cmd")) {
               bCmd = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("optimise")) {
               sOptimalTermStrengths = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("annotatecol")) {
               iTextColForAnnotation = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("textcol")) {
               iTextCol = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("idcol")) {
               iIdCol = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("lang")) {
               sLanguage = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("train")) {
               bTrain = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("all")) {
               bDoAll = true;
               bTrain = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("numcorrect")) {
               bUseTotalDifference = false;
               bTrain = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("iterations")) {
               iIterations = Integer.parseInt(args[i + 1]);
               bTrain = true;
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("minimprovement")) {
               iMinImprovement = Integer.parseInt(args[i + 1]);
               bTrain = true;
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("multi")) {
               iMultiOptimisations = Integer.parseInt(args[i + 1]);
               bTrain = true;
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("termWeights")) {
               bReportNewTermWeightsForBadClassifications = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("wait")) {
               bWait = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("help")) {
               this.printCommandLineOptions();
               return;
            }
         } catch (NumberFormatException var32) {
            System.err.println("Error in argument for " + args[i] + ". Integer expected!");
            return;
         } catch (Exception var33) {
            System.err.println("Error in argument for " + args[i] + ". Argument missing?");
            return;
         }
      }

      this.parseParametersForCorpusOptions(args, bArgumentRecognised);
      if (sLanguage.length() > 1) {
         Locale l = new Locale(sLanguage);
         Locale.setDefault(l);
      }

      for (i = 0; i < args.length; ++i) {
         if (!bArgumentRecognised[i]) {
            System.err.println("Unrecognised command - wrong spelling or case?: " + args[i]);
            this.showBriefHelp();
            return;
         }
      }

      if (c.initialise()) {
         if (!sTextToParse.equals("")) {
            if (bURLEncoded) {
               try {
                  sTextToParse = URLDecoder.decode(sTextToParse, "UTF-8");
               } catch (UnsupportedEncodingException var31) {
                  var31.printStackTrace();
               }
            } else {
               sTextToParse = sTextToParse.replace("+", " ");
            }

            this.parseOneText(c, sTextToParse, bURLEncoded);
         } else if (iListenPort > 0) {
            this.listenAtPort(c, iListenPort);
         } else if (bCmd) {
            this.listenForCmdInput(c);
         } else if (bStdIn) {
            this.listenToStdIn(c, iTextCol);
         } else if (!bWait) {
            if (!sOptimalTermStrengths.equals("")) {
               if (sInputFile.equals("")) {
                  System.err.println("Input file must be specified to optimise term weights");
                  return;
               }

               if (c.setCorpus(sInputFile)) {
                  c.optimiseDictionaryWeightingsForCorpus(iMinImprovement, bUseTotalDifference);
                  c.resources.sentimentWords.saveSentimentList(sOptimalTermStrengths, c);
                  System.err.println("Saved optimised term weights to " + sOptimalTermStrengths);
               } else {
                  System.err.println("Error: Too few texts in " + sInputFile);
               }
            } else if (bReportNewTermWeightsForBadClassifications) {
               if (c.setCorpus(sInputFile)) {
                  c.printCorpusUnusedTermsClassificationIndex(FileOps.s_ChopFileNameExtension(sInputFile) + "_unusedTerms.txt", 1);
               } else {
                  System.err.println("Error: Too few texts in " + sInputFile);
               }
            } else if (iTextCol > 0 && iIdCol > 0) {
               this.classifyAndSaveWithID(c, sInputFile, sInputFolder, iTextCol, iIdCol);
            } else if (iTextColForAnnotation > 0) {
               this.annotationTextCol(c, sInputFile, sInputFolder, sFileSubString, iTextColForAnnotation, bOkToOverwrite);
            } else {
               if (!sInputFolder.equals("")) {
                  System.err.println("Input folder specified but textCol and IDcol or annotateCol needed");
               }

               if (sInputFile.equals("")) {
                  System.err.println("No action taken because no input file nor text specified");
                  this.showBriefHelp();
                  return;
               }

               String sOutputFile = FileOps.getNextAvailableFilename(FileOps.s_ChopFileNameExtension(sInputFile), sResultsFileExtension);
               if (sResultsFolder.length() > 0) {
                  sOutputFile = sResultsFolder + (new File(sOutputFile)).getName();
               }

               if (bTrain) {
                  this.runMachineLearning(c, sInputFile, bDoAll, iMinImprovement, bUseTotalDifference,
                          iIterations, iMultiOptimisations, sOutputFile);
               } else {
                  --iTextCol;
                  c.classifyAllLinesInInputFile(sInputFile, iTextCol, sOutputFile);
               }

               System.err.println("Finished! Results in: " + sOutputFile);
            }
         }
      } else {
         System.err.println("Failed to initialise!");

         try {
            File f = new File(c.resources.sgSentiStrengthFolder);
            if (!f.exists()) {
               System.err.println("Folder does not exist! " + c.resources.sgSentiStrengthFolder);
            }
         } catch (Exception var30) {
            System.err.println("Folder doesn't exist! " + c.resources.sgSentiStrengthFolder);
         }

         this.showBriefHelp();
      }

   }

   /**
    * 解析传递给程序的命令行参数。
    * 该方法贯穿args数组的所有元素，并试图将每个元素与一组预定义的选项匹配。如果找到匹配项，则执行相应的操作，并相应地更新bArgumentRecognized数组。
    * UC-16
    * @param args 命令行参数
    * @param bArgumentRecognised 判断参数是否被识别
    * @author ruohao.zhang
    */
   private void parseParametersForCorpusOptions(String[] args, boolean[] bArgumentRecognised) {
      for (int i = 0; i < args.length; ++i) {
         try {
            /*
              包含情感文件的文件夹路径
             */
            if (args[i].equalsIgnoreCase("sentidata")) {
               this.corpus.resources.sgSentiStrengthFolder = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            /*
              包含情绪查找表的文件路径
             */
            if (args[i].equalsIgnoreCase("emotionlookuptable")) {
               this.corpus.resources.sgSentimentWordsFile = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("additionalfile")) {
               this.corpus.resources.sgAdditionalFile = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("keywords")) {
               this.corpus.options.parseKeywordList(args[i + 1].toLowerCase());
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("wordsBeforeKeywords")) {
               this.corpus.options.igWordsToIncludeBeforeKeyword = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("wordsAfterKeywords")) {
               this.corpus.options.igWordsToIncludeAfterKeyword = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("sentiment")) {
               this.corpus.options.nameProgram(false);
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("stress")) {
               this.corpus.options.nameProgram(true);
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("trinary")) {
               this.corpus.options.bgTrinaryMode = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("binary")) {
               this.corpus.options.bgBinaryVersionOfTrinaryMode = true;
               this.corpus.options.bgTrinaryMode = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("scale")) {
               this.corpus.options.bgScaleMode = true;
               bArgumentRecognised[i] = true;
               if (this.corpus.options.bgTrinaryMode) {
                  System.err.println("Must choose binary/trinary OR scale mode");
                  return;
               }
            }

            ClassificationOptions var10000;
            if (args[i].equalsIgnoreCase("sentenceCombineAv")) {
               var10000 = this.corpus.options;
               this.corpus.options.getClass();
               var10000.igEmotionSentenceCombineMethod = 1;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("sentenceCombineTot")) {
               var10000 = this.corpus.options;
               this.corpus.options.getClass();
               var10000.igEmotionSentenceCombineMethod = 2;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("paragraphCombineAv")) {
               var10000 = this.corpus.options;
               this.corpus.options.getClass();
               var10000.igEmotionParagraphCombineMethod = 1;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("paragraphCombineTot")) {
               var10000 = this.corpus.options;
               this.corpus.options.getClass();
               var10000.igEmotionParagraphCombineMethod = 2;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("negativeMultiplier")) {
               this.corpus.options.fgNegativeSentimentMultiplier = Float.parseFloat(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("noBoosters")) {
               this.corpus.options.bgBoosterWordsChangeEmotion = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("noNegatingPositiveFlipsEmotion")) {
               this.corpus.options.bgNegatingPositiveFlipsEmotion = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("noNegatingNegativeNeutralisesEmotion")) {
               this.corpus.options.bgNegatingNegativeNeutralisesEmotion = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("noNegators")) {
               this.corpus.options.bgNegatingWordsFlipEmotion = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("noIdioms")) {
               this.corpus.options.bgUseIdiomLookupTable = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("questionsReduceNeg")) {
               this.corpus.options.bgReduceNegativeEmotionInQuestionSentences = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("noEmoticons")) {
               this.corpus.options.bgUseEmoticons = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("exclamations2")) {
               this.corpus.options.bgExclamationInNeutralSentenceCountsAsPlus2 = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("minPunctuationWithExclamation")) {
               this.corpus.options.igMinPunctuationWithExclamationToChangeSentenceSentiment = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("mood")) {
               this.corpus.options.igMoodToInterpretNeutralEmphasis = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("noMultiplePosWords")) {
               this.corpus.options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("noMultipleNegWords")) {
               this.corpus.options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("noIgnoreBoosterWordsAfterNegatives")) {
               this.corpus.options.bgIgnoreBoosterWordsAfterNegatives = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("noDictionary")) {
               this.corpus.options.bgCorrectSpellingsUsingDictionary = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("noDeleteExtraDuplicateLetters")) {
               this.corpus.options.bgCorrectExtraLetterSpellingErrors = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("illegalDoubleLettersInWordMiddle")) {
               this.corpus.options.sgIllegalDoubleLettersInWordMiddle = args[i + 1].toLowerCase();
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("illegalDoubleLettersAtWordEnd")) {
               this.corpus.options.sgIllegalDoubleLettersAtWordEnd = args[i + 1].toLowerCase();
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("noMultipleLetters")) {
               this.corpus.options.bgMultipleLettersBoostSentiment = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("negatedWordStrengthMultiplier")) {
               this.corpus.options.fgStrengthMultiplierForNegatedWords = Float.parseFloat(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("maxWordsBeforeSentimentToNegate")) {
               this.corpus.options.igMaxWordsBeforeSentimentToNegate = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("negatingWordsDontOccurBeforeSentiment")) {
               this.corpus.options.bgNegatingWordsOccurBeforeSentiment = false;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("maxWordsAfterSentimentToNegate")) {
               this.corpus.options.igMaxWordsAfterSentimentToNegate = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("negatingWordsOccurAfterSentiment")) {
               this.corpus.options.bgNegatingWordsOccurAfterSentiment = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("alwaysSplitWordsAtApostrophes")) {
               this.corpus.options.bgAlwaysSplitWordsAtApostrophes = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("capitalsBoostTermSentiment")) {
               this.corpus.options.bgCapitalsBoostTermSentiment = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("lemmaFile")) {
               this.corpus.options.bgUseLemmatisation = true;
               this.corpus.resources.sgLemmaFile = args[i + 1];
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("MinSentencePosForQuotesIrony")) {
               this.corpus.options.igMinSentencePosForQuotesIrony = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("MinSentencePosForPunctuationIrony")) {
               this.corpus.options.igMinSentencePosForPunctuationIrony = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("MinSentencePosForTermsIrony")) {
               this.corpus.options.igMinSentencePosForTermsIrony = Integer.parseInt(args[i + 1]);
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("MinSentencePosForAllIrony")) {
               this.corpus.options.igMinSentencePosForTermsIrony = Integer.parseInt(args[i + 1]);
               this.corpus.options.igMinSentencePosForPunctuationIrony = this.corpus.options.igMinSentencePosForTermsIrony;
               this.corpus.options.igMinSentencePosForQuotesIrony = this.corpus.options.igMinSentencePosForTermsIrony;
               bArgumentRecognised[i] = true;
               bArgumentRecognised[i + 1] = true;
            }

            if (args[i].equalsIgnoreCase("explain")) {
               this.corpus.options.bgExplainClassification = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("echo")) {
               this.corpus.options.bgEchoText = true;
               bArgumentRecognised[i] = true;
            }

            if (args[i].equalsIgnoreCase("UTF8")) {
               this.corpus.options.bgForceUTF8 = true;
               bArgumentRecognised[i] = true;
            }

         } catch (NumberFormatException var5) {
            System.err.println("Error in argument for " + args[i] + ". Integer expected!");
            return;
         } catch (Exception var6) {
            System.err.println("Error in argument for " + args[i] + ". Argument missing?");
            return;
         }
      }

   }

   /**
    * 对命令行参数进行解析和处理，以便初始化语料库
    * 没有被识别，则输出错误信息并调用showBriefHelp方法展示帮助信息。最后，如果初始化成功，则初始化语料库。
    * UC-16
    * @param args 命令行参数
    * @author ruohao.zhang
    */
   public void initialise(String[] args) {
      boolean[] bArgumentRecognised = new boolean[args.length];

      int i;
      for (i = 0; i < args.length; ++i) {
         bArgumentRecognised[i] = false;
      }

      this.parseParametersForCorpusOptions(args, bArgumentRecognised);

      for (i = 0; i < args.length; ++i) {
         if (!bArgumentRecognised[i]) {
            System.err.println("Unrecognised command - wrong spelling or case?: " + args[i]);
            this.showBriefHelp();
            return;
         }
      }

      if (!this.corpus.initialise()) {
         System.err.println("Failed to initialise!");
      }

   }

   /**
    * 用于计算情感分数
    * UC-22,UC-23,UC-24
    * @param sentence 要进行计算的句子
    * @return String
    * @author ruohao.zhang
    */
   public String computeSentimentScores(String sentence) {
      int iPos = 1;
      int iNeg = 1;
      int iTrinary = 0;
      int iScale = 0;
      Paragraph paragraph = new Paragraph();
      paragraph.setParagraph(sentence, this.corpus.resources, this.corpus.options);
      iNeg = paragraph.getParagraphNegativeSentiment();
      iPos = paragraph.getParagraphPositiveSentiment();
      iTrinary = paragraph.getParagraphTrinarySentiment();
      iScale = paragraph.getParagraphScaleSentiment();
      String sRationale = "";
      if (this.corpus.options.bgEchoText) {
         sRationale = " " + sentence;
      }

      if (this.corpus.options.bgExplainClassification) {
         sRationale = " " + paragraph.getClassificationRationale();
      }

      if (this.corpus.options.bgTrinaryMode) {
         return iPos + " " + iNeg + " " + iTrinary + sRationale;
      } else {
         return this.corpus.options.bgScaleMode ? iPos + " " + iNeg + " " + iScale + sRationale : iPos + " " + iNeg + sRationale;
      }
   }

   /**
    * 在文本语料库上运行机器学习算法，评估结果的准确性，并将结果写入文件。
    * UC-29
    * @param c 语料库
    * @param sInputFile 输入文件
    * @param bDoAll 确定是否运行所有语料库中所有选项的迭代（即运行run10FoldCrossValidationForAllOptionVariations()方法）
    * @param iMinImprovement 算法训练过程中所设置的最小精度提升值（小于1不进行任何操作）
    * @param bUseTotalDifference 用于控制在训练机器学习模型时是否使用总体的差异（total difference）来计算精度
    * @param iIterations 迭代次数
    * @param iMultiOptimisations 控制机器学习算法在优化期间使用不同参数配置运行的次数
    * @param sOutputFile 输出文件名
    * @author ruohao.zhang
    */
   public void runMachineLearning(Corpus c, String sInputFile, boolean bDoAll, int iMinImprovement,
                                   boolean bUseTotalDifference, int iIterations, int iMultiOptimisations, String sOutputFile) {
      if (iMinImprovement < 1) {
         System.err.println("No action taken because min improvement < 1");
         this.showBriefHelp();
      } else {
         c.setCorpus(sInputFile);
         c.calculateCorpusSentimentScores();
         int corpusSize = c.getCorpusSize();
         if (c.options.bgTrinaryMode) {
            if (c.options.bgBinaryVersionOfTrinaryMode) {
               System.err.print("Before training, binary accuracy: " + c.getClassificationTrinaryNumberCorrect() + " "
                       + (float) c.getClassificationTrinaryNumberCorrect() / (float) corpusSize * 100.0F + "%");
            } else {
               System.err.print("Before training, trinary accuracy: " + c.getClassificationTrinaryNumberCorrect() + " "
                       + (float) c.getClassificationTrinaryNumberCorrect() / (float) corpusSize * 100.0F + "%");
            }
         } else if (c.options.bgScaleMode) {
            System.err.print("Before training, scale accuracy: " + c.getClassificationScaleNumberCorrect() + " "
                    + (float) c.getClassificationScaleNumberCorrect() * 100.0F / (float) corpusSize + "% corr "
                    + c.getClassificationScaleCorrelationWholeCorpus());
         } else {
            System.err.print("Before training, positive: " + c.getClassificationPositiveNumberCorrect() + " "
                    + c.getClassificationPositiveAccuracyProportion() * 100.0F + "% negative "
                    + c.getClassificationNegativeNumberCorrect() + " "
                    + c.getClassificationNegativeAccuracyProportion() * 100.0F + "% ");
            System.err.print("   Positive corr: " + c.getClassificationPosCorrelationWholeCorpus() + " negative "
                    + c.getClassificationNegCorrelationWholeCorpus());
         }

         System.err.println(" err of " + c.getCorpusSize());
         if (bDoAll) {
            System.err.println("Running " + iIterations + " iteration(s) of all options on file "
                    + sInputFile + "; results in " + sOutputFile);
            c.run10FoldCrossValidationForAllOptionVariations(iMinImprovement, bUseTotalDifference,
                    iIterations, iMultiOptimisations, sOutputFile);
         } else {
            System.err.println("Running " + iIterations + " iteration(s) for standard or selected options on file "
                    + sInputFile + "; results in " + sOutputFile);
            c.run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference,
                    iIterations, iMultiOptimisations, sOutputFile);
         }

      }
   }

   /**
    * 将给定的文本文件或文件夹中的文本进行分类，并将结果和文本ID保存在文件中
    * UC-11,UC-12，UC-13,UC-19,UC-20
    * @param c 语料库
    * @param sInputFile 输入文件
    * @param sInputFolder 输入文件夹
    * @param iTextCol 输入文本所在的列数
    * @param iIdCol ID所在的列数
    * @author ruohao.zhang
    */
   private void classifyAndSaveWithID(Corpus c, String sInputFile, String sInputFolder, int iTextCol, int iIdCol) {
      if (!sInputFile.equals("")) {
         c.classifyAllLinesAndRecordWithID(sInputFile, iTextCol - 1, iIdCol - 1,
                 FileOps.s_ChopFileNameExtension(sInputFile) + "_classID.txt");
      } else {
         if (sInputFolder.equals("")) {
            System.err.println("No annotations done because no input file or folder specfied");
            this.showBriefHelp();
            return;
         }

         File folder = new File(sInputFolder);
         File[] listOfFiles = folder.listFiles();
         if (listOfFiles == null) {
            System.err.println("Incorrect or empty input folder specfied");
            this.showBriefHelp();
            return;
         }

         for (int i = 0; i < listOfFiles.length; ++i) {
            if (listOfFiles[i].isFile()) {
               System.err.println("Classify + save with ID: " + listOfFiles[i].getName());
               c.classifyAllLinesAndRecordWithID(sInputFolder + "/" + listOfFiles[i].getName(),
                       iTextCol - 1, iIdCol - 1, sInputFolder + "/"
                               + FileOps.s_ChopFileNameExtension(listOfFiles[i].getName()) + "_classID.txt");
            }
         }
      }

   }

   /**
    * 作用是为语料库中的文本添加注释
    * 如果bOkToOverwrite为false，则输出一条错误信息。
    * 如果sInputFile非空，则注释该文件中的所有行。
    * 如果sInputFolder非空，则注释该文件夹中所有文件的所有行。
    * 如果sFileSubString非空，则只注释文件名中包含sFileSubString的文件。
    * 如果以上条件都不满足，则输出一条错误信息。
    * @param c 要注释的语料库
    * @param sInputFile 要注释的输入文件名
    * @param sInputFolder 要注释的输入文件夹名
    * @param sFileSubString 要过滤的文件名子字符串
    * @param iTextColForAnnotation 要注释的文本所在的列数
    * @param bOkToOverwrite 是否允许覆盖已有的注释
    * @author ruohao.zhang
    */
   private void annotationTextCol(Corpus c, String sInputFile, String sInputFolder, String sFileSubString,
                                  int iTextColForAnnotation, boolean bOkToOverwrite) {
      if (!bOkToOverwrite) {
         System.err.println("Must include parameter overwrite to annotate");
      } else {
         if (!sInputFile.equals("")) {
            c.annotateAllLinesInInputFile(sInputFile, iTextColForAnnotation - 1);
         } else {
            if (sInputFolder.equals("")) {
               System.err.println("No annotations done because no input file or folder specfied");
               this.showBriefHelp();
               return;
            }
            File folder = new File(sInputFolder);
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; ++i) {
               if (listOfFiles[i].isFile()) {
                  if (!sFileSubString.equals("") && listOfFiles[i].getName().indexOf(sFileSubString) <= 0) {
                     System.err.println("  Ignoring " + listOfFiles[i].getName());
                  } else {
                     System.err.println("Annotate: " + listOfFiles[i].getName());
                     c.annotateAllLinesInInputFile(sInputFolder + "/" + listOfFiles[i].getName(), iTextColForAnnotation - 1);
                  }
               }
            }
         }

      }
   }

   /**
    * 解析给定文本，并为给定语料库设置情感值。情绪值包括正负情绪评分、情绪值和尺度情绪值。如果指定，输出可以进行URL编码。
    * @param c 语料库对象
    * @param sTextToParse 要解析的文本
    * @param bURLEncodedOutput 指示输出是否应该进行URL编码的标志
    * @author ruohao.zhang
    */
   private void parseOneText(Corpus c, String sTextToParse, boolean bURLEncodedOutput) {
      int iPos = 1;
      int iNeg = 1;
      int iTrinary = 0;
      int iScale = 0;
      Paragraph paragraph = new Paragraph();
      paragraph.setParagraph(sTextToParse, c.resources, c.options);
      iNeg = paragraph.getParagraphNegativeSentiment();
      iPos = paragraph.getParagraphPositiveSentiment();
      iTrinary = paragraph.getParagraphTrinarySentiment();
      iScale = paragraph.getParagraphScaleSentiment();
      String sRationale = "";
      if (c.options.bgEchoText) {
         sRationale = " " + sTextToParse;
      }

      if (c.options.bgExplainClassification) {
         sRationale = " " + paragraph.getClassificationRationale();
      }

      String sOutput = "";
      if (c.options.bgTrinaryMode) {
         sOutput = iPos + " " + iNeg + " " + iTrinary + sRationale;
      } else if (c.options.bgScaleMode) {
         sOutput = iPos + " " + iNeg + " " + iScale + sRationale;
      } else {
         sOutput = iPos + " " + iNeg + sRationale;
      }

      if (bURLEncodedOutput) {
         try {
            System.err.println(URLEncoder.encode(sOutput, "UTF-8"));
         } catch (UnsupportedEncodingException var13) {
            var13.printStackTrace();
         }
      } else if (c.options.bgForceUTF8) {
         try {
            System.err.println(new String(sOutput.getBytes("UTF-8"), "UTF-8"));
         } catch (UnsupportedEncodingException var12) {
            System.err.println("UTF-8 Not found on your system!");
            var12.printStackTrace();
         }
      } else {
         System.err.println(sOutput);
      }

   }

   /**
    * 由listenToStdIn剥离出来的方法.
    * @param c Corpus
    * @param sOutput 输出
    * @param sTextToParse 处理文本
    * @param iTextCol 文本列
    * @param bSuccess 表明成功的布尔值
    */
   private void setupOutput(Corpus c, String sOutput, String sTextToParse, int iTextCol, boolean bSuccess) {
      int iPos = 1;
      bSuccess = false;
      int iTrinary = 0;
      int iScale = 0;
      Paragraph paragraph = new Paragraph();
      if (iTextCol > -1) {
         String[] sData = sTextToParse.split("\t");
         if (sData.length >= iTextCol) {
            paragraph.setParagraph(sData[iTextCol], c.resources, c.options);
         }
      } else {
         paragraph.setParagraph(sTextToParse, c.resources, c.options);
      }

      int iNeg = paragraph.getParagraphNegativeSentiment();
      iPos = paragraph.getParagraphPositiveSentiment();
      iTrinary = paragraph.getParagraphTrinarySentiment();
      iScale = paragraph.getParagraphScaleSentiment();
      String sRationale = "";
      if (c.options.bgEchoText) {
         sOutput = sTextToParse + "\t";
      } else {
         sOutput = "";
      }

      if (c.options.bgExplainClassification) {
         sRationale = paragraph.getClassificationRationale();
      }

      if (c.options.bgTrinaryMode) {
         sOutput = sOutput + iPos + "\t" + iNeg + "\t" + iTrinary + "\t" + sRationale;
      } else if (c.options.bgScaleMode) {
         sOutput = sOutput + iPos + "\t" + iNeg + "\t" + iScale + "\t" + sRationale;
      } else {
         sOutput = sOutput + iPos + "\t" + iNeg + "\t" + sRationale;
      }
   }

   /**
    * 监听标准输入，利用语料库对通过它提供的文本进行情感分析。
    * <p>
    * 根据特定的输入字符串更改某些情感单词的术语权重。然后，该方法将情绪分析结果作为文本输出。
    * 输出文本包括积极情绪评分、负面情绪评分以及对分类原理的可选解释。
    * 该方法还处理不同的输出模式，具体取决于Corpus对象中设置的选项。
    * </p>
    * UC-16
    * @param c 语料库对象
    * @param iTextCol 要分析的文本的输入文本中列的索引
    * @author ruohao.zhang
    */
   private void listenToStdIn(Corpus c, int iTextCol) {
      BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

      String sTextToParse;
      try {
         while ((sTextToParse = stdin.readLine()) != null) {
            boolean bSuccess = false;
            if (sTextToParse.indexOf("#Change_TermWeight") >= 0) {
               String[] sData = sTextToParse.split("\t");
               bSuccess = c.resources.sentimentWords.setSentiment(sData[1], Integer.parseInt(sData[2]));
               if (bSuccess) {
                  System.err.println("1");
               } else {
                  System.err.println("0");
               }
            } else {
               String sOutput = "";
               setupOutput(c, sOutput, sTextToParse, iTextCol, bSuccess);

               if (c.options.bgForceUTF8) {
                  try {
                     System.err.println(new String(sOutput.getBytes("UTF-8"), "UTF-8"));
                  } catch (UnsupportedEncodingException var13) {
                     System.err.println("UTF-8Not found on your system!");
                     var13.printStackTrace();
                  }
               } else {
                  System.err.println(sOutput);
               }
            }
         }
      } catch (IOException var14) {
         System.err.println("Error reading input");
         var14.printStackTrace();
      }

   }

   /**
    * 命令行监听用户输入，并使用Corpus对象提供的情感分析功能分析每个输入的情绪。
    * UC-15,UC-16
    * @param c 语料库对象
    * @author ruohao.zhang
    */
   private void listenForCmdInput(Corpus c) {
      BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
      while (true) {
         while (true) {
            try {
               while (true) {
                  String sTextToParse = stdin.readLine();
                  if (sTextToParse.toLowerCase().equals("@end")) {
                     return;
                  }
                  int iPos = 1, iNeg = 1;
                  int iTrinary = 0, iScale = 0;
                  Paragraph paragraph = new Paragraph();
                  paragraph.setParagraph(sTextToParse, c.resources, c.options);
                  iNeg = paragraph.getParagraphNegativeSentiment();
                  iPos = paragraph.getParagraphPositiveSentiment();
                  iTrinary = paragraph.getParagraphTrinarySentiment();
                  iScale = paragraph.getParagraphScaleSentiment();
                  String sRationale = "";
                  if (c.options.bgEchoText) {
                     sRationale = " " + sTextToParse;
                  }
                  if (c.options.bgExplainClassification) {
                     sRationale = " " + paragraph.getClassificationRationale();
                  }
                  String sOutput;
                  if (c.options.bgTrinaryMode) {
                     sOutput = iPos + " " + iNeg + " " + iTrinary + sRationale;
                  } else if (c.options.bgScaleMode) {
                     sOutput = iPos + " " + iNeg + " " + iScale + sRationale;
                  } else {
                     sOutput = iPos + " " + iNeg + sRationale;
                  }
                  if (!c.options.bgForceUTF8) {
                     System.err.println(sOutput);
                  } else {
                     try {
                        System.err.println(new String(sOutput.getBytes("UTF-8"), "UTF-8"));
                     } catch (UnsupportedEncodingException var12) {
                        System.err.println("UTF-8Not found on your system!");
                        var12.printStackTrace();
                     }
                  }
               }
            } catch (IOException var13) {
               System.err.println(var13);
            }
         }
      }
   }

   /**
    * 由listenAtPort中细化出来的方法.
    * @param iListenPort 监听端口
    * @return
    */
   private ServerSocket setupSocket(int iListenPort) {
      ServerSocket serverSocket = null;
      boolean var6 = false;

      try {
         serverSocket = new ServerSocket(iListenPort);
      } catch (IOException var23) {
         System.err.println("Could not listen on port " + iListenPort + " because\n" + var23.getMessage());
         return null;
      }

      System.err.println("Listening on port: " + iListenPort + " IP: " + serverSocket.getInetAddress());
      return serverSocket;
   }

   /**
    * 由listenAtPort剥离出来的方法.
    * @param decodedText
    * @param in
    */
   private void setupDecodedText(String decodedText, BufferedReader in) {
      String inputLine;
      try {
         while ((inputLine = in.readLine()) != null) {
            if (inputLine.indexOf("GET /") == 0) {
               int lastSpacePos = inputLine.lastIndexOf(" ");
               if (lastSpacePos < 5) {
                  lastSpacePos = inputLine.length();
               }

               decodedText = URLDecoder.decode(inputLine.substring(5, lastSpacePos), "UTF-8");
               System.err.println("Analysis of text: " + decodedText);
               break;
            }

            if (inputLine.equals("MikeSpecialMessageToEnd.")) {
               break;
            }
         }
      } catch (IOException var24) {
         System.err.println("IOException " + var24.getMessage());
         var24.printStackTrace();
         decodedText = "";
      } catch (Exception var25) {
         System.err.println("Non-IOException " + var25.getMessage());
         decodedText = "";
      }
   }

   private void printOutput(Corpus c, String decodedText, PrintWriter err) {
      int iPos = 1;
      int iNeg = 1;
      int iTrinary = 0;
      int iScale = 0;
      Paragraph paragraph = new Paragraph();
      paragraph.setParagraph(decodedText, c.resources, c.options);
      iNeg = paragraph.getParagraphNegativeSentiment();
      iPos = paragraph.getParagraphPositiveSentiment();
      iTrinary = paragraph.getParagraphTrinarySentiment();
      iScale = paragraph.getParagraphScaleSentiment();
      String sRationale = "";
      if (c.options.bgEchoText) {
         sRationale = " " + decodedText;
      }

      if (c.options.bgExplainClassification) {
         sRationale = " " + paragraph.getClassificationRationale();
      }

      String sOutput;
      if (c.options.bgTrinaryMode) {
         sOutput = iPos + " " + iNeg + " " + iTrinary + sRationale;
      } else if (c.options.bgScaleMode) {
         sOutput = iPos + " " + iNeg + " " + iScale + sRationale;
      } else {
         sOutput = iPos + " " + iNeg + sRationale;
      }

      if (c.options.bgForceUTF8) {
         try {
            err.print(new String(sOutput.getBytes("UTF-8"), "UTF-8"));
         } catch (UnsupportedEncodingException var22) {
            err.print("UTF-8 Not found on your system!");
            var22.printStackTrace();
         }
      } else {
         err.print(sOutput);
      }
   }

   /**
    * 在指定端口上监听并接受来自客户端的请求
    * UC-14
    * @param c 要使用的Corpus对象
    * @param iListenPort 监听的端口号
    * @author ruohao.zhang
    */
   private void listenAtPort(Corpus c, int iListenPort) {
      ServerSocket serverSocket = setupSocket(iListenPort);
      if (serverSocket == null) {
         return;
      }
      String decodedText = "";

      while (true) {
         Socket clientSocket = null;
         try {
            clientSocket = serverSocket.accept();
         } catch (IOException var20) {
            System.err.println("Accept failed at port: " + iListenPort);
            return;
         }

         PrintWriter err;
         try {
            err = new PrintWriter(clientSocket.getOutputStream(), true);
         } catch (IOException var19) {
            System.err.println("IOException clientSocket.getOutputStream " + var19.getMessage());
            var19.printStackTrace();
            return;
         }

         BufferedReader in;
         try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         } catch (IOException var18) {
            System.err.println("IOException InputStreamReader " + var18.getMessage());
            var18.printStackTrace();
            return;
         }

         setupDecodedText(decodedText, in);

         printOutput(c, decodedText, err);

         try {
            err.close();
            in.close();
            clientSocket.close();
         } catch (IOException var21) {
            System.err.println("IOException closing streams or sockets" + var21.getMessage());
            var21.printStackTrace();
         }
      }
   }

   /**
    * 在用户输入错误的情况输出简短的帮助信息，帮助用户确定如何使用。
    * @author ruohao.zhang
    */
   private void showBriefHelp() {
      System.err.println();
      System.err.println("====" + this.corpus.options.sgProgramName + "Brief Help====");
      System.err.println("For most operations, a minimum of two parameters must be set");
      System.err.println("1) folder location for the linguistic files");
      System.err.println("   e.g., on Windows: C:/mike/Lexical_Data/");
      System.err.println("   e.g., on Mac/Linux/Unix: /usr/Lexical_Data/");
      if (this.corpus.options.bgTensiStrength) {
         System.err.println("TensiiStrength_Data can be downloaded from...[not completed yet]");
      } else {
         System.err.println("SentiStrength_Data can be downloaded with the Windows version of SentiStrength from sentistrength.wlv.ac.uk");
      }

      System.err.println();
      System.err.println("2) text to be classified or file name of texts to be classified");
      System.err.println("   e.g., To classify one text: text love+u");
      System.err.println("   e.g., To classify a file of texts: input /bob/data.txt");
      System.err.println();
      System.err.println("Here is an example complete command:");
      if (this.corpus.options.bgTensiStrength) {
         System.err.println("java -jar TensiStrength.jar sentidata C:/a/Stress_Data/ text am+stressed");
      } else {
         System.err.println("java -jar SentiStrength.jar sentidata C:/a/SentStrength_Data/ text love+u");
      }

      System.err.println();
      if (!this.corpus.options.bgTensiStrength) {
         System.err.println("To list all commands: java -jar SentiStrength.jar help");
      }

   }

   /**
    * 输出如何使用命令行
    * @author ruohao.zhang
    */
   private void printCommandLineOptions() {
      System.err.println("====" + this.corpus.options.sgProgramName + " Command Line Options====");
      System.err.println("=Source of data to be classified=");
      System.err.println(" text [text to process] OR");
      System.err.println(" input [filename] (each line of the file is classified SEPARATELY");
      System.err.println("        May have +ve 1st col., -ve 2nd col. in evaluation mode) OR");
      System.err.println(" annotateCol [col # 1..] (classify text in col, result at line end) OR");
      System.err.println(" textCol, idCol [col # 1..] (classify text in col, result & ID in new file) OR");
      System.err.println(" inputFolder  [foldername] (all files in folder will be *annotated*)");
      System.err.println(" outputFolder [foldername where to put the output (default: folder of input)]");
      System.err.println(" resultsExtension [file-extension for output (default _out.txt)]");
      System.err.println("  fileSubstring [text] (string must be present in files to annotate)");
      System.err.println("  Ok to overwrite files [overwrite]");
      System.err.println(" listen [port number to listen at - call http://127.0.0.1:81/text]");
      System.err.println(" cmd (wait for stdin input, write to stdout, terminate on input: @end");
      System.err.println(" stdin (read from stdin input, write to stdout, terminate when stdin finished)");
      System.err.println(" wait (just initialise; allow calls to public String computeSentimentScores)");
      System.err.println("=Linguistic data source=");
      System.err.println(" sentidata [folder for " + this.corpus.options.sgProgramName + " data (end in slash, no spaces)]");
      System.err.println("=Options=");
      System.err.println(" keywords [comma-separated list - " + this.corpus.options.sgProgramMeasuring
              + " only classified close to these]");
      System.err.println("   wordsBeforeKeywords [words to classify before keyword (default 4)]");
      System.err.println("   wordsAfterKeywords [words to classify after keyword (default 4)]");
      System.err.println(" trinary (report positive-negative-neutral classifcation instead)");
      System.err.println(" binary (report positive-negative classifcation instead)");
      System.err.println(" scale (report single -4 to +4 classifcation instead)");
      System.err.println(" emotionLookupTable [filename (default: EmotionLookupTable.txt)]");
      System.err.println(" additionalFile [filename] (domain-specific terms and evaluations)");
      System.err.println(" lemmaFile [filename] (word tab lemma list for lemmatisation)");
      System.err.println("=Classification algorithm parameters=");
      System.err.println(" noBoosters (ignore sentiment booster words (e.g., very))");
      System.err.println(" noNegators (don't use negating words (e.g., not) to flip sentiment) -OR-");
      System.err.println(" noNegatingPositiveFlipsEmotion (don't use negating words to flip +ve words)");
      System.err.println(" bgNegatingNegativeNeutralisesEmotion (negating words don't neuter -ve words)");
      System.err.println(" negatedWordStrengthMultiplier (strength multiplier when negated (default=0.5))");
      System.err.println(" negatingWordsOccurAfterSentiment (negate "
              + this.corpus.options.sgProgramMeasuring + " occurring before negatives)");
      System.err.println("  maxWordsAfterSentimentToNegate (max words "
              + this.corpus.options.sgProgramMeasuring + " to negator (default 0))");
      System.err.println(" negatingWordsDontOccurBeforeSentiment (don't negate "
              + this.corpus.options.sgProgramMeasuring + " after negatives)");
      System.err.println("   maxWordsBeforeSentimentToNegate (max from negator to "
              + this.corpus.options.sgProgramMeasuring + " (default 0))");
      System.err.println(" noIdioms (ignore idiom list)");
      System.err.println(" questionsReduceNeg (-ve sentiment reduced in questions)");
      System.err.println(" noEmoticons (ignore emoticon list)");
      System.err.println(" exclamations2 (sentence with ! counts as +2 if otherwise neutral)");
      System.err.println(" minPunctuationWithExclamation (min punctuation with ! to boost term "
              + this.corpus.options.sgProgramMeasuring + ")");
      System.err.println(" mood [-1,0,1] (default 1: -1 assume neutral emphasis is neg, 1, assume is pos");
      System.err.println(" noMultiplePosWords (multiple +ve words don't increase "
              + this.corpus.options.sgProgramPos + ")");
      System.err.println(" noMultipleNegWords (multiple -ve words don't increase "
              + this.corpus.options.sgProgramNeg + ")");
      System.err.println(" noIgnoreBoosterWordsAfterNegatives (don't ignore boosters after negating words)");
      System.err.println(" noDictionary (don't try to correct spellings using the dictionary)");
      System.err.println(" noMultipleLetters (don't use additional letters in a word to boost "
              + this.corpus.options.sgProgramMeasuring + ")");
      System.err.println(" noDeleteExtraDuplicateLetters (don't delete extra duplicate letters in words)");
      System.err.println(" illegalDoubleLettersInWordMiddle [letters never duplicate in word middles]");
      System.err.println("    default for English: ahijkquvxyz (specify list without spaces)");
      System.err.println(" illegalDoubleLettersAtWordEnd [letters never duplicate at word ends]");
      System.err.println("    default for English: achijkmnpqruvwxyz (specify list without spaces)");
      System.err.println(" sentenceCombineAv (average " + this.corpus.options.sgProgramMeasuring
              + " strength of terms in each sentence) OR");
      System.err.println(" sentenceCombineTot (total the " + this.corpus.options.sgProgramMeasuring
              + " strength of terms in each sentence)");
      System.err.println(" paragraphCombineAv (average " + this.corpus.options.sgProgramMeasuring
              + " strength of sentences in each text) OR");
      System.err.println(" paragraphCombineTot (total the " + this.corpus.options.sgProgramMeasuring
              + " strength of sentences in each text)");
      System.err.println("  *the default for the above 4 options is the maximum, not the total or average");
      System.err.println(" negativeMultiplier [negative total strength polarity multiplier, default 1.5]");
      System.err.println(" capitalsBoostTermSentiment (" + this.corpus.options.sgProgramMeasuring + " words in CAPITALS are stronger)");
      System.err.println(" alwaysSplitWordsAtApostrophes (e.g., t'aime -> t ' aime)");
      System.err.println(" MinSentencePosForQuotesIrony [integer] quotes in +ve sentences indicate irony");
      System.err.println(" MinSentencePosForPunctuationIrony [integer] +ve ending in !!+ indicates irony");
      System.err.println(" MinSentencePosForTermsIrony [integer] irony terms in +ve sent. indicate irony");
      System.err.println(" MinSentencePosForAllIrony [integer] all of the above irony terms");
      System.err.println(" lang [ISO-639 lower-case two-letter langauge code] set processing language");
      System.err.println("=Input and Output=");
      System.err.println(" explain (explain classification after results)");
      System.err.println(" echo (echo original text after results [for pipeline processes])");
      System.err.println(" UTF8 (force all processing to be in UTF-8 format)");
      System.err.println(" urlencoded (input and output text is URL encoded)");
      System.err.println("=Advanced - machine learning [1st input line ignored]=");
      System.err.println(" termWeights (list terms in badly classified texts; must specify inputFile)");
      System.err.println(" optimise [Filename for optimal term strengths (eg. EmotionLookupTable2.txt)]");
      System.err.println(" train (evaluate " + this.corpus.options.sgProgramName + " by training term strengths on results in file)");
      System.err.println("   all (test all option variations rather than use default)");
      System.err.println("   numCorrect (optimise by # correct - not total classification difference)");
      System.err.println("   iterations [number of 10-fold iterations] (default 1)");
      System.err.println("   minImprovement [min. accuracy improvement to change "
              + this.corpus.options.sgProgramMeasuring + " weights (default 1)]");
      System.err.println("   multi [# duplicate term strength optimisations to change "
              + this.corpus.options.sgProgramMeasuring + " weights (default 1)]");
   }

   /**
    * getCorpus方法
    * @return Corpus SentiStrength的成员
    * @author ruohao.zhang
    */
   public Corpus getCorpus() {
      return this.corpus;
   }
}

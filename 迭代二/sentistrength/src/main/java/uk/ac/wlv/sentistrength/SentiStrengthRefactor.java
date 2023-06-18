package uk.ac.wlv.sentistrength;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;

import java.io.File;

public class SentiStrengthRefactor {

    Corpus corpus = new Corpus();
    public static void main(String[] args) {
        String text = "hey witch wat cha been up too";
//        String filePath = "/Users/seedoilz/Codes/sentistrength/src/main/resources/test.txt";
//        File file = new File(filePath);
        Corpus corpus = new Corpus();
        corpus.initialise();
        corpus.options.bgExplainClassification = false;
        corpus.options.bgEchoText = false;
        analyzeText(corpus, text);
    }
    public static String analyzeText(Corpus corpus, String text) {
        int iPos = 1;
        int iNeg = 1;
        int iTrinary = 0;
        int iScale = 0;
        Paragraph paragraph = new Paragraph();
        paragraph.setParagraph(text, corpus.resources, corpus.options);
        iNeg = paragraph.getParagraphNegativeSentiment();
        iPos = paragraph.getParagraphPositiveSentiment();
        iTrinary = paragraph.getParagraphTrinarySentiment();
        iScale = paragraph.getParagraphScaleSentiment();
        String sRationale = "";
        if (corpus.options.bgEchoText) {
            sRationale = " " + text;
        }

        if (corpus.options.bgExplainClassification) {
            sRationale = " " + paragraph.getClassificationRationale();
        }

        String sOutput = "";
        if (corpus.options.bgTrinaryMode) {
            sOutput = iPos + " " + iNeg + " " + iTrinary + sRationale;
        } else if (corpus.options.bgScaleMode) {
            sOutput = iPos + " " + iNeg + " " + iScale + sRationale;
        } else {
            sOutput = iPos + " " + iNeg + sRationale;
        }

//        if (bURLEncodedOutput) {
//            try {
//                System.err.println(URLEncoder.encode(sOutput, "UTF-8"));
//            } catch (UnsupportedEncodingException var13) {
//                var13.printStackTrace();
//            }
//        } else if (corpus.options.bgForceUTF8) {
//            try {
//                System.err.println(new String(sOutput.getBytes("UTF-8"), "UTF-8"));
//            } catch (UnsupportedEncodingException var12) {
//                System.err.println("UTF-8 Not found on your system!");
//                var12.printStackTrace();
//            }
//        } else {
//            System.err.println(sOutput);
//        }
        System.err.println(sOutput);
        return sOutput;
    }

    public static String analyzeFile(Corpus corpus, File file) {
        String sOutputFile = "./tmp/" + file.getName().substring(0, file.getName().length() - 4) + "_err.txt";
        corpus.classifyAllLinesInInputFile(file, -1, sOutputFile);
        return sOutputFile;
    }

    public static String machineLearning(Corpus corpus, String sInputFile, boolean bDoAll, int iMinImprovement,
                                       boolean bUseTotalDifference, int iIterations, int iMultiOptimisations) {
        SentiStrength sentiStrength = new SentiStrength();
        String outputPath = "./tmp/material_err.txt";
        sentiStrength.runMachineLearning(corpus, sInputFile, bDoAll, iMinImprovement,
                bUseTotalDifference, iIterations, iMultiOptimisations, outputPath);
        return outputPath;
    }

    public void parseOptionsForCorpus(String[] checkedOptions) {
        for (int i = 0; i < checkedOptions.length; ++i) {
            try {
                if (checkedOptions[i].equalsIgnoreCase("sentidata")) {
                    this.corpus.resources.sgSentiStrengthFolder = checkedOptions[i + 1];
                }

                if (checkedOptions[i].equalsIgnoreCase("emotionlookuptable")) {
                    this.corpus.resources.sgSentimentWordsFile = checkedOptions[i + 1];
                }

                if (checkedOptions[i].equalsIgnoreCase("additionalfile")) {
                    this.corpus.resources.sgAdditionalFile = checkedOptions[i + 1];
                }

                if (checkedOptions[i].equalsIgnoreCase("keywords")) {
                    this.corpus.options.parseKeywordList(checkedOptions[i + 1].toLowerCase());
                }

                if (checkedOptions[i].equalsIgnoreCase("wordsBeforeKeywords")) {
                    this.corpus.options.igWordsToIncludeBeforeKeyword = Integer.parseInt(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("wordsAfterKeywords")) {
                    this.corpus.options.igWordsToIncludeAfterKeyword = Integer.parseInt(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("sentiment")) {
                    this.corpus.options.nameProgram(false);
                }

                if (checkedOptions[i].equalsIgnoreCase("stress")) {
                    this.corpus.options.nameProgram(true);
                }

                if (checkedOptions[i].equalsIgnoreCase("trinary")) {
                    this.corpus.options.bgTrinaryMode = true;
                }

                if (checkedOptions[i].equalsIgnoreCase("binary")) {
                    this.corpus.options.bgBinaryVersionOfTrinaryMode = true;
                    this.corpus.options.bgTrinaryMode = true;
                }

                if (checkedOptions[i].equalsIgnoreCase("scale")) {
                    this.corpus.options.bgScaleMode = true;
                    if (this.corpus.options.bgTrinaryMode) {
//                        System.err.println("Must choose binary/trinary OR scale mode");
                        return;
                    }
                }

                ClassificationOptions var10000;
                if (checkedOptions[i].equalsIgnoreCase("sentenceCombineAv")) {
                    var10000 = this.corpus.options;
                    this.corpus.options.getClass();
                    var10000.igEmotionSentenceCombineMethod = 1;
                }

                if (checkedOptions[i].equalsIgnoreCase("sentenceCombineTot")) {
                    var10000 = this.corpus.options;
                    this.corpus.options.getClass();
                    var10000.igEmotionSentenceCombineMethod = 2;
                }

                if (checkedOptions[i].equalsIgnoreCase("paragraphCombineAv")) {
                    var10000 = this.corpus.options;
                    this.corpus.options.getClass();
                    var10000.igEmotionParagraphCombineMethod = 1;
                }

                if (checkedOptions[i].equalsIgnoreCase("paragraphCombineTot")) {
                    var10000 = this.corpus.options;
                    this.corpus.options.getClass();
                    var10000.igEmotionParagraphCombineMethod = 2;
                }

                if (checkedOptions[i].equalsIgnoreCase("negativeMultiplier")) {
                    this.corpus.options.fgNegativeSentimentMultiplier = Float.parseFloat(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("noBoosters")) {
                    this.corpus.options.bgBoosterWordsChangeEmotion = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("noNegatingPositiveFlipsEmotion")) {
                    this.corpus.options.bgNegatingPositiveFlipsEmotion = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("noNegatingNegativeNeutralisesEmotion")) {
                    this.corpus.options.bgNegatingNegativeNeutralisesEmotion = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("noNegators")) {
                    this.corpus.options.bgNegatingWordsFlipEmotion = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("noIdioms")) {
                    this.corpus.options.bgUseIdiomLookupTable = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("questionsReduceNeg")) {
                    this.corpus.options.bgReduceNegativeEmotionInQuestionSentences = true;
                }

                if (checkedOptions[i].equalsIgnoreCase("noEmoticons")) {
                    this.corpus.options.bgUseEmoticons = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("exclamations2")) {
                    this.corpus.options.bgExclamationInNeutralSentenceCountsAsPlus2 = true;
                }

                if (checkedOptions[i].equalsIgnoreCase("minPunctuationWithExclamation")) {
                    this.corpus.options.igMinPunctuationWithExclamationToChangeSentenceSentiment = Integer.parseInt(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("mood")) {
                    this.corpus.options.igMoodToInterpretNeutralEmphasis = Integer.parseInt(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("noMultiplePosWords")) {
                    this.corpus.options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("noMultipleNegWords")) {
                    this.corpus.options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("noIgnoreBoosterWordsAfterNegatives")) {
                    this.corpus.options.bgIgnoreBoosterWordsAfterNegatives = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("noDictionary")) {
                    this.corpus.options.bgCorrectSpellingsUsingDictionary = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("noDeleteExtraDuplicateLetters")) {
                    this.corpus.options.bgCorrectExtraLetterSpellingErrors = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("illegalDoubleLettersInWordMiddle")) {
                    this.corpus.options.sgIllegalDoubleLettersInWordMiddle = checkedOptions[i + 1].toLowerCase();
                }

                if (checkedOptions[i].equalsIgnoreCase("illegalDoubleLettersAtWordEnd")) {
                    this.corpus.options.sgIllegalDoubleLettersAtWordEnd = checkedOptions[i + 1].toLowerCase();
                }

                if (checkedOptions[i].equalsIgnoreCase("noMultipleLetters")) {
                    this.corpus.options.bgMultipleLettersBoostSentiment = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("negatedWordStrengthMultiplier")) {
                    this.corpus.options.fgStrengthMultiplierForNegatedWords = Float.parseFloat(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("maxWordsBeforeSentimentToNegate")) {
                    this.corpus.options.igMaxWordsBeforeSentimentToNegate = Integer.parseInt(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("negatingWordsDontOccurBeforeSentiment")) {
                    this.corpus.options.bgNegatingWordsOccurBeforeSentiment = false;
                }

                if (checkedOptions[i].equalsIgnoreCase("maxWordsAfterSentimentToNegate")) {
                    this.corpus.options.igMaxWordsAfterSentimentToNegate = Integer.parseInt(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("negatingWordsOccurAfterSentiment")) {
                    this.corpus.options.bgNegatingWordsOccurAfterSentiment = true;
                }

                if (checkedOptions[i].equalsIgnoreCase("alwaysSplitWordsAtApostrophes")) {
                    this.corpus.options.bgAlwaysSplitWordsAtApostrophes = true;
                }

                if (checkedOptions[i].equalsIgnoreCase("capitalsBoostTermSentiment")) {
                    this.corpus.options.bgCapitalsBoostTermSentiment = true;
                }

                if (checkedOptions[i].equalsIgnoreCase("lemmaFile")) {
                    this.corpus.options.bgUseLemmatisation = true;
                    this.corpus.resources.sgLemmaFile = checkedOptions[i + 1];
                }

                if (checkedOptions[i].equalsIgnoreCase("MinSentencePosForQuotesIrony")) {
                    this.corpus.options.igMinSentencePosForQuotesIrony = Integer.parseInt(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("MinSentencePosForPunctuationIrony")) {
                    this.corpus.options.igMinSentencePosForPunctuationIrony = Integer.parseInt(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("MinSentencePosForTermsIrony")) {
                    this.corpus.options.igMinSentencePosForTermsIrony = Integer.parseInt(checkedOptions[i + 1]);
                }

                if (checkedOptions[i].equalsIgnoreCase("MinSentencePosForAllIrony")) {
                    this.corpus.options.igMinSentencePosForTermsIrony = Integer.parseInt(checkedOptions[i + 1]);
                    this.corpus.options.igMinSentencePosForPunctuationIrony = this.corpus.options.igMinSentencePosForTermsIrony;
                    this.corpus.options.igMinSentencePosForQuotesIrony = this.corpus.options.igMinSentencePosForTermsIrony;
                }

                if (checkedOptions[i].equalsIgnoreCase("explain")) {
                    this.corpus.options.bgExplainClassification = true;
                }

                if (checkedOptions[i].equalsIgnoreCase("echo")) {
                    this.corpus.options.bgEchoText = true;
                }

                if (checkedOptions[i].equalsIgnoreCase("UTF8")) {
                    this.corpus.options.bgForceUTF8 = true;

                }

            } catch (NumberFormatException var5) {
//                System.err.println("Error in argument for " + checkedOptions[i] + ". Integer expected!");
                return;
            } catch (Exception var6) {
//                System.err.println("Error in argument for " + checkedOptions[i] + ". Argument missing?");
                return;
            }
        }
    }
}

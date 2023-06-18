package nju.sentistrength.project;

import uk.ac.wlv.sentistrength.*;
import uk.ac.wlv.sentistrength.Corpus;
import uk.ac.wlv.utilities.FileOps;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class SentiStrengthWeb {

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
//                System.out.println(URLEncoder.encode(sOutput, "UTF-8"));
//            } catch (UnsupportedEncodingException var13) {
//                var13.printStackTrace();
//            }
//        } else if (corpus.options.bgForceUTF8) {
//            try {
//                System.out.println(new String(sOutput.getBytes("UTF-8"), "UTF-8"));
//            } catch (UnsupportedEncodingException var12) {
//                System.out.println("UTF-8 Not found on your system!");
//                var12.printStackTrace();
//            }
//        } else {
//            System.out.println(sOutput);
//        }
        System.out.println(sOutput);
        return sOutput;
    }

    public static String analyzeFile(Corpus corpus, File file) {
        String sOutputFile = "./tmp/" + file.getName().substring(0,file.getName().length()-4) + "_out.txt";
        corpus.classifyAllLinesInInputFile(file, -1, sOutputFile);
        return sOutputFile;
    }

    public static String machineLearning(Corpus corpus, String sInputFile, boolean bDoAll, int iMinImprovement,
                                       boolean bUseTotalDifference, int iIterations, int iMultiOptimisations){
        SentiStrength sentiStrength = new SentiStrength();
        String outputPath = "./tmp/material_out.txt";
        sentiStrength.runMachineLearning(corpus, sInputFile, bDoAll, iMinImprovement,
                bUseTotalDifference, iIterations, iMultiOptimisations, outputPath);
        return outputPath;
    }
}

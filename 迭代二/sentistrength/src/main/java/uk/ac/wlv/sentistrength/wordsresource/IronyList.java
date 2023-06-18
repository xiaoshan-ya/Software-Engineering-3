// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   IronyList.java

package uk.ac.wlv.sentistrength.wordsresource;

//import java.util.logging.Logger;
import java.io.*;
//import org.apache.log4j.Logger;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

/**
 * 该类表示讽刺语列表，提供初始化列表，和判断术语是否讽刺语的方法
 *
 */
public class IronyList implements WordResource {
    /**
     * 讽刺语字符串数组
     */
    private String[] sgIronyTerm;

    /**
     * 列表中讽刺语当前数量
     */
    private int igIronyTermCount;

    /**
     * 列表中最大讽刺语容量
     */
    private int igIronyTermMax;

    /**
     * 构造一个空的讽刺语列表
     */
    public IronyList() {
        igIronyTermCount = 0;
        igIronyTermMax = 0;
    }

    /**
     * 判断输入的术语是否是讽刺语
     * @param term 要检查的术语
     * @return 是讽刺语返回true，反之返回false
     */
    public boolean termIsIronic(String term) {
        int iIronyTermCount = Sort.i_FindStringPositionInSortedArray(term, sgIronyTerm, 1, igIronyTermCount);
        return iIronyTermCount >= 0;
    }
    /**
     * 使用输入的源文件路径、分类选项，初始化讽刺语列表
     * @param sSourceFile 包含讽刺语的源文件路径
     * @param options 一个ClassificationOptions类型的分类选项
     * @return 初始化成功则返回true，反之则返回false
     */
    public boolean initialise(String sSourceFile, ClassificationOptions options) {
        if (igIronyTermCount > 0) {
            return true;
        }
        File f = new File(sSourceFile);
        if (!f.exists()) {
            return true;
        }
        try {
            igIronyTermMax = FileOps.i_CountLinesInTextFile(sSourceFile) + 2;
            igIronyTermCount = 0;
            String[] sIronyTermTemp = new String[igIronyTermMax];
            sgIronyTerm = sIronyTermTemp;
            BufferedReader rReader;
            if (options.bgForceUTF8) {
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSourceFile), "UTF8"));
            } else {
                rReader = new BufferedReader(new FileReader(sSourceFile));
            }
            String sLine;
            while ((sLine = rReader.readLine()) != null) {
                if (!"".equals(sLine)) {
                    String[] sData = sLine.split("\t");
                    if (sData.length > 0) {
                        sgIronyTerm[++igIronyTermCount] = sData[0];
                    }
                }
            }
            rReader.close();
        } catch (FileNotFoundException e) {
            //System.err.println((new StringBuilder("Could not find IronyTerm file: ")).append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            //System.err.println((new StringBuilder("Found IronyTerm file but could not read from it: ")).append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        }
        Sort.quickSortStrings(sgIronyTerm, 1, igIronyTermCount);
        return true;
    }
}

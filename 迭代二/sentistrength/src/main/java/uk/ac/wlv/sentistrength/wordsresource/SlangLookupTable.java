package uk.ac.wlv.sentistrength.wordsresource;

import java.io.*;


import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

/**
 * 该类表示缩写词列表
 *
 */
public class SlangLookupTable implements WordResource {
    /**
     * 缩写词数组
     */
    private String[] sgSlangLookupTable;

    /**
     * 列表中缩写词数量
     */
    private int igSlangLookupTableCount;

    /**
     * 列表中能存最大缩写词容量
     */
    private int igSlangLookupTableMax;

    /**
     * 构造一个缩写词列表
     */
    public SlangLookupTable() {
        igSlangLookupTableCount = 0;
        igSlangLookupTableMax = 0;
    }

    /**
     * 使用输入的文件名、分类选项，初始化缩写词列表
     * @param sFilename 文件名
     * @param options 一个ClassificationOptions类型的分类选项
     * @return 初始化成功返回true，反之返回false
     */
    public boolean initialise(String sFilename, ClassificationOptions options) {
        boolean flag = true;
        if (igSlangLookupTableMax > 0) {
            flag = true;
        }
        File f = new File(sFilename);
        if (!f.exists()) {
            System.err.println((new StringBuilder("Could not find the SlangLookupTable file: ")).append(sFilename).toString());
            flag = false;
        }
        igSlangLookupTableMax = FileOps.i_CountLinesInTextFile(sFilename) + 2;
        sgSlangLookupTable = new String[igSlangLookupTableMax];
        igSlangLookupTableCount = 0;
        try {
            BufferedReader rReader;
            if (options.bgForceUTF8) {
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFilename), "UTF8"));
            } else {
                rReader = new BufferedReader(new FileReader(sFilename));
            }
            String sLine;
            while ((sLine = rReader.readLine()) != null) {
                if (!"".equals(sLine)) {
                    igSlangLookupTableCount++;
                    sgSlangLookupTable[igSlangLookupTableCount] = sLine;
                }
            }
            rReader.close();
            Sort.quickSortStrings(sgSlangLookupTable, 1, igSlangLookupTableCount);
        } catch (FileNotFoundException e) {
            System.err.println((new StringBuilder("Could not find SlangLookupTable file: ")).append(sFilename).toString());
            e.printStackTrace();
            flag = false;
        } catch (IOException e) {
            System.err.println((new StringBuilder("Found SlangLookupTable file but could not read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 判断输入词是否为缩写词
     * @param sWord 要判断的单词
     * @return 是缩写词则返回true，反之返回false
     */
    public boolean slangLookupTable(String sWord) {
        return Sort.i_FindStringPositionInSortedArray(sWord, sgSlangLookupTable, 1, igSlangLookupTableCount) >= 0;
    }
}

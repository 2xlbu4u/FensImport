package gov.faa.fens;

import java.io.BufferedWriter;

public class FileType
{
    public static final int BLOCK_SIZE = 3000;

    public String UrlPattern;
    public String HeaderCsv;
    public String PatternCsv;
    public String PatternJson;
    public String Record;
    public StringBuffer Sb = new StringBuffer();

    public int BlockCounter = BLOCK_SIZE;
    public String Hostname;
    public BufferedWriter outWriter = null;

    protected FileType(){}
}

package gov.faa.fens;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;

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

    public static void setInFileType(DataRecordSet dataRecordSet)
    {
        String[] header = dataRecordSet.InRows.get(0);
        if (header[0].equals("Timestamp") && header[1].equals("SrcPort"))
        {
            dataRecordSet.InFileType = new XenaFileType();
        }
        else if (header[0].equals("timestamp") && header[1].equals("mix"))
        {
            dataRecordSet.InFileType = new ChariotMixFileType();
        }
        else if (header[0].equals("WAN name") && header[1].equals("Type"))
        {
            dataRecordSet.InFileType = new WanQualityFileType();
        }
    }
    public void ExportData(DataRecordSet dataRecordSet) throws Exception
    {
        DataManager.exportFile(dataRecordSet);
    }
    public void SetOutRecordHeader()
    {

    }
}

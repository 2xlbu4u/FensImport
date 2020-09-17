package gov.faa.fens;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataRecordSet
{
    public String ProcessType;
    public String OutFileTypeStr;
    public String Filename;
    public String Hostname;
    public File RootFolder;
    public List<String[]> Rows = new ArrayList<>();
    public FileType OutFileType;
    public BufferedWriter OutWriter;
    public int[] OldNewseqerr = new int[]{0};
 //   public FileType OutFileTypePing;
 //   public FileType OutFileTypeXena;
 //   public FileType OutFileTypeException;
 //   public FileType OutFileTypeHisto;
 //   public FileType OutFileTypePLoss;


    public DataRecordSet(){}
    public DataRecordSet(DataRecordSet dataRecordSet)
    {
        ProcessType = dataRecordSet.ProcessType;
        OutFileTypeStr = dataRecordSet.OutFileTypeStr;
        Filename = dataRecordSet.Filename;
        Hostname = dataRecordSet.Hostname;
        RootFolder = dataRecordSet.RootFolder;
        Rows = dataRecordSet.Rows;
        OutFileType = dataRecordSet.OutFileType;
        OutWriter = dataRecordSet.OutWriter;
        OldNewseqerr = dataRecordSet.OldNewseqerr;
    }

};




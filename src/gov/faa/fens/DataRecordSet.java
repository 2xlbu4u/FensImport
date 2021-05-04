package gov.faa.fens;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataRecordSet
{
    public String ProcessType;
    public String Technology;
    public String Testname;
    public String OutFileTypeStr;
    public String InFileTypeStr;
    public String Filename;
    public String Hostname;
    public File RootFolderOrFile;
    public List<String> InRows = new ArrayList<>();
    public List<String> OutRows = new ArrayList<>();
    public FileType InFileType;
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
        Testname = dataRecordSet.Testname;
        Technology = dataRecordSet.Technology;
        ProcessType = dataRecordSet.ProcessType;
        OutFileTypeStr = dataRecordSet.OutFileTypeStr;
        InFileTypeStr = dataRecordSet.InFileTypeStr;
        Filename = dataRecordSet.Filename;
        Hostname = dataRecordSet.Hostname;
        RootFolderOrFile = dataRecordSet.RootFolderOrFile;
        InFileType = dataRecordSet.InFileType;
        OutFileType = dataRecordSet.OutFileType;
        OutWriter = dataRecordSet.OutWriter;
    }
    public void ExportData() throws Exception
    {
        OutFileType.FormatDataRows(this);
    }
    public void SetOutRecordHeader()
    {
        OutFileType.SetOutRecordHeader();
    }
};




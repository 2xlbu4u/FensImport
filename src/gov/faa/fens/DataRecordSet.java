package gov.faa.fens;

import java.util.ArrayList;
import java.util.List;

public class DataRecordSet
{
    public String Filename;
    public List<String[]> Rows = new ArrayList<>();
    public FileType InFileType;

    public FileType OutFileTypePing = new PingFileType();
    public FileType OutFileTypeXena = new XenaFileType();
    public FileType OutFileTypeDB = new DBFileType();

    public int[] OldNewseqerr = new int[]{0};
};




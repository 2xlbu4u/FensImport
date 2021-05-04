package gov.faa.fens;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

public class FileType
{
    public static final int BLOCK_SIZE = 3000;

    protected String UrlPattern;
    protected String HeaderCsv;
    protected String PatternCsv;
    protected String PatternJson;
   // public String Record;
    public StringBuffer Sb = new StringBuffer();

    public int BlockCounter = BLOCK_SIZE;
    public String Hostname;
    public BufferedWriter outWriter = null;

    protected FileType(){}

    public static void setInFileType(DataRecordSet dataRecordSet)
    {
        String[] header = dataRecordSet.InRows.get(0).split(",");

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
    public void FormatDataRows(DataRecordSet dataRecordSet) throws Exception
    {
        throw new Exception("No ExportData Override");
        //DataManager.exportFile(dataRecordSet);
    }
    public void SetOutRecordHeader()
    {

    }
    protected Map<String, String> getPortMap(DataRecordSet dataRecordSet)
    {
        Map<String, String> portMap = null;

        if (dataRecordSet.Filename.contains("BL2a"))
        {
            portMap = MapManager.portMapA;
        }
        else if (dataRecordSet.Filename.contains("BL2b"))
        {
            portMap = MapManager.portMapB;
        }
        else
        {
            portMap = MapManager.portMapC;
        }
        return portMap;
    }
    protected Map<String, String> getPortReMap(DataRecordSet dataRecordSet)
    {
        Map<String, String> portReMap = null;
        if (dataRecordSet.Filename.contains("BL2a"))
        {
            portReMap = MapManager.portReMapA;
        }
        else if (dataRecordSet.Filename.contains("BL2b"))
        {
            portReMap = MapManager.portReMapB;
        }
        else
        {
            portReMap = MapManager.portReMapC;
        }
        return portReMap;
    }

    protected static int floatToInt(String floatstr)
    {
        String[] floatstrSplit = floatstr.split("\\.");
        int intVal = floatstr.equals("error") || floatstr.equals("timeout") || floatstr.equals("timedout") || floatstr.equals("unreach") ? -1 : Integer.parseInt(floatstrSplit[0]);
        if (floatstrSplit.length > 1 && Integer.parseInt(floatstrSplit[1]) >= 5)
            intVal++;
        return intVal;
    }
}

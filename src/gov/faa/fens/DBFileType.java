package gov.faa.fens;

import java.io.BufferedWriter;

public class DBFileType
{
    public static String HeaderCsv = "timestamp,dayofweek,ip,srctodest,bytes,rtt,ttl";
    public static String PatternCsv = "\"%s\",%d,\"%s\",\"%s\",%d,%d,%d";
    public static String PatternJson = "{\"index\":{\"_id\":\"%s\"}}\n{\"timestamp\":\"%s\",\"time\":\"%s\",\"dayofweek\":\"%d\",\"year\":\"%d\",\"month\":\"%d\",\"day\":\"%d\",\"hour\":\"%d\",\"minute\":\"%d\",\"second\":\"%d\"," +
            "\"ip\":\"%s\",\"srctodest\":\"%s\",\"bytes\":\"%d\",\"rtt\":\"%d\",\"ttl\":\"%d\"}\n";
    //    _urlPattern = "/pingpr/_bulk?pretty";
}
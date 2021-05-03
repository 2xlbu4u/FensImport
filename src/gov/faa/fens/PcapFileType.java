package gov.faa.fens;

public class PcapFileType extends FileType
{
    PcapFileType()
    {

        PatternJson = "{\"index\":{\"_id\":\"%s\"}}\n{\"timestamp\":\"%s\",\"srcname\":\"%s\",\"destname\":\"%s\",\"srctodest\":\"%s\",\"packetbytes\":\"%s\"}\n";
        UrlPattern = "/pcap/_bulk?pretty";
        HeaderCsv = "timestamp,srcname,destname,srctodest,packetbytes";
        PatternCsv = "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"";
       // PatternInout = "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"";
    }
}

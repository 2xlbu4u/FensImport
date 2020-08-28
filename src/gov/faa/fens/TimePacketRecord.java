package gov.faa.fens;

public class TimePacketRecord
{
    public String TimeBucket;
    public String PacketCount;

    TimePacketRecord(String timeBucket, String packetCount)
    {
        TimeBucket = timeBucket;
        PacketCount = packetCount;
    }
}

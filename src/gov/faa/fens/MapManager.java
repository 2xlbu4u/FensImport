package gov.faa.fens;

import java.util.HashMap;
import java.util.Map;


public class MapManager
{
   // public static MapManager Inst = new MapManager();

    public static final Map<String, String> monthMap = new HashMap<>();

    public static final Map<String, String> portMapA = new HashMap<>();
    public static final Map<String, String> portMapB = new HashMap<>();
    public static final Map<String, String> portMapC = new HashMap<>();

    public static final Map<String, String> portReMapA = new HashMap<>();
    public static final Map<String, String> portReMapB = new HashMap<>();
    public static final Map<String, String> portReMapC = new HashMap<>();

    static
    {
        // Maps

        // BL2a
        portMapA.put("P-0-3-0","Cisco 2800");
        portMapA.put("P-0-3-1","Digi D2");
        portMapA.put("P-0-3-2","Digi D3");
        portMapA.put("P-0-3-3","Pepwave P4");
        portMapA.put("P-0-3-4","Pepwave P5");
        portMapA.put("P-0-3-5","Pepwave B305");
        portMapA.put("P-1-5-0","Digi D1");
        portMapA.put("P-1-5-1","CradlePoint CP5");
        portMapA.put("P-1-5-2","Digi D1");
        portMapA.put("P-1-5-3","Pepwave P1");
        portMapA.put("P-1-5-4","Pepwave P2");
        portMapA.put("P-1-5-5","Pepwave P2");
        portMapA.put("P-1-5-6","CradlePoint CP6");

        // BL2b
        portMapB.put("P-0-3-0","Cisco 2800");
        portMapB.put("P-0-3-1","Digi D2");
        portMapB.put("P-0-3-2","Digi D3");
        portMapB.put("P-0-3-3","Pepwave P4");
        portMapB.put("P-0-3-4","Pepwave P5");
        portMapB.put("P-0-3-5","Pepwave B305");
        portMapB.put("P-1-5-0","Digi D1");
        portMapB.put("P-1-5-1","Pepwave P6");
        portMapB.put("P-1-5-2","CradlePoint CP1");
        portMapB.put("P-1-5-3","CradlePoint CP5");
        portMapB.put("P-1-5-4","Pepwave P1");
        portMapB.put("P-1-5-5","Pepwave P2");

        // BL2c
        portMapC.put("P-0-3-0","Cisco 2800");
        portMapC.put("P-0-3-1","Digi D2");
        portMapC.put("P-0-3-2","Digi D3");
        portMapC.put("P-0-3-3","Pepwave P4");
        portMapC.put("P-0-3-4","Pepwave P5");
        portMapC.put("P-0-3-5","Pepwave B305");
        portMapC.put("P-1-5-0","Digi D1");
        portMapC.put("P-1-5-1","Pepwave P6");
        portMapC.put("P-1-5-2","CradlePoint CP1");
        portMapC.put("P-1-5-3","CradlePoint CP5");
        portMapC.put("P-1-5-4","Pepwave P1");
        portMapC.put("P-1-5-5","Pepwave P2");

        // BL2a
        portReMapA.put("Cisco 2800 to CradlePoint CP5","BL2a-2 I2OKC AT&T");
        portReMapA.put("CradlePoint CP5 to Cisco 2800","BL2a-2 OKC2I AT&T");
        portReMapA.put("Cisco 2800 to Digi D3","BL2a-3 I2ACY Vz");
        portReMapA.put("Digi D3 to Cisco 2800","BL2a-3 ACY2I Vz");
        portReMapA.put("Pepwave B305 to Pepwave P4","BL2a-4 I2ACY T-Mobile");
        portReMapA.put("Pepwave P4 to Pepwave B305","BL2a-4 ACY2I T-Mobile");
        portReMapA.put("Pepwave B305 to Pepwave P5","BL2a-5 I2ACY T-Mobile unencrypted");
        portReMapA.put("Pepwave P5 to Pepwave B305","BL2a-5 ACY2I T-Mobile unencrypted");
        portReMapA.put("Digi D2 to Digi D1","BL2a-6 ACY2OKC AT&T");
        portReMapA.put("Digi D1 to Digi D2","BL2a-6 OKC2ACY AT&T");
        portReMapA.put("Digi D3 to CradlePoint CP6","BL2a-6e ACY2OKC Vz");
        portReMapA.put("CradlePoint CP6 to Digi D3","BL2a-6e OKC2ACY Vz");
        portReMapA.put("Pepwave P4 to Pepwave P2","BL2a-6e ACY2OKC T-Mobile");
        portReMapA.put("Pepwave P2 to Pepwave P4","BL2a-6e OKC2ACY T-Mobile");
        portReMapA.put("Pepwave P1 to Pepwave P2","BL2a-7 OKC2OKC AT&T to T-Mobile");
        portReMapA.put("Pepwave P2 to Pepwave P1","BL2a-7 OKC2OKC T-Mobile to AT&T");
        portReMapA.put("ACY to OKC","BL2a-8 Internet");

        // BL2b
        portReMapB.put("Cisco 2800 to Digi D1","BL2b-2 I2OKC AT&T");
        portReMapB.put("Digi D1 to Cisco 2800","BL2b-2 OKC2I AT&T");
        portReMapB.put("Cisco 2800 to Digi D3","BL2b-3 I2ACY Vz");
        portReMapB.put("Digi D3 to Cisco 2800","BL2b-3 ACY2I Vz");
        portReMapB.put("Pepwave B305 to Pepwave P6","BL2b-4 I2OKC T-Mobile");
        portReMapB.put("Pepwave P6 to Pepwave B305","BL2b-4 OKC2I T-Mobile");
        portReMapB.put("CradlePoint CP1 to CradlePoint CP5","BL2b-6 OKC2OKC Verizon to AT&T");
        portReMapB.put("CradlePoint CP5 to CradlePoint CP1","BL2b-6 OKC2OKC AT&T to Verizon");
        portReMapB.put("Pepwave P1 to Pepwave P2","BL2b-7 OKC2OKC AT&T to T-Mobile");
        portReMapB.put("Pepwave P2 to Pepwave P1","BL2b-7 OKC2OKC T-Mobile to AT&T");
        portReMapB.put("ACY to OKC","BL2b-8 Internet");
        portReMapB.put("Pepwave B305 to Pepwave P4","BL2b-9 I2ACY T-Mobile");
        portReMapB.put("Pepwave P4 to Pepwave B305","BL2b-9 ACY2I T-Mobile");
        portReMapB.put("Pepwave B305 to Pepwave P5","BL2b-10 I2ACY T-Mobile unencrypted");
        portReMapB.put("Pepwave P5 to Pepwave B305","BL2b-10 ACY2I T-Mobile unencrypted");

        // BL2c
/*      portReMapC.put("Pepwave B305 to Pepwave P4","BL2c-1a I2ACY T-Mobile");
        portReMapC.put("Pepwave P4 to Pepwave B305","BL2c-1a ACY2I T-Mobile");
        portReMapC.put("Pepwave B305 to Pepwave P5","BL2c-1b I2ACY T-Mobile unencrypted");
        portReMapC.put("Pepwave P5 to Pepwave B305","BL2c-1b ACY2I T-Mobile unencrypted");

        portReMapC.put("Pepwave B305 to Pepwave P4","BL2c-2a I2ACY T-Mobile");
        portReMapC.put("Pepwave P4 to Pepwave B305","BL2c-2a ACY2I T-Mobile");
        portReMapC.put("Pepwave B305 to Pepwave P5","BL2c-2b I2ACY T-Mobile unencrypted");
        portReMapC.put("Pepwave P5 to Pepwave B305","BL2c-2b ACY2I T-Mobile unencrypted");

        portReMapC.put("Pepwave B305 to Pepwave P4","BL2c-3a I2ACY T-Mobile");
        portReMapC.put("Pepwave P4 to Pepwave B305","BL2c-3a ACY2I T-Mobile");
        portReMapC.put("Pepwave B305 to Pepwave P5","BL2c-3b I2ACY T-Mobile");
        portReMapC.put("Pepwave P5 to Pepwave B305","BL2c-3b ACY2I T-Mobile");*/

        portReMapC.put("Pepwave P4 to Pepwave P5","BL2c-4 P42P5 ACY T-Mobile to T-Mobile");
        portReMapC.put("Pepwave P5 to Pepwave P4","BL2c-4 P52P4 ACY T-Mobile to T-Mobile");
        portReMapC.put("CradlePoint CP1 to CradlePoint CP5","BL2c-5 CP12CP5 OKC Verizon to Verizon");
        portReMapC.put("CradlePoint CP5 to CradlePoint CP1","BL2c-5 CP52CP1 OKC Verizon to Verizon");
        portReMapC.put("Pepwave P1 to Pepwave P2","BL2c-6 P12P2 OKC AT&T to AT&T");
        portReMapC.put("Pepwave P2 to Pepwave P1","BL2c-6 P22P1 OKC AT&T to AT&T");
        portReMapC.put("ACY to OKC","BL2c-8 Internet");

 /*       portReMapC.put("Cisco 2800 to Digi D1","BL2b-2 I2OKC AT&T");
        portReMapC.put("Digi D1 to Cisco 2800","BL2b-2 OKC2I AT&T");
        portReMapC.put("Cisco 2800 to Digi D3","BL2b-3 I2ACY Vz");
        portReMapC.put("Digi D3 to Cisco 2800","BL2b-3 ACY2I Vz");
        portReMapC.put("Pepwave B305 to Pepwave P6","BL2b-4 I2OKC T-Mobile");
        portReMapC.put("Pepwave P6 to Pepwave B305","BL2b-4 OKC2I T-Mobile");
        portReMapC.put("CradlePoint CP1 to CradlePoint CP5","BL2b-6 OKC2OKC Verizon to AT&T");
        portReMapC.put("CradlePoint CP5 to CradlePoint CP1","BL2b-6 OKC2OKC AT&T to Verizon");
        portReMapC.put("Pepwave P1 to Pepwave P2","BL2b-7 OKC2OKC AT&T to T-Mobile");
        portReMapC.put("Pepwave P2 to Pepwave P1","BL2b-7 OKC2OKC T-Mobile to AT&T");
        portReMapC.put("ACY to OKC","BL2b-8 Internet");
*/
        monthMap.put("Jan", "01");
        monthMap.put("Feb", "02");
        monthMap.put("Mar", "03");
        monthMap.put("Apr", "04");
        monthMap.put("May", "05");
        monthMap.put("Jun", "06");
        monthMap.put("Jul", "07");
        monthMap.put("Aug", "08");
        monthMap.put("Sep", "09");
        monthMap.put("Oct", "10");
        monthMap.put("Nov", "11");
        monthMap.put("Dec", "12");

    }

}

package gov.faa.fens;

import java.io.*;
import java.nio.file.Files;

public class FensImporter
{
    public static void main(String[] args) throws IOException
    {
        DataRecordSet dataRecordSet = new DataRecordSet(){};
        try
        {
            if (args.length < 3)
            {
                System.out.println("\nUsage: java -jar FensImport.jar -tTechnology -nTestName FullPathnameToImportFile\n");
                System.out.println("e.g java -jar FensImport.jar -tLTE -nXena_TR15_P6_Jitter_Re-Run_T-Mobile \"./PostgresImport/Xena_TR15_P6_Jitter_Re-Run_T-Mobile Test_PW_Sierra_20210407_085934.csv\"\n");
                System.exit(1);
            }

            for (String arg : args)
            {
                if (arg.startsWith("-t"))
                {
                    dataRecordSet.Technology = arg.substring(2);
                }
                else if (arg.startsWith("-n"))
                {
                    dataRecordSet.Testname = arg.substring(2);
                }
                else
                {
                    dataRecordSet.Filename = arg;
                }
            }

            dataRecordSet.RootFolderOrFile = dataRecordSet.Filename.toLowerCase().endsWith(".zip") ? UnzipFiles.UnzipAndCopyLocal(dataRecordSet.Filename) : new File(dataRecordSet.Filename);

            if (dataRecordSet.RootFolderOrFile == null)
            {
                System.out.println("Unable to establish root Folder from: " + dataRecordSet.Filename);
                return;
            }

        //    dataRecordSet.OutFileType = MapManager.mapFileType.get(dataRecordSet.OutFileTypeStr);

            String[] splitName = dataRecordSet.Filename.split("\\.");

            dataRecordSet.OutFileTypeStr = splitName[splitName.length-1];

            // Move to the in file type if not csv
            DataManager.ImportFileRows(dataRecordSet);

            FileType.setInFileType(dataRecordSet);

            // Default out is same as in for now (csv)
            dataRecordSet.OutFileType = dataRecordSet.InFileType;

            // Export the file
            dataRecordSet.ExportData();

          //  List<DataRecordSet> recordSetList = DataManager.ProcessInputFiles(dataRecordSet);

            String filePath = dataRecordSet.Filename;
            boolean isExport = dataRecordSet.ProcessType.equals("-x");
            if (isExport)
            {
                String outFileRoot = (filePath.endsWith(".csv")) ? filePath.substring(0, filePath.lastIndexOf("\\")) : dataRecordSet.RootFolderOrFile.getAbsolutePath();
                String outFilename = outFileRoot + "\\outData.txt";

                Files.deleteIfExists(new File(outFilename).toPath());
                dataRecordSet.OutWriter = new BufferedWriter(new FileWriter(outFilename, true));
                dataRecordSet.OutWriter.append(dataRecordSet.OutFileType.HeaderCsv).append("\n");
            }

            if (dataRecordSet.OutFileType instanceof HistoFileType)
            {
                dataRecordSet.OutFileType.FormatDataRows(dataRecordSet);
              //  DataManager.ExportHistogram(dataRecordSet.RootFolderOrFile, recordSetList);
            }
            else
            {
/*                for (DataRecordSet _dataRecordSet : recordSetList)
                {
                    _dataRecordSet.OutWriter =  dataRecordSet.OutWriter;
                    String loadExp = isExport ? "Exporting " : "Loading ";
                    System.out.println(loadExp + _dataRecordSet.Filename);
                    DataManager.OutputData(_dataRecordSet);
                }*/
            }
            System.out.println("\nCompleted");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (dataRecordSet.OutWriter != null)
                dataRecordSet.OutWriter.close();
        }
    }

}
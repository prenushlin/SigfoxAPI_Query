package com.sigfox.support;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Driver {


    public static void main(String []  args) throws IOException {


        System.out.println("Please enter the action option:\n0 - Device Resubscription\n1 - " +
                "Device Locator\n2 - Device Checker\n3 - Pending...");
        // Enter data using BufferReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int input = Integer.parseInt(reader.readLine());
        reader.close();
        switch (input)
        {
            case 0:

                DeviceResubscribe ds = new DeviceResubscribe();
                ds.run();
                appendOutput(ds.getJobResponse());
                break;

            case 1:
                    DeviceFinder df = new DeviceFinder();
                    df.run();
                    break;

            case 2:
                DeviceChecker dc = new DeviceChecker();
                dc.run();
                break;

            default: System.out.println("Invalid selection chosen");
                     break;
        }
        reader.close();
    }

    public static void appendOutput(String jobResponse) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String txtInfo="";
        System.out.println("Please Enter the requester");
         txtInfo += "\n"+ reader.readLine();
        System.out.println("Please Enter a small description");
         txtInfo +="\t"+ reader.readLine();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
        LocalDate localDate = LocalDate.now();
         txtInfo+= "\t"+ dtf.format(localDate)+"\t"+jobResponse;

        FileWriter fw = new FileWriter("C:\\Users\\User\\Desktop",true);
        fw.write(txtInfo);
    }
}

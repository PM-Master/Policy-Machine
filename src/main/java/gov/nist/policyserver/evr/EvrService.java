package gov.nist.policyserver.evr;

import java.io.*;

public class EvrService {
    public String createEvr(String scriptName, String source) throws IOException {
        System.out.println(System.getProperty("user.dir"));

        FileOutputStream outputStream = new FileOutputStream(scriptName + ".createEvr");
        byte[] strToBytes = source.getBytes();
        outputStream.write(strToBytes);

        outputStream.close();

        BufferedReader br = null;
        FileReader fr = null;
        try {
            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(scriptName + ".createEvr");
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return scriptName;
    }

    public String update(String scriptName, String source) {
        return null;
    }

    public void enableEvr(String scriptName) {
    }

    public void processSql(String sqlId) {
    }
}

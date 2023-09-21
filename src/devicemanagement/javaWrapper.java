
package devicemanagement;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import static com.sun.jna.Pointer.NULL;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.Structure;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class javaWrapper {
    
    private Pointer hDevice;
    private FTRSCAN_IMAGE_SIZE  ImageSize = new FTRSCAN_IMAGE_SIZE();
    // Class wrapper Enum values
    public enum deviceVersion{
        API_VERSION,
        HARDWARE_VERSION,
        FIRMWARE_VERSION
    }
    public enum keys{
        PRIVATE,
        PUBLIC
    }
    
    // Futronic fs88h structure data types necessary for ftrScanAPI.dll
    public  static class FTRSCAN_IMAGE_SIZE extends Structure{
            public int                                 nWidth;
            public int                                 nHeight;
            public int                                 nImageSize;

             @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("nWidth", "nHeight", "nImageSize");
            }
            public static class ByReference extends FTRSCAN_IMAGE_SIZE implements Structure.ByReference {}
            public static class ByValue extends FTRSCAN_IMAGE_SIZE implements Structure.ByValue {}
        }

    public static class FTRSCAN_VERSION extends Structure {
        public short wMajorVersionHi;
        public short wMajorVersionLo;
        public short wMinorVersionHi;
        public short wMinorVersionLo;

          @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("wMajorVersionHi", "wMajorVersionLo", "wMinorVersionHi","wMinorVersionLo");

            }

        public static class ByReference extends FTRSCAN_VERSION implements Structure.ByReference {}
        public static class ByValue extends FTRSCAN_VERSION implements Structure.ByValue {}

    }

    public static class FTRSCAN_VERSION_INFO extends Structure {
        public long dwVersionInfoSize;
        public FTRSCAN_VERSION APIVersion;
        public FTRSCAN_VERSION HardwareVersion;
        public FTRSCAN_VERSION FirmwareVersion;

        public FTRSCAN_VERSION_INFO() {
            dwVersionInfoSize = size();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwVersionInfoSize", "APIVersion", "HardwareVersion","FirmwareVersion");
        }

        public static class ByReference extends FTRSCAN_VERSION_INFO implements Structure.ByReference {}
        public static class ByValue extends FTRSCAN_VERSION_INFO implements Structure.ByValue {}
    }

    // Under progress
   
    public static class FTRSCAN_FAKE_REPLICA_PARAMETERS extends Structure{
        public boolean                            bCalculated;
        public int                                 nCalculatedSum1;
        public int                                 nCalculatedSumFuzzy;
        public int                                 nCalculatedSumEmpty;
        public int                                 nCalculatedSum2;
        public double                              dblCalculatedTremor;
        public double                              dblCalculatedValue;
         
        protected List<String> getFieldOrder() {
            return Arrays.asList("bCalculated", "nCalculatedSum1", "nCalculatedSumFuzzy","nCalculatedSumEmpty","nCalculatedSum2","dblCalculatedTremor","dblCalculatedValue");

        }
    }
    public static class FTRSCAN_FRAME_PARAMETERS extends Structure{
        public int                                 nContrastOnDose2;
        public int                                 nContrastOnDose4;
        public int                                 nDose;
        public int                                 nBrightnessOnDose1;
        public int                                 nBrightnessOnDose2;
        public int                                 nBrightnessOnDose3;
        public int                                 nBrightnessOnDose4;
        FTRSCAN_FAKE_REPLICA_PARAMETERS            FakeReplicaParams;
        
        public static class ByReference extends FTRSCAN_FRAME_PARAMETERS implements Structure.ByReference {}
        public static class ByValue extends FTRSCAN_FRAME_PARAMETERS implements Structure.ByValue {}
   
        protected List<String> getFieldOrder() {
            return Arrays.asList("nContrastOnDose2", "nContrastOnDose4", "nDose","nBrightnessOnDose1","nBrightnessOnDose2","nBrightnessOnDose3","nBrightnessOnDose4");

        }
    }
   
// Prototyping functions found in the ftrScanAPI.dll
    public interface dllFunction extends StdCallLibrary{       
        dllFunction INSTANCE = (dllFunction)Native.load("ftrScanAPI",dllFunction.class);

        //ftrScanAPI functions
        boolean ftrScanSetDiodesStatus( Pointer ftrHandle, byte byGreenDiodeStatus, byte byRedDiodeStatus );
        Pointer ftrScanOpenDevice();    
        void ftrScanCloseDevice(Pointer hDevice);   
        boolean ftrScanGetSerialNumber(Pointer hDevice, byte[] pBuffer); //00000185
        boolean ftrScanGetImageSize(Pointer hDevice,FTRSCAN_IMAGE_SIZE FTRSCAN_IMAGE_SIZE);
        boolean ftrScanGetImage(Pointer hDevice,int nDose, byte[] pBuffer);
        boolean ftrScanGetVersion(Pointer hDevice, FTRSCAN_VERSION_INFO VERSION_INFO);  
        boolean  ftrScanRestoreExtMemory( Pointer hDevice, byte[] pBuffer, int nOffset, int nCount );
        boolean ftrScanRestore7Bytes(Pointer hDevice,byte[] pBuffer);
        boolean ftrScanSave7Bytes( Pointer hDevice, byte[] pBuffer );
        boolean ftrScanSaveExtMemory(Pointer hDevice,byte[] pBuffer,int nOffset, int nCount);
        boolean ftrScanIsFingerPresent(Pointer hDevice, Pointer FrameParameters);
    }
    /*public static void main(String[] args) {
        FTRSCAN_FRAME_PARAMETERS FrameParameters = new FTRSCAN_FRAME_PARAMETERS();
        Pointer hDevice    = dllFunction.INSTANCE.ftrScanOpenDevice();
        
        if(dllFunction.INSTANCE.ftrScanIsFingerPresent(hDevice, NULL)) System.out.println("Finger Present");
        else if(!dllFunction.INSTANCE.ftrScanIsFingerPresent(hDevice, NULL))System.out.println("Finger Not present");
    }*/
    public static void writeFile(byte[] fileBuffer) throws FileNotFoundException{
        FileOutputStream fos = new FileOutputStream("test.RAW");
           try {
               fos.write(fileBuffer);
           } catch (IOException ex) {
               Logger.getLogger(javaWrapper.class.getName()).log(Level.SEVERE, null, ex);
           }
        try {
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(javaWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        }

    // Java wrapper functions over the native dll functions
    // Open a device handler to the fs88H
    public boolean openDevice(){
        hDevice = dllFunction.INSTANCE.ftrScanOpenDevice();
        return hDevice!= NULL;
    }

    // Close the connection to device fs88H
    public boolean closeDevice(){
        if( hDevice != NULL ){
            dllFunction.INSTANCE.ftrScanCloseDevice(hDevice);
            hDevice = NULL;
        }
            return true;
    }

    //Check if Finger is present 
    public boolean isFingerPresent(){
        if(hDevice == NULL)
            return false;
        
        if(dllFunction.INSTANCE.ftrScanIsFingerPresent(hDevice, NULL)) return true;
        
        return false;
    }
    
    // Get firmware , hardware , APIversion of the model fs88H
    public String getVersionInfo(deviceVersion deviceVersiontype){
        if( hDevice == NULL )return null;

        FTRSCAN_VERSION_INFO infoVersion = new FTRSCAN_VERSION_INFO();
        dllFunction.INSTANCE.ftrScanGetVersion(hDevice, infoVersion);

        String APIVersion = infoVersion.APIVersion.wMajorVersionHi +"."+
                            infoVersion.APIVersion.wMajorVersionLo +"."+
                            infoVersion.APIVersion.wMinorVersionHi +"."+
                            infoVersion.APIVersion.wMinorVersionLo ;

        String FirmwareVersion = infoVersion.FirmwareVersion.wMajorVersionHi+"";
        if(infoVersion.FirmwareVersion.wMajorVersionLo != (short)0xffff){
            FirmwareVersion += "." + infoVersion.FirmwareVersion.wMajorVersionLo;
        }
        if( infoVersion.FirmwareVersion.wMinorVersionHi != (short)0xffff )
            {
                FirmwareVersion += "." + infoVersion.FirmwareVersion.wMinorVersionHi;
            }
        if( infoVersion.FirmwareVersion.wMinorVersionLo != (short)0xffff )
            {
                FirmwareVersion += "." + infoVersion.FirmwareVersion.wMinorVersionLo;
            }

        String HardwareVersion = infoVersion.HardwareVersion.wMajorVersionHi+"."+
                                infoVersion.HardwareVersion.wMajorVersionLo;

        if( infoVersion.HardwareVersion.wMinorVersionHi != (short)0xffff )
            {
                HardwareVersion += "." + infoVersion.HardwareVersion.wMinorVersionHi;
            }
        if( infoVersion.HardwareVersion.wMinorVersionLo != (short)0xffff )
            {
                HardwareVersion += "." + infoVersion.HardwareVersion.wMinorVersionLo;
            }

        if(deviceVersiontype == deviceVersion.API_VERSION) return APIVersion;
        if(deviceVersiontype == deviceVersion.FIRMWARE_VERSION) return FirmwareVersion;
        if(deviceVersiontype == deviceVersion.HARDWARE_VERSION) return HardwareVersion;

        return null;
        }

    // Get the device manufacturer unique serial number
    public String getSerialNumber(){
        if(hDevice == NULL)
            return null;
         byte[] serialNum = new byte[16];

        if(!dllFunction.INSTANCE.ftrScanGetSerialNumber(hDevice, serialNum))
            return null;
        String serial ="";

        for(byte c : serialNum){
               serial+= (char)c;
           }

        return  serial.replaceAll("\\u0000", ""); 
    }

    // Get necessary Image parameters to capture a frame
    public boolean getImageSize(){
        if(hDevice == NULL)
            return false;

        if(!dllFunction.INSTANCE.ftrScanGetImageSize( hDevice, ImageSize ) )
            return false;

        return true;
    }

    // Capture the image 
    // dose is contrast value of the image 
    public byte[] getImage(int dose){
        if( hDevice == NULL )
            return null;

        // Initialize the image parameters 
        getImageSize();
        byte[] imageBuffer = new byte[ImageSize.nImageSize];

        if(!dllFunction.INSTANCE.ftrScanGetImage(hDevice, dose, imageBuffer)){
            return null;
        }
        return imageBuffer;
    }

    // Check if a public and private key are stored in the EEPROM
    public boolean getKeyState(){
        if(hDevice == NULL){
            return false;
        }

        byte[] status = new byte[7];
        if(!dllFunction.INSTANCE.ftrScanRestore7Bytes(hDevice, status)){
            return false;
        }

        String keyStatus = new String(status, StandardCharsets.UTF_8);

        // PASS - public and private key is found
        // FAIL - no public and private key found
        return keyStatus.equals("PASS");

    }

    // Get the key stored in the EEPROM
    public String getKey(keys keytype){
        if(hDevice == NULL){
            return null;
        }
        byte[] holder = new byte[4048];
        if(!dllFunction.INSTANCE.ftrScanRestoreExtMemory(hDevice, holder, 0, 4048)){
            return null;
        }

        String key = new String(holder, StandardCharsets.UTF_8);
        String publicKeyContent ="";
        String privateKeyContent ="";

        Pattern publicPattern = Pattern.compile("-----BEGIN PUBLIC KEY-----(.*?)-----END PUBLIC KEY-----", Pattern.DOTALL);
        Matcher publicMatcher = publicPattern.matcher(key);

        Pattern privatePattern = Pattern.compile("-----BEGIN PRIVATE KEY-----(.*?)-----END PRIVATE KEY-----", Pattern.DOTALL);
        Matcher privateMatcher = privatePattern.matcher(key);

        if (publicMatcher.find() && privateMatcher.find()) {
            publicKeyContent = publicMatcher.group(1).trim();
            privateKeyContent = privateMatcher.group(1).trim();
            //System.out.println("public key : \n"+publicKeyContent);
            //System.out.println("private key : \n"+ privateKeyContent);
        } else {
            System.out.println("Failed to extract public key content.");
        }



        if(keytype == keys.PRIVATE ) return privateKeyContent;
        else if(keytype == keys.PUBLIC) return publicKeyContent;
        return null;
    }

    // Insert new public and private key values to the EEPROM storage
    public boolean setKey(String key){
         if(hDevice == NULL){
            return false;
        }
        byte[] keyValue = new byte[key.length()];
        keyValue = key.getBytes();

        if(!dllFunction.INSTANCE.ftrScanSaveExtMemory(hDevice, keyValue, 0, key.length())){
            return false;
        }
        return true;
    }

}
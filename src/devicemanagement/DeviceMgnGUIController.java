package devicemanagement;

import com.sun.jdi.connect.spi.Connection;
import static com.sun.jna.Pointer.NULL;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;

/**
 *
 * @author USER
 */
public class DeviceMgnGUIController implements Initializable {

    public static java.sql.Connection con;
    public static Statement st;
    public static ResultSet rs;
    String dateTime = capDate();
    javaWrapper device = new javaWrapper();

    @FXML
    private Label status;
    @FXML
    private Label deviceid;

    @FXML
    private void RegisterDevice(ActionEvent event) throws SQLException, NoSuchAlgorithmException {
        if (!deviceid.getText().equals("")) {
            String query = "SELECT * from futronicdevice where deviceid = '" + deviceid.getText() + "'";
            rs = st.executeQuery(query);
            if (!rs.next()) {
                // Create a keypair generator for the device
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                PublicKey publicKey = keyPair.getPublic();
                PrivateKey privateKey = keyPair.getPrivate();

                // Get a mock public key for mocip
                KeyPairGenerator keyPairGeneratorForMocip = KeyPairGenerator.getInstance("RSA");
                keyPairGeneratorForMocip.initialize(2048);
                KeyPair keyPairForMocip = keyPairGenerator.generateKeyPair();

                PublicKey MocippublicKey = keyPairForMocip.getPublic();

                // Convert the keys to a string with bse64 encoding
                String devicePrivateString = "-----BEGIN PRIVATE KEY-----" + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "-----END PRIVATE KEY-----";
                String devicePublicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
                String mocipPublicKeyString = "-----BEGIN PUBLIC KEY-----" + Base64.getEncoder().encodeToString(MocippublicKey.getEncoded()) + "-----END PUBLIC KEY-----";

                if (device.setKey(devicePrivateString + mocipPublicKeyString)) {
                    query = "INSERT INTO futronicdevice VALUES ('" + deviceid.getText() + "','" + devicePublicKeyString + "','" + dateTime + "')";
                    st.executeUpdate(query);
                    status.setText("DEVICE REGISTERED");
                    deviceid.setText("");
                }

            } else {
                status.setText("Device Already registered");
            }

        } else {
            status.setText("No device connected or Selected, press refresh");
        }
    }

    @FXML
    private void Refresh(ActionEvent event) {
        if (device.openDevice()) {
            deviceid.setText(device.getSerialNumber());
        }else{
            deviceid.setText("");
        }
    }

    private static void connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/managementserverdb", "root", "");
        st = con.createStatement();
        System.out.println("Connected");
    }

  
    public String capDate() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String captureTime = dateFormat.format(now);
        return captureTime;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        try {
            connect();
        } catch (Exception ex) {
            System.out.println("There is error in database connection" + ex);
        }
    }

}

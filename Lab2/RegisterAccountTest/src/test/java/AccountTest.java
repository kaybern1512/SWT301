import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import VoVanDong.AccountService;

import java.io.*;

public class AccountTest {

    private final AccountService accountService = new AccountService();

    @Test
    public void testRegisterAccountsFromCSV() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File inputFile = new File(classLoader.getResource("test-data.csv").getFile());

        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter("UnitTest.csv"))
        ) {
            // Ghi header đầy đủ và đúng định dạng
            writer.write("username,password,email,expected,actual,result,type\n");

            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                String username = parts[0];
                String password = parts[1];
                String email = parts[2];
                boolean expected = Boolean.parseBoolean(parts[3]);

                boolean actual = accountService.registerAccount(username, password, email);
                boolean result = (expected == actual);

                String type = getTestCaseType(username, password, email);

                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                        username, password, email, expected, actual, result ? "PASS" : "FAIL", type));

                Assertions.assertEquals(expected, actual,
                        String.format("Failed for input: %s, %s, %s", username, password, email));
            }
        }
    }

    private String getTestCaseType(String username, String password, String email) {
        boolean isAbnormal = false;
        boolean isBoundary = false;

        if (username == null || username.trim().isEmpty()) {
            isAbnormal = true;
        }
        if (email == null || !email.contains("@")) {
            isAbnormal = true;
        }
        if (password == null || password.length() <= 6) {
            isBoundary = true;
        }

        if (isAbnormal && isBoundary) return "A+B";
        if (isAbnormal) return "A";
        if (isBoundary) return "B";
        return "N";
    }
}


package util;

import java.util.UUID;

public class PNRGenerator {

    public static String generatePNR() {

        String uniqueId = UUID.randomUUID()
                              .toString()
                              .substring(0, 5)
                              .toUpperCase();

        return "PNR-" + uniqueId;
    }
}

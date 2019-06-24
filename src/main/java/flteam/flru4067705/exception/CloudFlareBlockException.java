package flteam.flru4067705.exception;

import java.io.IOException;

public class CloudFlareBlockException extends IOException {

    public CloudFlareBlockException(String message) {
        super(message);
    }

}

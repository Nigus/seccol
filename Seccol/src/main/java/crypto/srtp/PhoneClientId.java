package crypto.srtp;

import android.util.Log;

public class PhoneClientId {

  private boolean isClient;
  private int     clientIdInteger;

  public PhoneClientId(String clientId) {
    String[] clientIdParts = clientId.split(" ");

    if (clientIdParts.length < 2) {
      isClient = false;
      return;
    }

    if (!"RedPhone".equals(clientIdParts[0].trim())) {
      isClient = false;
      return;
    }
    try {
      this.clientIdInteger = Integer.parseInt(clientIdParts[1]);
    } catch (NumberFormatException nfe) {
      Log.w("RedPhoneClientId", nfe);
      this.isClient = false;
    }
    this.isClient = true;
  }

  public boolean isImplicitDh3kVersion() {
    return this.isClient && (this.clientIdInteger == 19 || this.clientIdInteger == 24);
  }
  public boolean isLegacyConfirmConnectionVersion() {
    return this.isClient && this.clientIdInteger < 24;
  }
}

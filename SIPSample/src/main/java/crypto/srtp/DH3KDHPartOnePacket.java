
package crypto.srtp;

import crypto.srtp.retained.RetainedSecretsDerivatives;
import network.RtpPacket;

public class DH3KDHPartOnePacket extends DHPartOnePacket {
  public DH3KDHPartOnePacket(RtpPacket packet) {
    super(packet, DHPacket.DH3K_AGREEMENT_TYPE);
  }

  public DH3KDHPartOnePacket(RtpPacket packet, boolean deepCopy) {
    super(packet, DHPacket.DH3K_AGREEMENT_TYPE, deepCopy);
  }

  public DH3KDHPartOnePacket(HashChain hashChain, byte[] pvr,
                             RetainedSecretsDerivatives retainedSecrets,
                             boolean includeLegacyHeaderBug)
  {
    super(DHPacket.DH3K_AGREEMENT_TYPE, hashChain, pvr, retainedSecrets, includeLegacyHeaderBug);
  }
}

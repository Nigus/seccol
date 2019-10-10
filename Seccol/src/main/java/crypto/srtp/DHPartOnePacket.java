package crypto.srtp;

import crypto.srtp.retained.RetainedSecretsDerivatives;
import network.RtpPacket;

/**
 * DH part one ZRTP handshake packet.
 *
 * @author Moxie Marlinspike
 *
 */

public abstract class DHPartOnePacket extends DHPacket {
  public static final String TYPE = "DHPart1 ";

  public DHPartOnePacket(RtpPacket packet, int agreementType) {
    super(packet, agreementType);
  }

  public DHPartOnePacket(RtpPacket packet, int agreementType, boolean deepCopy) {
    super(packet, agreementType, deepCopy);
  }

  public DHPartOnePacket(int agreementType, HashChain hashChain, byte[] pvr,
                         RetainedSecretsDerivatives retainedSecrets,
                         boolean includeLegacyHeaderBug)
  {
    super(TYPE, agreementType, hashChain, pvr, retainedSecrets, includeLegacyHeaderBug);
  }
}


package crypto.srtp;

import crypto.srtp.retained.RetainedSecretsDerivatives;
import network.RtpPacket;
/**
 * A DHPartOnePacket for the EC25 KA type.
 *
 * @author Moxie Marlinspike
 *
 */

public class EC25DHPartOnePacket extends DHPartOnePacket {

  public EC25DHPartOnePacket(RtpPacket packet) {
    super(packet, DHPacket.EC25_AGREEMENT_TYPE);
  }

  public EC25DHPartOnePacket(RtpPacket packet, boolean deepCopy) {
    super(packet, DHPacket.EC25_AGREEMENT_TYPE, deepCopy);
  }

  public EC25DHPartOnePacket(HashChain hashChain, byte[] pvr,
                             RetainedSecretsDerivatives retainedSecrets,
                             boolean includeLegacyHeaderBug)
  {
    super(DHPacket.EC25_AGREEMENT_TYPE, hashChain, pvr, retainedSecrets, includeLegacyHeaderBug);
    assert(pvr.length == 64);
  }

}

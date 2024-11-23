package dns;

import jdk.jfr.Experimental;

/**
 * https://www.rfc-editor.org/rfc/rfc1035#section-3.2.2
 */
public enum DnsType {
    // host address
    A(1),
    // an authoritative name server
    NS(2),
    // a mail destination
    @Deprecated MD(3),
    // a mail forwarder
    @Deprecated MF(4),
    // the canonical name for an alias
    CNAME(5),
    // marks the start of a zone of authority
    SOA(6),
    // a mailbox domain name
    @Experimental MB(7),
    // a mail group member
    @Experimental MG(8),
    // a mail rename domain name
    @Experimental MR(9),
    // a null RR
    @Experimental NULL(10),
    // a well known service description
    WKS(11),
    // a domain name pointer
    PTR(12),
    // host information
    HINFO(13),
    // mailbox or mail list information
    MINFO(14),
    // mail exchange
    MX(15),
    // text strings
    TXT(16);

    public static final int TYPE_SIZE_BYTES = 2;

    private final int value;

    DnsType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

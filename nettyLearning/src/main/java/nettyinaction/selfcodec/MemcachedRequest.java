package nettyinaction.selfcodec;

import java.util.Random;

public class MemcachedRequest {
	private static final Random random = new Random();
	private final int magic = 0x80; //fixed so harded code
	private final byte opCode; //the operation code e.g. get or set 
	private final String key; //the key to delete set or get
	private final int flags = 0xdeabcdef; // random
	private final int expires;
	private final String body;
	private final int id = random.nextInt();
	private final long cas = 0L;
	private final boolean hasExtras;
	
	public MemcachedRequest(byte opCode, String key, String value) {
		this.opCode = opCode;
		this.key = key;
		this.body = value == null ? "" : value;
		this.expires = 0;
		this.hasExtras = opCode == Opcode.SET;
	}
	
	public MemcachedRequest(byte opCode, String key) {
		this(opCode, key, null);
	}

	public int getMagic() {
		return magic;
	}

	public byte getOpCode() {
		return opCode;
	}

	public String getKey() {
		return key;
	}

	public int getFlags() {
		return flags;
	}

	public int getExpires() {
		return expires;
	}

	public String getBody() {
		return body;
	}

	public int getId() {
		return id;
	}

	public long getCas() {
		return cas;
	}

	public boolean hasExtras() {
		return hasExtras;
	}
	
}

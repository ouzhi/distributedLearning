package nettyinaction.selfcodec;

public class MemcachedResponse {
	private final byte magic;
	private final byte opCode;
	private byte dataType;
	private final short status;
	private final int id;
	private final long cas;
	private final int flags;
	private final int expires;
	private final String key;
	private final String data;
	
	
	public MemcachedResponse(byte magic, byte opCode, 
			byte dataType, short status, int id, 
			long cas, int flags, int expires, 
			String key, String data) {
		this.magic = magic;
		this.opCode = opCode;
		this.dataType = dataType;
		this.status = status;
		this.id = id;
		this.cas = cas;
		this.flags = flags;
		this.expires = expires;
		this.key = key;
		this.data = data;
	}


	public byte getMagic() {
		return magic;
	}


	public byte getOpCode() {
		return opCode;
	}


	public byte getDataType() {
		return dataType;
	}


	public short getStatus() {
		return status;
	}


	public int getId() {
		return id;
	}


	public long getCas() {
		return cas;
	}


	public int getFlags() {
		return flags;
	}


	public int getExpires() {
		return expires;
	}


	public String getKey() {
		return key;
	}


	public String getData() {
		return data;
	}
	
}

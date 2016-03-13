import java.nio.ByteBuffer;
import java.util.Date;
import java.util.zip.CRC32;


public class ChatData {
    private static int USERNAME_LENGTH = 6;
    private static int MESSAGE_LENGTH = 900;
    private static int TIME_LENGTH = Long.BYTES;
    private static int SUM_LENGTH = Long.BYTES;

    private static int USERNAME_OFFSET = 0;
    private static int MESSAGE_OFFSET = USERNAME_OFFSET + USERNAME_LENGTH;
    private static int TIME_OFFSET = MESSAGE_OFFSET + MESSAGE_LENGTH;
    private static int SUM_OFFSET = TIME_OFFSET + TIME_LENGTH;

    private static int BUFFER_LENGTH = 1024;

    private byte[] buffer;
    private CRC32 crc = new CRC32();

    public ChatData(byte[] data) {
        buffer = data;
    }

    public ChatData(String username, String message) {
        buffer = new byte[BUFFER_LENGTH];
        setUsername(username);
        setMessage(message);
        setTime((new Date()).getTime());
        setSum(calculateSum());
    }

    public byte[] getBytes() {
        return buffer;
    }

    public String getUsername() {
        return new String(buffer, USERNAME_OFFSET, strlen(USERNAME_OFFSET, USERNAME_LENGTH));
    }

    public void setUsername(String username) {
        put(username.getBytes(), USERNAME_OFFSET, Math.min(USERNAME_LENGTH, username.length()));
    }

    public String getMessage() {
        return new String(buffer, MESSAGE_OFFSET, strlen(MESSAGE_OFFSET, MESSAGE_LENGTH));
    }

    public void setMessage(String message) {
        put(message.getBytes(), MESSAGE_OFFSET, Math.min(MESSAGE_LENGTH, message.length()));
        int end_of_message = MESSAGE_OFFSET + message.length();
        if(end_of_message < MESSAGE_OFFSET + MESSAGE_LENGTH) {
            buffer[end_of_message] = 0;
        }
    }

    public long getTime() {
        return getLong(TIME_OFFSET);
    }

    public void setTime(long time) {
        putLong(time, TIME_OFFSET);
    }

    public long getSum() {
        return getLong(SUM_OFFSET);
    }

    public void setSum(long sum) {
        putLong(sum, SUM_OFFSET);
    }

    public long calculateSum() {
        crc.reset();
        crc.update(buffer, 0, SUM_OFFSET);
        return crc.getValue();
    }

    public boolean is_valid() {
        return calculateSum() == getSum();
    }


    private void put(byte[] src, int index, int length) {
        for(int i = 0; i < length; i++) {
            buffer[index + i] = src[i];
        }
    }

    private int strlen(int offset, int max_length) {
        int strlen = 0;
        while(strlen < max_length && buffer[offset + strlen] != 0) {
            strlen++;
        }
        return strlen;
    }

    private long getLong(int index) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.put(buffer, index, Long.BYTES);
        byteBuffer.flip();
        return byteBuffer.getLong();
    }

    private void putLong(long value, int index) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(value);
        for(int i = 0; i<Long.BYTES; i++) {
            buffer[i + index] = byteBuffer.array()[i];
        }
    }
}

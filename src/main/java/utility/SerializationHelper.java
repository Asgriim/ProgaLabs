package utility;

import java.io.*;
import java.nio.ByteBuffer;

public class SerializationHelper {
    ByteArrayOutputStream outputStream;
    ObjectOutputStream oos;

    public SerializationHelper() {
        this.outputStream = new ByteArrayOutputStream();
        try {
            this.oos = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] serialize(Serializable obj) throws IOException {
        this.outputStream = new ByteArrayOutputStream();
        this.oos = new ObjectOutputStream(outputStream);
        oos.writeObject(obj);
        oos.flush();
        return outputStream.toByteArray();
    }

    public Object deSerialization(ByteBuffer inpBuf) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(inpBuf.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();

    }

}

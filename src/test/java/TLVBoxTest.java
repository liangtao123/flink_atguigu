import com.teligen.common.TLVTypeEnum;
import com.teligen.common.TlvBox;
import org.junit.Test;

public class TLVBoxTest {
   @Test
   public void testSerialize(){
       TlvBox inner = TlvBox.create();
       inner.put(255, "Inner TlvBox");

       TlvBox box = TlvBox.create()
               .put(0, true)
               .put(1, (byte) 1)
               .put(2, (short) 2)
               .put(3, 3)
               .put(4, (long) 4)
               .put(5, 5.5f)
               .put(6, 6.6)
               .put(7, 'A')
               .put(8, "Hello world!")
               .put(9, new byte[]{3, 4, 6, 3, 9})
               .put(10, inner);
       byte[] bytes = box.serialize();
       System.out.println(111);
   }

   @Test
    public void testParse(){
       TlvBox inner = TlvBox.create();
       inner.put(255, "Inner TlvBox");

       TlvBox box = TlvBox.create()
               .put(0, true)
               .put(1, (byte) 1)
               .put(2, (short) 2)
               .put(3, 3)
               .put(4, (long) 4)
               .put(5, 5.5f)
               .put(6, 6.6)
               .put(7, 'A')
               .put(8, "Hello world!")
               .put(9, new byte[]{3, 4, 6, 3, 9})
               .put(10, inner);
       byte[] bytes = box.serialize();
       TlvBox parsed = TlvBox.parse(bytes);
       Boolean   v0 = parsed.getBoolean(TLVTypeEnum.BOOLEAN.getTypeint());
       Byte      v1 = parsed.getByte(TLVTypeEnum.BYTE.getTypeint());
       Short     v2 = parsed.getShort(TLVTypeEnum.SHORT.getTypeint());
       Integer   v3 = parsed.getInteger(TLVTypeEnum.INTEGER.getTypeint());
       Long      v4 = parsed.getLong(TLVTypeEnum.LONG.getTypeint());
       Float     v5 = parsed.getFloat(TLVTypeEnum.FLOAT.getTypeint());
       Double    v6 = parsed.getDouble(TLVTypeEnum.DOUBLE.getTypeint());
       Character v7 = parsed.getCharacter(TLVTypeEnum.CHAR.getTypeint());
       String    v8 = parsed.getString(TLVTypeEnum.STRING.getTypeint());
       byte[]    v9 = parsed.getBytes(TLVTypeEnum.BYTEARR.getTypeint());
       TlvBox    v10 = parsed.getObject(TLVTypeEnum.INNER.getTypeint());
       System.out.println(111);
   }
}

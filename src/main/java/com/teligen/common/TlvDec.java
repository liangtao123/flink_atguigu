package com.teligen.common;

import cn.hutool.core.util.StrUtil;
import com.payneteasy.tlv.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


public class TlvDec {


    public static void main(String[] args) throws UnsupportedEncodingException {

        BerTlvBuilder berTlvBuilder = new BerTlvBuilder();

        /**
         * 编码
         */
        //1. 编码 这里的Tag要用16进制,Length是自动算出来的,最后是要存的数据
        berTlvBuilder.addText(new BerTag(0x1), "test");

        berTlvBuilder.addText(new BerTag(0x2), "{" +
                "\"name\":\"tlv\"," +
                "\"type\":\"pro\"," +
                "\"add\":\"中文\"" +
                "}");
        //1.1 add value的时候可以选择是text格式或者是data byte[]等等其他都可以
        long timeStamp = System.currentTimeMillis();
        berTlvBuilder.addDate(new BerTag(0x3), new Date(timeStamp));
        //默认是不支持汉字的，所以我们要把他转成字节
        byte[] bytes3 = "{\"name\":\"tlv\",\"type\":\"pro\", \"add\":\"中文\"}".getBytes(StandardCharsets.UTF_8);
        berTlvBuilder.addBytes(new BerTag(0x4), bytes3);

        //1.2 这里就完成参数的输入了，然后将它转成字节
        byte[] bytes = berTlvBuilder.buildArray();
        //转成Hex码来传输
        String hexString = HexUtil.toHexString(bytes);

        /**
         * 解码
         */
        //2. 解码 将hex码转成byte字节
        byte[] bytes2 = HexUtil.parseHex(hexString);
        BerTlvParser parser = new BerTlvParser();
        BerTlvs tlvs = parser.parse(bytes2, 0, bytes2.length);

        //如果value的数据类型都一样的话可以通过getList来获取然后便利输出
        List<BerTlv> list = tlvs.getList();
        for (BerTlv berTlv : list) {
            byte[] bytesValue1 = berTlv.getBytesValue();
            String s = new String(bytesValue1, StandardCharsets.UTF_8);
            System.out.println(StrUtil.format(" {} ", s));
        }


        //也可以指定Tag来获得数据
        BerTlv berTlv = tlvs.find(new BerTag(0x3));
        System.out.println(berTlv.getHexValue());

    }


}

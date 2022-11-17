package com.teligen.flink.cep;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 需求：对于输入的数据流中，匹配所有以 x 或 y 开头的数据
 */
public class CepDemo {
    public static void main(String[] args)throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
//        DataStream<String> sourceStream = env.socketTextStream("localhost", 7777);
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("auto.offset.reset", "latest");
        DataStream<String> sourceStream = env.addSource(new FlinkKafkaConsumer<String>(
                "flink_demo",new SimpleStringSchema(),properties
        ));

        // 使用 where 和 or 来定义两个需求；
        // 当然也可以放在一个 where 里。
        //为满足规则的打上了start标签
//        Pattern<String, String> pattern = Pattern.<String>begin("start").where(new IterativeCondition<String>() {
//            @Override
//            public boolean filter(String s, Context<String> context) throws Exception {
//                return s.startsWith("x");
//            }
//        }).or(new IterativeCondition<String>() {
//            @Override
//            public boolean filter(String s, Context<String> context) throws Exception {
//                return s.startsWith("y");
//            }
//        });

        //过滤出以a开头单词出现三次以上的情况,onesOrMore API，一般要结合utils来终止
//        Pattern<String, String> pattern = Pattern.<String>begin("start").where(new IterativeCondition<String>() {
//            @Override
//            public boolean filter(String s, Context<String> context) throws Exception {
//                return s.startsWith("a");
//            }
//        }).timesOrMore(3);

        //过滤a开头单词连续出现3次的数据，使用times api,支持times(from,to) api.
//        Pattern<String, String> pattern = Pattern.<String>begin("start").where(new IterativeCondition<String>() {
//            @Override
//            public boolean filter(String s, Context<String> context) throws Exception {
//                return s.startsWith("a");
//            }
//        }).times(3);

        //一直有一个讨厌的 b1。由于不满足我们的基本匹配条件，b1 直接被我们的程序忽略掉了。这是因为
        // Flink CEP 默认采用了不严格的匹配模式，
        // 而在某些情况下，这种数据是不能忽略的，这时候就可以使用 consecutive() 函数，指定严格的匹配模式。
//        Pattern<String, String> pattern = Pattern.<String>begin("start").where(new IterativeCondition<String>() {
//            @Override
//            public boolean filter(String s, Context<String> context) throws Exception {
//                return s.startsWith("a");
//            }
//        }).times(3).consecutive();

//        匹配"包含 2-3 个 a 开头的字符串，同时包含 1-2 个 b 开头的字符串"的输入数据
//        Pattern<String, String> pattern = Pattern.<String>begin("start").where(new IterativeCondition<String>() {
//            @Override
//            public boolean filter(String s, Context<String> context) throws Exception {
//                return s.startsWith("a");
//            }
//        }).times(2,3)
//                .followedBy("middle").where(new IterativeCondition<String>() {
//                    @Override
//                    public boolean filter(String s, Context<String> context) throws Exception {
//                        return s.startsWith("b");
//                    }
//                }).times(1,2);
//
        //next看作加强版的folledBy
         Pattern<String, String> pattern = Pattern.<String>begin("start").where(new IterativeCondition<String>() {
            @Override
            public boolean filter(String s, Context<String> context) throws Exception {
                return s.startsWith("a");
            }
        }).times(2,3)
                .next("middle").where(new IterativeCondition<String>() {
                    @Override
                    public boolean filter(String s, Context<String> context) throws Exception {
                        return s.startsWith("b");
                    }
                }).times(1,2);


        // CEP.pattern 的第一个参数是数据流，第二个是规则；
        // 然后利用 select 方法抽取出匹配到的数据。
        // 这里用了 lambda 表达式
        CEP.pattern(sourceStream, pattern).inProcessingTime().select(map ->{
//            Arrays.toString(map.get("start").toArray())
                            List<String> list = map.get("start");
                            list.addAll(map.get("middle"));
                            return Arrays.toString(list.toArray());

            }
        ).
                addSink(new SinkFunction<String>() {
            @Override
            public void invoke(String value, Context context) throws Exception {
                System.out.println(value);
            }
        });
        env.execute();
    }
}


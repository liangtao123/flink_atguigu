package com.teligen.cep;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Arrays;
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
        DataStream<String> sourceStream = env.addSource(new FlinkKafkaConsumer<String>(
                "flink_demo",new SimpleStringSchema(),properties
        ));

        // 使用 where 和 or 来定义两个需求；
        // 当然也可以放在一个 where 里。
        //为满足规则的打上了start标签
        Pattern<String, String> pattern = Pattern.<String>begin("start").where(new IterativeCondition<String>() {
            @Override
            public boolean filter(String s, Context<String> context) throws Exception {
                return s.startsWith("x");
            }
        }).or(new IterativeCondition<String>() {
            @Override
            public boolean filter(String s, Context<String> context) throws Exception {
                return s.startsWith("y");
            }
        });

        // CEP.pattern 的第一个参数是数据流，第二个是规则；
        // 然后利用 select 方法抽取出匹配到的数据。
        // 这里用了 lambda 表达式
        CEP.pattern(sourceStream,pattern).inProcessingTime().select((map->
            Arrays.toString(map.get("start").toArray()))
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


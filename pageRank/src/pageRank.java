import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class pageRank {

    public static class TransitionMapper extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            
            String line = value.toString();            
            line = line.trim();            
            String[] transtion = line.split("\t");
            String from = transtion[0];
            String[] to = transtion[1].split(",");
            double length = (double)1/to.length;
            for(int i = 0; i < to.length; i++){
                context.write(new Text(from), new Text(to[i]+"="+length));
            }
        }
    }

    public static class PRMapper extends
            Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            
            String line = value.toString();            
            line = line.trim();            
            String[] transtion = line.split("\t");
            String from = transtion[0];
            double weight = Double.parseDouble(transtion[1]);
            context.write(new Text(from), new Text(""+weight));
        }
    }   

    public static class PageRankReducer extends
            Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values,
                           Context context) throws IOException, InterruptedException {
            double weight = 0;
            for (Text val : values) {
                String line = val.toString();
                if (line.contains("=")==false){
                    weight = Double.parseDouble(line);
                    break;//remove break make it even worse
                }
            }
            for (Text val : values) {
                String line = val.toString().trim();
                if (line.contains("=")==true){
                    String[] record = line.split("=");
                    String website = record[0];
                    double value = Double.parseDouble(record[1]);
                    context.write(new Text(website), new Text(weight+" "+value));
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = new Job(conf, "page rank");
        job.setJarByClass(pageRank.class);

        ChainMapper.addMapper(job, TransitionMapper.class, Object.class, Text.class, Text.class, Text.class, conf);
        ChainMapper.addMapper(job, PRMapper.class, Object.class, Text.class, Text.class, Text.class, conf);

        //job.setMapperClass(PageRankMapper.class);
        job.setReducerClass(PageRankReducer.class);

        //job.setMapOutputKeyClass(Text.class);
        //job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //job.setInputFormatClass(TextInputFormat.class);
        //job.setOutputFormatClass(TextOutputFormat.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TransitionMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, PRMapper.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        //FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        job.waitForCompletion(true);
    }
}
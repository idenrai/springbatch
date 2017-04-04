package com.test.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

/*
 * １．Jobを実行
 * ２．Listenerを実行
 * ３．Step１を実行
 *    ３−１、Readerでアイテムを読み込む
 *    ３−２、Processorで読み込んだ物を加工
 *    ３−３、Writerで加工したデータを書き込む
 * ４．Step２を実行（内容は3と同様）
 * ５．Listenerを実行
 */
@Configuration
@EnableBatchProcessing
public class Batch {

  // 必要な要素にAutowiredで接続
  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Autowired
  public DataSource dataSource;

  // Reader
  @Bean
  public FlatFileItemReader<Fruit> reader() {

    // readerに「fruit_price.csv」を入れる
    FlatFileItemReader<Fruit> reader = new FlatFileItemReader<Fruit>();
    reader.setResource(new ClassPathResource("fruit_price.csv"));

    // writerに入れてSQLが書けるように配列かし、Fruit Classを付ける
    reader.setLineMapper(new DefaultLineMapper<Fruit>() {{
      setLineTokenizer(new DelimitedLineTokenizer() {{
        setNames(new String[] { "name" , "price" });
      }});
      setFieldSetMapper(new BeanWrapperFieldSetMapper<Fruit>() {{
        setTargetType(Fruit.class);
      }});
    }});
    return reader;
  }

  // Reader2
  @Bean
  public FlatFileItemReader<Fruit> readerTwo() {
    FlatFileItemReader<Fruit> reader = new FlatFileItemReader<Fruit>();
    reader.setResource(new ClassPathResource("fruit_price2.csv"));
    reader.setLineMapper(new DefaultLineMapper<Fruit>() {{
      setLineTokenizer(new DelimitedLineTokenizer() {{
        setNames(new String[] { "name" , "price" });
      }});
      setFieldSetMapper(new BeanWrapperFieldSetMapper<Fruit>() {{
        setTargetType(Fruit.class);
      }});
    }});
    return reader;
  }

  // Processor
  // FruitItemProcessorで読み込んだデータを加工
  @Bean
  public FruitItemProcessor processor() {
    return new FruitItemProcessor();
  }

  // Writer
  @Bean
  public JdbcBatchItemWriter<Fruit> writer() {
    JdbcBatchItemWriter<Fruit> writer = new JdbcBatchItemWriter<Fruit>();
    writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Fruit>());

    // Readerで設定した名前でデータをSQLに読み込ませる
    writer.setSql("INSERT INTO fruit (name, price) VALUES (:name, :price)");
    writer.setDataSource(dataSource);
    return writer;
  }

  @Bean
  // Jobの開始と終了を案内する「JobStartEndListener」を稼働
  public JobExecutionListener listener() {
    return new JobStartEndListener(new JdbcTemplate(dataSource));
  }

  // ステップ１
  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<Fruit,Fruit> chunk(10)
        .reader(reader())
        .processor(processor())
        .writer(writer())
        .build();
  }

  // ステップ２
  @Bean
  public Step step2() {
    return stepBuilderFactory.get("step2")
        .<Fruit,Fruit> chunk(10)
        .reader(readerTwo())
        .processor(processor())
        .writer(writer())
        .build();
  }

  // ジョブ
  @Bean
  public Job testJob() {
    return jobBuilderFactory.get("goodjobKwon")
        .incrementer(new RunIdIncrementer())
        .listener(listener())
        .flow(step1())
        .next(step2())
        .end()
        .build();
  }
}

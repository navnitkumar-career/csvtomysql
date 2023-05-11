package com.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.batch.model.User;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public FlatFileItemReader<User> reader() {
		FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<User>();
		flatFileItemReader.setResource(new ClassPathResource("records.csv"));
		flatFileItemReader.setLineMapper(getLineMapper());
		flatFileItemReader.setLinesToSkip(1);
		return flatFileItemReader;
	}

	private LineMapper<User> getLineMapper() {
		DefaultLineMapper<User> lineMapper = new DefaultLineMapper<User>();

		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames(new String[] { "id", "status", "description" });
		delimitedLineTokenizer.setIncludedFields(new int[] { 0, 2, 3 });

		BeanWrapperFieldSetMapper<User> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<User>();
		beanWrapperFieldSetMapper.setTargetType(User.class);

		lineMapper.setLineTokenizer(delimitedLineTokenizer);
		lineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		return lineMapper;
	}

	@Bean
	public UserItemProcessor processor() {
		return new UserItemProcessor();
	}

	public JdbcBatchItemWriter<User> writer() {
		JdbcBatchItemWriter<User> jdbcBatchItemWriter = new JdbcBatchItemWriter<User>();
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());
		jdbcBatchItemWriter
				.setSql("insert into user(id,status,description) values(:id,:status,:description)");
		jdbcBatchItemWriter.setDataSource(this.dataSource);

		return jdbcBatchItemWriter;
	}

	@Bean
	public Job importUserJob() {
		return this.jobBuilderFactory.get("USER_IMPORT_JOB").incrementer(new RunIdIncrementer()).flow(step()).end()
				.build();
	}

	@Bean
	public Step step() {
		return this.stepBuilderFactory.get("step").<User, User>chunk(10).reader(reader()).processor(processor())
				.writer(writer()).build();
		
	}

}

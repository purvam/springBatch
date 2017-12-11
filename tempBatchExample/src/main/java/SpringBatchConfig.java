import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class SpringBatchConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Value("sample-data.csv")
	private Resource inputCsv;

	@Value("output.csv")
	private Resource outputCsv;

	//TODO: change item reader to cassandra reader
	@Bean
	public ItemReader<Person> itemReader()
			throws UnexpectedInputException, ParseException {
		
		FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		String[] tokens = { "firstName", "lastName" };
		tokenizer.setNames(tokens);

		reader.setResource(inputCsv);

		DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<Person>();
		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
            setTargetType(Person.class);
        }});

		reader.setLineMapper(lineMapper);
				return reader;
				
				
		}

	@Bean
	public PersonItemProcessor itemProcessor() {
	    return new PersonItemProcessor();
	}

	@Bean
	public ItemWriter<Person> itemWriter() {
	    ItemWriter<Person> writer = new FlatFileItemWriter<Person>();
	    
	    //TODO: file had to create first
	    ((FlatFileItemWriter<Person>) writer).setResource(new ClassPathResource("output.csv"));
	    
	    LineAggregator<Person> delLineAgg = new DelimitedLineAggregator<Person>();
		((DelimitedLineAggregator<Person>) delLineAgg).setDelimiter(",");
		FieldExtractor<Person> fieldExtractor = new BeanWrapperFieldExtractor<Person>();
		
		//TODO change the field names
		((BeanWrapperFieldExtractor<Person>) fieldExtractor).setNames(new String[] {"firstName","lastName"});
		((DelimitedLineAggregator<Person>) delLineAgg).setFieldExtractor(fieldExtractor);
		
		((FlatFileItemWriter<Person>) writer).setLineAggregator(delLineAgg);
	    return writer;
	}

	@Bean
	protected Step step1(ItemReader<Person> reader,
		ItemProcessor<Person, Person> processor,
		ItemWriter<Person> writer) {
		return stepBuilderFactory.get("step1")
				.<Person, Person> chunk(10)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}

	@Bean(name = "firstBatchJob")
	public Job job(@Qualifier("step1") Step step1) {
		return jobBuilderFactory.get("firstBatchJob")
				.start(step1)
				.build();	
	}
	
}

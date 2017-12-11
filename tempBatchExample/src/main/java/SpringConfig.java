import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class SpringConfig {
    private JobRepository getJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        
      //TODO: Manish will give datasource object
        //factory.setDataSource(dataSource());
        factory.setDataSource(null);
        factory.setTransactionManager(getTransactionManager());
        factory.afterPropertiesSet();
        return (JobRepository) factory.getObject();
    }
 
  //TODO: not sure which transaction manager will use
    private PlatformTransactionManager getTransactionManager() {
        return new ResourcelessTransactionManager();
    }
 
    public JobLauncher getJobLauncher() throws Exception {
        JobLauncher jobLauncher = new SimpleJobLauncher();
        ((SimpleJobLauncher) jobLauncher).setJobRepository(getJobRepository());
        ((SimpleJobLauncher) jobLauncher).afterPropertiesSet();
        return jobLauncher;
    }
}
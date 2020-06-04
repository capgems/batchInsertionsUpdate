package com.dbtesting.database;

import com.dbtesting.model.Fertilizers;
import com.dbtesting.model.VillageMaster;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
        asyncTaskExecutor.setConcurrencyLimit(50);
        return asyncTaskExecutor;
    }

    @Bean
    public FlatFileItemReader<VillageMaster> villageReader() {
        FlatFileItemReader<VillageMaster> reader = new FlatFileItemReader<VillageMaster>();
        reader.setResource(new ClassPathResource("MasterVillageList.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<VillageMaster>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"Id", "StateCode", "StateName", "DistrictName", "DistrictCode", "SubDistrict", "SubDisCode", "Village", "VillageCode",
                        "Ns", "Ps", "Ks", "pH", "OC", "Bs", "Cus", "Fes", "Mns", "Ss", "Zns"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<VillageMaster>() {{
                setTargetType(VillageMaster.class);
            }});

        }});

        return reader;
    }

    @Bean
    public FlatFileItemReader<Fertilizers> fertilizerReader() {
        FlatFileItemReader<Fertilizers> reader = new FlatFileItemReader<Fertilizers>();
        reader.setResource(new ClassPathResource("FertilizerList.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<Fertilizers>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"Id", "fertilizerSources", "N", "P", "K", "priceBag", "priceCart", "priceTractor",
                        "type"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Fertilizers>() {{
                setTargetType(Fertilizers.class);
            }});

        }});

        return reader;
    }

    @Bean
    public VillageProcessor processor() {
        return new VillageProcessor();
    }

    @Bean
    public FerlilizerProcessor fertilizerProcessor() {
        return new FerlilizerProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<VillageMaster> villageWriter() {
        JdbcBatchItemWriter<VillageMaster> writer = new JdbcBatchItemWriter<VillageMaster>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<VillageMaster>());
        writer.setSql("INSERT INTO villagemaster(id,StateCode,StateName,DistrictName,DistrictCode,subdistrictname,subdistrictcode,villagename,villagecode,Ns,Ps,Ks,pH,OC,Bs,Cus,Fes,Mns,Ss,Zns) VALUES (:Id,:StateCode,:StateName,:DistrictName,:DistrictCode,:SubDistrict,:SubDisCode,:Village,:VillageCode,:Ns,:Ps,:Ks,:pH,:OC,:Bs,:Cus,:Fes,:Mns,:Ss,:Zns)");
        writer.setDataSource(dataSource);

        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<Fertilizers> ferlilizerwriter() {
        JdbcBatchItemWriter<Fertilizers> writer = new JdbcBatchItemWriter<Fertilizers>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Fertilizers>());
        writer.setSql("INSERT INTO fertilizerslist(id,fertilizersources,n,p,k,pricebag,pricecart,pricetractor,type) VALUES (:Id,:fertilizerSources,:N,:P,:K,:priceBag,:priceCart,:priceTractor,:type)");
        writer.setDataSource(dataSource);

        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<VillageMaster> updateVillageWriter() {
        JdbcBatchItemWriter<VillageMaster> writer = new JdbcBatchItemWriter<VillageMaster>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setDataSource(dataSource);
        writer.setSql("update villagemaster set StateCode=:StateCode,StateName=:StateName,DistrictName=:DistrictName where id=:Id");
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<Fertilizers> updateFertilizerWriter() {
        JdbcBatchItemWriter<Fertilizers> writer = new JdbcBatchItemWriter<Fertilizers>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setDataSource(dataSource);
        writer.setSql("update fertilizerslist set fertilizersources=:fertilizerSources,n=:N,p=:P,k=:K where id=:Id");
        return writer;
    }

    @Bean
    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "insert")
    public Step step1() {
        return stepBuilderFactory.get("step1").<VillageMaster, VillageMaster>chunk(5000)
                .reader(villageReader())
                .processor(processor())
                .writer(villageWriter()).taskExecutor(taskExecutor())
                .throttleLimit(50)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "insert")
    public Step step2() {
        return stepBuilderFactory.get("step2").<Fertilizers, Fertilizers>chunk(5000)
                .reader(fertilizerReader())
                .processor(fertilizerProcessor())
                .writer(ferlilizerwriter()).taskExecutor(taskExecutor())
                .throttleLimit(50)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "update")
    public Step step3() {
        return stepBuilderFactory.get("step3").<VillageMaster, VillageMaster>chunk(5000)
                .reader(villageReader())
                .processor(processor())
                .writer(updateVillageWriter()).taskExecutor(taskExecutor())
                .throttleLimit(50)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "update")
    public Step step4() {
        return stepBuilderFactory.get("step4").<Fertilizers, Fertilizers>chunk(5000)
                .reader(fertilizerReader())
                .processor(fertilizerProcessor())
                .writer(updateFertilizerWriter()).taskExecutor(taskExecutor())
                .throttleLimit(50)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "insert")
    public Job insertsJob(JobCompletionListener listener) {
        return jobBuilderFactory.get("InsertsJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(splitFlowInsert())
                .build().build();
    }

    @Bean
    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "update")
    public Job updatesJob(JobCompletionListener listener) {
        return jobBuilderFactory.get("UpdatesJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(splitFlowUpdate())
                .build().build();
    }

    @Bean
    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "insert")
    public Flow splitFlowInsert() {
        return new FlowBuilder<SimpleFlow>("SimpleFlowInsert")
                .split(taskExecutor())
                .add(flow1(),flow2())
                .build();
    }

    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "update")
    @Bean
    public Flow splitFlowUpdate() {
        return new FlowBuilder<SimpleFlow>("SimpleFlowUpdate")
                .split(taskExecutor())
                .add(flow3(),flow4())
                .build();
    }

    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "insert")
    @Bean
    public Flow flow1() {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(step1())
                .build();
    }

    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "insert")
    @Bean
    public Flow flow2() {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(step2())
                .build();
    }

    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "update")
    @Bean
    public Flow flow3() {
        return new FlowBuilder<SimpleFlow>("flow3")
                .start(step3())
                .build();
    }

    @ConditionalOnProperty(name = "config.operation.sql", havingValue = "update")
    @Bean
    public Flow flow4() {
        return new FlowBuilder<SimpleFlow>("flow4")
                .start(step4())
                .build();
    }
}

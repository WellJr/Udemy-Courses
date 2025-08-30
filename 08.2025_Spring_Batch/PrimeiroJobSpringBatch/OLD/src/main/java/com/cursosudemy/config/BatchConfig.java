package com.cursosudemy.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job imprimeOlaJob() {
        return jobBuilderFactory
                .get("imprimeOlaJob")
                .start(imprimeOlaStep()) //Aqui se declaram as etapas
                .build();
    }

    public Step imprimeOlaStep() {
        return stepBuilderFactory
                .get("imprimeOlaStep")
                //informa o tipo das tarefas
                .tasklet(new Tasklet() { // <-- para tarefas simples
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Ola, mundo!");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}

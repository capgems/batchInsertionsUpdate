package com.dbtesting.database;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JobCompletionListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionListener.class);


    @Override
    public void afterJob(JobExecution jobexecution) {
        if (jobexecution.getStatus() == BatchStatus.COMPLETED) {
            Date start = jobexecution.getCreateTime();
            //  get job's end time
            Date end = jobexecution.getEndTime();
            // get diff between end time and start time
            long diff = end.getTime() - start.getTime();
            // log diff time
            log.info("Time taken to complete in seconds {}", TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS));
            log.info("Successful {}", jobexecution.getJobInstance().getJobName());
        } else if (jobexecution.getStatus() == BatchStatus.FAILED) {
            log.info("Failed {}", jobexecution.getJobInstance().getJobName());
        }
    }

}

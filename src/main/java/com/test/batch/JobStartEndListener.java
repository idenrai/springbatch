package com.test.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/*
 * バッチ処理のStart・Endを表示
 */
public class JobStartEndListener extends JobExecutionListenerSupport {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public JobStartEndListener(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  // ステップの開始前に実行
  @Override
  public void beforeJob(JobExecution jobExecution) {
    super.beforeJob(jobExecution);
    System.out.println("Spring Batch Start");
  }

  // ステップの終了後に実行
  @Override
  public void afterJob(JobExecution jobExecution) {
    super.afterJob(jobExecution);
    System.out.println("Spring Batch End");
  }

}

package com.springbatch.jdbcpagingreader.reader;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springbatch.jdbcpagingreader.dominio.Cliente;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Configuration
public class JdbcPagingReaderReaderConfig {
	//appDataSource: nome do método que cria a bean em DataSourceConfig
	@Bean
	public JdbcPagingItemReader<Cliente> jdbcPagingReader(
			@Qualifier("appDataSource")DataSource dataSource,
			PagingQueryProvider queryProvider
			) {
		return new JdbcPagingItemReaderBuilder<Cliente>()
				.name("jdbcPagingReader")
				.dataSource(dataSource)
				.queryProvider(queryProvider)
				.pageSize(1)
				.rowMapper(new BeanPropertyRowMapper<Cliente>(Cliente.class))
				.build();
	}

	@Bean
	public SqlPagingQueryProviderFactoryBean queryProvider(
			@Qualifier("appDataSource") DataSource dataSource) {

		SqlPagingQueryProviderFactoryBean  queryProvider = new SqlPagingQueryProviderFactoryBean();
		queryProvider.setDataSource(dataSource);

		queryProvider.setSelectClause("select *");
		queryProvider.setFromClause("cliente");
		queryProvider.setSortKey("email"); // <-- Define a ordenação

		return queryProvider;

	}
}

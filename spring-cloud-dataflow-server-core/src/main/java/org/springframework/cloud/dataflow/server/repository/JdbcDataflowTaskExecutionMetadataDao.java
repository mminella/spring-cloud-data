/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.server.repository;

import java.io.IOException;
import javax.sql.DataSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.cloud.dataflow.core.TaskDefinition;
import org.springframework.cloud.dataflow.core.TaskManifest;
import org.springframework.cloud.dataflow.server.service.impl.ResourceMixin;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

/**
 * @author Michael Minella
 */
public class JdbcDataflowTaskExecutionMetadataDao implements DataflowTaskExecutionMetadataDao {

	private static final String INSERT_SQL = "INSERT INTO TASK_EXECUTION_METADATA (ID, TASK_EXECUTION_ID, " +
			"TASK_EXECUTION_MANIFEST) VALUES (:id, :taskExecutionId, :taskExecutionManifest)";

	private static final String FIND_LATEST_MANIFEST_BY_NAME = "select m.task_execution_manifest as task_execution_manifest\n" +
			"from task_execution_metadata m inner join\n" +
			"        task_execution e on m.task_execution_id = e.task_execution_id\n" +
			"where e.task_name = :taskName\n" +
			"order by e.task_execution_id desc\n" +
			"limit 0,1;";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final DataFieldMaxValueIncrementer incrementer;

	private final ObjectMapper objectMapper;

	public JdbcDataflowTaskExecutionMetadataDao(DataSource dataSource, DataFieldMaxValueIncrementer incrementer) {
		this.incrementer = incrementer;

		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.objectMapper = new ObjectMapper();
		this.objectMapper.addMixIn(Resource.class, ResourceMixin.class);
		this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Override
	public void save(TaskExecution taskExecution, TaskManifest manifest) {
		try {
			final String manifestJson = this.objectMapper.writeValueAsString(manifest);

			final MapSqlParameterSource queryParameters = new MapSqlParameterSource()
					.addValue("id", incrementer.nextLongValue())
					.addValue("taskExecutionId", taskExecution.getExecutionId())
					.addValue("taskExecutionManifest", manifestJson);

			this.jdbcTemplate.update(INSERT_SQL, queryParameters);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Unable to serialize manifest", e);
		}
	}

	@Override
	public TaskManifest getLastManifest(TaskDefinition taskDefinition) {
		final MapSqlParameterSource queryParameters = new MapSqlParameterSource()
				.addValue("taskName", taskDefinition.getTaskName());

		return this.jdbcTemplate.queryForObject(FIND_LATEST_MANIFEST_BY_NAME,
				queryParameters,
				(resultSet, i) -> {
					try {
						return objectMapper.readValue(resultSet.getString("task_execution_manifest"), TaskManifest.class);
					}
					catch (IOException e) {
						throw new IllegalArgumentException("Unable to deserialize manifest", e);
					}
				});

	}
}

/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.server.db.migration.mysql;

import java.util.Arrays;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import org.springframework.cloud.dataflow.server.db.migration.SqlCommand;
import org.springframework.cloud.dataflow.server.db.migration.SqlCommandsRunner;

/**
 * @author Michael Minella
 */
public class V3__Add_Task_Metadata_Table extends BaseJavaMigration {

	public final static String CREATE_TASK_METADATA_TABLE =
			"CREATE TABLE task_execution_metadata (\n" +
					"    id BIGINT NOT NULL,\n" +
					"    task_execution_id BIGINT NOT NULL,\n" +
					"    task_execution_manifest TEXT,\n" +
					"    primary key (id),\n" +
					"    CONSTRAINT TASK_METADATA_FK FOREIGN KEY (TASK_EXECUTION_ID)\n" +
					"    REFERENCES TASK_EXECUTION(TASK_EXECUTION_ID)\n" +
					")";

	public final static String CREATE_TASK_METADATA_SEQUENCE =
			"CREATE TABLE task_execution_metadata_seq (\n" +
					"  ID BIGINT NOT NULL,\n" +
					"  UNIQUE_KEY CHAR(1) NOT NULL,\n" +
					"  constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)\n" +
					")";

	public final static String INSERT_TASK_METADATA_SEQUENCE =
			"INSERT INTO task_execution_metadata_seq (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from task_execution_metadata_seq)";

	private final SqlCommandsRunner runner = new SqlCommandsRunner();

	@Override
	public void migrate(Context context) throws Exception {
		runner.execute(context.getConnection(), Arrays.asList(
				SqlCommand.from(CREATE_TASK_METADATA_TABLE),
				SqlCommand.from(CREATE_TASK_METADATA_SEQUENCE),
				SqlCommand.from(INSERT_TASK_METADATA_SEQUENCE)));
	}
}

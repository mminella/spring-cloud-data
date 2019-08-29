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
package org.springframework.cloud.dataflow.core;

import java.util.List;

import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;

/**
 * @author Mark Pollack
 */
public class TaskManifest {

	private String dslText;

	private AppDeploymentRequest taskDeploymentRequest;

	private List<AppDeploymentRequest> subTaskDeploymentRequests;

	public String getDslText() {
		return dslText;
	}

	public void setDslText(String dslText) {
		this.dslText = dslText;
	}

	public AppDeploymentRequest getTaskDeploymentRequest() {
		return taskDeploymentRequest;
	}

	public void setTaskDeploymentRequest(AppDeploymentRequest taskDeploymentRequest) {
		this.taskDeploymentRequest = taskDeploymentRequest;
	}

	public List<AppDeploymentRequest> getSubTaskDeploymentRequests() {
		return subTaskDeploymentRequests;
	}

	public void setSubTaskDeploymentRequests(List<AppDeploymentRequest> subTaskDeploymentRequests) {
		this.subTaskDeploymentRequests = subTaskDeploymentRequests;
	}
}

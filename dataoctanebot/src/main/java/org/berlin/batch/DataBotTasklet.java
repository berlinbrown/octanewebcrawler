package org.berlin.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

/**
 * @author acogoluegnes
 *
 */
public class DataBotTasklet implements Tasklet {

	private Resource inputResource;
	private String targetDirectory;
	private String targetFile;

	private static final Logger logger = LoggerFactory.getLogger(DataBotTasklet.class);

	public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
		logger.info("Executing task");
		return RepeatStatus.FINISHED;
	}

	public void setInputResource(Resource inputResource) {
		this.inputResource = inputResource;
	}

	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}

} // End of the class //
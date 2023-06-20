package api.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class POJO {
	private List<OutputParam> outputParameters;
	private List<OutputFiles> outputFiles;

	public List<OutputParam> getOutputParameters() {
		return outputParameters;
	}

	public void setOutputParameters(List<OutputParam> outputParameters) {
		this.outputParameters = outputParameters;
	}

	public List<OutputFiles> getOutputFiles() {
		return outputFiles;
	}

	public void setOutputFiles(List<OutputFiles> outputFiles) {
		this.outputFiles = outputFiles;
	}
	
	

}

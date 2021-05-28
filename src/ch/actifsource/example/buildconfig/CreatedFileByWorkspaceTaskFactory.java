package ch.actifsource.example.buildconfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.annotation.CheckForNull;
import ch.actifsource.core.INode;
import ch.actifsource.core.dependency.IDependency;
import ch.actifsource.generator.AbstractBuildTaskSingleThread;
import ch.actifsource.generator.GenerationException;
import ch.actifsource.generator.IBuildTask;
import ch.actifsource.generator.target.ISingleThreadBuildTargetInfo;
import ch.actifsource.generator.workspacetask.IWorkspaceTaskFactory;
import ch.actifsource.util.ICancelStatus;
import ch.actifsource.util.file.IAsFile;
import ch.actifsource.util.file.IAsFolder;


public class CreatedFileByWorkspaceTaskFactory implements IWorkspaceTaskFactory {
  
  private static final String fFileName = "CreatedFile.txt";
  
  @Override
  public IBuildTask create(INode buildTask, ICancelStatus status) throws GenerationException {
    
    return new AbstractBuildTaskSingleThread(buildTask, status) {
      
      @Override
      @CheckForNull
      protected IDependency internalGenerate(ISingleThreadBuildTargetInfo buildTargetInfo) throws GenerationException {
        IAsFolder targetFolder = buildTargetInfo.getTargetFolder();
        IAsFile file = targetFolder.getFile(fFileName);
        
        try {
          file.write(new ByteArrayInputStream(new byte[] {'1'}));
        } catch (IOException e) {
          throw new GenerationException(e.getMessage());
        }
        return null;
      }
      
    };
  }
  
}

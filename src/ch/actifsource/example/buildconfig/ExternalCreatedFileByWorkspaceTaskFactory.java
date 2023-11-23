package ch.actifsource.example.buildconfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.CheckForNull;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import ch.actifsource.core.INode;
import ch.actifsource.core.dependency.IDependency;
import ch.actifsource.generator.AbstractBuildTaskSingleThread;
import ch.actifsource.generator.GenerationException;
import ch.actifsource.generator.IBuildTask;
import ch.actifsource.generator.console.IGeneratorConsole;
import ch.actifsource.generator.target.ISingleThreadBuildTargetInfo;
import ch.actifsource.generator.workspacetask.IWorkspaceTaskFactory;
import ch.actifsource.util.ICancelStatus;
import ch.actifsource.util.ObjectUtil;
import ch.actifsource.util.file.IAsFile;
import ch.actifsource.util.file.IAsFolder;

public class ExternalCreatedFileByWorkspaceTaskFactory implements IWorkspaceTaskFactory {
  
  private static final String fFileNameCreatedInsideEclipse = "ExternalCreatedFileInsideEclipse.txt";
  private static final String fFileNameCreatedOutsideEclipse = "ExternalCreatedFileOutsideEclipse.txt";
  
  @Override
  public IBuildTask create(INode buildTask, ICancelStatus status) throws GenerationException {
    
    return new AbstractBuildTaskSingleThread(buildTask, status) {
      
      @Override
      @CheckForNull
      protected IDependency internalGenerate(ISingleThreadBuildTargetInfo buildTargetInfo) throws GenerationException {
        IGeneratorConsole console = buildTargetInfo.getBuildContext().console();
        
        try {
          IAsFolder targetFolder = buildTargetInfo.getTargetFolder();
          
          /** Create file inside external library or tool. */
          File externCreatedfile = new File(targetFolder.getUrl().getPath()+"/"+fFileNameCreatedInsideEclipse);
          externCreatedfile.createNewFile();
          
          /** Create file outside the project or workspace. Synchronization of the workspace does not need to be performed, as the case is located outside of eclipse. */
          File outputProjectFolder = buildTargetInfo.getOutputScope().getFolder("").getAdapter(File.class);
          File externalLocation = new File(outputProjectFolder, "../"+fFileNameCreatedOutsideEclipse);
          externalLocation.createNewFile();
          
          /** Read external created file and sync with eclipse workspace */
          IAsFile file = targetFolder.getFile(fFileNameCreatedInsideEclipse);
          syncFileIfNotExists(file, console);
          
          InputStream inputStream = file.getContents();
          try {  
            // TODO some work with external created file
          } finally {
            inputStream.close();          
          }
          
        } catch (IOException e) {
          throw new GenerationException(e.getMessage());
        }
        return null;
      }
      
    };
  }
  
  /**
   * Sync file with eclipse file system
   */
  private static void syncFileIfNotExists(IAsFile file, IGeneratorConsole console) {
    IFile resource = file.getAdapter(IFile.class);
    if (resource.exists()) {
      console.warning().print("Test: "+ObjectUtil.getSimpleName(resource.getClass()) + " " + resource.getName() + " allready exist.\n");
      return;
    }
    if (!resource.isSynchronized(IResource.DEPTH_ZERO)) {
      try {
        resource.refreshLocal(IResource.DEPTH_ZERO, null);
      } catch (CoreException e) {
        throw ch.actifsource.util.Assert.fail(e);
      }
      console.warning().print("Test: "+ObjectUtil.getSimpleName(resource.getClass()) + " " + resource.getName() + " was out of sync with filesystem, refreshed.\n");
    }
  }
  
}

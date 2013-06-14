package ws.antonov.gradle.plugins.protobuf

import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.logging.LogLevel
import org.gradle.util.CollectionUtils
import org.gradle.api.tasks.compile.AbstractCompile;

public class ProtobufCompile extends AbstractCompile {
    @Input
    def includeDirs = []

    @OutputDirectories
    def outputDirs = []

    public String getProtocPath() {
        return null
    }

    public File getDestinationCPPDir() {
        return null
    }

    public File getDestinationPythonDir() {
        return null
    }

    /**
     * Add a directory to protoc's include path.
     */
    public void include(Object dir) {
        if (dir instanceof File) {
            includeDirs += dir
        } else {
            includeDirs += project.file(dir)
        }
    }

    /**
     * Add a directory to Gradle's incremental build output cache
     */
    public void includeOutput(Object dir) {
        if (dir instanceof File) {
            outputDirs += dir
        } else {
            outputDirs += project.file(dir)
        }
    }

    protected void compile() {
        //println "Compiling protos..."
        //println "${sourceSets.main.java.srcDirs}"
        //println project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).protobuf.class
        getDestinationDir().mkdirs()
        def dirs = CollectionUtils.join(" -I", getSource().srcDirs)
        logger.debug "ProtobufCompile using directories ${dirs}"
        logger.debug "ProtobufCompile using files ${getSource().getFiles()}"
        def cmd = [ getProtocPath() ]
        cmd.addAll(getSource().srcDirs*.path.collect {"-I${it}"})
        cmd.addAll(includeDirs*.path.collect {"-I${it}"})
        cmd += "--java_out=${getDestinationDir()}"

        if (getDestinationCPPDir() != null) {
            getDestinationCPPDir().mkdirs()
            includeOutput(getDestinationCPPDir())
            cmd += "--cpp_out=${getDestinationCPPDir()}"
        }

        if (getDestinationPythonDir() != null) {
            getDestinationPythonDir().mkdirs()
            includeOutput(getDestinationPythonDir())
            cmd += "--python_out=${getDestinationPythonDir()}"
        }

        cmd.addAll getSource().getFiles()
        logger.log(LogLevel.INFO, cmd.toString())
        def output = new StringBuffer()
        Process result = cmd.execute()
        result.consumeProcessOutput(output, output)
        result.waitFor()
        if (result.exitValue() == 0) {
            logger.log(LogLevel.INFO, output.toString())
        } else {
            throw new InvalidUserDataException(output.toString())
        }
    }
}

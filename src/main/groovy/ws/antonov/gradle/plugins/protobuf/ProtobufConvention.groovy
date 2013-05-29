package ws.antonov.gradle.plugins.protobuf

import org.gradle.api.Project

class ProtobufConvention {
    def ProtobufConvention(Project project) {
        extractedProtosDir = "${project.buildDir.path}/extracted-protos"
    }

    def String protocPath = "protoc"
    /**
     * Directory to extract proto files into
     */
    def String extractedProtosDir

    /**
     *	Directory to save java files to
     */
    def String generatedFileDir

    /**
     * CPP files
     */
    // Directory to save cpp files to
    def String generatedCPPFileDir

    /**
     * Python files
     */
    // Directory to save python files to
    def String generatedPythonFileDir
}

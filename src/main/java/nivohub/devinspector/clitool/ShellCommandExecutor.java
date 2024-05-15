package nivohub.devinspector.clitool;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class ShellCommandExecutor {
    private final Executor executor;
    private final PipedOutputStream output;
    private final PipedOutputStream error;

    public ShellCommandExecutor() {
        this.executor = new DefaultExecutor();
        this.output = new PipedOutputStream();
        this.error = new PipedOutputStream();
        this.executor.setStreamHandler(new PumpStreamHandler(output, error));
    }

    public void executeCommand(String command) throws IOException {
        CommandLine cmdLine = CommandLine.parse(command);
        executor.execute(cmdLine);
    }

    public PipedInputStream getOutputStream() throws IOException {
        return new PipedInputStream(output);
    }

    public InputStream getErrorStream() throws IOException {
        return new PipedInputStream(error);
    }

    public void close() {
        executor.getWatchdog().destroyProcess();
    }
}
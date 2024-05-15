package nivohub.devinspector.interactor;

import nivohub.devinspector.model.CLModel;
import nivohub.devinspector.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class CLInteractorTest {
    private CLInteractor clInteractor;
    private CLModel clModel;
    private UserModel userModel;

    @BeforeEach
    public void setup() {
        clModel = mock(CLModel.class);
        userModel = mock(UserModel.class);
        clInteractor = new CLInteractor(clModel, userModel);
    }

    @Test
    public void startsCLInteractorSuccessfully() {
        when(userModel.getPlatform()).thenReturn("win");
        clInteractor.start();
        verify(clModel, times(1)).appendOutput("Starting CLI for Windows...");
    }

    @Test
    public void stopsCLInteractorSuccessfully() {
        clInteractor.stop();
        verify(clModel, times(1)).appendOutput("Stopping CLI...");
    }

    @Test
    public void clearsOutputSuccessfully() {
        clInteractor.clear();
        verify(clModel, times(1)).appendOutput("Clearing output...");
    }

    @Test
    public void runsMenuInTerminalSuccessfully() {
        when(userModel.getPlatform()).thenReturn("win");
        when(clModel.getMenuCommand()).thenReturn("java -cp target/classes/ nivohub.devInspector.controller.CommandLineMenu");
        clInteractor.runCLMenu();
        verify(clModel, times(1)).appendOutput("Running menu in terminal for platform: win");
    }

    @Test
    public void addsMessageToOutputSuccessfully() {
        clInteractor.addMessageToOutput("Test message");
        verify(clModel, times(1)).appendOutput("Test message");
    }
}
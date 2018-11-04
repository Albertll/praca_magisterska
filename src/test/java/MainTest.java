import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import agh.Main;

import static org.junit.Assert.*;

public class MainTest extends ApplicationTest {
    private Parent mainNode;

    @Override
    public void start (Stage stage) throws Exception {
        mainNode = FXMLLoader.load(Main.class.getResource("/fxml/main.fxml"));
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @Before
    public void setUp () throws Exception {
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void testClassifyButton () {
        clickOn("#b1");
        Button button = from(mainNode).lookup("#b1").query();
        assertEquals("Klasyfikuj", button.getText());
    }

    @Test
    public void testExtremeButton () {
        clickOn("#b2");
        Button button = from(mainNode).lookup("#b2").query();
        assertEquals("Wyznacz wartości ekstremalne", button.getText());
    }

    @Test
    public void testPredictButton () {
        clickOn("#b3");
        Button button = from(mainNode).lookup("#b3").query();
        assertEquals("Przewidywanie", button.getText());
    }

    @Test
    public void testStatsButton () {
        clickOn("#b4");
        Button button = from(mainNode).lookup("#b4").query();
        assertEquals("Statystyki", button.getText());
    }

    @Test
    public void testGenerateButton () {
        clickOn("#b5");
        Button button = from(mainNode).lookup("#b5").query();
        assertEquals("Wprowadź nowe dane", button.getText());
    }
}
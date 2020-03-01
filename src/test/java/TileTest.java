import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jws.soap.SOAPBinding;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TileTest {
    private Tile tile;
    private Set<Color> expectedCol;
    private int expectedNum;

    @Before
    public void setUp() throws Exception {
        tile = new Tile(true);
    }

    @After
    public void tearDown() throws Exception {
    }
}
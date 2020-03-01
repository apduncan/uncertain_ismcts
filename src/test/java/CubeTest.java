import org.junit.Test;

import static org.junit.Assert.*;

public class CubeTest {

    @Test
    public void getColor() {
        Cube cube = new Cube(Color.BLUE);
        assertEquals(cube.getColor(), Color.BLUE);
    }

    @Test
    public void testToString() {
        Cube cube = new Cube(Color.BLUE);
        assertEquals(cube.toString(), ("C[BLUE]"));
    }

    @Test
    public void testEquals() {
        Cube blueOne = new Cube(Color.BLUE);
        Cube blueTwo = new Cube(Color.BLUE);
        Cube orange = new Cube(Color.ORANGE);
        assertEquals(blueOne, blueTwo);
        assertNotEquals(blueOne, orange);
        assertNotEquals(blueTwo, orange);
    }

    @Test
    public void testHashCode() {
        Cube blueOne = new Cube(Color.BLUE);
        Cube blueTwo = new Cube(Color.BLUE);
        Cube green = new Cube(Color.GREEN);
        assertEquals(blueOne.hashCode(), blueTwo.hashCode());
        assertNotEquals(blueOne.hashCode(), green.hashCode());
        assertNotEquals(blueTwo.hashCode(), green.hashCode());
    }
}
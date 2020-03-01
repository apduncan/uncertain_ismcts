import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class SpaceTest {
    private Space space;

    @Before
    public void setUp() throws Exception {
        this.space = new Space();
        this.space.addCube(new Cube(Color.BLUE));
        this.space.addCube(new Cube(Color.BLUE));
        this.space.addCube(new Cube(Color.ORANGE));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getCubeCount() {
        assertEquals(3, this.space.getCubeCount());
    }

    @Test
    public void getColors() {
        Set<Color> expected = new HashSet<>();
        expected.add(Color.BLUE);
        expected.add(Color.ORANGE);
        assertEquals(this.space.getColors(), expected);
    }
     @Test
    public void testToString() {
         System.out.println(this.space.toString());
     }

    @Test
    public void testEquals() {
        // Set up an equivalent space
        // Using copy constructor
        Space equiSpace = new Space(this.space);
        assertEquals(equiSpace, this.space);
        assertEquals(this.space, equiSpace);
        // Set up equivalent space from scratch, with the cubes in a different order
        Space equiNewSpace = new Space();
        equiNewSpace.addCubes(Arrays.asList(new Cube[] {
                new Cube(Color.BLUE),
                new Cube(Color.ORANGE),
                new Cube(Color.BLUE)
        }));
        assertEquals(this.space, equiNewSpace);
        assertEquals(equiNewSpace, this.space);
        // Create an unequal space
        Space diffSpace = new Space(this.space);
        diffSpace.addCube(new Cube(Color.GREEN));
        assertNotEquals(this.space, diffSpace);
    }


    @Test
    public void testHashCode() {
        // Test copy has same hash
        Space copy = new Space(this.space);
        assertEquals(copy.hashCode(), this.space.hashCode());
        // Add a cube and check different
        copy.addCube(new Cube(Color.GREEN));
        assertNotEquals(copy.hashCode(), this.space.hashCode());
        // Test different order has same hash
        Space equiv = new Space(Arrays.asList(new Cube[] {
                new Cube(Color.ORANGE),
                new Cube(Color.BLUE),
                new Cube(Color.BLUE)
        }));
        assertEquals(equiv.hashCode(), this.space.hashCode());
    }
}
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Cube implements Comparable<Cube> {
    private Color color;

    public Cube(Color color) {
        this.color = color;
    }

    public Cube(Cube cube) {
        this.color = cube.color;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return "C["
                + color +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        // Consider two cubes of the same color to be equal
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Cube cube = (Cube) o;

        return new EqualsBuilder()
                .append(color, cube.color)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(color)
                .toHashCode();
    }

    @Override
    public int compareTo(Cube cube) {
        return this.toString().compareTo(cube.toString());
    }
}

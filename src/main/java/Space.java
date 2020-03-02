import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class Space {
    private List<Cube> cubes;
    private Set<Color> colors;

    public Space() {
        this.cubes = new ArrayList<>();
        this.colors = new HashSet<>();
    }

    public Space(Space space) {
        // Copy constructor, does not copy adjacent tiles or spaces
        this();
        this.addCubes(space.cubes.stream().map((cube) -> new Cube(cube)).collect(Collectors.toList()));
    }

    public Space(List<Cube> cubes) {
        // Copy the list, do not use reference to list
        this();
        this.addCubes(cubes.stream().collect(Collectors.toList()));
    }

    public int getCubeCount() {
        return this.cubes.size();
    }

    public Set<Color> getColors() {
        return this.colors;
    }

    public void addCube(Cube cube) {
        this.addCubes(Arrays.asList(cube));
    }

    public void addCubes(Collection<Cube> cubes) {
        if(!Objects.isNull(cubes)) {
            this.cubes.addAll(cubes);
            // Sort list
            this.sortCubes();
        }
    }

    public void removeCube(Cube cube) {
        this.cubes.remove(cube);
        this.sortCubes();
    }

    private void sortCubes() {
        this.cubes = this.cubes.stream().sorted(Cube::compareTo).collect(Collectors.toList());
        this.colors = this.getCubes().stream()
                .map(Cube::getColor)
                .collect(Collectors.toSet());
    }

    public void clearCubes() {
        this.cubes = new ArrayList<>();
    }

    public List<Cube> getCubes() {
        // Return a sorted list of cubes
        return this.cubes;
    }

    @Override
    public String toString() {
        String cubes = this.getCubes().stream()
                .map(c -> String.valueOf(c.getColor().toString().charAt(0)))
                .collect(Collectors.joining(""));
        return cubes;
    }

    public void setCubes(List<Cube> cubes) {
        this.cubes = cubes;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.getCubes())
                .toHashCode();
    }
}
